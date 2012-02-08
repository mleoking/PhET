// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.energyskateparkbasicsfebruary2012

import edu.colorado.phet.simsharinganalysis.{Log, phet}
import java.io.File
import collection.mutable.HashMap

/**
 * NP said: Since I just left the sim running, class B (which is last) will probably have C and B data.
 * So the first task will be to filter the C data out of these files.
 * (I think you can find the mid-point in B where the log saving button was pressed).
 * @author Sam Reid
 */
object SplitClasses {

  //  def write(file: File, s: String) {
  //    val out = new PrintWriter(file)
  //    try {out.println(s)}
  //    finally {out.close()}
  //  }

  //  def prefix(file: File) = file.getName.substring(0, file.getName.lastIndexOf('.'))

  val logs = phet load new File("C:\\Users\\Sam\\Desktop\\np-espb-Data Collection\\Data Collection")
  //  val target = new File("C:\\Users\\Sam\\Desktop\\np-espb-Data Collection\\target") {
  //    mkdirs()
  //  }
  //  println("loaded " + logs.length + " logs")

  val _splitLogs = new HashMap[String, List[Log]]
  _splitLogs("b") = Nil
  _splitLogs("c") = Nil
  for ( log <- logs ) {
    val savePressed = log.entries.filter(_.matches("simSharingLogFileDialog.fileChooserSaveButton", "pressed"))
    val indices = savePressed.map(p => log.entries.indexWhere(e => e eq p))
    //    println("for log: " + log.file.getName+", session: "+log.session + " (" + log.entries.length + "), save pressed " + indices)

    if ( log.file.getName.toLowerCase.startsWith("b") ) {
      val splitPoint = if ( indices.length == 3 ) indices(1) else indices(0)
      val elapsed = indices.last - splitPoint
      println(log.file.getName + ", (" + log.entries.length + " total entries), save pressed: " + indices.mkString(", ") + " : elapsed: " + elapsed)

      //B20.txt has only 6 events elapsed between save actions, so those are spurious and should be filtered out
      if ( elapsed > 100 ) {
        val b = Log(log.file, log.machine, log.session, log.entries.slice(0, splitPoint))

        //Keep the start message, TODO: update the start message time
        val c = Log(log.file, log.machine, log.session, log.entries.slice(0, 3) ++ log.entries.slice(splitPoint, log.entries.length))

        //        write(new File(target, prefix(log.file) + "_b.txt"), b.text)
        //        write(new File(target, prefix(log.file) + "_c.txt"), c.text)
        _splitLogs("b") = b :: _splitLogs("b")
        _splitLogs("c") = c :: _splitLogs("c")
      }
      else {
        //        write(new File(target, log.file.getName), log.text)
        _splitLogs("b") = log :: _splitLogs("b")
      }
    }
    else {
      //      write(new File(target, log.file.getName), log.text)
      _splitLogs("c") = log :: _splitLogs("c")
    }
  }
  val classMap = _splitLogs.toMap
}