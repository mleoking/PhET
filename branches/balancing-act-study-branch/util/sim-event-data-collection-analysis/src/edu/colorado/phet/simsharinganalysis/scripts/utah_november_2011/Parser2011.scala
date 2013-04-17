// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011

import java.io.{File, FileReader, BufferedReader}
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager
import java.util.{ArrayList, StringTokenizer}
import edu.colorado.phet.common.phetcommon.simsharing.messages.{IParameterKey, Parameter}
import edu.colorado.phet.simsharinganalysis.{Log, Entry}
import collection.mutable.{HashMap, ArrayBuffer}

/**
 * This parser loads a data file and turns it into a Log[Entry].
 * This was forked from Parser to handle data of old format (2011) style
 * 2011 style had machineID, sessionID and serverTime as fields before the start message, and had a different number of columns (no componentType)
 * @author Sam Reid
 */
class Parser2011 {
  var machineID: String = _
  var sessionID: String = _
  var serverTime: Long = _
  val lines = new ArrayBuffer[Entry]

  def getToken(line: String, index: Int) = {

    val tok = new StringTokenizer(line, " ")
    for ( i <- 0 until index ) {
      tok.nextToken()
    }
    tok.nextToken()
  }

  def parseLine(line: String) {
    if ( line.trim.length == 0 ) return
    //one of the initial startup messages, will need to be parsed
    if ( line startsWith "machineID = " ) {
      machineID = getToken(line, 2)
    } else if ( line startsWith "sessionID = " ) {
      sessionID = getToken(line, 2)
    } else if ( line startsWith "serverTime = " ) {
      serverTime = getToken(line, 2).toLong
    } else if ( line.contains("connected to server") ) {
      //don't worry about it
    } else {
      lines += parseMessage(line)
    }
  }

  def parseMessage(line: String): Entry = {
    val tokenizer = new StringTokenizer(line, SimSharingManager.DELIMITER)
    val time = java.lang.Long.parseLong(tokenizer.nextToken()) + serverTime
    val messageType = tokenizer.nextToken()
    val obj = tokenizer.nextToken()
    if ( messageType == "system" && obj == "started" ) {
      val remainderOfLineBuf = new StringBuffer
      val remainderOfLine = remainderOfLineBuf.toString.trim
      val parameters = parseParameters(remainderOfLine, SimSharingManager.DELIMITER)
      val map = new HashMap[String, String]()
      for ( p <- parameters ) {
        if ( map.contains(p.name.toString) ) {
          throw new RuntimeException("Duplicate string key for " + p.name)
        }
        map.put(p.name.toString, p.value)
      }
      new Entry(time, "system", "simsharingManager", "simsharingManager", "started", map.toMap.updated("name", "some name!?").updated("serverStartTime", serverTime.toString))
    } else {
      //      val componentType = tokenizer.nextToken()
      //
      val event = tokenizer.nextToken()

      val remainderOfLineBuf = new StringBuffer
      val remainderOfLine = remainderOfLineBuf.toString.trim
      val parameters = parseParameters(remainderOfLine, SimSharingManager.DELIMITER)
      val map = new HashMap[String, String]()
      for ( p <- parameters ) {
        if ( map.contains(p.name.toString) ) {
          throw new RuntimeException("Duplicate string key for " + p.name)
        }
        map.put(p.name.toString, p.value)
      }
      new Entry(time, messageType, obj, "object type", event, map.toMap)
    }
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