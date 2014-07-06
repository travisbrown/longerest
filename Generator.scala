import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader
import scala.collection.JavaConverters._

sealed trait Shapes
case object Circles extends Shapes
case object Lines extends Shapes

case class Line(image: String, shapes: Shapes, colors: Int)

class Generator(val expId: String) {
  def createImageUri(line: Line) =
    "http://ling.umd.edu/~wellwood/longerest_circles_imgs/" + line.image

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

    s"""
      <img src="$uri"> <p>$sentence</p>
    """.trim.replaceAll("\n", " ")
  }

  def createJsItem(line: Line) = {
    val html = createHtml(line)
    val imageId = line.image.split("\\.")(0)

    s"""["$imageId", "AcceptabilityJudgment", {s: {html: '$html'}}]"""
  }

  def fieldsToLine(fields: Array[String]) = Line(
    fields(5),
    if (fields(1).startsWith("Circle")) Circles else Lines,
    if (fields(7) == "two") 2 else 3
  )
}

object Generator extends App {
  val generator = new Generator(args(0))
  val reader = new CSVReader(new FileReader(args(1)))
  val lines = reader.readAll().asScala
  reader.close()

  val output = lines.map(fields =>
    generator.createJsItem(generator.fieldsToLine(fields))
  ).mkString(",\n")

  println(output)
}
