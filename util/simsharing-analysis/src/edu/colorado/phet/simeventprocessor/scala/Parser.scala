// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.util.StringTokenizer
import collection.mutable.ArrayBuffer
import java.io.{File, FileReader, BufferedReader}

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
      lines += Entry.parse(line)
    }
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