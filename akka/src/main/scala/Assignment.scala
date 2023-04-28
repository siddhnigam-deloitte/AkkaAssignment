import Assignment.{Record}
import MasterActor.ReaderActor
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success, Try}

object Assignment extends App {

  import scala.io.Source

  case class Record(var OrderDate: String, Shipdate: String, ShipMode: String, Customer: String, Segment: String, Country: String, City: String, State: String, Region: String, Category: String, SubCategory: String, Name: String, Sales: String, Quanity: String, Discount: String, Profit: String)

  val applicationConf = ConfigFactory.load("application.conf")
  val filepath = applicationConf.getString("app.filepath")

  val source = Source.fromFile(filepath)
  val lines = source.getLines()

  val system = ActorSystem("FileReaderSystem")
  val fileReaderActor = system.actorOf(Props[ReaderActor], "fileReaderActor")

  for (line <- lines) {
    val record = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?<![ ])(?![ ])")
    fileReaderActor ! record
  }

// source.close()
//
// system.terminate()
}





