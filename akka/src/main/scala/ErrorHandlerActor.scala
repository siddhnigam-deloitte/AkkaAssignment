import akka.actor.Actor
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object ErrorHandlerActor {

  class ErrorHandler extends Actor {
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    override def receive: Receive = {
      case ex: Throwable =>
        Source.single(ex).runWith(Sink.foreach(println))
    }

  }}
