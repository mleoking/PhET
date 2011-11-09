import edu.colorado.phet.simeventprocessor.scala._
import phet._

val all = phet load "C:\\Users\\Sam\\Desktop\\0all-11-8-2011-v"

val logs = all.filter(log =>
                      //                        log.study == "utah" &&
                      //                        log.user != "samreid" &&
                      //                        !log.machine.startsWith("samreid") &&
                        !log.machine.startsWith("chrismalley")).sortBy(_.epoch)
println("found: " + logs.length + " logs")

//Do students use the window close button or the file-> exit button?
val closeButtonPressed = logs.flatMap(_.entries).count(entry => entry.actor == "window" && entry.event == "closeButtonPressed")
val fileExitPressed = logs.flatMap(_.entries).count(entry => entry.actor == "menuItem" && entry.event == "selected" && entry("text") == "Exit")
println("Number that pressed close button: " + closeButtonPressed + ", " + "Number that pressed file->exit: " + fileExitPressed)

//How many different machines had JME crashes?
val numberMachinesCrashed = logs.filter(_.contains("system", "erred")).distinct.size
val totalNumberMachines = logs.map(_.machine).distinct.size
println("crashed with system erred " + numberMachinesCrashed + " / " + totalNumberMachines)

val systemErred = logs.filter(_.contains("system","erred", "errMsg" -> "Failed to create display")).flatMap(_.entries).filter( e => e.actor=="system" && e("errMsg") == "Failed to create display")
println(systemErred mkString "\n")