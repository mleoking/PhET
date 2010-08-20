package edu.colorado.phet.motionseries.model

//This class stores all state information used in record/playback
case class RecordedState(rampState: RampState,
                         selectedObject: MotionSeriesObjectTypeState,
                         motionSeriesObjectState: MotionSeriesObjectState,
                         manState: MotionSeriesObjectState,
                         appliedForce: Double,
                         walls: Boolean,
                         motionStrategyMemento: MotionStrategyMemento,
                         time: Double,
                         frictionless: Boolean)

case class RampState(angle: Double, heat: Double, wetness: Double)