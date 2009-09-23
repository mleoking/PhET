package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.record.RecordModel
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable
import edu.colorado.phet.motionseries.model.RecordedState

/**
 * Adds the functionality that clears the remainder of a dataset, e.g. after recording 10 sec of data, placing the playback cursor at 5 sec, then pressing record again (should clear the latter 5 sec of data)
 */
class MotionSeriesDefaultTemporalVariable(model: RecordModel[RecordedState]) extends DefaultTemporalVariable {
  model.historyRemainderClearListeners += (() => keepRange(0.0, model.getTime))
}