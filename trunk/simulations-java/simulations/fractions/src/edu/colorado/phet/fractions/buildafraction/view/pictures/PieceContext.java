// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface PieceContext {
    void endDrag( PieceNode piece, PInputEvent event );

    double getNextAngle( final PieceNode pieceNode );
}