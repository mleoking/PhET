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

//    //See if the user used lots of the important controls
//    println()
//    println("#########################")
//    println("######### Processing coverage")
//    val userEvents = eventLog find moleculePolarityEvents
//    println("At the end of the sim, the user had played with " + userEvents.size + " / " + moleculePolarityEvents.size + " interesting events.")
//    val userMissed = moleculePolarityEvents -- userEvents
//    println("Things the user didn't do: " + userMissed)