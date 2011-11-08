import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\all-11-8-2011-iii"

//def jcNov8Morning(log: EventLog) = log.day == "11-08-2011" && log.study == "colorado" && log.user != "null" && log.user != "samreid"
//def emNov7Filter(log: EventLog) = log.day == "11-07-2011" && log.study == "utah" && log.user != "null" && log.user != "samreid"

val selected = all.filter(log => log.day == "11-08-2011" && log.epoch >= 1320770161752L).sortBy(_.userNumber)
println("found: " + selected.length + " logs")
selected.print

val totalEvents = selected.map(_.size).sum
println("Total events: " + totalEvents)

val machines = selected.map(_.machine).distinct
println("Number of unique machines: " + machines.size)

val users = selected.map(_.user).distinct
println("Unique userIDs: " + users.size + ": " + users)

val allEvents = selected.map(log => timeSeries(log, log.countEvents(_)))
xyplot("Events vs time", "Time (minutes)", "Events", allEvents)

for ( log <- selected ) {
  println(log.histogramByObject)
}

val simToUse = simEvents.balancingChemicalEquations

val importantEvents = selected.map(log => timeSeries(log, log.countMatches(simToUse, _)))
xyplot("Filtered events vs time", "Time (minutes)", "Events", importantEvents)

//Print how many features each user used
val sorted = selected.sortBy(_.findMatches(simToUse).size)
for ( log <- sorted ) {
  val userEvents = log findMatches simToUse
  println("user " + log.user + " matched " + userEvents.size + "/" + simToUse.size)
}

println("unused features")
//val unusedFeatures = for (event <- simToUse if selected.filter(log => (log find (event :: Nil))).size==0) yield event
val unusedFeatures = simToUse.filter(event => selected.filter(_.matches(event)).size == 0)
println(unusedFeatures mkString "\n")

println("who used what")
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