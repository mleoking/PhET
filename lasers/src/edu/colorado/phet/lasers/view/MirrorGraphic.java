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
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class MirrorGraphic extends PhetGraphic implements PartialMirror.ReflectivityChangeListener {

    private final static double thickness = 15;
    public final static int LEFT_FACING = 1;
    public final static int RIGHT_FACING = 2;

    private PartialMirror mirror;
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
    private BufferedImage mirrorBI;
    private float outlineStrokeWidth = 1;
    private Stroke outlineStroke = new BasicStroke( outlineStrokeWidth );
    private Color mirrorColor = new Color( 180, 180, 180 );

    public MirrorGraphic( Component component, PartialMirror mirror, int direction ) {
        super( component );
        this.mirror = mirror;
        switch( direction ) {
            case LEFT_FACING:
                xOffset = thickness;
                break;
            case RIGHT_FACING:
                xOffset = 0;
                break;
            default:
                throw new RuntimeException( "Invalid direction specified in constructor" );
        }
        mirror.addListener( this );
        update( mirror.getReflectivity() );
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void reflectivityChanged( PartialMirror.ReflectivityChangedEvent event ) {
        update( event.getReflectivity() );
    }

    public void update( double reflectivity ) {

        // Set the basic color based on the reflectivity of the mirror
        int maxGray = 100;
        int minGray = 220;
        int gray = minGray - (int)( reflectivity * ( minGray - maxGray ) );
        mirrorColor = new Color( gray, gray, gray );

        bounds.setFrame( 0, 0, thickness * 2 + outlineStrokeWidth, mirror.getBounds().getHeight() );
        double xLoc = bounds.getMinX();
        face1 = new Ellipse2D.Double( xLoc + thickness, bounds.getMinY(), thickness, bounds.getHeight() );
        face2 = new Ellipse2D.Double( xLoc, bounds.getMinY(), thickness, bounds.getHeight() );
        body = new Rectangle2D.Double( xLoc + thickness / 2, bounds.getMinY(),
                                       thickness, bounds.getHeight() );
        upperLine = new Line2D.Double( xLoc + thickness / 2, bounds.getMinY(),
                                       xLoc + thickness * 3 / 2, bounds.getMinY() );
        lowerLine = new Line2D.Double( xLoc + thickness / 2, bounds.getMinY() + bounds.getHeight(),
                                       xLoc + thickness * 3 / 2, bounds.getMinY() + bounds.getHeight() );
        p1 = new Point2D.Double( xLoc + thickness * 1 / 4, bounds.getMinY() + bounds.getHeight() / 2 - 0.5 );
        p2 = new Point2D.Double( xLoc + thickness / 2, bounds.getMinY() + bounds.getHeight() / 2 );
        mirrorPaint1 = new GradientPaint( p1, mirrorColor,
                                          p2, new Color( 0, 0, 0, 0 ), false );
        Point2D p3 = new Point2D.Double( xLoc + thickness / 2, bounds.getMinY() + bounds.getHeight() / 2 );
        Point2D p4 = new Point2D.Double( xLoc + thickness * 3 / 4, bounds.getMinY() + bounds.getHeight() / 2 + 0.5 );
        mirrorPaint2 = new GradientPaint( p3, new Color( 0, 0, 0, 0 ),
                                          p4, mirrorColor, false );

        mirrorBI = new BufferedImage( (int)bounds.getWidth(), (int)( bounds.getHeight() + outlineStrokeWidth ),
                                      BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)mirrorBI.getGraphics();
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( mirrorColor );
        g.fill( face1 );
        g.setColor( Color.black );
        g.draw( face1 );
        g.setColor( mirrorColor );
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

        setBoundsDirty();
        repaint();
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        g.drawImage( mirrorBI,
                     (int)( mirror.getPosition().getX() - thickness + xOffset ),
                     (int)mirror.getPosition().getY(), null );
        //        g.setColor( Color.red );
        //        g.drawArc( (int)p1.getX(), (int)p1.getY(), 3, 3, 0, 360 );
        //        g.drawArc( (int)p2.getX(), (int)p2.getY(), 3, 3, 0, 360 );
        restoreGraphicsState();
    }
}
