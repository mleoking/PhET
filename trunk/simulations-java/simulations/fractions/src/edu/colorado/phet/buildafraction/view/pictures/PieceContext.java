package edu.colorado.phet.buildafraction.view.pictures;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface PieceContext {
    void endDrag( RectangularPiece piece, PInputEvent event );
}