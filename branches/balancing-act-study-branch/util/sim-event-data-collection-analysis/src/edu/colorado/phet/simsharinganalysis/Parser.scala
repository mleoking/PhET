// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

import java.io.{File, FileReader, BufferedReader}
import collection.mutable.{HashMap, ArrayBuffer}
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager
import java.util.{ArrayList, StringTokenizer}
import edu.colorado.phet.common.phetcommon.simsharing.messages.{IParameterKey, Parameter}

/**
 * This parser loads a data file and turns it into a Log[Entry]
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
    if ( tokenizer.countTokens() < 5 ) {
      println("I predict parse error in your near future, line = " + line)
    }
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
    val parameters = parseParameters(remainderOfLine, SimSharingManager.DELIMITER)
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

  def parseParameters(line: String, delimiter: String): Array[Parameter] = {
    val st = new StringTokenizer(line, delimiter)
    val parameters = new ArrayList[Parameter]
    while ( st.hasMoreTokens ) {
      parameters.add(parseParameter(st.nextToken))
    }
    parameters.toArray(new Array[Parameter](parameters.size))
  }

  private def parseParameter(s: String): Parameter = {
    val index = s.indexOf('=')
    val parsed = s.substring(0, index).trim
    new Parameter(new ParsedParameter(parsed), s.substring(index + 1).trim)
  }

  /**
   * This class allows us to represent a parsed parameter key.  This is required since IParameterKey is a marker interface
   * that makes it easy to identify the source of different parameters.  This parameter is one that was parsed during postprocessing/analysis.
   */
  private class ParsedParameter(parsed: String) extends IParameterKey {
    override def toString: String = parsed
  }

}