/**
 * Class: ResonatingCavityGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.graphics.ShapeGraphic;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.physics.body.Particle;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

public class ResonatingCavityGraphic extends ShapeGraphic {

    private Rectangle2D.Float rep;

    public void init( Particle body ) {
        super.init( body );
        ResonatingCavity chamber = (ResonatingCavity)body;
        rep = new Rectangle2D.Float( (float)chamber.getOrigin().getX(),
                                     (float)chamber.getOrigin().getY(),
                                     chamber.getWidth(),
                                     chamber.getHeight() );
        setRep( rep );
//        this.setFill( new Color( 255, 240, 240 ) );
    }

    public void update( Observable o, Object arg ) {
        super.update( o, arg );
        if( o instanceof ResonatingCavity ) {
            ResonatingCavity cavity = (ResonatingCavity)o;

            rep = new Rectangle2D.Float( (float)cavity.getOrigin().getX(),
                                         (float)cavity.getOrigin().getY(),
                                         cavity.getWidth(),
                                         cavity.getHeight() );
            setRep( rep );
        }
    }

    protected void setPosition( Particle body ) {
    }
}
