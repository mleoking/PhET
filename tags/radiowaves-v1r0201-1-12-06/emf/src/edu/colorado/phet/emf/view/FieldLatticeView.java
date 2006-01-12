/**
 * Class: FieldLatticeView
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Jun 2, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.view.graphics.splines.CubicSpline;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FieldLatticeView implements Graphic, SimpleObserver {

    private static int curveStartingIdx = 1;

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    public final static int maxArrowHeadWidth = 10;
    public final static Color curveColor = new Color( 200, 0, 0 );
    public final static Color arrowRed = new Color( 200, 0, 0 );
    public final static Color arrowGreen = new Color( 0, 100, 0 );
    private static Color arrowColor = new Color( 255, 48, 48 );
    private static int hollowArrowWidth = 5;
    private static int hollowArrowHeadWidth = 10;
//    private static int hollowArrowWidth = 10;
//    private static int hollowArrowHeadWidth = 20;
    private static BasicStroke hollowArrowStroke = new BasicStroke( 2 );
    private static BasicStroke curveStroke = new BasicStroke( 2 );
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


    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    // Note that transmittingElectronOrigin is the view transmittingElectronOrigin, NOT the transmittingElectronOrigin of the electron
    // emitting the field
    private Point transmittingElectronOrigin;
    private int latticeSpacingX;
    private int latticeSpacingY;
    private int numLatticePtsX;
    private int numLatticePtsY;
    // The lattice points used for the full field view
    private FieldPt[] latticePts;
    // The lattice points along the x axis that are used for arrows to the left of the transmitter
    private ArrayList latticePtsNeg = new ArrayList();
    // The lattice points along the x axis that are used for arrows to the right of the transmitter
    private ArrayList latticePtsPos = new ArrayList();
    private AffineTransform[] latticeTx;
    private Electron sourceElectron;
    private CubicSpline spline = new CubicSpline();
    private boolean autoscaleEnabled = false;
    private AffineTransform atx;

    private boolean displayStaticField;
    private boolean displayDynamicField;
    private int fieldSense = FORCE_ON_ELECTRON;
    private int fieldDisplayType;
    private boolean fieldDisplayAlphaEnabled = true;

    private ArrayList arrows = new ArrayList();
    private GeneralPath negPath;
    private GeneralPath posPath;
    // How far from the transmitting antenna the first field arrow is drawn
    private int firstArrowOffset = 25;
    private Component component;
    private boolean curveVisible = true;
    private boolean fixedSizeArrows = false;
    private int fullAlphaFieldMagnitude = 100;

    /**
     * Constructor
     *
     * @param sourceElectron
     * @param origin
     * @param width
     * @param height
     * @param latticeSpacingX
     * @param latticeSpacingY
     * @param component
     */
    public FieldLatticeView( Electron sourceElectron, Point origin,
                             int width, int height,
                             int latticeSpacingX, int latticeSpacingY,
                             Component component ) {
        this.component = component;

        if( !( fieldSense == FORCE_ON_ELECTRON
               || fieldSense == ELECTRIC_FIELD ) ) {
            throw new RuntimeException( "Bad actual parameter: fieldSense " );
        }

        //        this.width = width;
        this.transmittingElectronOrigin = origin;
        this.latticeSpacingX = latticeSpacingX;
        this.latticeSpacingY = latticeSpacingY;
        numLatticePtsX = 1 + ( width - 1 ) / latticeSpacingX;
        numLatticePtsY = 1 + ( height - 1 ) / latticeSpacingY;
        latticePts = new FieldPt[numLatticePtsY * numLatticePtsX];
        latticeTx = new AffineTransform[numLatticePtsY * numLatticePtsX];

        int xOffset = (int)transmittingElectronOrigin.getX() - latticeSpacingX / 2;
        for( int i = 0; i < numLatticePtsY * numLatticePtsX; i++ ) {
            latticePts[i] = new FieldPt( ( i % numLatticePtsX ) * latticeSpacingX,
                                         ( i / numLatticePtsX ) * latticeSpacingY );
            latticeTx[i] = new AffineTransform();
        }

        // Create field points
        Point2D.Double dp1 = new Point2D.Double( transmittingElectronOrigin.getX() - 0.001, transmittingElectronOrigin.getY() );
        FieldPt fp = new FieldPt( dp1.getX(), dp1.getY() );
        latticePtsNeg.add( fp );
        for( double x = (int)transmittingElectronOrigin.getX() - firstArrowOffset; x >= -latticeSpacingX; x -= latticeSpacingX ) {
            FieldPt fieldPt = new FieldPt( x, transmittingElectronOrigin.getY() );
            latticePtsNeg.add( fieldPt );
        }

        Point2D.Double dp2 = new Point2D.Double( transmittingElectronOrigin.getX() + 0.001, transmittingElectronOrigin.getY() );
        latticePtsPos.add( new FieldPt( dp2.getX(), dp2.getY() ) );
        for( double x = (int)transmittingElectronOrigin.getX() + firstArrowOffset; x < width; x += latticeSpacingX ) {
            FieldPt fieldPt = new FieldPt( x, transmittingElectronOrigin.getY() );
            latticePtsPos.add( fieldPt );
        }

        this.sourceElectron = sourceElectron;
        sourceElectron.addObserver( this );

        update();
    }

    public synchronized void paint( Graphics2D g2 ) {
        // When the app starts up, there is an intermittent exception that
        // gets thrown from something that's called here. This sometimes results
        // in the control panel coming up blank. By catching the exception and
        // eating it, the control panel always comes up properly
        try {

            AffineTransform orgTx = g2.getTransform();
            g2.transform( atx );

            Color color = fieldSense == FORCE_ON_ELECTRON ? arrowRed : arrowGreen;

            if( fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS
                || fieldDisplayType == EmfPanel.VECTORS_CENTERED_ON_X_AXIS ) {
                g2.setColor( color );
                g2.setStroke( hollowArrowStroke );
                for( int i = 0; i < arrows.size(); i++ ) {

                    // Set the alpha of the arrow, based on its length
                    if( fieldDisplayAlphaEnabled ) {
                        Arrow arrow = (Arrow)arrows.get( i );
                        double m = arrow.getTipLocation().distance( arrow.getTailLocation() );
                        double alpha = Math.min( 1, m / fullAlphaFieldMagnitude );
                        GraphicsUtil.setAlpha( g2, alpha );
                    }
                    Arrow arrow = (Arrow)arrows.get( i );
                    g2.draw( arrow.getShape() );
                    GraphicsUtil.setAlpha( g2, 1 );
                }
            }

            if( fieldDisplayType == EmfPanel.CURVE
                || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
                g2.setColor( curveColor );
                g2.setStroke( curveStroke );
                if( curveVisible ) {
                    g2.draw( negPath );
                    g2.draw( posPath );
                }
            }

            if( fieldDisplayType == EmfPanel.FULL_FIELD ) {

                RenderingHints orgRH = g2.getRenderingHints();
                GraphicsUtil.setAntiAliasingOn( g2 );
                for( int i = 0; i < latticePts.length; i++ ) {
                    Vector2D f = latticePts[i].field.scale( fieldSense );
                    double l = f.getMagnitude();

                    if( l > 3 ) {
                        double theta = f.getAngle();
                        Arrow arrow = new Arrow( new Point2D.Double( -l / 2, 0 ), new Point2D.Double( l / 2, 0 ),
                                                 maxArrowHeadWidth,
                                                 maxArrowHeadWidth,
                                                 3,
                                                 0.5,
                                                 true );
                        AffineTransform orgTx2 = g2.getTransform();
                        AffineTransform tx = latticePts[i].tx;
                        tx.setToTranslation( latticePts[i].location.getX(), latticePts[i].location.getY() );
                        tx.rotate( theta );
                        g2.transform( tx );

                        // Set the alpha of the arrow, based on the magnitude of the field at this point
                        if( fieldDisplayAlphaEnabled ) {
                            double alpha = Math.min( 1, l / fullAlphaFieldMagnitude );
                            GraphicsUtil.setAlpha( g2, alpha );
                        }
                        g2.setColor( color );
//                            g2.fill( arrow.getShape() );
                        g2.draw( arrow.getShape() );
                        g2.setTransform( orgTx2 );
                    }
                }
                GraphicsUtil.setAlpha( g2, 1.0 );
                g2.setRenderingHints( orgRH );
            }
            g2.setTransform( orgTx );
            GraphicsUtil.setAlpha( g2, 1.0 );
        }
        catch( java.lang.InternalError e ) {
            System.out.println( "Caught Internal Error: " + e.getMessage() );
        }
    }

    /**
     * Determines the field at specified FieldPt and sets its value
     *
     * @param fieldPt
     */
    private void evaluateFieldPt( FieldPt fieldPt ) {
        Vector2D tf = null;
        if( displayStaticField && fieldDisplayType != EmfPanel.NO_FIELD ) {
            tf = sourceElectron.getStaticFieldAt( fieldPt.location );
        }
        else if( displayDynamicField && fieldDisplayType != EmfPanel.NO_FIELD ) {
            tf = sourceElectron.getDynamicFieldAt( fieldPt.location );
        }
        fieldPt.field.setComponents( tf.getX(), tf.getY() );
    }

    /**
     * Get the strength of the field at each of the lattice points
     */
    public synchronized void update() {

        if( fieldDisplayType == EmfPanel.FULL_FIELD ) {
            for( int i = 0; i < latticePts.length; i++ ) {
                evaluateFieldPt( latticePts[i] );
            }
        }

        if( fieldDisplayType == EmfPanel.CURVE
            || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS
            || fieldDisplayType == EmfPanel.VECTORS_CENTERED_ON_X_AXIS ) {
            // Set the field magnitudes for all the negative and positive arrows
            for( int i = 0; i < latticePtsNeg.size(); i++ ) {
                FieldPt fieldPt = (FieldPt)latticePtsNeg.get( i );
                evaluateFieldPt( fieldPt );
            }
            for( int i = 0; i < latticePtsPos.size(); i++ ) {
                FieldPt fieldPt = (FieldPt)latticePtsPos.get( i );
                evaluateFieldPt( fieldPt );
            }

            Rectangle oldBounds = new Rectangle();
            if( negPath != null ) {
                Rectangle negBounds = negPath.getBounds();
                oldBounds.union( negBounds );
            }
            if( posPath != null ) {
                Rectangle posBounds = posPath.getBounds();
                oldBounds.union( posBounds );
            }

            this.negPath = createCurves( latticePtsNeg );
            this.posPath = createCurves( latticePtsPos );

            Rectangle negBounds = negPath.getBounds();
            oldBounds.union( negBounds );
            Rectangle posBounds = posPath.getBounds();
            oldBounds.union( posBounds );

            // For fast painting
            for( int i = 0; i < arrows.size(); i++ ) {
                Arrow arrow = (Arrow)arrows.get( i );
                Rectangle arrowBounds = arrow.getShape().getBounds();
                oldBounds.union( arrowBounds );
            }

            arrows.clear();
            addArrows( arrows, latticePtsNeg );
            addArrows( arrows, latticePtsPos );

            // For fast painting
            for( int i = 0; i < arrows.size(); i++ ) {
                Arrow arrow = (Arrow)arrows.get( i );
                Rectangle arrowBounds = arrow.getShape().getBounds();
                oldBounds.union( arrowBounds );
            }
            component.repaint( oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height );
        }
    }

    private void addArrows( ArrayList arrows, ArrayList pts ) {
        // We do not draw an arrow for the first field point because it is very close
        // to the antenna (so that we get a nice steep line there )
        for( int i = 1; i < pts.size(); i++ ) {
            FieldPt fieldPt = (FieldPt)pts.get( i );
            int arrowDir = MathUtil.getSign( fieldPt.field.getY() ) * ( fieldSense == FORCE_ON_ELECTRON ? 1 : -1 );
            double magnitude = fieldPt.field.getMagnitude();
            if( fixedSizeArrows ) {
                magnitude = 50;
            }
            if( magnitude > 0 ) {
                double y = fieldPt.getY() + magnitude * arrowDir;
                Point2D tailLoc = new Point2D.Double( fieldPt.location.getX(), fieldPt.location.getY() );
                y -= magnitude * arrowDir / 2;
                tailLoc.setLocation( tailLoc.getX(), tailLoc.getY() - magnitude * arrowDir / 2 );
                Arrow arrow = new Arrow( tailLoc,
                                         new Point2D.Double( fieldPt.getX(), y ),
                                         maxArrowHeadWidth,
                                         maxArrowHeadWidth,
                                         3,
                                         0.5,
                                         true );
                arrows.add( arrow );
            }
        }
    }

    /**
     * @param pts
     * @return
     */
    private GeneralPath createCurves( ArrayList pts ) {

        // We modify the amplitude of the curve because it is supposed to connect the heads of the vectors on
        // the x axis that show the field strength, and those vectors are centered on the x axis. Their tails are
        // not on the axis
        double curveAmplitudeOffset = 0.5;
        FieldPt orig = (FieldPt)pts.get( curveStartingIdx );
        int xSign = MathUtil.getSign( orig.getX() - transmittingElectronOrigin.getX() );
        DoubleGeneralPath curve = new DoubleGeneralPath( orig.getX(),
                                                         orig.getY() + orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() ) * curveAmplitudeOffset );
        double yLast = orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() * curveAmplitudeOffset );
        double yCurr = yLast;
        double xLimit = ( (FieldPt)pts.get( pts.size() - 1 ) ).getX();
        for( double x = orig.getX();
             xSign > 0 ? x < xLimit : x > xLimit;
             x += 1 * xSign ) {
            Vector2D field = sourceElectron.getDynamicFieldAt( new Point2D.Double( Math.abs( x ),
                                                                                   transmittingElectronOrigin.getY() ) );
            yCurr = field.getMagnitude() * MathUtil.getSign( field.getY() );
            if( yCurr != yLast ) {
                curve.lineTo( x, transmittingElectronOrigin.getY() + yCurr * curveAmplitudeOffset );
                yLast = yCurr;
            }
        }
        curve.lineTo( xLimit, transmittingElectronOrigin.getY() + yCurr * xSign );
        return curve.getGeneralPath();
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

    public void setTransform( AffineTransform atx ) {
        this.atx = atx;
    }

    public void setCurveVisible( boolean visible ) {
        curveVisible = visible;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     *
     */
    private class FieldPt {

        Point2D.Double location;
        Vector2D.Float field = new Vector2D.Float();
        AffineTransform tx = new AffineTransform();

        public FieldPt( double x, double y ) {
            this( new Point2D.Double( x, y ), new Vector2D.Float() );
        }

        public FieldPt( Point2D.Double location, Vector2D.Float field ) {
            this.location = new Point2D.Double( location.getX(), location.getY() );
            this.field = new Vector2D.Float( field.getX(), field.getY() );
        }


        public double getX() {
            return location.x;
        }

        public double getY() {
            return location.y;
        }
    }
}
