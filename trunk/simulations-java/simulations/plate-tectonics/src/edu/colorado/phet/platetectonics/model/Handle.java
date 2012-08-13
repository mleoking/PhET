// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.platetectonics.tabs.PlateMotionTab;

import static edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;

/**
 * Responsible for the model-changing parts of the handles, and tracking the handle's state
 */
public class Handle {

    public final Property<Vector2F> orientation = new Property<Vector2F>( Vector2F.ZERO );

    // whether this handle is currently controlling the motion
    public final Property<Boolean> active = new Property<Boolean>( false );
    public final boolean isRightHandle;
    private final PlateMotionTab tab;
    private final PlateMotionModel model;

    public Handle( final Property<Vector2F> motionVectorRight, final boolean isRightHandle, final PlateMotionTab tab ) {
        this.isRightHandle = isRightHandle;
        this.tab = tab;
        model = tab.getPlateMotionModel();
        motionVectorRight.addObserver( new SimpleObserver() {
            public void update() {
                orientation.set( isRightHandle ? motionVectorRight.get() : motionVectorRight.get().negated() );
            }
        } );

        orientation.addObserver( new SimpleObserver() {
            public void update() {
                if ( active.get() ) {
                    motionVectorRight.set( isRightHandle ? orientation.get() : orientation.get().negated() );
                }
            }
        } );
    }

    public boolean isVisible() {
        final boolean modesOK = model.hasBothPlates.get() && !tab.isAutoMode.get();
        if ( !modesOK ) {
            return false;
        }
        PlateType myType = ( isRightHandle ? model.rightPlateType : model.leftPlateType ).get();
        PlateType otherType = ( isRightHandle ? model.leftPlateType : model.rightPlateType ).get();

        // set visibility based on making sure we are the most dense plate if visible
        return !( myType.isContinental() && otherType.isOceanic() )
               && !( myType == PlateType.YOUNG_OCEANIC && otherType == PlateType.OLD_OCEANIC );
    }

    public MotionType getRightMotionType() {
        return isRightHandle ? MotionType.DIVERGENT : MotionType.CONVERGENT;
    }

    public MotionType getLeftMotionType() {
        return isRightHandle ? MotionType.CONVERGENT : MotionType.DIVERGENT;
    }

    public void startArrowPress( MotionType motionType ) {
        // set the motion type if it is not already decided
        if ( model.motionType.get() == null ) {
            // do the motion-rotation flag before the motionType, since the motionType changing is used as a signal
            if ( motionType == MotionType.TRANSFORM ) {
                model.setTransformMotionCCW( !isRightHandle );
            }
            model.motionType.set( motionType );
        }

        if ( motionType == MotionType.TRANSFORM ) {
            Vector2F vector = new Vector2F( 0, 0.8f * Math.PI / 2 );
            if ( model.isTransformMotionCCW() ) {
                vector = vector.negated();
            }
            tab.motionVectorRight.set( vector );
        }
        else {
            Vector2F vector = new Vector2F( 0.8f * Math.PI / 2, 0 );
            if ( ( getRightMotionType() == motionType ) != isRightHandle ) {
                vector = vector.negated();
            }
            tab.motionVectorRight.set( vector );
        }
    }

    public void endArrowPress() {
        tab.motionVectorRight.set( Vector2F.ZERO );
    }

    // success flag indicates that the dragging should continue
    public boolean checkInitialMotion( Vector3F xyDelta ) {
        // if they haven't dragged it far enough, bail out. should prevent accidentally moving it in a wrong direction
        if ( xyDelta.magnitude() > 5 ) {
            float rightStrength = xyDelta.dot( Vector3F.X_UNIT );
            float verticalStrength = Math.abs( xyDelta.dot( Vector3F.Y_UNIT ) );
            if ( model.allowsTransformMotion() && verticalStrength > Math.abs( rightStrength ) ) {
                // starting transform
                model.setTransformMotionCCW( !isRightHandle );
                model.motionType.set( MotionType.TRANSFORM );
            }
            else {
                boolean pullingLeft = xyDelta.x < 0;
                if ( model.allowsDivergentMotion() && ( pullingLeft != isRightHandle ) ) {
                    model.motionType.set( MotionType.DIVERGENT );
                }
                else if ( model.allowsConvergentMotion() && pullingLeft == isRightHandle ) {
                    model.motionType.set( MotionType.CONVERGENT );
                }
                else {
                    // not a valid magnitude
                    return false;
                }
            }
        }
        else {
            // not enough starting magnitude
            return false;
        }

        // success!
        return true;
    }

    public void startHandleDrag() {
        active.set( true );
    }

    public void horizontalHandleDrag( float angle ) {
        tab.motionVectorRight.set( new Vector2F( isRightHandle ? angle : -angle, 0 ) );
    }

    public void verticalHandleDrag( float angle ) {
        tab.motionVectorRight.set( new Vector2F( 0, isRightHandle ? angle : -angle ) );
    }

    public void endHandleDrag() {
        active.set( false );
        tab.motionVectorRight.set( Vector2F.ZERO );
    }

    public void reset() {
        active.reset();
        orientation.reset();
    }
}
