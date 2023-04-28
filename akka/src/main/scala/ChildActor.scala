import Assignment.{Record, applicationConf}
import akka.NotUsed
import akka.actor.Actor
import akka.stream.alpakka.csv.impl.CsvFormatter
import akka.stream.{ActorAttributes, ActorMaterializer, IOResult}
import akka.stream.alpakka.csv.scaladsl
import akka.stream.alpakka.csv.scaladsl.{CsvFormatting, CsvQuotingStyle}
import akka.stream.scaladsl.BroadcastHub.sink
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import java.io.{File, FileWriter}
import java.nio.file.Paths
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}



object ChildActor {

  def personToCsv(person: Record): String = {
    s"${person.Category},${person.Name}\n"
  }

  val applicationConf=ConfigFactory.load("application.conf");
  val categorysinkfile = applicationConf.getString("app.CategorySinkFile")
  class ChildActor extends Actor {
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    override def receive: Receive = {
      case record: Array[String] =>

        val result = Try(Record(record(0), record(1), record(2), record(3), record(4), record(5), record(6), record(7), record(8), record(9), record(10), record(11), record(12), record(13), record(14), record(15)))
        result match {
          case Success(data) =>
            if(validation(record)) {
              val source=Source.single(data)

              val sink=Sink.foreach(println)

              //source.to(sink).run()



            }
          case Failure(ex) =>
            context.actorSelection("/user/masterActor/errorHandler") ! ex
        }
    }

  }
  def validation(record:Array[String]):Boolean= {
    for(i<-record)
    {
      if(i==""||i==null)
      {
        return false
      }
    }
    return true
  }

}
