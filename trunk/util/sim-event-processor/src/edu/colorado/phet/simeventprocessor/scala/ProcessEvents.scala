import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\all-11-8-2011-i"

def jcNov8Morning(log: EventLog) = log.day == "11-08-2011" && log.study == "colorado" && log.user != "null" && log.user != "samreid"

val selected = all.filter(log =>
                            log.day == "11-07-2011" &&
                            log.study == "utah" &&
                            log.user != "null" &&
                            log.user != "samreid").sortBy(_.epoch)
println("found: " + selected.length + " logs")
selected.print

val totalEvents = selected.map(_.size).reduceLeft(_ + _)
println("Total events: " + totalEvents)

val machines = selected.map(_.machine).distinct
println("Unique machine IDs: " + machines.size)

val users = selected.map(_.user).distinct.sorted
println("Unique userIDs: " + users.size + ": " + users)

val toPlot = selected.map(log => series(log, selected, log.countEvents(_)))
plot("Events vs time", "Time (minutes)", "Events", toPlot)

for ( log <- selected ) {
  val histogram = log.histogramByObject
  println(histogram)
}

val moleculeShapes = Rule("draggingState", "changed", "dragging" -> "true", "dragMode" -> "PAIR_EXISTING_SPHERICAL") ::
                     Rule("draggingState", "changed", "dragging" -> "true", "dragMode" -> "MODEL_ROTATE") ::
                     Rule("checkBox", "pressed", "text" -> "Molecule Geometry") ::
                     Rule("checkBox", "pressed", "text" -> "Show Bond Angles") ::
                     Rule("checkBox", "pressed", "text" -> "Show Lone Pairs") ::
                     Rule("buttonNode", "pressed", "actionCommand" -> "Remove All") ::
                     Rule("bond", "created", "bondOrder" -> "0") ::
                     Rule("bond", "created", "bondOrder" -> "1") ::
                     Rule("bond", "created", "bondOrder" -> "2") ::
                     Rule("bond", "created", "bondOrder" -> "3") ::
                     Rule("bond", "removed", "bondOrder" -> "0") ::
                     Rule("bond", "removed", "bondOrder" -> "1") ::
                     Rule("bond", "removed", "bondOrder" -> "2") ::
                     Rule("bond", "removed", "bondOrder" -> "3") ::
                     Nil

val sorted = selected.sortBy(_.find(moleculeShapes).size)

for ( log: EventLog <- sorted ) {
  val userEvents = log find moleculeShapes
  println("user " + log.user + " matched " + userEvents.size + "/" + moleculeShapes.size)

  //  val userMissed = importantMoleculeShapesEvents -- userEvents
  //  println("Things the user didn't do: " + userMissed)
}