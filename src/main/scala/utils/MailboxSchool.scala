package utils

import Actors.School.{ReadFromDB, Statistic, WriteToDB}

import akka.actor.ActorSystem
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

class MailboxSchool(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    PriorityGenerator {
        case WriteToDB(value) => 0
        case ReadFromDB => 1
        case Statistic => 2
        case _ => 3
    })