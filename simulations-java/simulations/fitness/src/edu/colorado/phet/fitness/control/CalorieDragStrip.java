package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Created by: Sam
 * May 26, 2008 at 10:14:57 AM
 */
public class CalorieDragStrip extends PNode {
    static Random random = new Random();

    public CalorieDragStrip() {
        for ( int i = 0; i < 10; i++ ) {
            final Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            PPath path = createPath( color, i );
            final int i1 = i;
            path.addInputEventListener( new PBasicInputEventHandler() {
                PPath path = null;

                public void mouseDragged( PInputEvent event ) {
                    if ( path == null ) {
                        path = createPath( color, i1 );
                        addChild( path );
                    }
                    path.translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                }
            } );
            addChild( path );
        }
    }

    private PPath createPath( Color color, int i ) {
        return new PhetPPath( new Rectangle( 0, i * 12, 10, 10 ), color );
    }
}
