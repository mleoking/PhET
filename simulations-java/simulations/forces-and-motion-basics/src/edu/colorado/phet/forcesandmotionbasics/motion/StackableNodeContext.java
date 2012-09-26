// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * @author Sam Reid
 */
public interface StackableNodeContext {
    void stackableNodeDropped( StackableNode stackableNode );

    void stackableNodePressed( StackableNode stackableNode );

    DoubleProperty getAppliedForce();
}