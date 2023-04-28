import ChildActor.ChildActor
import ErrorHandlerActor.ErrorHandler
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.routing.RoundRobinPool

object MasterActor {

  class ReaderActor extends Actor {

    val workerRouter: ActorRef = context.actorOf(RoundRobinPool(10).props(Props[ChildActor]), "workerRouter")
    val errorHandler: ActorRef = context.actorOf(Props[ErrorHandler], "errorHandler")

    override def receive: Receive = {
      case record: Array[String] =>
        workerRouter ! record
      case Terminated(child) =>
        println(s"Child actor ${child.path.name} terminated")

    }
  }
}
