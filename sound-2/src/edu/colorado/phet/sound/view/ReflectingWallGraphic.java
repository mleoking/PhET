/**
 * Class: ReflectingWallGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 16, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.*;

public class ReflectingWallGraphic extends PhetShapeGraphic {

    private double x;
    private double y;
    private double w;
    private double h;
    private double theta;
    private Shape xformedWall;
    private AffineTransform xform;
    private Color wallColor = new Color( 48, 0, 128 );
    private Line2D.Double wallEdge = new Line2D.Double();
    private Stroke wallEdgeStroke = new BasicStroke( 4f );
    private GeneralPath interferingWaveMask;

    private Rectangle2D.Double wall;
    private Color color;

    /**
     * @param x
     * @param y
     * @param w
     * @param h
     * @param theta Angle of rotation, countreclockwise
     */
    public ReflectingWallGraphic( Component component, Color color,
                                  double x, double y, double w, double h, double theta ) {
        super( component, null, color );

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.theta = theta;
        wall = new Rectangle2D.Double( x, y, w, h );

        interferingWaveMask = new GeneralPath();
        setAngle( theta );

        setLocation( x );

        // Needed to initialize the shape
        setShape( xformedWall );
    }

    public Point2D.Double getMidPoint() {
        double x = getShape().getBounds2D().getMinX() + getShape().getBounds2D().getWidth() / 2;
        double y = getShape().getBounds2D().getMinY() + getShape().getBounds2D().getHeight() / 2;
        return new Point2D.Double( x, y );
    }

    /**
     * @param x
     */
    public void setLocation( double x ) {
        xform = AffineTransform.getTranslateInstance( x - this.x, 0 );
        xformedWall = xform.createTransformedShape( xformedWall );
        setInterferingWaveMask();
        this.x = x;
    }

    /**
     * @param theta
     */
    public void setAngle( double theta ) {
        this.theta = theta;
        xform = AffineTransform.getRotateInstance( -Math.toRadians( theta ), x, y );
        xformedWall = xform.createTransformedShape( wall );
        setInterferingWaveMask();
    }

    private void setInterferingWaveMask() {

        // Create a interferingWaveMask to block out the new wavefront behind the wall.
        double xMax = 1000;
        double yMax = 800;
        double x0 = theta > 85 ? x : x - ( yMax - y ) / Math.tan( Math.toRadians( theta ) );
        double y0 = theta < 5 ? y : y - ( xMax - x ) * Math.tan( Math.toRadians( theta ) );
        y0 = theta < 5 ? y : y0;
        x0 = theta < 5 ? x : x0;

        synchronized( interferingWaveMask ) {
            interferingWaveMask.reset();

            if( theta > 0 && theta < 90 ) {
                // The +2 is here so the wall will show
                interferingWaveMask.moveTo( (float)x0 + 2, (float)yMax );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)yMax );
                interferingWaveMask.closePath();
            }
            else {
                // The +2 is here so the wall will show
                interferingWaveMask.moveTo( (float)x0 + 2, (float)yMax );
                interferingWaveMask.lineTo( (float)x0 + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)yMax );
                interferingWaveMask.closePath();
            }
            wallEdge.setLine( x0, yMax, xMax, y0 );
        }
    }

    /**
     * @return
     */
    double getX() {
        return x;
    }

    /**
     * @return
     */
    double getY() {
        return y;
    }

    /**
     * @param g
     */
    public void paint( Graphics2D g ) {
        g.setColor( wallColor );
//        g.draw( xformedWall );
//        g.fill( xformedWall );

        // TODO: Make this a Paintable
        g.setColor( new Color( 128, 128, 128 ) );
        synchronized( interferingWaveMask ) {
            g.fill( interferingWaveMask );
            g.setColor( wallColor );
            g.setStroke( wallEdgeStroke );
            g.draw( wallEdge );
        }

        // For debugging
        //            g.setColor(Color.RED);
        //            g.fillArc((int) x, (int) y, 5, 5, 0, 360);
        //            g.setColor(Color.RED);
        //            g.fillArc((int) b.getX(), (int) b.getY(), 5, 5, 0, 360);
        //            g.fillArc((int) pp.getX(), (int) pp.getY(), 5, 5, 0, 360);
        //            g.fillArc((int) p.getX(), (int) p.getY(), 5, 5, 0, 360);
        //            g.fillArc(300, 300, 5, 5, 0, 360);
    }

}
