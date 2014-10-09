import java.io.{ FileReader, FileWriter, PrintWriter }
import au.com.bytecode.opencsv.CSVReader
import scala.collection.JavaConverters._

sealed trait Shapes
case object Circles extends Shapes
case object Lines extends Shapes

case class Line(image: String, shapes: Shapes, colors: Int)

class Generator(val expId: String) {
  def createImageUri(line: Line) =
    s"http://alexiswellwood.org/experiments/longerest/${ line.image }"

  def createSentence(line: Line) =
    (expId, line.shapes, line.colors) match {
      case ("c", Circles, 2) => "The red circles are bigger than the blue circles."
      case ("c", Circles, 3) => "The red circles are bigger than the other circles."
      case ("c", Lines, 2)   => "The red lines are longer than the blue lines."
      case ("c", Lines, 3)   => "The red lines are longer than the other lines."
      case ("s", Circles, _) => "The red circles are the biggest."
      case ("s", Lines, _)   => "The red lines are the longest."
    }

  def createHtml(line: Line) = {
    val uri = createImageUri(line)
    val sentence = createSentence(line)

    /* Hi! You can put stuff in between the """s here, and line breaks are ok too! " */
    s"""
      <img width="640.8" height="475.2" src="$uri">
	  <p style="font-size:30px" align="center">$sentence</p>
    """.trim.replaceAll("\n", " ")
  }

  def createJsItem(line: Line) = {
    val html = createHtml(line)
    val imageId = line.image.split("\\.")(0)

    s"""["$imageId", "AcceptabilityJudgment", {s: {html: '$html'}}]"""
  }

  def fieldsToLine(fields: Array[String]) = if (fields(1).startsWith("Circle"))
    Line(s"longerest_circles_imgs/${ fields(5) }", Circles, if (fields(7) == "two") 2 else 3)
  else
    Line(s"longerest_lines_imgs/${ fields(5) }", Lines, if (fields(7) == "two") 2 else 3)
}

object Generator extends App {
  val generator = new Generator(args(0))
  val times = args(1).toInt
  val reader = new CSVReader(new FileReader(args(2)))
  val lines = reader.readAll().asScala.tail
  reader.close()

  val output = (1 to times).flatMap(_ => lines).map(fields =>
    generator.createJsItem(generator.fieldsToLine(fields))
  ).mkString(",\n")

  val writer = new PrintWriter(new FileWriter(args(2).split("\\.").dropRight(1).mkString(".") + ".js"))

  writer.println(output)
  writer.close()
}
