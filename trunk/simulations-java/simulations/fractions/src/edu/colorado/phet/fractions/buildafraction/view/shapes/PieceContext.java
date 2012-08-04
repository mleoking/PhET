// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.data.Option;

/**
 * Context in which PieceNodes can be dragged/dropped.
 *
 * @author Sam Reid
 */
public interface PieceContext {
    void endDrag( PieceNode piece );

    Option<Double> getNextAngle( final PieceNode pieceNode );
}