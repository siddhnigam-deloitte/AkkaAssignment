import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import java.nio.file.Paths
import scala.concurrent.Future

object Checkpoint5 extends App{

  val applicationConf = ConfigFactory.load("application.conf") //loading from configuration file
  val filepath = applicationConf.getString("app.filepath") //getting file path from configuration file

  implicit val system = ActorSystem("FileReaderSystem")

  val categoryFilter = applicationConf.getString("app.categoryFilter") //getting category filter from configuration file
  val FinancialYear = applicationConf.getString("app.year") //getting category filter from configuration file
  val categorySinkFile = applicationConf.getString("app.CategorySinkFile") //getting category sink file from configuration file



  val fileSource: Source[ByteString, Future[IOResult]] =
    FileIO.fromPath(Paths.get(filepath))


  val categoryFilterFlow: Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .filter(record => {
        val fields = record.utf8String.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
        fields(9) == categoryFilter
      })


  val categorySink: Sink[ByteString, Future[IOResult]] =
    FileIO.toPath(Paths.get(categorySinkFile))

  fileSource
    .via(categoryFilterFlow)
    .to(categorySink)
    .run()

}
