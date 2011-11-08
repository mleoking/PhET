import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\all-11-8-2011-iv"

//def jcNov8Morning(log: EventLog) = log.day == "11-08-2011" && log.study == "colorado" && log.user != "null" && log.user != "samreid"
//def emNov7Filter(log: EventLog) = log.day == "11-07-2011" && log.study == "utah" && log.user != "null" && log.user != "samreid"

val selected = all.filter(log =>
                            log.day == "11-08-2011" &&
                            log.study == "colorado" &&
                            log.epoch > 1320783060834L).sortBy(_.epoch)
println("found: " + selected.length + " logs")
selected.print

val totalEvents = selected.map(_.size).sum
println("Total events: " + totalEvents)

val machines = selected.map(_.machine).distinct
println("Number of unique machines: " + machines.size)

val users = selected.map(_.user).distinct.sortBy(numerical)
println("Unique userIDs: " + users.size + ": " + users)

val eventCountData = selected.map(log => timeSeries(log, log.countEvents(_)))
xyplot("Events vs time", "Time (minutes)", "Events", eventCountData)

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

val allEvents = selected.flatMap(_.events)

//See all the system events
//println("Distinct system events")
//val systemEvents = allEvents.filter(_.actor == "system").distinct
//println(systemEvents mkString "\n")

println("Distinct window sizes")
val sizes = all.flatMap(_.events).filter(entry => entry.actor == "window" && entry.event = "resized").map(event => event.get("width") + ", " + event.get("height")).distinct
println(sizes mkString "\n")