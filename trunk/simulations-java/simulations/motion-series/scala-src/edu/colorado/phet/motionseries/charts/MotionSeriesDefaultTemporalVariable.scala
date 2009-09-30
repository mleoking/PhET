package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.record.RecordModel
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable
import edu.colorado.phet.motionseries.model.RecordedState
import edu.colorado.phet.motionseries.MotionSeriesDefaults

/**
 * Adds the functionality that clears the remainder of a dataset, e.g. after recording 10 sec of data, placing the playback cursor at 5 sec, then pressing record again (should clear the latter 5 sec of data)
 */
class MotionSeriesDefaultTemporalVariable(model: RecordModel[RecordedState]) extends DefaultTemporalVariable {
  model.historyRemainderClearListeners += (() => keepRange(0.0, model.getTime))

  //wrapper that ensures that
  // 1. no data overflow when running for a long time
  // 2. series still shows the correct value even after full time has passed
  // Could be changed to be an override (might be less risky)
  def doAddValue(value: Double, time: Double) = {
    if (inTimeRange(model.getTime))
      addValue(value, time)
    else
      setValue(value)
  }

  def inTimeRange(time: Double) = time <= MotionSeriesDefaults.MAX_RECORD_TIME
}