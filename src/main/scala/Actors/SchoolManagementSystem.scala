package Actors

import akka.Done
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class SchoolManagementSystem extends Actor with ActorLogging {

    //lazy val listOfSchools = (for (child <- context.children) yield child.path.name).toList
    override def preStart(): Unit = {
        log.info(s"[SMS] SMS is created")
    }

    override def postStop(): Unit =  log.info(s"[SMS] SMS is stopped")

    override val supervisorStrategy =
        OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
        case _: RuntimeException      => Resume
        case _: NullPointerException     => Restart
        case _: IllegalArgumentException => Stop
        case _: Exception                => Escalate
    }

    override def receive: Receive = {
        // create school if not exist
        case CreateSchool(name) if !context.children.toList.map(_.path.name).contains(name) =>
            log.info(s"[SMS] created a new school - $name")
            context.actorOf(Props(new School(self)).withDispatcher("mailbox-school-dispatcher"), name)
        case SchoolList =>
            log.info(s"[SMS] the following schools are active: ${(for (child <- context.children)
                yield child.path.name).toList.mkString(", ")}. Sending them to ${sender.path.name}")
            sender() ! SchoolList(context.children.toList)
        case Done => context.stop(self)
        case _ => log.info("[SMS] Unknown message")
    }
}

case class CreateSchool(name: String)
case object SchoolList
case class SchoolList(schoolList: List[ActorRef])

//case class SchoolStatistic(name: String, numberOfGrades: Int, average: Float)