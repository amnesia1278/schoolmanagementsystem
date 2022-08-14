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
    println("power nap 5 sec")
    Thread.sleep(5000)
    dataImporter ! GetRegisteredSchools // dataImported switches to withSchools
    println("power nap 5 sec")
    Thread.sleep(5000)
    dataImporter ! SendData // loading data for each registered school which can be found in csv
    println("power nap 60 sec")
    //Thread.sleep(60000)
    system.scheduler.scheduleOnce(60 seconds) {
        dataImporter ! AskSchoolStatistic("GP")
        dataImporter ! AskSchoolStatistic("MS")
    }(system.dispatcher)
//        dataImporter ! AskSchoolStatistic("GP")
//        dataImporter ! AskSchoolStatistic("MS")
    Thread.sleep(500)
    dataImporter ! Done

}



