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
import edu.colorado.phet.collision.Wall;

import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.Observable;

public class MirrorGraphic extends WallGraphic {

    private final static Color s_mirrorColor = new Color( 180, 180, 180 );

    public MirrorGraphic( Component component, Mirror mirror ){
        super( component, mirror );
        this.setPaint( s_mirrorColor );
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

    protected void adjustRep( Wall wall ) {
        int xAdjustment1 = -6;
        if( ((Mirror)wall).isLeftReflecting() ) {
            xAdjustment1 = 0;
        }
        rep.setFrame( wall.getBounds().getX() + xAdjustment1,
                            wall.getBounds().getY(),
                            6,
                            wall.getBounds().getHeight() );
    }
}
