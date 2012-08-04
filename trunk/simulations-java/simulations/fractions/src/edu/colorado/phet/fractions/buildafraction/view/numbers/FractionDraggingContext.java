// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

/**
 * Context in which a FractionNode can be dragged.
 *
 * @author Sam Reid
 */
public interface FractionDraggingContext {
    void endDrag( FractionNode fractionGraphic );

    void updateStacks();
}