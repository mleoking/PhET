// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class TogglePiece extends PhetPPath {
    public TogglePiece( Shape shape, Paint fill, Stroke stroke, Paint strokePaint, final VoidFunction0 toggleOff, final VoidFunction0 toggleOn ) {
        super( shape, fill, stroke, strokePaint );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                if ( getPaint() == Color.white ) {
                    setPaint( FractionsIntroCanvas.FILL_COLOR );
                    toggleOn.apply();
                }
                else {
                    setPaint( Color.white );
                    toggleOff.apply();
                }
            }
        } );
    }
}
