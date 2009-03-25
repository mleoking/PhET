package edu.colorado.phet.movingman.ladybug.model

import _root_.edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import scalacommon.util.Observable
import _root_.scala.collection.mutable.ArrayBuffer

abstract class TimeModel extends Observable {
  val recordHistory = new ArrayBuffer[DataPoint]

  //todo make private after refactor
  private var record = true
  private var paused = true
  private var time = 0.0
  private var playbackIndexFloat = 0.0 //floor this to get playbackIndex
  private var playbackSpeed = 1.0

  def setStateToPlaybackIndex() = {
    val playbackIndex = getPlaybackIndex
    if (playbackIndex >= 0 && playbackIndex < recordHistory.length) {
      setPlaybackState(recordHistory(getPlaybackIndex).state)
      time = recordHistory(getPlaybackIndex).time
    }
  }

  def setPlayback(speed: Double) = {
    setPlaybackSpeed(speed)
    setRecord(false)
  }

  def rewind = setPlaybackIndexFloat(0.0)

  def setTime(t: Double) {
    time = t
  }

  def resetAll() {
    record = true
    paused = true
    playbackIndexFloat = 0.0
    playbackSpeed = 1.0
    recordHistory.clear()
    time = 0

    notifyListeners() //todo: duplicate notification
  }

  def setPlaybackSpeed(speed: Double) = {
    if (speed != playbackSpeed) {
      playbackSpeed = speed
      notifyListeners()
    }
  }

  def getPlaybackIndexFloat(): Double = playbackIndexFloat

  def setPlaybackState(state: LadybugState)

  def getMaxRecordPoints: Int

  def handleRecordStartedDuringPlayback()

  def setRecord(rec: Boolean) = {
    if (record != rec) {
      record = rec
      if (record) {
        clearHistoryRemainder()
        handleRecordStartedDuringPlayback()
      }

      notifyListeners()
    }
  }

  def clearHistoryRemainder() = {
    val earlyEnoughRecordData = recordHistory.filter(_.time < time)
    recordHistory.clear
    recordHistory.appendAll(earlyEnoughRecordData)
    //todo: notify listeners?
  }

  def stepPlayback() = {
    if (getPlaybackIndex() < recordHistory.length) {
      setStateToPlaybackIndex()
      time = recordHistory(getPlaybackIndex()).time
      playbackIndexFloat = playbackIndexFloat + playbackSpeed
      notifyListeners()
    } else {
      if (LadybugDefaults.recordAtEndOfPlayback) {
        setRecord(true)
      }

      if (LadybugDefaults.pauseAtEndOfPlayback) {
        setPaused(true)
      }
    }
  }

  def clearHistory() = {
    recordHistory.clear()
    notifyListeners()
  }

  def isPlayback() = !record

  def isRecord() = record

  def setPlaybackIndexFloat(index: Double) = {
    playbackIndexFloat = index
    setStateToPlaybackIndex()
    notifyListeners()
  }

  def setPaused(p: Boolean) = {
    if (paused != p) {
      paused = p
      notifyListeners()
    }
  }

  def isPaused() = paused

  def getPlaybackIndex(): Int = java.lang.Math.floor(playbackIndexFloat).toInt

  def isRecordingFull = {
    recordHistory.length >= getMaxRecordPoints
  }

  def getRecordingHistory = recordHistory

  def getRecordedTimeRange(): Double = {
    if (recordHistory.length == 0) {
      0
    } else {
      recordHistory(recordHistory.length - 1).time - recordHistory(0).time
    }
  }

  def getTime() = time

  def getMaxRecordedTime() = if (recordHistory.length == 0) 0.0 else recordHistory(recordHistory.length - 1).time

  def getMinRecordedTime() = if (recordHistory.length == 0) 0.0 else recordHistory(0).time

  def setPlaybackTime(t: Double) = {
    val f = new LinearFunction(getMinRecordedTime, getMaxRecordedTime, 0, recordHistory.length - 1)
    setPlaybackIndexFloat(f.evaluate(t))
  }

  def getFloatTime(): Double = {
    val f = new LinearFunction(0, recordHistory.length - 1, getMinRecordedTime, getMaxRecordedTime)
    f.evaluate(playbackIndexFloat)
  }

}