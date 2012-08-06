package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011

import java.io.File
import io.Source

/**
 * @author Sam Reid
 */
case class Arg(key: String, value: String)

case class TextComponent(component: String, name: Option[String]) {
  override def toString = component + ( if ( name.isDefined ) ": " else "" ) + name.getOrElse("")
}

case class Entry(time: Long, component: String, action: String, args: List[Arg]) {
  def text = args.find(_.key == "text").map(_.value)
}

case class Log(file: File, machineID: String, sessionID: String, serverTime: Long, entries: List[Entry]) {
  def id = entries(0).args.find(_.key == "id").map(_.value).getOrElse("")
}

object NewParser {
  def readText(f: File) = {
    val source = Source.fromFile(f)
    val text = source.mkString
    source.close()
    text
  }

  def afterEquals(s: String) = s.split('=').last.trim

  def parseFile(file: File, text: String) = {
    val lines = text.split('\n').map(_.trim)
    Log(file, afterEquals(lines(0)), afterEquals(lines(1)), afterEquals(lines(2)).toLong, lines.slice(3, lines.length).map(toEntry).toList)
  }

  def parseElement(s: String) = {
    val parsed = s.split('=').map(_.trim)
    if ( parsed.length == 2 )
      Arg(parsed(0), parsed(1))
    else
      Arg(parsed(0), "")
  }

  def toEntry(s: String) = {
    val elements = s.split('\t').map(_.trim)
    val remainingElements = elements.slice(3, elements.length)
    Entry(elements(0).toLong, elements(1), elements(2), remainingElements.map(parseElement).toList)
  }

  def minutesToMilliseconds(minutes: Double) = ( minutes * 60000.0 ).toLong

  def main(args: Array[String]) {
    val file = new File("C:\\Users\\Sam\\Desktop\\molecule-polarity")
    val logs = file.listFiles.map(file => (file, readText(file))).map(tuple => parseFile(tuple._1, tuple._2))
    logs.foreach(println)
    logs.map(_.id).foreach(println)
    logs.filter(_.id == "2").foreach(elm => println(elm.file))

    //Time on the server
    val startPlayTime: Long = 1320867727969L
    val elapsedPlayTime: Long = minutesToMilliseconds(9.5)
    val endPlayTime: Long = startPlayTime + elapsedPlayTime

    val textComponents = logs.flatMap(_.entries).map(entry => TextComponent(entry.component, entry.text)).toSet.toList
    val allUsedComponents = textComponents.map(_.toString).sorted
    allUsedComponents.foreach(println)


  }
}
