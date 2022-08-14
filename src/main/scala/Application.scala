import Actors.{AskSchoolStatistic, GetRegisteredSchools, LoadData, SchoolManagementSystem, SendData, StudentDataImporter}
import akka.Done
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


object Application extends App {


    implicit val system = ActorSystem("SchoolManagementSystem", ConfigFactory.load().getConfig("mailboxes"))

    val sms = system.actorOf(Props[SchoolManagementSystem], "SMS")
    val dataImporter = system.actorOf(Props(new StudentDataImporter(sms)).withMailbox("mailbox-data-importer-dispatcher"), "SDI")


    val pathToData = "src/main/resources/data/studentspart.csv"
    val pathToData2 = "src/main/resources/data/student.csv"

    dataImporter ! LoadData(pathToData2)
    system.scheduler.scheduleOnce(1 seconds, dataImporter, GetRegisteredSchools)(system.dispatcher)
    system.scheduler.scheduleOnce(5 seconds, dataImporter, SendData)(system.dispatcher)
    system.scheduler.scheduleOnce(60 seconds, dataImporter, AskSchoolStatistic("GP"))(system.dispatcher)
    system.scheduler.scheduleOnce(60 seconds, dataImporter, AskSchoolStatistic("MS"))(system.dispatcher)
    system.scheduler.scheduleOnce(65 seconds, dataImporter, Done)(system.dispatcher)


}



