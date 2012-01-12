// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

import com.mongodb.{DBCollection, DBCursor, DBObject}


/**
 * Example of how to parse messages from mongodb.
 * @author Sam Reid
 */
class MessageParser {
  def parseCollection(collection: DBCollection) {
    val cur: DBCursor = collection.find
    while ( cur.hasNext ) {
      val obj: DBObject = cur.next
      parse(obj)
    }
  }

  private def parse(obj: DBObject): Unit = {
    val machineID: String = obj.get("machineID").toString
    val sessionID: String = obj.get("sessionID").toString
    val time: String = obj.get("time").toString
    val messageType: String = obj.get("messageType").toString
    val `object`: String = obj.get("object").toString
    val action: String = obj.get("action").toString
    val parameters: DBObject = obj.get("parameters").asInstanceOf[DBObject]
  }

}