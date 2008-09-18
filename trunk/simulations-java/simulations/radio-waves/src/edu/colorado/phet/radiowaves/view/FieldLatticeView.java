/**
 * Class: FieldLatticeView Package: edu.colorado.phet.emf.view Author: Another
 * Guy Date: Jun 2, 2003
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common_1200.math.MathUtil;
import edu.colorado.phet.common_1200.math.Vector2D;
import edu.colorado.phet.common_1200.view.graphics.Graphic;
import edu.colorado.phet.common_1200.view.graphics.shapes.Arrow;
import edu.colorado.phet.common_1200.view.util.DoubleGeneralPath;
import edu.colorado.phet.common_1200.view.util.GraphicsUtil;
import edu.colorado.phet.radiowaves.EmfConfig;
import edu.colorado.phet.radiowaves.model.Electron;
import edu.colorado.phet.radiowaves.view.graphics.splines.CubicSpline;

public class FieldLatticeView implements Graphic, SimpleObserver {

    private static int curveStartingIdx = 1;

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    public final static int maxArrowHeadWidth = 10;
    public final static Color curveColor = new Color( 200, 0, 0 );
    public final static Color arrowRed = EmfConfig.FORCE_COLOR;
    public final static Color arrowGreen = EmfConfig.FIELD_COLOR;
    private static int hollowArrowWidth = 5;
    private static int hollowArrowHeadWidth = 10;
    //    private static int hollowArrowWidth = 10;
    //    private static int hollowArrowHeadWidth = 20;
    private static BasicStroke hollowArrowStroke = new BasicStroke( 1f );
    private static BasicStroke curveStroke = new BasicStroke( 1f );
    //    private static BasicStroke curveStroke = new BasicStroke( 2 );
    private static int s_latticePtDiam = 5;
    private static BufferedImage s_latticePtImg = new BufferedImage( s_latticePtDiam, s_latticePtDiam, BufferedImage.TYPE_INT_ARGB );

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
    private ArrayList negArrows = new ArrayList();
    private ArrayList posArrows = new ArrayList();

    // The lattice points along the x axis that are used for arrows to the right of the transmitter
    private ArrayList latticePtsPos = new ArrayList();
    private AffineTransform[] latticeTx;
    private Electron sourceElectron;
    private CubicSpline spline = new CubicSpline();
    private boolean autoscaleEnabled = false;
    private AffineTransform atx;

    private boolean displayStaticField;
    private boolean displayDynamicField;
    private int fieldSense = EmfConfig.SHOW_FORCE_ON_ELECTRON;
    private int fieldDisplayType;
    //    private boolean fieldDisplayAlphaEnabled = true;
    private boolean fieldDisplayAlphaEnabled = false;

    private ArrayList arrows = new ArrayList();
    private GeneralPath negPath;
    private GeneralPath posPath;
    // How far from the transmitting antenna the first field arrow is drawn
    private int firstArrowOffset = 25;
    private Component component;
    private boolean curveVisible = true;
    private boolean fixedSizeArrows = false;
    private int fullAlphaFieldMagnitude = 80;
    private BasicStroke arrowStroke = new BasicStroke( 2 );

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
    public FieldLatticeView( Electron sourceElectron, Point origin, int width, int height, int latticeSpacingX, int latticeSpacingY, Component component ) {
        this.component = component;

        if ( !( fieldSense == EmfConfig.SHOW_FORCE_ON_ELECTRON || fieldSense == EmfConfig.SHOW_ELECTRIC_FIELD ) ) {
            throw new RuntimeException( "Bad actual parameter: fieldSense " );
        }

        // Set up the lattice
        this.transmittingElectronOrigin = origin;
        this.latticeSpacingX = latticeSpacingX;
        this.latticeSpacingY = latticeSpacingY;
        numLatticePtsX = 1 + ( width - 1 ) / latticeSpacingX;
        numLatticePtsY = 1 + ( height - 1 ) / latticeSpacingY;
        latticePts = new FieldPt[numLatticePtsY * numLatticePtsX];
        latticeTx = new AffineTransform[numLatticePtsY * numLatticePtsX];
        for ( int i = 0; i < numLatticePtsY * numLatticePtsX; i++ ) {
            latticePts[i] = new FieldPt( ( i % numLatticePtsX ) * latticeSpacingX, ( i / numLatticePtsX ) * latticeSpacingY );
            latticeTx[i] = new AffineTransform();
        }

        // Create field points
        Point2D.Double dp1 = new Point2D.Double( transmittingElectronOrigin.getX() - 0.001, transmittingElectronOrigin.getY() );
        FieldPt fp = new FieldPt( dp1.getX(), dp1.getY() );
        latticePtsNeg.add( fp );
        for ( double x = (int) transmittingElectronOrigin.getX() - firstArrowOffset; x >= -latticeSpacingX; x -= latticeSpacingX ) {
            FieldPt fieldPt = new FieldPt( x, transmittingElectronOrigin.getY() );
            latticePtsNeg.add( fieldPt );
            negArrows.add( new Arrow( new Point2D.Double(), new Point2D.Double(), maxArrowHeadWidth, maxArrowHeadWidth, 3, 0.5, false ) );
        }

        Point2D.Double dp2 = new Point2D.Double( transmittingElectronOrigin.getX() + 0.001, transmittingElectronOrigin.getY() );
        latticePtsPos.add( new FieldPt( dp2.getX(), dp2.getY() ) );
        for ( double x = (int) transmittingElectronOrigin.getX() + firstArrowOffset; x < width; x += latticeSpacingX ) {
            FieldPt fieldPt = new FieldPt( x, transmittingElectronOrigin.getY() );
            latticePtsPos.add( fieldPt );
            posArrows.add( new Arrow( new Point2D.Double(), new Point2D.Double(), maxArrowHeadWidth, maxArrowHeadWidth, 3, 0.5, false ) );
        }
        arrows.addAll( negArrows );
        arrows.addAll( posArrows );

        this.sourceElectron = sourceElectron;
        sourceElectron.addObserver( this );

        update();
    }

    public synchronized void paint( Graphics2D g2 ) {
        // When the app starts up, there is an intermittent exception that
        // gets thrown from something that's called here. This sometimes results
        // in the control panel coming up blank. By catching the exception and
        // eating it, the control panel always comes up properly
        RenderingHints orgRh = g2.getRenderingHints();

        GraphicsUtil.setAntiAliasingOn( g2 );

        AffineTransform orgTx = g2.getTransform();
        g2.transform( atx );

        Color color = fieldSense == EmfConfig.SHOW_FORCE_ON_ELECTRON ? arrowRed : arrowGreen;

        if ( fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS || fieldDisplayType == EmfPanel.VECTORS_CENTERED_ON_X_AXIS ) {
            g2.setColor( color );
            g2.setStroke( hollowArrowStroke );
            for ( int i = 0; i < arrows.size(); i++ ) {

                // Set the alpha of the arrow, based on its length
                if ( fieldDisplayAlphaEnabled ) {
                    Arrow arrow = (Arrow) arrows.get( i );
                    double m = arrow.getTipLocation().distance( arrow.getTailLocation() );
                    double alpha = Math.min( 1, m / fullAlphaFieldMagnitude );
                    GraphicsUtil.setAlpha( g2, alpha );
                }
                Arrow arrow = (Arrow) arrows.get( i );
                g2.draw( arrow.getShape() );
            }
            GraphicsUtil.setAlpha( g2, 1 );
        }

        if ( fieldDisplayType == EmfPanel.CURVE || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
            g2.setColor( curveColor );
            g2.setStroke( curveStroke );

            // Tried Sam M's idea of drawing transparent curves
            //            if( negPath != null && posPath != null ) {
            if ( curveVisible && negPath != null && posPath != null ) {
                RenderingHints orgRhB = g2.getRenderingHints();
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                if ( !curveVisible ) {
                    GraphicsUtil.setAlpha( g2, 0 );
                }
                g2.draw( negPath );
                g2.draw( posPath );
                g2.setRenderingHints( orgRhB );
            }
        }

        if ( fieldDisplayType == EmfPanel.FULL_FIELD ) {
            Point2D tailPt = new Point2D.Double();
            Point2D tipPt = new Point2D.Double();
            Arrow arrow = new Arrow( tailPt, tipPt, maxArrowHeadWidth, maxArrowHeadWidth, 3, 0.5, true );
            g2.setColor( color );
            g2.setStroke( hollowArrowStroke );
            for ( int i = 0; i < latticePts.length; i++ ) {
                Vector2D f = latticePts[i].field.scale( fieldSense );
                double l = f.getMagnitude();

                if ( l > 3 ) {
                    double theta = f.getAngle();
                    tailPt.setLocation( -l / 2, 0 );
                    tipPt.setLocation( l / 2, 0 );
                    arrow.setTailLocation( tailPt );
                    arrow.setTipLocation( tipPt );
                    AffineTransform orgTx2 = g2.getTransform();
                    AffineTransform tx = latticePts[i].tx;
                    tx.setToTranslation( latticePts[i].location.getX(), latticePts[i].location.getY() );
                    tx.rotate( theta );
                    g2.transform( tx );

                    // Set the alpha of the arrow, based on the magnitude of the field at this point
                    if ( fieldDisplayAlphaEnabled ) {
                        double alpha = Math.min( 1, l / fullAlphaFieldMagnitude );
                        GraphicsUtil.setAlpha( g2, alpha );
                    }
                    //                            g2.fill( arrow.getShape() );
                    g2.draw( arrow.getShape() );
                    g2.setTransform( orgTx2 );
                }
            }
            GraphicsUtil.setAlpha( g2, 1.0 );
        }
        g2.setTransform( orgTx );
        GraphicsUtil.setAlpha( g2, 1.0 );
        g2.setRenderingHints( orgRh );
    }

    /**
     * Determines the field at specified FieldPt and sets its value
     *
     * @param fieldPt
     */
    private void evaluateFieldPt( FieldPt fieldPt ) {
        Vector2D tf = null;
        if ( displayStaticField && fieldDisplayType != EmfPanel.NO_FIELD ) {
            tf = sourceElectron.getStaticFieldAt( fieldPt.location );
        }
        else if ( displayDynamicField && fieldDisplayType != EmfPanel.NO_FIELD ) {
            tf = sourceElectron.getDynamicFieldAt( fieldPt.location );
        }
        fieldPt.field.setComponents( tf.getX(), tf.getY() );
    }


    /**
     * Get the strength of the field at each of the lattice points
     */
    public synchronized void update() {

        // Full field display
        if ( fieldDisplayType == EmfPanel.FULL_FIELD ) {
            for ( int i = 0; i < latticePts.length; i++ ) {
                evaluateFieldPt( latticePts[i] );
            }
        }

        // Single line display
        if ( fieldDisplayType == EmfPanel.CURVE || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS || fieldDisplayType == EmfPanel.VECTORS_CENTERED_ON_X_AXIS ) {

            // Set the field magnitudes for all the negative and positive arrows
            for ( int i = 0; i < latticePtsNeg.size(); i++ ) {
                FieldPt fieldPt = (FieldPt) latticePtsNeg.get( i );
                evaluateFieldPt( fieldPt );
            }
            for ( int i = 0; i < latticePtsPos.size(); i++ ) {
                FieldPt fieldPt = (FieldPt) latticePtsPos.get( i );
                evaluateFieldPt( fieldPt );
            }

            // The first two lines will create cubic splines through the arrow heads. The other two
            // make piecewise linear curves through a finer-grained list of field points.
            //            this.negPath = createSpline( latticePtsNeg );
            //            this.posPath = createSpline( latticePtsPos );
            this.negPath = createCurves( latticePtsNeg );
            this.posPath = createCurves( latticePtsPos );
            addArrows( negArrows, latticePtsNeg );
            addArrows( posArrows, latticePtsPos );
        }
    }

    private void addArrows( ArrayList arrows, ArrayList pts ) {
        // We do not draw an arrow for the first field point because it is very close
        // to the antenna (so that we get a nice steep line there )
        for ( int i = 1; i < pts.size(); i++ ) {
            FieldPt fieldPt = (FieldPt) pts.get( i );
            int arrowDir = MathUtil.getSign( fieldPt.field.getY() ) * ( fieldSense == EmfConfig.SHOW_FORCE_ON_ELECTRON ? 1 : -1 );
            double magnitude = fieldPt.field.getMagnitude();
            if ( fixedSizeArrows ) {
                magnitude = 50;
            }
            double y = fieldPt.getY() + magnitude * arrowDir;
            y -= magnitude * arrowDir * EmfConfig.SINGLE_VECTOR_ROW_OFFSET;
            Arrow arrow = (Arrow) arrows.get( i - 1 );
            arrow.setTailLocation( new Point2D.Double( fieldPt.location.getX(), fieldPt.location.getY() - magnitude * arrowDir * EmfConfig.SINGLE_VECTOR_ROW_OFFSET ) );
            arrow.setTipLocation( new Point2D.Double( fieldPt.getX(), y ) );
        }
    }

    /**
     * Gets the location of the tip of the vector that will represent the field at a specified FieldPt in the single
     * line vector mode.
     *
     * @param fieldPt
     * @return
     */
    private Point2D getVectorTipLocation( FieldPt fieldPt ) {
        Point2D p = new Point2D.Double();
        p.setLocation( new Point2D.Double( Math.abs( fieldPt.getX() ), transmittingElectronOrigin.getY() ) );
        Vector2D field = sourceElectron.getDynamicFieldAt( p );
        double curveAmplitudeOffset = 1 - EmfConfig.SINGLE_VECTOR_ROW_OFFSET;
        double yTip = transmittingElectronOrigin.getY() + ( field.getMagnitude() * MathUtil.getSign( field.getY() ) * curveAmplitudeOffset );
        p.setLocation( p.getX(), yTip );
        return p;
    }

    /**
     * Create a cubic spline that connects the tips of all the arrows in the single-line-of-vectors view
     *
     * @param pts
     * @return
     */
    private GeneralPath createSpline( ArrayList pts ) {

        double curveAmplitudeOffset = 1 - EmfConfig.SINGLE_VECTOR_ROW_OFFSET;
        FieldPt orig = (FieldPt) pts.get( curveStartingIdx );

        // Start the path at the first field point
        GeneralPath curve = new GeneralPath();
        curve.moveTo( (float) orig.getX(), (float) ( orig.getY() + orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() ) * curveAmplitudeOffset ) );
        // Make cubic curves through the rest of the points
        Point2D p = new Point2D.Double();

        // Generate a cubic to each of the points, starting with the second one. Note that the 0 index doesn't
        // correspond to a valid field point
        for ( int i = 2; i < pts.size() - 1; i++ ) {

            FieldPt fieldPt = (FieldPt) pts.get( i );
            p.setLocation( new Point2D.Double( Math.abs( fieldPt.getX() ), transmittingElectronOrigin.getY() ) );
            Vector2D field = sourceElectron.getDynamicFieldAt( p );

            Point2D pPrev = null;
            if ( i > 0 ) {
                pPrev = getVectorTipLocation( (FieldPt) pts.get( i - 1 ) );
            }
            else {
                pPrev = p;
            }


            Point2D pNext = new Point2D.Double( ( (FieldPt) pts.get( i + 1 ) ).getX(), ( (FieldPt) pts.get( i + 1 ) ).getY() );
            if ( i < pts.size() - 1 ) {
                pNext = getVectorTipLocation( (FieldPt) pts.get( i + 1 ) );
            }

            Point2D pCurr = getVectorTipLocation( (FieldPt) fieldPt );
            double x3 = pCurr.getX();
            double y3 = pCurr.getY();
            double d1 = pCurr.distance( pPrev );
            double alpha = Math.atan2( pNext.getY() - pPrev.getY(), pNext.getX() - pPrev.getX() );
            double x2 = pCurr.getX() - ( d1 / 3 ) * Math.cos( alpha );
            double y2 = pCurr.getY() - ( d1 / 3 ) * Math.sin( alpha );
            double x1 = 0;
            double y1 = 0;
            if ( i > 2 ) {
                Point2D pPrevPrev = getVectorTipLocation( (FieldPt) pts.get( i - 2 ) );
                double beta = Math.atan2( pCurr.getY() - pPrevPrev.getY(), pCurr.getX() - pPrevPrev.getX() );
                x1 = pPrev.getX() + ( d1 / 3 ) * Math.cos( beta );
                y1 = pPrev.getY() + ( d1 / 3 ) * Math.sin( beta );
            }
            else {
                // If there isn't a field point previous to the previous point, come up with something
                // plausible for the control point
                double beta = Math.atan2( ( pCurr.getY() - pPrev.getY() ) * 2, pCurr.getX() - pPrev.getX() );
                x1 = pPrev.getX() + ( d1 / 3 ) * Math.cos( beta );
                y1 = pPrev.getY() + ( d1 / 3 ) * Math.sin( beta );
            }

            curve.curveTo( (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3 );
        }
        return curve;
    }

    //    ArrayList controlPoints = new ArrayList();
    //    ArrayList lines = new ArrayList();

    /**
     * @param pts
     * @return
     */
    private GeneralPath createCurves( ArrayList pts ) {

        // We modify the amplitude of the curve because it is supposed to connect the heads of the vectors on
        // the x axis that show the field strength, and those vectors are centered on the x axis. Their tails are
        // not on the axis
        double curveAmplitudeOffset = 1 - EmfConfig.SINGLE_VECTOR_ROW_OFFSET;
        FieldPt orig = (FieldPt) pts.get( curveStartingIdx );
        int xSign = MathUtil.getSign( orig.getX() - transmittingElectronOrigin.getX() );
        DoubleGeneralPath curve = new DoubleGeneralPath( orig.getX(), orig.getY() + orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() ) * curveAmplitudeOffset );
        double yLast = orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() * curveAmplitudeOffset );
        double yCurr = yLast;
        double xLimit = ( (FieldPt) pts.get( pts.size() - 1 ) ).getX();
        Point2D fieldPt = new Point2D.Double();
        for ( double x = orig.getX(); xSign > 0 ? x < xLimit : x > xLimit; x += 10 * xSign ) {
            fieldPt.setLocation( new Point2D.Double( Math.abs( x ), transmittingElectronOrigin.getY() ) );
            Vector2D field = sourceElectron.getDynamicFieldAt( fieldPt );
            yCurr = field.getMagnitude() * MathUtil.getSign( field.getY() );
            //            if( yCurr != yLast ) {
            curve.lineTo( x, transmittingElectronOrigin.getY() + yCurr * curveAmplitudeOffset );
            yLast = yCurr;
            //            }
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
