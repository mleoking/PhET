// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scala

import java.util.StringTokenizer
import java.io.{File, FileReader, BufferedReader}
import edu.colorado.phet.common.phetcommon.simsharing.Parameter
import collection.mutable.{HashMap, ArrayBuffer}

/**
 * @author Sam Reid
 */

class Parser {
  var machineID: String = _
  var sessionID: String = _
  var serverTime: Long = _
  val lines = new ArrayBuffer[Entry]

  def parseLine(line: String) {
    if ( line.startsWith("machineID") ) {
      machineID = readValue(line)
    }
    else if ( line.startsWith("sessionID") ) {
      sessionID = readValue(line)
    }
    else if ( line.startsWith("serverTime") ) {
      serverTime = java.lang.Long.parseLong(readValue(line))
    }
    else {
      lines += parseKeyValueLine(line)
    }
  }

  def parseKeyValueLine(line: String): Entry = {
    val tokenizer = new StringTokenizer(line, "\t")
    val time = java.lang.Long.parseLong(tokenizer.nextToken)
    val obj = tokenizer.nextToken
    val event = tokenizer.nextToken
    val remainderOfLineBuf = new StringBuffer
    while ( tokenizer.hasMoreTokens ) {
      remainderOfLineBuf.append(tokenizer.nextToken)
      remainderOfLineBuf.append(Parameter.DELIMITER)
    }
    val remainderOfLine = remainderOfLineBuf.toString.trim
    val parameters = Parameter.parseParameters(remainderOfLine)
    val map = new HashMap[String, String]()
    for ( p <- parameters ) {
      if ( map.contains(p.name) ) {
        throw new RuntimeException("Duplicate string key for " + p.name)
      }
      map.put(p.name, p.value)
    }

    //make map immutable
    new Entry(time, obj, event, map.toMap)
  }

  def parse(file: File): Log = {
    val bufferedReader = new BufferedReader(new FileReader(file))
    var line = bufferedReader.readLine
    while ( line != null ) {
      parseLine(line)
      line = bufferedReader.readLine
    }
    new Log(file, machineID, sessionID, serverTime, lines.toList)
  }

  private def readValue(line: String): String = {
    val stringTokenizer: StringTokenizer = new StringTokenizer(line, " ")
    stringTokenizer.nextToken
    stringTokenizer.nextToken
    stringTokenizer.nextToken
  }
}