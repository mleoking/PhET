/**
 * Class: WallGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;


import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.collision.Wall;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.Observable;

public class WallGraphic extends PhetShapeGraphic {

    private final static Color s_mirrorColor = new Color( 180, 180, 180 );
    private Rectangle2D.Float rep = new Rectangle2D.Float();
    private Wall wall;

    /**
     *
     */
    public WallGraphic( Component component, Wall mirror ){
        super( component, null, s_mirrorColor );
        this.wall = mirror;
        setShape( rep );
        update();
    }

//    public void init( Particle body ) {
//        super.init( body );
//        if( body instanceof Mirror ) {
//            Mirror wall = (Mirror)body;
//            adjustRep( wall );
//            this.setFill( Color.gray );
//        }
//    }

//    public void update( Observable o, Object arg ) {
//        if( o instanceof Mirror ) {
//            Mirror wall = (Mirror)o;
//            adjustRep( wall );
//        }
//    }

    public void update() {
        adjustRep( wall );
    }

    /**
     *
     * @param wall
     */
    protected void adjustRep( Wall wall ) {

        rep.setFrame( wall.getBounds().getX() - 6,
                            wall.getBounds().getY(),
                            6,
                            wall.getBounds().getHeight() );
    }

//
//    private Point2D.Float origin;
//    private Point2D.Float end = new Point2D.Float();
//    Line2D.Float rep = new Line2D.Float();
//
//    public WallGraphic() {
//    }
//
//    public void init( Particle body ) {
//        Wall wall = (Wall)body;
//        this.origin = wall.getEnd1();
//        this.end = wall.getEnd2();
//        rep.setLine( origin, end );
//        setRep( rep );
//
//        // TODO: test!!!
//        body.addObserver( this );
//    }
//
//    protected void setPosition( Particle body ) {
//        origin = new Point2D.Float( body.getPosition().getX(), body.getPosition().getY() );
//    }
//
//    public void update( Observable o, Object arg ) {
//        super.update( o, arg );
//        Wall wall = (Wall)o;
//        this.origin = wall.getEnd1();
//        this.end = wall.getEnd2();
//        rep.setLine( origin, end );
//    }
}
