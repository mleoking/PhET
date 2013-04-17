// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * Context in which a FractionNode can be dragged, mainly deals with interactions between other elements (such as dropping into a toolbox or collection box)
 *
 * @author Sam Reid
 */
public interface FractionDraggingContext {

    //Called when a FractionNode starts being dragged
    void startDrag( FractionNode fractionNode );

    //Called when a FractionNode stops being dragged
    void endDrag( FractionNode fractionNode );

    //Fix the z-ordering and location within stacks when something comes back to the toolbox to make sure the stacks look correct.
    void updateStacks();

    //Get the center of the screen for purposes of animating an object there
    Vector2D getCenterOfScreen();

    //Find out which card is being dragged, to prevent the user from creating improper fractions when in mixed number mode
    ObservableProperty<Option<Integer>> getDraggedCardProperty();

    //Determine whether it is the "Fraction Lab" tab
    boolean isFractionLab();

    //Find out whether the FractionNode has been collected.
    boolean isInCollectionBox( FractionNode fractionNode );
}