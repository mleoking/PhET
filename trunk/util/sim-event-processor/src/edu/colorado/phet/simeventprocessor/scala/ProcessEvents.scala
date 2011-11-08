import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\all-11-8-2011-iii"

//def jcNov8Morning(log: EventLog) = log.day == "11-08-2011" && log.study == "colorado" && log.user != "null" && log.user != "samreid"
//def emNov7Filter(log: EventLog) = log.day == "11-07-2011" && log.study == "utah" && log.user != "null" && log.user != "samreid"

val selected = all.filter(log =>
                            log.day == "11-08-2011"
                            && log.epoch >= 1320770161752L).sortBy(_.epoch)
println("found: " + selected.length + " logs")
selected.print

val totalEvents = selected.map(_.size).reduceLeft(_ + _)
println("Total events: " + totalEvents)

val machines = selected.map(_.machine).distinct
println("Unique machine IDs: " + machines.size)

val users = selected.map(_.user).distinct.sorted
println("Unique userIDs: " + users.size + ": " + users.sortBy(value => {
  try {
    value.toInt
  }
  catch {
    case nfe: NumberFormatException => -1;
  }
}))

val toPlot = selected.map(log => series(log, selected, log.countEvents(_)))
xyplot("Events vs time", "Time (minutes)", "Events", toPlot)

for ( log <- selected ) {
  println(log.histogramByObject)
}

val moleculeShapes =
  Rule("draggingState", "changed", "dragging" -> "true", "dragMode" -> "PAIR_EXISTING_SPHERICAL") ::
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

val moleculePolarity =
  Rule("tab", "pressed", "text" -> "Two Atoms") ::
  Rule("tab", "pressed", "text" -> "Three Atoms") ::
  Rule("tab", "pressed", "text" -> "Real Molecules") ::
  Rule("checkBox", "pressed", "text" -> "Bond Dipole") ::
  Rule("checkBox", "pressed", "text" -> "Partial Charges") ::
  Rule("checkBox", "pressed", "text" -> "Bond Character") ::
  Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "none") ::
  Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "Electrostatic Potential") ::
  Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "Electron Density") ::
  Rule("radioButton", "pressed", "description" -> "Electric field on", "text" -> "on") ::
  Rule("radioButton", "pressed", "description" -> "Electric field on", "text" -> "off") ::
  Rule("buttonNode", "pressed", "actionCommand" -> "Reset All") ::
  Rule("bond", "removed", "bondOrder" -> "3") ::
  Rule("mouse", "startDrag", "atom" -> "A") ::
  Rule("mouse", "startDrag", "atom" -> "B") ::
  Rule("mouse", "startDrag", "atom" -> "C") ::
  Rule("molecule rotation drag", "started") ::
  Rule("bondAngleDrag", "started") ::
  Rule("comboBoxItem", "selected") ::
  Rule("mouse", "startDrag", "component" -> "jmolViewerNode") ::
  Rule("checkBox", "pressed", "text" -> "Bond Dipoles") ::
  Rule("checkBox", "pressed", "text" -> "Molecular Dipole") ::
  Rule("checkBox", "pressed", "text" -> "Atom Electronegativities") ::
  Rule("checkBox", "pressed", "text" -> "Atom Labels") ::
  Nil

val balancingChemicalEquations =
  Rule("tab", "pressed", "text" -> "Introduction") ::
  Rule("tab", "pressed", "text" -> "Balancing Game") ::
  Rule("radioButton", "pressed", "text" -> "None") ::
  Rule("radioButton", "pressed", "text" -> "Balance Scales") ::
  Rule("radioButton", "pressed", "text" -> "Bar Charts") ::
  Rule("radioButton", "pressed", "text" -> "<html>Make Ammonia</html>") ::
  Rule("radioButton", "pressed", "text" -> "<html>Separate Water</html>") ::
  Rule("radioButton", "pressed", "text" -> "<html>Combust Methane</html>") ::
  Rule("buttonNode", "pressed", "actionCommand" -> "Reset All") ::
  Rule("spinner", "changed", "description" -> "coefficient for N<sub>2</sub>") ::
  Rule("spinner", "changed", "description" -> "coefficient for H<sub>2</sub>") ::
  Rule("spinner", "changed", "description" -> "coefficient for NH<sub>3</sub>") ::
  Rule("spinner", "changed", "description" -> "coefficient for H<sub>2</sub>O") ::
  Rule("spinner", "changed", "description" -> "coefficient for O<sub>2</sub>") ::
  Rule("spinner", "changed", "description" -> "coefficient for CH<sub>4</sub>") ::
  Rule("spinner", "changed", "description" -> "coefficient for CO<sub>2</sub>") ::
  Rule("buttonNode", "pressed", "actionCommand" -> "Start") ::
  Rule("buttonNode", "pressed", "actionCommand" -> "Check") ::
  Rule("buttonNode", "pressed", "actionCommand" -> "Show Answer") ::
  Rule("system", "gameEnded", "level" -> "1") ::
  Rule("system", "gameEnded", "level" -> "2") ::
  Rule("system", "gameEnded", "level" -> "3") ::
  Nil

val simToUse = balancingChemicalEquations

val sorted = selected.sortBy(_.findMatches(simToUse).size)

for ( log <- sorted ) {
  val userEvents = log findMatches simToUse
  println("user " + log.user + " matched " + userEvents.size + "/" + simToUse.size)

  //  val userMissed = importantMoleculeShapesEvents -- userEvents
  //  println("Things the user didn't do: " + userMissed)
}

println("unused features")
//val unusedFeatures = for (event <- simToUse if selected.filter(log => (log find (event :: Nil))).size==0) yield event
val unusedFeatures = simToUse.filter(event => selected.filter(_.matches(event)).size == 0)
println(unusedFeatures mkString "\n")

println("who used what")
//val whoUsedWhat = simToUse.map(event => selected.filter(_.matches(event)))
val whoUsedWhat = for ( event <- simToUse ) yield {
  val used = selected.filter(_.matches(event)).map(_.user)
  used -> event
}
println(whoUsedWhat mkString "\n")

//See all the spinners that were used
//println("Spinners")
//for ( log: EventLog <- selected ) {
//  val events = log findEvents "spinner"
//  println(events mkString "\n")
//}