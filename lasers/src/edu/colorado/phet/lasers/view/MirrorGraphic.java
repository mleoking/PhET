/**
 * Class: MirrorGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 23, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.physics.mirror.Mirror;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.Observable;

public class MirrorGraphic extends PhetShapeGraphic {

    private final static Color s_mirrorColor = new Color( 180, 180, 180 );
    private Rectangle2D.Float rep = new Rectangle2D.Float();
    private Mirror mirror;

    /**
     *
     */
    public MirrorGraphic( Component component, Mirror mirror ){
        super( component, null, s_mirrorColor );
        this.mirror = mirror;
        setShape( rep );
        update();
    }

//    public void init( Particle body ) {
//        super.init( body );
//        if( body instanceof Mirror ) {
//            Mirror mirror = (Mirror)body;
//            adjustRep( mirror );
//            this.setFill( Color.gray );
//        }
//    }

//    public void update( Observable o, Object arg ) {
//        if( o instanceof Mirror ) {
//            Mirror mirror = (Mirror)o;
//            adjustRep( mirror );
//        }
//    }

    public void update() {
        adjustRep( mirror );
    }

    /**
     *
     * @param mirror
     */
    private void adjustRep( Mirror mirror ) {

        int xAdjustment1 = -6;
        if( mirror.isLeftReflecting() ) {
            xAdjustment1 = 0;
        }
        rep.setFrame( mirror.getBounds().getX() + xAdjustment1,
                            mirror.getBounds().getY(),
                            6,
                            mirror.getBounds().getHeight() );
    }
}
