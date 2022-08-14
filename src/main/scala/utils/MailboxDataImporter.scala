package utils

import Actors.{AskSchoolStatistic, GetRegisteredSchools, LoadData, SchoolList, SchoolStatistic, SendData}
import akka.actor.ActorSystem
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

class MailboxDataImporter(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    PriorityGenerator {
        case LoadData => 0
        case GetRegisteredSchools |  SchoolList => 1
        case SendData => 2
        case AskSchoolStatistic | SchoolStatistic => 3
        case _ => 4
    })