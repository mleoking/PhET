/**
 * Class: MirrorGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 23, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.model.mirror.Mirror;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MirrorGraphic extends PhetGraphic {
    //public class MirrorGraphic extends PhetImageGraphic {
    //public class MirrorGraphic extends WallGraphic {

    private final static Color s_mirrorColor = new Color( 180, 180, 180 );
    private final static double thickness = 15;
    public final static int LEFT_FACING = 1;
    public final static int RIGHT_FACING = 2;

    private Mirror mirror;
    Rectangle bounds = new Rectangle();
    private Ellipse2D face1;
    private Ellipse2D face2;
    private Rectangle2D body;
    private Line2D upperLine;
    private Line2D lowerLine;
    private Paint mirrorPaint1;
    private Paint mirrorPaint2;
    private Point2D.Double p1;
    private Point2D.Double p2;
    private double xOffset;

    public MirrorGraphic( Component component, Mirror mirror, int direction ) {
        super( component );
        //        super( component, mirrorImg, (int)mirror.getPosition().getX(), (int)mirror.getPosition().getY() );
        //        super( component, mirror );
        //        this.setPaint( s_mirrorColor );
        this.mirror = mirror;

        switch( direction ) {
            case LEFT_FACING:
                xOffset = -thickness;
                break;
            case RIGHT_FACING:
                xOffset = thickness;
                break;
            default:
                throw new RuntimeException( "Invalid direction specified in constructor" );
        }
        update();
    }

    protected Rectangle determineBounds() {
        return null;
    }

    public void update() {
        bounds.setFrame( mirror.getPosition().getX(), mirror.getPosition().getY(),
                         thickness * 3, mirror.getBounds().getHeight() );
        Rectangle2D rect = bounds;
        double xLoc = rect.getMinX() + xOffset;
        face1 = new Ellipse2D.Double( xLoc, rect.getMinY(), thickness, rect.getHeight() );
        face2 = new Ellipse2D.Double( xLoc - thickness, rect.getMinY(), thickness, rect.getHeight() );
        body = new Rectangle2D.Double( xLoc - thickness / 2, rect.getMinY(),
                                       thickness, rect.getHeight() );
        upperLine = new Line2D.Double( xLoc - thickness / 2, rect.getMinY(),
                                       xLoc + thickness / 2, rect.getMinY() );
        lowerLine = new Line2D.Double( xLoc - thickness / 2, rect.getMinY() + rect.getHeight(),
                                       xLoc + thickness / 2, rect.getMinY() + rect.getHeight() );
        p1 = new Point2D.Double( xLoc - thickness * 3 / 4, rect.getMinY() + rect.getHeight() / 2 - 0.5 );
        p2 = new Point2D.Double( xLoc - thickness / 2, rect.getMinY() + rect.getHeight() / 2 );
        mirrorPaint1 = new GradientPaint( p1, s_mirrorColor,
                                          p2, new Color( 0, 0, 0, 0 ), false );
        Point2D p3 = new Point2D.Double( xLoc - thickness / 2, rect.getMinY() + rect.getHeight() / 2 );
        Point2D p4 = new Point2D.Double( xLoc - thickness * 1 / 4, rect.getMinY() + rect.getHeight() / 2 + 0.5 );
        mirrorPaint2 = new GradientPaint( p3, new Color( 0, 0, 0, 0 ),
                                          p4, s_mirrorColor, false );
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( s_mirrorColor );
        g.fill( face1 );
        g.setColor( Color.black );
        g.draw( face1 );
        g.setColor( s_mirrorColor );
        g.fill( body );
        g.setPaint( Color.white );
        g.fill( face2 );
        g.setPaint( mirrorPaint1 );
        g.fill( face2 );
        g.setPaint( mirrorPaint2 );
        g.fill( face2 );
        g.setColor( Color.black );
        g.draw( face2 );
        g.draw( upperLine );
        g.draw( lowerLine );

        //        g.setColor( Color.red );
        //        g.drawArc( (int)p1.getX(), (int)p1.getY(), 3, 3, 0, 360 );
        //        g.drawArc( (int)p2.getX(), (int)p2.getY(), 3, 3, 0, 360 );
        restoreGraphicsState();
    }

    //    protected void adjustRep( Wall wall ) {
    ////        throw new RuntimeException( "TBI" );
    //
    ////        int xAdjustment1 = -6;
    ////        if( ((Mirror)wall).isLeftReflecting() ) {
    ////            xAdjustment1 = 0;
    ////        }
    ////        rep.setFrame( wall.getBounds().getX() + xAdjustment1,
    ////                            wall.getBounds().getY(),
    ////                            6,
    ////                            wall.getBounds().getHeight() );
    //    }
}
