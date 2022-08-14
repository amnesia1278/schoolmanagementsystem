package Actors

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import akka.stream.alpakka.csv.scaladsl.CsvParsing.SemiColon
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.Timeout

import java.nio.file.Paths
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

class StudentDataImporter(sms: ActorRef) extends Actor with ActorLogging {

    val mainSMS = sms

    override def preStart(): Unit = log.info(s"[StudentDataImporter] Actor is created")

    override def postStop(): Unit = log.info(s"[StudentDataImporter] Actor is stopped")

    override def receive: Receive = withoutSchools(None)

    /**
     * Default behavior withoutSchools(None) data and list of schools are unknown.
     * @param data: from external source e.g., csv
     * @return
     */
    def withoutSchools(data: Option[Seq[Map[String, String]]]): Receive = {

        /**
         * Read data from path, get set of schools and send it to the SMS to create a school if not exists
         * Change behavior to without known Schools but with "saved" batch of data.
         */
        case LoadData(pathToData: String) => log.info(s"[StudentDataImporter] Loading data from CSV file $pathToData")
            implicit val sys = context.system
            val file = Paths.get(pathToData)
            val source = FileIO.fromPath(file)
            val res = source
                .via(CsvParsing.lineScanner(delimiter = SemiColon))
                .via(CsvToMap.toMapAsStringsCombineAll(headerPlaceholder = Option.empty))
                .runWith(Sink.seq)

            res.onComplete({
                case Success(result) =>
                    log.info("[StudentDataImporter] Sending list of schools to be created to SMS")
                    val schools = result.map(elem => elem.get("school").get).toSet
                    schools.map(x =>
                        mainSMS ! CreateSchool(x))
                    context.become(withoutSchools(Some(result)))
                case Failure(exception) => log.info(exception.getMessage)
            })

        /**
         * Send message to SMS asking for the list of active schools
         */
        case GetRegisteredSchools =>
            log.info("[StudentDataImporter] getting list of registered schools")
            mainSMS ! SchoolList

        /**
         * Receiving a list of schools and change behavior to withSchools
         */
        case SchoolList(registeredSchools) if data.isDefined =>
            context.become(withSchools(registeredSchools, data.get))

    }

    /**
     * Behavior when potential active schools are known.
     * @param schoolList: list of active schools from SMS
     * @param batch: data to be loaded
     */
    def withSchools(schoolList: List[ActorRef], batch: Seq[Map[String, String]]): Receive = {
        /**
         * Send data to the known list if school. Each schools gets the list
         * of grades which should be processed internally
         */
        case SendData =>
            // get list of receivers from csv
            val listOfReceiver = batch.map(elem => elem.get("school").get).toSet

            listOfReceiver.map(school => {
                if (schoolList.map(_.path.name).contains(school)) {
                    val receiver = schoolList.filter(actor => actor.path.name == school).head

                    val gradesForGivenSchool = batch.filter(elem => elem.get("school").get == school)
                    log.info(s"[StudentDataImporter] sends ${gradesForGivenSchool.size} rows to the $school")
                    // filter data by school name and transform into Seq[Int]
                    val gradesList = gradesForGivenSchool
                        .map(elem => elem.get("G3"))
                        .filter(_.isDefined)
                        .map(str => str.get.toInt)

                    receiver ! WriteToDB(gradesList)

                } else {
                    // should not happen
                    log.info(s"[StudentDataImporter] School: $school does not exist. You should firstly create it.")
                }
            })

        /**
         * Send a message to a school by name asking for its statistic
         */
        case AskSchoolStatistic(school) =>
            if (schoolList.map(_.path.name).contains(school)) {
                implicit val timeout: Timeout = Timeout(5 seconds)
                log.info(s"[StudentDataImporter] Asking statistic of $school")
                val receiver = schoolList.filter(actor => actor.path.name == school).head
                receiver ! Statistic
            } else {
                log.info(s"[StudentDataImporter] School: $school does not exist")
            }

        /**
         * Print school statistic
         */
        case SchoolStatistic(name, number, average) =>
            log.info(s"Statistic of $name - noOfGrades: $number and average grade: $average")

        /**
         * Change behavior back to default
         */
        case LoadData(path) =>
            log.info("[StudentDataImporter] Change to loading mode")
            context.become(withoutSchools(None))
            self ! LoadData(path)

        /**
         * Terminate all SMS and schools when done
         */
        case Done =>
            schoolList.map(_ ! Done)
            mainSMS ! Done
            context.stop(self)

        case _ => log.info("[StudentDataImporter] Wrong input. I need path and actorRef")
    }
}

case class LoadData(path: String)

case object SendData

case class AskSchoolStatistic(school: String)

case object GetRegisteredSchools

case class SchoolStatistic(name: String, numberOfGrades: Int, average: Float)
