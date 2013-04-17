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
                         frictionless: Boolean,

                         //Record whether the walls were bouncy in that time step, will be set back to the graphics in the play area and the radio buttons in the control panel
                         wallsBouncy: Boolean)

case class RampState(angle: Double)