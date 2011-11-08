import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\file-vi"
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

val importantMoleculeShapesEvents = Entry("draggingState", "changed", "dragging" -> "true", "dragMode" -> "PAIR_EXISTING_SPHERICAL") ::
                                    Entry("draggingState", "changed", "dragging" -> "true", "dragMode" -> "MODEL_ROTATE") ::
                                    Entry("checkBox", "pressed", "text" -> "Molecule Geometry") ::
                                    Entry("checkBox", "pressed", "text" -> "Show Bond Angles") ::
                                    Entry("checkBox", "pressed", "text" -> "Show Lone Pairs") ::
                                    Entry("buttonNode", "pressed", "actionCommand" -> "Remove All") ::
                                    Entry("bond", "created", "bondOrder" -> "0") ::
                                    Entry("bond", "created", "bondOrder" -> "1") ::
                                    Entry("bond", "created", "bondOrder" -> "2") ::
                                    Entry("bond", "created", "bondOrder" -> "3") ::
                                    Entry("bond", "removed", "bondOrder" -> "0") ::
                                    Entry("bond", "removed", "bondOrder" -> "1") ::
                                    Entry("bond", "removed", "bondOrder" -> "2") ::
                                    Entry("bond", "removed", "bondOrder" -> "3") ::
                                    Nil

for ( log: ScalaEventLog <- selected ) {
  //  println("log: " + log)
  //  val userEvents = log find moleculePolarityEvents
  //  println("At the end of the sim, the user had played with " + userEvents.size + " / " + moleculePolarityEvents.size + " interesting events.")
  //  val userMissed = moleculePolarityEvents -- userEvents
  //  println("Things the user didn't do: " + userMissed)
}