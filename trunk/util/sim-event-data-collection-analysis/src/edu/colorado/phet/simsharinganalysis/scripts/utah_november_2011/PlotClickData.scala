package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet
import javax.swing.JFrame
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.umd.cs.piccolo.event.PZoomEventHandler
import java.awt.Dimension

/**
 * @author Sam Reid
 */
object PlotClickData {
  //Try to find out when they changed from "free play" to "directed activity" by plotting the data
  //There are server side timestamps in each file, can be used for synchronizing.

  //The data is from Nov 2011 and I do not know if the current parser is accurate with respect to its data style.
  def main(args: Array[String]) {
    val utah0 = new File("C:\\Users\\Sam\\Desktop\\utahdata\\utah_0")
    val logs = phet.load(utah0, f => new Parser2011().parse(f))
    println("loaded " + logs.length + " logs")
    println("first one = " + logs(0).file)
    println("first few lines: " + logs(0).entries.slice(0, 5).mkString("\n"))

    //    println("log 0 = \n"+logs(0).entries.map(_.toString).mkString("\n"))
    //    println("start message = "+logs(0).startMessage)
    //    println("log 0 name = "+logs(0).simName)

    //    val canvas = new StudentActivityCanvas(logs)
    //    canvas.setPreferredSize(new Dimension(1024, 768))
    val frame = new JFrame() {
      setContentPane(new PhetPCanvas() {
        setPreferredSize(new Dimension(1024, 768))
        setZoomEventHandler(new PZoomEventHandler)
        println("Timestamp\tLog")
        for ( log <- logs ) {
          val timestamps = log.entries.map(entry => entry.time)
          //          println("log: " + log.session + ", timestamps = " + timestamps.mkString(","))
          for ( timestamp <- timestamps ) {
            println(timestamp + "\t" + logs.indexOf(log))
          }
        }
      })
      pack()
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    }
    frame.setVisible(true)
    //

  }
}
