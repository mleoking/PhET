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
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class FieldLatticeView implements Graphic, SimpleObserver {

    private static int curveStartingIdx = 1;

    // Note that transmittingElectronOrigin is the view transmittingElectronOrigin, NOT the transmittingElectronOrigin of the electron
    // emitting the field
    private Point transmittingElectronOrigin;
    private int latticeSpacingX;
    private int latticeSpacingY;
    private int numLatticePtsX;
    private int numLatticePtsY;
    private FieldPt[] latticePts;
    private ArrayList latticePtsNeg = new ArrayList();
    private ArrayList latticePtsPos = new ArrayList();
    private AffineTransform[] latticeTx;
    private Electron sourceElectron;
    private CubicSpline spline = new CubicSpline();
    private boolean autoscaleEnabled = false;
    private AffineTransform atx;

    private ImageObserver imgObserver = new ImageObserver() {
        public boolean imageUpdate( Image img, int infoflags,
                                    int x, int y, int width, int height ) {
            return false;
        }
    };
    private boolean displayStaticField;
    private boolean displayDynamicField;
    private int fieldSense = FORCE_ON_ELECTRON;
    private int fieldDisplayType;
    private int width;
    private ArrayList arrows = new ArrayList();
    private GeneralPath negPath;
    private GeneralPath posPath;
    // How far from the transmitting antenna the first field arrow is drawn
    private int firstArrowOffset = 25;

    public FieldLatticeView( Electron sourceElectron, Point origin,
                             int width, int height,
                             int latticeSpacingX, int latticeSpacingY ) {

        if( !( fieldSense == FORCE_ON_ELECTRON
               || fieldSense == ELECTRIC_FIELD ) ) {
            throw new RuntimeException( "Bad actual parameter: fieldSense " );
        }

        this.width = width;
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
    }

    public synchronized void paint( Graphics2D g2 ) {
        //        GraphicsUtil.setAntiAliasingOn( g2 );

        AffineTransform orgTx = g2.getTransform();
        g2.transform( atx );

        Color color = fieldSense == FORCE_ON_ELECTRON ? arrowRed : arrowGreen;

        if( fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
            g2.setColor( color );
            g2.setStroke( hollowArrowStroke );
            for( int i = 0; i < arrows.size(); i++ ) {
                Arrow arrow = (Arrow)arrows.get( i );
                g2.draw( arrow.getShape() );
            }
        }

        if( fieldDisplayType == EmfPanel.CURVE
            || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
            g2.setColor( curveColor );
            g2.setStroke( curveStroke );
            g2.draw( negPath );
            g2.draw( posPath );
        }

        if( fieldDisplayType == EmfPanel.FULL_FIELD ) {
            for( int i = 0; i < latticePts.length; i++ ) {
                Vector2D f = latticePts[i].field.scale( fieldSense );
                double l = f.getMagnitude();
                if( l > 3 ) {
                    double theta = f.getAngle();
                    Arrow arrow = new Arrow( new Point2D.Double( -l / 2, 0 ), new Point2D.Double( l / 2, 0 ),
                                             maxArrowHeadWidth,
                                             maxArrowHeadWidth, 3, 0.5, true );
                    AffineTransform orgTx2 = g2.getTransform();
                    AffineTransform tx = latticePts[i].tx;
                    tx.setToTranslation( latticePts[i].location.getX(), latticePts[i].location.getY() );
                    tx.rotate( theta );
                    g2.transform( tx );

                    g2.setColor( color );
                    g2.draw( arrow.getShape() );
                    // GraphicsUtil.setAlpha( g2, 0.5 );
                    g2.fill( arrow.getShape() );
                    GraphicsUtil.setAlpha( g2, 1 );
                    g2.setTransform( orgTx2 );
                }
            }
        }
        g2.setTransform( orgTx );
    }

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
            || fieldDisplayType == EmfPanel.CURVE_WITH_VECTORS ) {
            for( int i = 0; i < latticePtsNeg.size(); i++ ) {
                FieldPt fieldPt = (FieldPt)latticePtsNeg.get( i );
                evaluateFieldPt( fieldPt );
            }

            for( int i = 0; i < latticePtsPos.size(); i++ ) {
                FieldPt fieldPt = (FieldPt)latticePtsPos.get( i );
                evaluateFieldPt( fieldPt );
            }

            this.negPath = createCurves( latticePtsNeg );
            this.posPath = createCurves( latticePtsPos );
            arrows.clear();
            addArrows( arrows, latticePtsNeg );
            addArrows( arrows, latticePtsPos );
        }
    }

    private void addArrows( ArrayList arrows, ArrayList pts ) {
        // We do not draw an arrow for the first field point because it is very close
        // to the antenna (so that we get a nice steep line there )
        for( int i = 1; i < pts.size(); i++ ) {
            FieldPt fieldPt = (FieldPt)pts.get( i );
            int arrowDir = MathUtil.getSign( fieldPt.field.getY() ) * ( fieldSense == FORCE_ON_ELECTRON ? 1 : -1 );
            double magnitude = fieldPt.field.getMagnitude();
            if( magnitude > 0 ) {
                arrows.add( new Arrow( fieldPt.location,
                                       new Point2D.Double( fieldPt.getX(),
                                                           fieldPt.getY() + magnitude * arrowDir ),
                                       hollowArrowHeadWidth * 0.7, hollowArrowHeadWidth,
                                       hollowArrowWidth, 0.5, false ) );
            }
        }
    }

    private GeneralPath createCurves( ArrayList pts ) {

        FieldPt orig = (FieldPt)pts.get( curveStartingIdx );
        int xSign = MathUtil.getSign( orig.getX() - transmittingElectronOrigin.getX() );
        DoubleGeneralPath curve = new DoubleGeneralPath( orig.getX(),
                                                         orig.getY() + orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() ) );
        double yLast = orig.field.getMagnitude() * MathUtil.getSign( orig.field.getY() );
        double yCurr = yLast;
        double xLimit = ( (FieldPt)pts.get( pts.size() - 1 ) ).getX();
        for( double x = orig.getX();
             xSign > 0 ? x < xLimit : x > xLimit;
             x += 1 * xSign ) {
            Vector2D field = sourceElectron.getDynamicFieldAt( new Point2D.Double( Math.abs( x ),
                                                                                   transmittingElectronOrigin.getY() ) );
            yCurr = field.getMagnitude() * MathUtil.getSign( field.getY() );
            if( yCurr != yLast ) {
                curve.lineTo( x, transmittingElectronOrigin.getY() + yCurr );
                yLast = yCurr;
            }
        }
        curve.lineTo( xLimit, transmittingElectronOrigin.getY() + yCurr * xSign );
        return curve.getGeneralPath();
    }

    //    private float getAutoscaleFactor() {
    //        float maxMag = 0;
    //
    //        // If the movement strategy is sinusoidal, then get the max field at the transmittingElectronOrigin,
    //        // and use it as the scale factor. Note that the 5 in the expression is strictly
    //        // empirical. I don't know why it looks right
    //        if( SinusoidalMovement.class.isAssignableFrom( sourceElectron.getMovementStrategyType() ) ) {
    //            maxMag = sourceElectron.getMaxAccelerationAtLocation( transmittingElectronOrigin ).getLength() / 5;
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

    public void setTransform( AffineTransform atx ) {
        this.atx = atx;
    }



    //
    // Statics
    //
    public final static int maxArrowHeadWidth = 10;
    public final static Color curveColor = new Color( 200, 0, 0 );
    public final static Color arrowRed = new Color( 200, 0, 0 );
    public final static Color arrowGreen = new Color( 0, 100, 0 );
    private static Color arrowColor = new Color( 255, 48, 48 );
    private static int hollowArrowWidth = 10;
    private static int hollowArrowHeadWidth = 20;
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
