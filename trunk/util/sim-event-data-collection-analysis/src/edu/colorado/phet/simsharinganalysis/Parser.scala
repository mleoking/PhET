// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

// Copyright 2002-2011, University of Colorado

import java.util.StringTokenizer
import java.io.{File, FileReader, BufferedReader}
import collection.mutable.{HashMap, ArrayBuffer}
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager

/**
 * @author Sam Reid
 */

class Parser {
  var machineID: String = _
  var sessionID: String = _
  var serverTime: Long = _
  val lines = new ArrayBuffer[Entry]

  def parseLine(line: String) {
    val entry = parseMessage(line)
    if ( entry.matches("simsharingManager", "started", Map()) ) {
      sessionID = entry("sessionId")
      serverTime = entry("time").toLong;
      machineID = entry("machineCookie")
    }
    lines += entry
  }

  def parseMessage(line: String): Entry = {
    val tokenizer = new StringTokenizer(line, SimSharingManager.DELIMITER)
    val time = java.lang.Long.parseLong(tokenizer.nextToken())
    val messageType = tokenizer.nextToken()
    val obj = tokenizer.nextToken()
    val componentType = tokenizer.nextToken()
    val event = tokenizer.nextToken()
    val remainderOfLineBuf = new StringBuffer
    while ( tokenizer.hasMoreTokens ) {
      remainderOfLineBuf.append(tokenizer.nextToken)
      remainderOfLineBuf.append(SimSharingManager.DELIMITER)
    }
    val remainderOfLine = remainderOfLineBuf.toString.trim
    val parameters = Parameter.parseParameters(remainderOfLine, SimSharingManager.DELIMITER)
    val map = new HashMap[String, String]()
    for ( p <- parameters ) {
      if ( map.contains(p.name.toString) ) {
        throw new RuntimeException("Duplicate string key for " + p.name)
      }
      map.put(p.name.toString, p.value)
    }

    //make map immutable
    new Entry(time, messageType, obj, componentType, event, map.toMap)
  }

  def parse(file: File): Log = {
    val bufferedReader = new BufferedReader(new FileReader(file))
    var line = bufferedReader.readLine
    while ( line != null ) {
      parseLine(line)
      line = bufferedReader.readLine
    }
    new Log(file, machineID, sessionID, lines.toList)
  }

  private def readValue(line: String): String = {
    val stringTokenizer: StringTokenizer = new StringTokenizer(line, " ")
    stringTokenizer.nextToken
    stringTokenizer.nextToken
    stringTokenizer.nextToken
  }
}