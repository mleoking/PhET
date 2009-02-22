/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Jan 19, 2009
 * Time: 11:20:01 AM
 */
package edu.colorado.phet.movingman.ladybug

import model.Vector2D

object LadybugDefaults {
  var remoteIsIndicator = true
  var vaSticky = true
//  var timelineLengthSeconds: Double = 15
      var timelineLengthSeconds: Double = 1//debugging
  var pauseAtEndOfPlayback = true
  var recordAtEndOfPlayback = false
  val defaultLocation = new Vector2D(5.5, 1.5)
  val ACCEL_VECTOR_SCALE = 0.2454545

  var WINDOW_SIZE = 7
  var HIDE_MOUSE_DURING_DRAG = true
  var POSITIVE_Y_IS_UP = true

  var LADYBUG_SCALE = 0.6

}