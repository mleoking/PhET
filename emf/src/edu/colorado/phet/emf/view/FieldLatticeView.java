/**
 * Class: FieldLatticeView
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Jun 2, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.view.graphics.splines.CubicSpline;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;

public class FieldLatticeView implements ObservingGraphic {

    // Note that origin is the view origin, NOT the origin of the electron
    // emitting the field
    private Point origin;
    private int latticeSpacingX;
    private int latticeSpacingY;
    private int numLatticePtsX;
    private int numLatticePtsY;
    private Vector2D[][] latticePts;
    private Electron sourceElectron;
    private CubicSpline spline = new CubicSpline();
    private boolean autoscaleEnabled = false;
    private BufferedImage buffImg;
    private Point2D.Double latticePtLocation = new Point2D.Double();

    private ImageObserver imgObserver = new ImageObserver() {
        public boolean imageUpdate( Image img, int infoflags,
                                    int x, int y, int width, int height ) {
            return false;
        }
    };
    private boolean displayStaticField;
    private boolean displayDynamicField;
    private int fieldSense = FORCE_ON_ELECTRON;
    private Point2D.Double tempPt = new Point2D.Double();
    private int fieldDisplayType;

    public FieldLatticeView( Electron sourceElectron, Point origin,
                             int width, int height,
                             int latticeSpacingX, int latticeSpacingY ) {

        if( !( fieldSense == FORCE_ON_ELECTRON
                || fieldSense == ELECTRIC_FIELD ) ) {
            throw new RuntimeException( "Bad actual parameter: fieldSense " );
        }
        this.fieldSense = fieldSense;
        this.origin = origin;
        this.latticeSpacingX = latticeSpacingX;
        this.latticeSpacingY = latticeSpacingY;
        numLatticePtsX = 1 + ( width - 1 ) / latticeSpacingX;
        numLatticePtsY = 1 + ( height - 1 ) / latticeSpacingY;
        latticePts = new Vector2D[numLatticePtsY][numLatticePtsX];
        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                latticePts[i][j] = new Vector2D();
            }
        }
        this.sourceElectron = sourceElectron;
        sourceElectron.addObserver( this );

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gs = ge.getDefaultScreenDevice();
//        GraphicsConfiguration gc = gs.getDefaultConfiguration();
//        buffImg = gc.createCompatibleImage( 800, 800, Transparency.TRANSLUCENT );
//        g2DBuffImg = buffImg.createGraphics();
    }


    public synchronized void paint( Graphics2D g2 ) {

        boolean displayCurve = fieldDisplayType == EmfPanel.CURVE
                || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS;

        float scaleFactor = 1.0f;
//        if( autoscaleEnabled ) {
//            scaleFactor = getAutoscaleFactor();
//        }

        // Draw field vectors and a spline curve
        for( int i = 0; i < numLatticePtsY; i++ ) {

            boolean atXAxis = Math.abs( sourceElectron.getCurrentPosition().getY() - ( origin.getY() + i * latticeSpacingY ) )
                    < latticeSpacingY / 2;

            // If we are to display a spline on this row of the lattice, clear its data cache now
            if( displayCurve && atXAxis ) {
                spline.reset();
            }

            for( int j = 0; j < numLatticePtsX; j++ ) {
                int x = (int)origin.getX() + j * latticeSpacingX;
                if( !displayCurve || atXAxis ) {
                    int y = (int)origin.getY() + i * latticeSpacingY;
                    int fx = fieldSense * (int)( latticePts[i][j].getX() * scaleFactor );
                    int fy = fieldSense * (int)( latticePts[i][j].getY() * scaleFactor );

                    // This draws an arrow that pivots around the latice point
                    double l = Math.sqrt( fx * fx + fy * fy );
                    double theta = Math.atan2( fy, fx );
                    Color curveColor = fieldSense == FORCE_ON_ELECTRON ? arrowRed : arrowGreen;
                    if( displayCurve ) {

                        // Add a point for the source electron's origin, if the last spline
                        // point and the next one straddle it
//                        if( x - sourceElectron.getStartPosition().getX() > 0
//                                && x - sourceElectron.getStartPosition().getX() < latticeSpacingX ) {
//                            spline.addPoint( (int)sourceElectron.getStartPosition().getX(),
//                                             (int)sourceElectron.getStartPosition().getY() );
//                        }

                        tempPt.setLocation( x, sourceElectron.getCurrentPosition().getY() );
                        Vector2D ff = null;
                        if( displayDynamicField ) {
                            ff = sourceElectron.getDynamicFieldAt( tempPt );
                        }
                        else if( displayStaticField ) {
                            ff = sourceElectron.getStaticFieldAt( tempPt );
                        }
                        y = (int)sourceElectron.getStartPosition().getY();
                        spline.addPoint( x, y + (int)ff.getY() );
                        ff.setY( fieldSense == FORCE_ON_ELECTRON ? ff.getY() : -ff.getY() );
                        if( fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
                            Color color = fieldSense == FORCE_ON_ELECTRON ? arrowRed : arrowGreen;
//                            Color color = fieldSense == FORCE_ON_ELECTRON ? arrowColor : arrowGreen;
                            g2.setColor( color );
                            drawHollowArrow( g2, x, y, x, y + (int)ff.getY() );
                        }
                    }
                    else if( fieldDisplayType == EmfPanel.FULL_FIELD ) {
                        BufferedImage bi = FieldVector.getGraphic( l, theta, curveColor );
                        if( bi != null ) {
                            g2.drawImage( bi, x - ( bi.getWidth() / 2 ), y - ( bi.getHeight() / 2 ), this.imgObserver );
                        }
                    }
                }
            }

            // Draw the spline curve
            if( displayCurve && atXAxis ) {
                g2.setColor( arrowRed );
                g2.setStroke( splineStroke );
                spline.paint( g2 );
//                spline.paint( g2DBuffImg );
            }
        }
//        g2.drawImage( buffImg, nopAT, null );
    }


    /**
     * Get the strength of the field at each of the lattice points
     */
    public synchronized void update( Observable o, Object arg ) {
        if( o instanceof Electron ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {

                for( int i = 0; i < numLatticePtsY; i++ ) {
                    latticePtLocation.setLocation( j * latticeSpacingX, i * latticeSpacingY );
                    Vector2D fs = null;
                    if( displayStaticField && fieldDisplayType != EmfPanel.NO_FIELD ) {
                        fs = sourceElectron.getStaticFieldAt( latticePtLocation );
                    }
                    else if( displayDynamicField && fieldDisplayType != EmfPanel.NO_FIELD ) {
                        fs = sourceElectron.getDynamicFieldAt( latticePtLocation );
                    }
                    if( fs != null ) {
                        latticePts[i][j].setX( fs.getX() );
                        latticePts[i][j].setY( fs.getY() );
                    }
                    else {
                        latticePts[i][j].setX( 0 );
                        latticePts[i][j].setY( 0 );
                    }
                }
            }
        }
    }

//    private float getAutoscaleFactor() {
//        float maxMag = 0;
//
//        // If the movement strategy is sinusoidal, then get the max field at the origin,
//        // and use it as the scale factor. Note that the 5 in the expression is strictly
//        // empirical. I don't know why it looks right
//        if( SinusoidalMovement.class.isAssignableFrom( sourceElectron.getMovementStrategyType() ) ) {
//            maxMag = sourceElectron.getMaxAccelerationAtLocation( origin ).getLength() / 5;
//        }
//        // If not sinusoidal, find the biggest vector in lattice and use it to determine
//        // the scale factor
//        else {
//            for( int i = 0; i < numLatticePtsY; i++ ) {
//                for( int j = 0; j < numLatticePtsX; j++ ) {
//                    float m = Math.max( maxMag, Math.max( latticePts[i][j].getX(), latticePts[i][j].getY() ) );
//                    if( !Float.isNaN( m ) ) {
//                        maxMag = m;
//                    }
//                }
//            }
//        }
//        // Determine the scale factor for the painting of the vectors
//        return latticeSpacingX / maxMag;
//    }

    public void setFieldCurvesEnabled( boolean enabled ) {
//        this.fieldCurvesEnabled = enabled;
    }

    public void setAutoscaleEnabled( boolean enabled ) {
        this.autoscaleEnabled = enabled;
    }

    public void setDisplayStaticField( boolean displayStaticField ) {
        this.displayStaticField = displayStaticField;
    }

    public void setDisplayDynamicField( boolean displayDynamicField ) {
        this.displayDynamicField = displayDynamicField;
    }

    public void setFieldSense( int fieldSense ) {
        this.fieldSense = fieldSense;
    }

    public void setDisplay( int display ) {
        fieldDisplayType = display;
    }

    public void paintDots( Graphics graphics ) {
        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                int x = (int)origin.getX() + j * latticeSpacingX - 1;
                int y = (int)origin.getY() + i * latticeSpacingY - 1;
                graphics.drawImage( s_latticePtImg, x - 1, y - 1, imgObserver );
            }
        }
    }


//
// Statics
//
    public final static Color arrowRed = new Color( 200, 0, 0 );
    public final static Color arrowGreen = new Color( 0, 100, 0 );
//    private static Color curveColor = new Color( 80, 0, 230 );
    private static BasicStroke arrowStroke = new BasicStroke( 4 );
    private static BasicStroke splineStroke = new BasicStroke( 2 );
    public static final int ELECTRIC_FIELD = -1;
    public static final int FORCE_ON_ELECTRON = 1;
    private static int s_latticePtDiam = 5;
    private static BufferedImage s_latticePtImg = new BufferedImage( s_latticePtDiam,
                                                                     s_latticePtDiam,
                                                                     BufferedImage.TYPE_INT_ARGB );

    static {
        // Create a graphics context on the buffered image
        Graphics2D g2d = s_latticePtImg.createGraphics();

        // Draw on the image
//        g2d.setColor( Color.blue );
//        g2d.drawArc( 0, 0,
//                     2, 2,
//                     0, 360 );
//     g2d.fill(new Ellipse2D.Float( 0, 0, s_latticePtDiam, s_latticePtDiam ));
        g2d.dispose();
    }


    private static Color arrowColor = new Color( 0, 0, 200 );
    private static int maxWidth = 10; // width of arrowhead
    private static int arrowWidth = 10;
    private static int arrowHeadWidth = 20;
    BasicStroke hollowArrowStroke = new BasicStroke( 4 );

    private static void drawHollowArrow( Graphics2D g2, int x1, int y1, int x2, int y2 ) {
        if( x1 != x2 || y1 != y2 ) {
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
}
