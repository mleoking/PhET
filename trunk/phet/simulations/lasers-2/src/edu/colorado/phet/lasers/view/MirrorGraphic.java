/**
 * Class: MirrorGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 23, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.physics.mirror.Mirror;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.Observable;

public class MirrorGraphic extends ShapeGraphic {

    private Rectangle2D.Float rectangle = new Rectangle2D.Float();

    /**
     *
     */
    public MirrorGraphic(){
    }

    /**
     *
     * @param body
     */
    public void init( Particle body ) {
        super.init( body );
        if( body instanceof Mirror ) {
            Mirror mirror = (Mirror)body;
            adjustRep( mirror );
            this.setFill( Color.gray );
        }
    }

    /**
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if( o instanceof Mirror ) {
            Mirror mirror = (Mirror)o;
            adjustRep( mirror );
        }
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
        rectangle.setFrame( mirror.getEnd1().getX() + xAdjustment1,
                            mirror.getEnd1().getY(),
                            6,
                            mirror.getEnd2().getY() - mirror.getEnd1().getY() );
        this.setRep( rectangle );
    }

    //
    // Abstract methods
    //
    protected void setPosition( Particle body ) {
    }
}
