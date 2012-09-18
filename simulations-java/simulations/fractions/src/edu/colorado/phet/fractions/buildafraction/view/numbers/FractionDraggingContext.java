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
    void startDrag( FractionNode fractionNode );

    void endDrag( FractionNode fractionNode );

    void updateStacks();

    Vector2D getCenterOfScreen();

    ObservableProperty<Option<Integer>> getDraggedCardProperty();

    boolean isFractionLab();

    boolean isInCollectionBox( FractionNode fractionNode );
}