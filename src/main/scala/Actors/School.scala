package Actors

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import akka.pattern.pipe
import akka.stream.scaladsl.Source
import utils.NotSoFancySchoolDatabase

import scala.concurrent.ExecutionContext

//import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise, blocking}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class School(master: ActorRef) extends Actor with ActorLogging {
    //internal state
    val parent = master
    val db = new NotSoFancySchoolDatabase

    override def preStart(): Unit = log.info(s"[School] School Actor ${self.path.name} is created")

    override def postStop(): Unit = log.info(s"[School] School Actor ${self.path.name} is stopped")

    override def receive: Receive = {
        /**
         * Write batch of data to DB
         */
        case WriteToDB(grades) =>
            log.info(s"[School] Writes batch to School ${self.path.name} DB.")
            implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("my-dispatcher")
            grades.foreach(x => {
                db.storeGrade(x) recoverWith {
                    //retry loading if failed
                    case e: RuntimeException =>
                        e.printStackTrace()
                        log.error(s"[Recovery]: after ${e.getMessage}. Retrying $x into ${self.path.name}")
                        db.storeGrade(x)
                }
            })

        /**
         * Read from DB and print current state
         */
        case ReadFromDB => log.info(s"Current state of ${self.path.name} - noOfGrade: ${db.retrieveNoOfGrades}, " +
            s"sumOfGrades: ${db.retrieveSumOfGrades}")

        /**
         * Send a statistic to a sender
         */
        case Statistic =>
            val noOfGrades = db.retrieveNoOfGrades
            val sumOfGrades = db.retrieveSumOfGrades
            val average = BigDecimal(sumOfGrades.toFloat / noOfGrades).setScale(2, BigDecimal.RoundingMode.HALF_EVEN).toFloat

            sender() ! SchoolStatistic(self.path.name, noOfGrades, average)

        /**
         * Terminate an actor
         */
        case Done => context.stop(self)

        case _ => log.info("[School] Unknown message")
    }
}

case class WriteToDB(grades: Seq[Int])

case object ReadFromDB

case object WriteFromDB

case object Statistic