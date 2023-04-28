import akka.NotUsed
import akka.actor.ActorSystem
import akka.actor.TypedActor.dispatcher
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.github.tototoshi.csv.CSVFormat
import org.apache.commons.csv.CSVFormat
import akka.stream.alpakka.csv.scaladsl.{CsvFormatting, CsvQuotingStyle}
import java.nio.file.Paths
import scala.concurrent.Future
import scala.reflect.io.File.separator

object Checkpoint6 extends App {
  val applicationConf = ConfigFactory.load("application.conf") //loading from configuration file
  val filepath = applicationConf.getString("app.filepath") //getting file path from configuration file

  object CsvFormattings {
    val RFC4180: CSVFormat = CSVFormat.DEFAULT.withRecordSeparator("\r\n").withDelimiter(',')
  }
  implicit val system = ActorSystem("FileReaderSystem")

  val categoryFilter = applicationConf.getString("app.categoryFilter") //getting category filter from configuration file
  val FinancialYear = applicationConf.getString("app.year") //getting category filter from configuration file
  val categorySinkFile = applicationConf.getString("app.CategorySinkFile") //getting category sink file from configuration file
  val categoryWiseFinancialYearSinkFile = applicationConf.getString("app.CategoryWiseFinancialYearSinkFile") //getting category sink file from configuration file


  val fileSource: Source[ByteString, Future[IOResult]] =
    FileIO.fromPath(Paths.get(filepath))


  val categoryFilterFlow: Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .filter(record => {
        val fields = record.utf8String.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
        fields(9) == categoryFilter
      })

  val financialYearAggregatorFlow: Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .filter(record => {
        val fields = record.utf8String.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
        fields(1).contains(FinancialYear)
      })
      .fold((0.0, 0.0, 0.0, 0.0)) { (acc, record) =>
        val fields = record.utf8String.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
        val sales = fields(12).toDouble
        val quantity = fields(13).toDouble
        val discount = fields(14).toDouble
        val profit = fields(15).toDouble
        (acc._1 + sales, acc._2 + quantity, acc._3 + discount, acc._4 + profit)
      }
      .map { result =>
        ByteString(s"${FinancialYear},${result._1},${result._2},${result._3},${result._4}\n")
      }

  val categoryWiseFinancialYearSinkcsv = FileIO.toPath(Paths.get(categoryWiseFinancialYearSinkFile))

//  val graph = financialYearAggregatorFlow.toMat(categoryWiseFinancialYearSinkcsv)(Keep.right).run()
//
//  // Run the graph and get the materialized value (a Future[IOResult])
//  val ioResultFuture = fileSource.viaMat(graph)(Keep.left).run()
//
//  // Print a message when the operation completes
//  ioResultFuture.onComplete(_ => println("CSV file written."))





    fileSource
    .via(categoryFilterFlow)
    .via(financialYearAggregatorFlow)
    .to(categoryWiseFinancialYearSinkcsv).run()


//  val graph = categoryFilterFlow.via(financialYearAggregatorFlow).toMat(categoryWiseFinancialYearSink)(Keep.right)
//
//  // Run the graph and get the materialized value (a Future[IOResult])
//  val ioResultFuture = fileSource.viaMat(graph)(Keep.left).run()
//
//  // Print a message when the operation completes
//  ioResultFuture.onComplete(_ => println("Financial year aggregator completed."))
}
