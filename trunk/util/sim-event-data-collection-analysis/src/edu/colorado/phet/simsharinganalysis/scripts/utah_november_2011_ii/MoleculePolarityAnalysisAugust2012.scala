package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011_ii

import java.io.File
import io.Source
import collection.mutable.ArrayBuffer
import java.text.DecimalFormat

/**
 * Emily said:
 * I’m back to working on the Molecule Polarity sim data (from the Utah study last November). I’d like to do two types of analysis:
 * 1)   What % of clickable/moveable options did each group interact with during the “play only” time? (this would include clicking on any tool/tab, and moving a moveable object.)
 * 2)   A histogram (representing all student groups) showing the amount of new moveable objects/tools clicked on over the “play only” time.
 */
case class Arg(key: String, value: String)

case class UniqueComponent(tab: String, component: String, name: String) {
  override def toString = tab + ": " + component + ( if ( name == "" ) "" else ": " + name )
}

case class Entry(localTime: Long, serverTime: Long, component: String, action: String, args: List[Arg]) {
  def text = args.find(_.key == "text").map(_.value)

  def apply(key: String) = {
    val matched = args.filter(_.key == key)
    if ( matched.length == 1 ) {
      matched(0).value
    } else {
      ""
      //      throw new RuntimeException("No match found or maybe multiple matches found: matched="+matched)
    }
  }
}

case class Log(file: File, machineID: String, sessionID: String, serverTime: Long, entries: List[Entry]) {
  def id = entries(0).args.find(_.key == "id").map(_.value).getOrElse("")
}

object MoleculePolarityAnalysisAugust2012 {}

case class State(tab: String)

case class Element(start: State, entry: Entry, end: State)

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
    val serverStartTime = afterEquals(lines(2)).toLong
    val localStartTime = lines(3).split('\t').apply(0).trim.toLong
    if ( file.getName.contains("urrpk0p2") ) {
      println("sst = " + serverStartTime + ", localstarttime=" + localStartTime)
    }
    Log(file, afterEquals(lines(0)), afterEquals(lines(1)), serverStartTime, lines.slice(3, lines.length).map(s => toEntry(s, serverStartTime, localStartTime)).toList)
  }

  def parseElement(s: String) = {
    val parsed = s.split('=').map(_.trim)
    if ( parsed.length == 2 )
      Arg(parsed(0), parsed(1))
    else
      Arg(parsed(0), "")
  }

  def toEntry(s: String, serverStartTime: Long, localStartTime: Long) = {
    val elements = s.split('\t').map(_.trim)
    val remainingElements = elements.slice(3, elements.length)
    val localTime = elements(0).toLong

    //    to get the time for any event in any log
    //    server time on line N = original server time on line 3 - local time on line 4 + local time on line N
    //    correct because line 4 happens exactly when server time is reported

    val serverTime = serverStartTime - localStartTime + localTime
    Entry(localTime, serverTime, elements(1), elements(2), remainingElements.map(parseElement).toList)
  }

  def minutesToMilliseconds(minutes: Double) = ( minutes * 60000.0 ).toLong

  def getUsedComponents(elements: Seq[Element], filter: Entry => Boolean) = {
    val allowedElements = elements.filter(element => filter(element.entry))
    val textComponents = allowedElements.map(element => {
      val text = element.entry match {
        case e: Entry if e.text.isDefined => e.text.get
        case e: Entry if e.component == "mouse" && e("component") == "jmolViewerNode" => e("component") + ":" + e("currentMolecule")
        case e: Entry if e.component == "mouse" && e.action == "startDrag" => "startDrag:" + e("atom")
        case e: Entry if e.component == "buttonNode" => e("actionCommand")
        case _ => ""
      }

      UniqueComponent(element.start.tab, element.entry.component, text)
    }).toSet.toList
    textComponents // .map(_.toString).sorted
  }

  val tabNames = "Two Atoms" :: "Three Atoms" :: "Real Molecules" :: Nil

  def process(state: State, entry: Entry) = {
    if ( entry.component == "tab" && entry.action == "pressed" )
      State(entry.text.get)
    else
      state
  }

  def getStates(log: Log) = {
    val states = new ArrayBuffer[Element]
    var startState = State(tabNames(0))
    for ( e <- log.entries ) {
      val newState = process(startState, e)
      states += Element(startState, e, newState)
      startState = newState
    }
    states.toList
  }

  def main(args: Array[String]) {
    val file = new File("C:\\Users\\Sam\\Desktop\\molecule-polarity")
    val logs = file.listFiles.map(file => (file, readText(file))).map(tuple => parseFile(tuple._1, tuple._2))
    logs.foreach(println)
    logs.map(_.id).foreach(println)
    logs.map(_.entries(0)).foreach(println)

    val group2 = logs.filter(_.id == "2")
    assert(group2.length == 1)
    //For the recording that is linked with audio #2, the play time was from (min:sec) 0:00-9:30 from the start.
    val startPlayTime = group2.apply(0).serverTime

    //Time on the server
    val elapsedPlayTime: Long = minutesToMilliseconds(9.5)
    val endPlayTime: Long = startPlayTime + elapsedPlayTime

    //Todo could flat map this probably
    val allComponents = new ArrayBuffer[UniqueComponent]
    for ( log <- logs ) {
      val used = getUsedComponents(getStates(log), e => true)
      allComponents ++= used
    }

    println("All")
    val componentSet = allComponents.toSet
    componentSet.map(_.toString).toList.sorted.foreach(println)

    println("Total items possible: " + componentSet.size)
    val formatter = new DecimalFormat("0.00")
    println("group\tcomponents used during\"play only\" time")
    for ( log <- logs.sortBy(_.id) ) {
      val elements = getStates(log)
      val entriesUsedInPlayTime = getUsedComponents(elements, e => e.serverTime >= startPlayTime && e.serverTime <= endPlayTime)
      val entriesUsedAnyTime = getUsedComponents(elements, e => true)
      //      println("log: " + log.id + ", used=" + entriesUsedInPlayTime.length + "/" + entriesUsedAnyTime.length + "/" + componentSet.size)
      //      println("log: " + log.id + ", used=" + entriesUsedInPlayTime.length + "/" + entriesUsedAnyTime.length + "/" + componentSet.size)
      println(log.id + "\t" + formatter.format(entriesUsedInPlayTime.length.toDouble / componentSet.size.toDouble * 100.0) + "%")
    }
  }
}
