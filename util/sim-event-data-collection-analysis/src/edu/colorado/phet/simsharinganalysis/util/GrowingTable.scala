// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.util

import collection.mutable.HashMap
import java.text.DecimalFormat

/**
 * This table returns 0 for nonexistent keys and can add values for nonexistent keys
 * @author Sam Reid
 */
class GrowingTable {
  var t = new HashMap[String, Long]

  def add(key: String, time: Long) {
    t.put(key, apply(key) + time)
  }

  def apply(s: String) = if ( t.contains(s) ) t(s) else 0L

  override def toString = t.toString

  def toMinuteMap = {
    val format = new DecimalFormat("0.00")
    t.map(elm => elm._1 -> format.format(elm._2 / 1000.0 / 60.0))
  }
}