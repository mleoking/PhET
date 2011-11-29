import edu.colorado.phet.simsharinganalysis._
import phet._
import scripts.DoProcessEvents

val logs = phet load "C:\\Users\\Sam\\Desktop\\phet-raw-data-11-13-2011"

//def jcNov8Morning(log: EventLog) = log.day == "11-08-2011" && log.study == "colorado" && log.user != "null" && log.user != "samreid"
//def emNov7Filter(log: EventLog) = log.day == "11-07-2011" && log.study == "utah" && log.user != "null" && log.user != "samreid"

val selected = logs.filter(log =>
                             log.day == "11-09-2011" &&
                             log.study == "utah" &&
                             //                            log.study == "utah" &&
                             //                            log.epoch > 1320792019874L &&
                             log.user != "samreid" &&
                             !log.machine.startsWith("samreid") &&
                             !log.machine.startsWith("chrismalley")).sortBy(_.epoch)
println("found: " + selected.length + " logs")
selected.print

val totalEvents = selected.map(_.size).sum
println("Total events: " + totalEvents)

val machines = selected.map(_.machine).distinct
println("Number of unique machines: " + machines.size)

val users = selected.map(_.user).distinct.sortBy(numerical)
println("Unique userIDs: " + users.size + ": " + users)

val eventCountData = selected.map(_.eventCountData)
xyplot("Events vs time", "Time (minutes)", "Events", eventCountData)

for ( log <- selected ) {
  println(log.histogramByObject)
}

val simEventMap = DoProcessEvents.simEventMap
for ( sim <- simEventMap.keys; simLogs = selected.filter(_.simName == sim); if simLogs.size > 0 ) {

  //Find which events are important in this sim
  val simEvents = simEventMap(sim)

  val filteredDataSets = simLogs.map(_.countEvents(simEvents))
  xyplot("Filtered events vs time for " + sim, "Time (minutes)", "Events", filteredDataSets)

  //Print how many features each user used
  val sorted = selected.sortBy(_.findMatches(simEvents).size)
  for ( log <- sorted ) {
    val userEvents = log findMatches simEvents
    println("user " + log.user + " matched " + userEvents.size + "/" + simEvents.size)
  }

  println("unused features in " + sim)
  //val unusedFeatures = for (event <- simToUse if selected.filter(log => (log find (event :: Nil))).size==0) yield event
  val unusedFeatures = simEvents.filter(event => selected.filter(_.matches(event)).size == 0)
  println(unusedFeatures mkString "\n")

  println("who used what in " + sim)
  val whoUsedWhat = for ( event <- simEvents ) yield {
    val used = selected.filter(_.matches(event)).map(_.user)
    used -> event
  }
  println(whoUsedWhat mkString "\n")

}

val allEvents = selected.flatMap(_.entries)

//See all the system events
//println("Distinct system events")
//val systemEvents = allEvents.filter(_.actor == "system").distinct
//println(systemEvents mkString "\n")

println("Distinct window sizes")
val sizes = logs.flatMap(_.entries).filter(entry => entry.actor == "window" && entry.event == "resized").map(event => event("width") + ", " + event("height")).distinct
println(sizes mkString "\n")

println(logs.flatMap(_.entries).filter(_.actor == "menu").map(_("text")).distinct mkString "\n")
println(logs.flatMap(_.entries).filter(_.actor == "menuItem").map(_("text")).distinct mkString "\n")