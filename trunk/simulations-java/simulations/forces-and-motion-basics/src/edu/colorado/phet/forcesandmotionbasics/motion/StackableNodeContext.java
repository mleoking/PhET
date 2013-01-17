// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface used by StackableNode and implemented by MotionCanvas, to decrease coupling between the two.
 *
 * @author Sam Reid
 */
public interface StackableNodeContext {
    void stackableNodeDropped( StackableNode stackableNode );

    void stackableNodePressed( StackableNode stackableNode );

    DoubleProperty getAppliedForce();

    BooleanProperty getUserIsDraggingSomething();

    boolean isInStackButNotInTop( StackableNode stackableNode );

    void addStackChangeListener( SimpleObserver observer );

    int getStackSize();

    boolean isInStack( StackableBucketNode stackableBucketNode );
}