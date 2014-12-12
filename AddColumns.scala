import java.io.{ FileReader, FileWriter }
import au.com.bytecode.opencsv.{ CSVReader, CSVWriter }
import scala.collection.JavaConverters._

object AddColumns extends App {
  val indexPath = args(0)
  val dataPath = args(1)
  val outPath = args(2)

  val indexReader = new CSVReader(new FileReader(indexPath))
  val imageNameIndex = indexReader.readAll().asScala.tail.map { columns =>
  	columns(5).dropRight(4) -> (columns(0) +: columns.drop(5).toList)
  }.toMap

  indexReader.close()

  val dataReader = new CSVReader(new FileReader(dataPath))
  val dataWriter = new CSVWriter(new FileWriter(outPath))
  val dataLines = dataReader.readAll().asScala

  dataReader.close()

  // Write header
  dataWriter.writeNext(dataLines.head)

  dataLines.tail.foreach { columns =>
  	val imageName = columns(5)

  	val extraColumns = imageNameIndex.getOrElse(
  	  imageName,
  	  throw new Exception(s"Invalid image name: $imageName")
  	)

  	val newColumns = columns.take(5) ++ extraColumns ++ columns.drop(5)

  	dataWriter.writeNext(newColumns)
  }
}
