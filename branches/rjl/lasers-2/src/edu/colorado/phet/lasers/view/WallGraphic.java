/**
 * Class: WallGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;


import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Observable;

public class WallGraphic extends ShapeGraphic {

    private Point2D.Float origin;
    private Point2D.Float end = new Point2D.Float();
    Line2D.Float rep = new Line2D.Float();

    public WallGraphic() {
    }

    public void init( Particle body ) {
        Wall wall = (Wall)body;
        this.origin = wall.getEnd1();
        this.end = wall.getEnd2();
        rep.setLine( origin, end );
        setRep( rep );

        // TODO: test!!!
        body.addObserver( this );
    }

    protected void setPosition( Particle body ) {
        origin = new Point2D.Float( body.getPosition().getX(), body.getPosition().getY() );
    }

    public void update( Observable o, Object arg ) {
        super.update( o, arg );
        Wall wall = (Wall)o;
        this.origin = wall.getEnd1();
        this.end = wall.getEnd2();
        rep.setLine( origin, end );
    }
}
