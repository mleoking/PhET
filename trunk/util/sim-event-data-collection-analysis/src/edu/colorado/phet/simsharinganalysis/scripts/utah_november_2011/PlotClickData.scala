package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet
import edu.colorado.phet.simsharinganalysis.gui.StudentActivityCanvas
import javax.swing.JFrame
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

    //    println("log 0 = \n"+logs(0).entries.map(_.toString).mkString("\n"))
    //    println("start message = "+logs(0).startMessage)
    //    println("log 0 name = "+logs(0).simName)

    val canvas = new StudentActivityCanvas(logs)
    canvas.setPreferredSize(new Dimension(1024, 768))
    val frame = new JFrame() {
      setContentPane(canvas)
      pack()
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    }
    frame.setVisible(true)
    //

  }
}
