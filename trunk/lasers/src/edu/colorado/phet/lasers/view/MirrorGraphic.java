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
    private final static double thickness = 30;

    private Mirror mirror;
    Rectangle bounds = new Rectangle();
    private Ellipse2D face1;
    private Ellipse2D face2;
    private Rectangle2D body;
    private Line2D upperLine;
    private Line2D lowerLine;
    private Paint mirrorPaint;
    private Point2D.Double p1;
    private Point2D.Double p2;

    public MirrorGraphic( Component component, Mirror mirror ) {
        super( component );
        //        super( component, mirrorImg, (int)mirror.getPosition().getX(), (int)mirror.getPosition().getY() );
        //        super( component, mirror );
        //        this.setPaint( s_mirrorColor );
        this.mirror = mirror;
        update();
    }

    protected Rectangle determineBounds() {
        return null;
    }

    public void update() {
        bounds.setFrame( mirror.getPosition().getX(), mirror.getPosition().getY(),
                         thickness * 3, mirror.getBounds().getHeight() );
        Rectangle2D rect = bounds;
        face1 = new Ellipse2D.Double( rect.getMinX(), rect.getMinY(), thickness, rect.getHeight() );
        face2 = new Ellipse2D.Double( rect.getMinX() - thickness, rect.getMinY(), thickness, rect.getHeight() );
        body = new Rectangle2D.Double( rect.getMinX() - thickness / 2, rect.getMinY(),
                                       thickness, rect.getHeight() );
        upperLine = new Line2D.Double( rect.getMinX() - thickness / 2, rect.getMinY(),
                                       rect.getMinX() + thickness / 2, rect.getMinY() );
        lowerLine = new Line2D.Double( rect.getMinX() - thickness / 2, rect.getMinY() + rect.getHeight(),
                                       rect.getMinX() + thickness / 2, rect.getMinY() + rect.getHeight() );
        p1 = new Point2D.Double( rect.getMinX() - thickness, rect.getMinY() + rect.getHeight() / 2 - 2 );
        p2 = new Point2D.Double( rect.getMinX() - thickness / 2, rect.getMinY() + rect.getHeight() / 2 );
        mirrorPaint = new GradientPaint( p1, s_mirrorColor,
                                         p2, Color.white, true );
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
        g.setPaint( mirrorPaint );
        g.fill( face2 );
        g.setColor( Color.black );
        g.draw( face2 );
        g.draw( upperLine );
        g.draw( lowerLine );

        g.setColor( Color.red );
        g.drawArc( (int)p1.getX(), (int)p1.getY(), 3, 3, 0, 360 );
        g.drawArc( (int)p2.getX(), (int)p2.getY(), 3, 3, 0, 360 );
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
