import edu.colorado.phet.simsharinganalysis._

val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

val logs = all.filter(log =>
                      //                        log.study == "utah" &&
                      //                        log.user != "samreid" &&
                      //                        !log.machine.startsWith("samreid") &&
                        !log.machine.startsWith("chrismalley")).sortBy(_.epoch)
println("found: " + logs.length + " logs")

//Do students use the window close button or the file-> exit button?
val closeButtonPressed = logs.flatMap(_.entries).count(entry => entry.component == "window" && entry.action == "closeButtonPressed")
val fileExitPressed = logs.flatMap(_.entries).count(entry => entry.component == "menuItem" && entry.action == "selected" && entry("text") == "Exit")
println("Number that pressed close button: " + closeButtonPressed + ", " + "Number that pressed file->exit: " + fileExitPressed)

//How many different machines had JME crashes?
val crashedLogs = logs.filter(_.contains("system", "erred", "errMsg" -> "Failed to create display"))
val numberMachinesCrashed = crashedLogs.map(_.machine).distinct.size
val totalNumberMachines = logs.map(_.machine).distinct.size
println("crashed with system erred " + numberMachinesCrashed + " / " + totalNumberMachines)

crashedLogs.map(log => log.osName + "\t" + log.osVersion + "\t" + log.machine + "\t" + log.javaVersion).distinct.foreach(println)

println("############")
//Show different Mac os's
val macMachines = logs.filter(_.osName.startsWith("Mac")).map(_.machine).distinct
val macLogs = logs.filter(log => macMachines.contains(log.machine))
println(macLogs.map(_.osVersion).sorted.mkString("\n"))



//crashedLogs.flatMap(_.entries).foreach(println)