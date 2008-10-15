/**
 * Class: FieldLatticeView
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 20, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common_microwaves.view.ApparatusPanel;
import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.microwaves.MicrowaveConfig;
import edu.colorado.phet.microwaves.coreadditions.TxObservingGraphic;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;
import edu.colorado.phet.microwaves.model.waves.WaveMedium;
import edu.colorado.phet.microwaves.view.graphics.splines.CubicSpline;

public class FieldLatticeView extends TxObservingGraphic {

    private static Color splineColor = new Color( 0, 200, 0 );
    private BasicStroke splineStroke = new BasicStroke( 2 );
    private Point2D.Double latticePtLocation = new Point2D.Double();

    private Point2D.Double origin;
    private double latticeSpacingX;
    private double latticeSpacingY;
    //    private ApparatusPanel panel;
    private int numLatticePtsX;
    private int numLatticePtsY;
    private Vector2D[][] latticePts;
    private CubicSpline spline = new CubicSpline();
    private boolean autoscaleEnabled = false;
    private WaveMedium waveMedium;
    private int latticeViewSpacingY;
    private int latticeViewSpacingX;
    private Point originView = new Point();
    private int viewType;
    private boolean splineVisible;

    /**
     * @param waveMedium
     * @param origin
     * @param width
     * @param height
     * @param latticeSpacingX
     * @param latticeSpacingY
     */
    public FieldLatticeView( WaveMedium waveMedium, Point2D.Double origin,
                             double width, double height,
                             double latticeSpacingX, double latticeSpacingY, ApparatusPanel panel,
                             ModelViewTransform2D tx ) {
        super( tx );
        this.origin = origin;
        this.latticeSpacingX = latticeSpacingX;
        this.latticeSpacingY = latticeSpacingY;
//        this.panel = panel;
        numLatticePtsX = (int)( 1 + ( width - 1 ) / latticeSpacingX );
        numLatticePtsY = (int)( 1 + ( height - 1 ) / latticeSpacingY );
        latticePts = new Vector2D[numLatticePtsY][numLatticePtsX];
        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                latticePts[i][j] = new Vector2D();
            }
        }
        this.waveMedium = waveMedium;
        waveMedium.addObserver( this );

        update( null, null );
    }

    public synchronized void paint( Graphics2D g2 ) {

        if( viewType != VIEW_NONE ) {
            RenderingHints orgRH = g2.getRenderingHints();
            GraphicsUtil.setAntiAliasingOn( g2 );
            g2.setColor( Color.BLUE );

            float scaleFactor = 1.0f;
            if( autoscaleEnabled ) {
                scaleFactor = getAutoscaleFactor();
            }

            // Draw lattice points, field vectors and a spline curve
            for( int i = 0; i < numLatticePtsY; i++ ) {

                boolean atXAxis = ( i == numLatticePtsX / 2 - 1 );

                // If we are to display a spline on this row of the lattice, clear its data cache now
                if( splineVisible && atXAxis ) {
                    spline.reset();
                }

                for( int j = 0; j < numLatticePtsX; j++ ) {
                    if( viewType == VIEW_FULL || ( viewType == VIEW_SINGLE && atXAxis ) ) {

                        int x = originView.x + j * latticeViewSpacingX - 1;
                        int y = originView.y + i * latticeViewSpacingY - 1;

                        // Get the components of the arrow. Note that we flip the sign of the y component
                        // because we need to work to view coordinates
                        int fx = (int)( latticePts[i][j].getX() * scaleFactor );
                        int fy = -(int)( latticePts[i][j].getY() * scaleFactor );

                        // This draws an arrow that pivots around the latice point
                        // Limit the length to the spacing between lattice points
                        double l = Math.sqrt( fx * fx + fy * fy );
                        l = Math.min( l, Math.min( this.latticeViewSpacingX, Math.abs( this.latticeViewSpacingY ) ) );

                        if( viewType == VIEW_FULL ) {
                            int arrowWidthSave = arrowWidth;
                            int headWidthSave = arrowHeadWidth;
                            arrowWidth = (int)( 4 * ( l / 20 ) );
                            arrowHeadWidth = (int)( 8 * ( l / 20 ) );
                            float alpha = (float)l / this.latticeViewSpacingY;
                            Composite orgComposite = g2.getComposite();
                            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
                            drawHollowArrow( g2, x, y - fy / 2, x, y + fy / 2 );
                            g2.setComposite( orgComposite );
                            arrowWidth = arrowWidthSave;
                            arrowHeadWidth = headWidthSave;
                        }
                        if( viewType == VIEW_SINGLE ) {
                            drawHollowArrow( g2, x, y, x, y + fy );
                        }

                        if( splineVisible && atXAxis ) {
                            spline.addPoint( x, y + fy );
                        }
                    }
                } // for( int i = 0; i < numLatticePtsY; i++ )

                // Draw the spline curve
                if( splineVisible && atXAxis ) {
                    g2.setColor( splineColor );
                    g2.setStroke( splineStroke );
                    spline.paint( g2 );
                }
            }
            g2.setRenderingHints( orgRH );
        } // if( viewType != VIEW_NONE )
    }

    private float getAutoscaleFactor() {

        float maxMag = 0;

        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                float m = Math.max( maxMag, Math.max( latticePts[i][j].getX(), latticePts[i][j].getY() ) );
                if( !Float.isNaN( m ) ) {
                    maxMag = m;
                }
            }
        }
        return (int)( latticeSpacingX / maxMag );
    }

    /**
     * Get the strength of the field at each of the lattice points
     */
    public synchronized void update( Observable o, Object arg ) {

        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                latticePtLocation.x = j * latticeSpacingX + origin.getX();
                latticePtLocation.y = i * latticeSpacingY + origin.getY();

                Vector2D fs = this.waveMedium.getFieldAtLocation( latticePtLocation );
                latticePts[i][j].setX( fs.getX() * s_scaleFactor );
                latticePts[i][j].setY( fs.getY() * s_scaleFactor );
            }
        }

        // Update the latticeViewSpacing
        latticeViewSpacingX = (int)latticeSpacingX;
        latticeViewSpacingY = (int)latticeSpacingY;
        originView.setLocation( origin.x, origin.y );
    }

    private void drawHollowArrow( Graphics2D g2, int x1, int y1, int x2, int y2 ) {
        if( x1 != x2 || y1 != y2 ) {
            g2.setColor( arrowColor );

            g2.setStroke( new BasicStroke( 1f ) );

            int y3 = y2 - Math.min( Math.abs( y2 - y1 ) / 2, maxWidth ) * MathUtil.getSign( y2 - y1 );
            GeneralPath arrow = new GeneralPath();
            arrow.moveTo( x1 - arrowWidth / 2, y1 );
            arrow.lineTo( x1 - arrowWidth / 2, y3 );

            arrow.lineTo( x1 - arrowHeadWidth / 2, y3 );
            arrow.lineTo( x1, y2 );
            arrow.lineTo( x1 + arrowHeadWidth / 2, y3 );

            arrow.lineTo( x1 + arrowWidth / 2, y3 );
            arrow.lineTo( x1 + arrowWidth / 2, y1 );
            arrow.lineTo( x1 - arrowWidth / 2, y1 );

            g2.draw( arrow );
        }
    }


    public void setViewOff() {
        viewType = VIEW_NONE;
        splineVisible = false;
    }

    public void setViewFull() {
        viewType = VIEW_FULL;
        splineVisible = false;
    }

    public void setViewSingle() {
        viewType = VIEW_SINGLE;
        splineVisible = false;
    }

    //
    // Statics
    //
    private static Color arrowColor = new Color( 235, 235, 235 );
    //    private static Color arrowColor = new Color( 32, 32, 32 );
    private static Color curveColor = new Color( 80, 0, 230 );
    private static Polygon arrowHead = new Polygon();
    private static int maxWidth = 10; // width of arrowhead
    private static int arrowWidth = 10;
    private static int arrowHeadWidth = 20;
    BasicStroke arrowStroke = new BasicStroke( 4 );
    private static float s_scaleFactor = (float)( 50 / MicrowaveConfig.s_maxAmp );
    private static int s_latticePtDiam = 5;
    private static BufferedImage s_latticePtImg = new BufferedImage( s_latticePtDiam,
                                                                     s_latticePtDiam,
                                                                     BufferedImage.TYPE_INT_ARGB );
    private int VIEW_NONE = 0;
    private int VIEW_FULL = 1;
    private int VIEW_SINGLE = 2;

    public void setViewSpline() {
        splineVisible = true;
    }

}
