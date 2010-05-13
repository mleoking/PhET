package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.recordandplayback.model.RecordModel
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable
import edu.colorado.phet.motionseries.model.RecordedState
import edu.colorado.phet.motionseries.MotionSeriesDefaults

/**
 * Adds the functionality that clears the remainder of a dataset, e.g. after recording 10 sec of data, placing the playback cursor at 5 sec, then pressing record again (should clear the latter 5 sec of data)
 */
class MotionSeriesDefaultTemporalVariable(model: RecordModel[RecordedState]) extends DefaultTemporalVariable {
  model.addHistoryClearListener(new RecordModel.HistoryClearListener{
    def historyCleared = keepRange(0.0,model.getTime)
  })

  //wrapper that ensures that
  // 1. no data overflow when running for a long time
  // 2. series still shows the correct value even after full time has passed
  // Could be changed to be an override (might be less risky)
  def doAddValue(value: Double, time: Double) = {
    if (inTimeRange(model.getTime))
      addValue(value, time)
    else
      doSetValue(value) //todo: can't call setValue because that is used in overrides for mode changing
    //todo: the problem is caused by passing the DefaultTemporalVariable to a 3rd party ( the ControlGraph) which relies on being able to call setValue for mode changing
  }

  def inTimeRange(time: Double) = time <= MotionSeriesDefaults.MAX_RECORD_TIME
}