// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes.conductivitytester;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the conductivity tester.
 * A simple circuit with a battery and a light bulb, and a probe at each end of the circuit.
 * When the probes are inserted in the solution, the circuit is completed, and the light bulb
 * lights up based on the tester's brightness value.
 * Origin is at the bottom-center of the light bulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConductivityTesterNode extends PhetPNode {

    //Strings to be shown on the probes
    public static final String PLUS = "+";
    public static final String MINUS = "-";

    //Images used here
    public static final BufferedImage BATTERY = getBufferedImage( "battery.png" );
    public static final BufferedImage LIGHT_BULB_BASE = getBufferedImage( "lightBulbBase.png" );
    public static final BufferedImage LIGHT_BULB_GLASS = getBufferedImage( "lightBulbGlass.png" );
    public static final BufferedImage LIGHT_BULB_GLASS_MASK = getBufferedImage( "lightBulbGlassMask.png" );

    //Utility method for loading the images used in this tester node
    private static BufferedImage getBufferedImage( String image ) {
        return new PhetResources( "piccolo-phet" ).getImage( image );
    }

    // light bulb properties
    private static final double PERCENT_LIGHT_BULB_ATTACHMENT = 0.12; // percent of light bulb's full height, from bottom of bulb, determines where to attach the probe wire
    private static final LinearFunction BRIGHTNESS_TO_ALPHA_FUNCTION = new LinearFunction( 0, 1, 0.85, 1 ); // alpha of the bulb
    private static final LinearFunction BRIGHTNESS_TO_INTENSITY_FUNCTION = new LinearFunction( 0, 1, 0, 1 ); // intensity of the light rays

    // probe properties
    private static final Color PROBE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PROBE_STROKE = new BasicStroke( 1f );
    private static final Font PROBE_LABEL_FONT = new PhetFont( Font.BOLD, 24 );

    private static final String POSITIVE_PROBE_LABEL = PLUS;
    private static final Color DEFAULT_POSITIVE_PROBE_LABEL_COLOR = Color.WHITE;
    private final Color positiveProbeLabelColor;

    private static final String NEGATIVE_PROBE_LABEL = MINUS;
    private static final Color DEFAULT_NEGATIVE_PROBE_LABEL_COLOR = Color.WHITE;
    private final Color negativeProbeLabelColor;

    // wire properties
    private static final double BULB_TO_BATTERY_WIRE_LENGTH = 50;
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
    private static final int POSITIVE_WIRE_CONTROL_POINT_DX = 25;
    private static final int POSITIVE_WIRE_CONTROL_POINT_DY = -100;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DX = -POSITIVE_WIRE_CONTROL_POINT_DX;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DY = POSITIVE_WIRE_CONTROL_POINT_DY;

    private final ModelViewTransform transform;
    private final IConductivityTester tester;
    private final Color connectorWireColor;
    private final Color positiveProbeFillColor, negativeProbeFillColor;
    private final Color positiveWireColor, negativeWireColor;

    protected final LightBulbNode lightBulbNode;//Protected so that subclasses can add listeners if necessary, such as in Sugar and Salt Solutions
    private final LightRaysNode lightRaysNode;
    protected final BatteryNode batteryNode;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    private final CubicWireNode positiveWireNode, negativeWireNode;
    private final ValueNode valueNode;

    // Convenience constructor, uses the same color for all wires.
    public ConductivityTesterNode( final IConductivityTester tester, ModelViewTransform transform, Color wireColor, Color positiveProbeFillColor, Color negativeProbeFillColor, boolean dev ) {
        this( tester, transform, wireColor, wireColor, wireColor, positiveProbeFillColor, negativeProbeFillColor, DEFAULT_NEGATIVE_PROBE_LABEL_COLOR, DEFAULT_POSITIVE_PROBE_LABEL_COLOR, dev );
    }

    public ConductivityTesterNode( final IConductivityTester tester, final ModelViewTransform transform, Color positiveWireColor, Color negativeWireColor, Color connectorWireColor,
                                   Color positiveProbeFillColor, Color negativeProbeFillColor, Color negativeProbeLabelColor, Color positiveProbeLabelColor, boolean dev ) {
        this.transform = transform;
        this.tester = tester;
        this.positiveWireColor = positiveWireColor;
        this.negativeWireColor = negativeWireColor;
        this.connectorWireColor = connectorWireColor;
        this.positiveProbeFillColor = positiveProbeFillColor;
        this.negativeProbeFillColor = negativeProbeFillColor;
        this.negativeProbeLabelColor = negativeProbeLabelColor;
        this.positiveProbeLabelColor = positiveProbeFillColor;

        // light bulb
        lightBulbNode = new LightBulbNode();
        lightBulbNode.setScale( 0.6 );

        // light rays
        double lightBulbRadius = lightBulbNode.getFullBoundsReference().getWidth() / 2;
        lightRaysNode = new LightRaysNode( lightBulbRadius );

        // battery
        batteryNode = new BatteryNode();

        // wire that connects the light bulb to the battery
        StraightWireNode connectorWireNode = new StraightWireNode( this.connectorWireColor );
        Point2D lightBulbConnectionPoint = new Point2D.Double( 0, 0 );
        Point2D batteryConnectionPoint = new Point2D.Double( 0, BULB_TO_BATTERY_WIRE_LENGTH );
        connectorWireNode.setEndPoints( lightBulbConnectionPoint, batteryConnectionPoint );

        // positive probe
        positiveProbeNode = new ProbeNode( transform.modelToViewSize( tester.getProbeSizeReference() ), this.positiveProbeFillColor, POSITIVE_PROBE_LABEL, positiveProbeLabelColor );
        positiveProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        positiveProbeNode.addInputEventListener(
                new ProbeDragHandler( transform, positiveProbeNode,
                                      new Function0<Point2D>() {
                                          public Point2D apply() {
                                              return tester.getPositiveProbeLocationReference();
                                          }
                                      },
                                      new VoidFunction1<Point2D>() {
                                          public void apply( Point2D point2D ) {
                                              tester.setPositiveProbeLocation( point2D.getX(), point2D.getY() );
                                          }
                                      }
                ) );

        // negative probe
        negativeProbeNode = new ProbeNode( transform.modelToViewSize( tester.getProbeSizeReference() ), this.negativeProbeFillColor, NEGATIVE_PROBE_LABEL, negativeProbeLabelColor );
        negativeProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        negativeProbeNode.addInputEventListener(
                new ProbeDragHandler( transform, negativeProbeNode,
                                      new Function0<Point2D>() {
                                          public Point2D apply() {
                                              return tester.getNegativeProbeLocationReference();
                                          }
                                      },
                                      new VoidFunction1<Point2D>() {
                                          public void apply( Point2D point2D ) {
                                              tester.setNegativeProbeLocation( point2D.getX(), point2D.getY() );
                                          }
                                      }
                ) );

        // positive wire
        positiveWireNode = new CubicWireNode( this.positiveWireColor, POSITIVE_WIRE_CONTROL_POINT_DX, POSITIVE_WIRE_CONTROL_POINT_DY );

        // negative wire
        negativeWireNode = new CubicWireNode( this.negativeWireColor, NEGATIVE_WIRE_CONTROL_POINT_DX, NEGATIVE_WIRE_CONTROL_POINT_DY );

        // brightness value, for debugging
        valueNode = new ValueNode();

        // rendering order
        addChild( lightRaysNode );
        addChild( positiveWireNode );
        addChild( negativeWireNode );
        addChild( positiveProbeNode );
        addChild( negativeProbeNode );
        addChild( connectorWireNode );
        addChild( lightBulbNode );
        addChild( batteryNode );
        if ( dev ) {
            addChild( valueNode );
        }

        // layout 
        double x = 0;
        double y = 0;
        lightBulbNode.setOffset( x, y );
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = lightBulbNode.getFullBoundsReference().getMinY() + lightBulbRadius;
        lightRaysNode.setOffset( x, y );
        x = lightBulbNode.getFullBoundsReference().getCenterX() + BULB_TO_BATTERY_WIRE_LENGTH;
        y = lightBulbNode.getFullBounds().getMaxY();
        batteryNode.setOffset( x, y );
        connectorWireNode.setOffset( lightBulbNode.getFullBoundsReference().getCenterX(), lightBulbNode.getFullBoundsReference().getMaxY() );
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = batteryNode.getFullBoundsReference().getMaxY() + 3;
        valueNode.setOffset( x, y );

        // location & visibility
        setOffset( transform.modelToView( tester.getLocationReference() ) );
        setVisible( tester.isVisible() );

        // Listeners
        tester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {

            public void positiveProbeLocationChanged() {
                updatePositiveProbeLocation();
            }

            public void negativeProbeLocationChanged() {
                updateNegativeProbeLocation();
            }

            public void locationChanged() {
                updateLocation();
            }

            public void brightnessChanged() {
                updateBrightness();
            }
        } );

        //Synchronize with any state in case it was non-default
        updateBrightness();
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
    }

    //Update the location of the light bulb and battery of the conductivity tester
    private void updateLocation() {
        setOffset( tester.getLocationReference() );

        //After updating the entire offset, we have to update the probes again
        //Since they have to subtract out the parent offset (i.e. their locations are absolute, not
        //Relative to the parent
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
    }

    private void updatePositiveProbeLocation() {
        // probe
        Point2D probeLocation = new Point2D.Double( tester.getPositiveProbeLocationReference().getX(),
                                                    tester.getPositiveProbeLocationReference().getY() );
        Point2D viewLocation = transform.modelToView( probeLocation );
        positiveProbeNode.setOffset( viewLocation.getX() - getOffset().getX(), viewLocation.getY() - getOffset().getY() );

        // wire
        double x = batteryNode.getFullBoundsReference().getMaxX();
        double y = batteryNode.getFullBoundsReference().getCenterY();
        Point2D batteryConnectionPoint = new Point2D.Double( x, y );
        x = positiveProbeNode.getFullBoundsReference().getCenterX();
        y = positiveProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        positiveWireNode.setEndPoints( batteryConnectionPoint, probeConnectionPoint );
    }

    private void updateNegativeProbeLocation() {
        // probe
        Point2D probeLocation = new Point2D.Double( tester.getNegativeProbeLocationReference().getX(),
                                                    tester.getNegativeProbeLocationReference().getY() );
        Point2D viewLocation = transform.modelToView( probeLocation );
        negativeProbeNode.setOffset( viewLocation.getX() - getOffset().getX(), viewLocation.getY() - getOffset().getY() );

        // wire
        double x = lightBulbNode.getFullBoundsReference().getCenterX();
        double y = lightBulbNode.getFullBoundsReference().getMaxY() - ( lightBulbNode.getFullBoundsReference().getHeight() * PERCENT_LIGHT_BULB_ATTACHMENT );
        Point2D componentConnectionPoint = new Point2D.Double( x, y );
        x = negativeProbeNode.getFullBoundsReference().getCenterX();
        y = negativeProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        negativeWireNode.setEndPoints( componentConnectionPoint, probeConnectionPoint );
    }

    private void updateBrightness() {
        lightBulbNode.setGlassTransparency( (float) BRIGHTNESS_TO_ALPHA_FUNCTION.evaluate( tester.getBrightness() ) );
        lightRaysNode.setIntensity( BRIGHTNESS_TO_INTENSITY_FUNCTION.evaluate( tester.getBrightness() ) );
        valueNode.setValue( tester.getBrightness() );
    }

    /*
    * Light bulb, origin at bottom center.  Protected so that subclasses can listen to drag events on the light bulb, such as in Sugar and Salt Solutions.
    */
    protected static class LightBulbNode extends PComposite {

        private final PImage glassNode;

        public LightBulbNode() {

            PNode baseNode = new PImage( LIGHT_BULB_BASE );
            glassNode = new PImage( LIGHT_BULB_GLASS );
            PNode maskNode = new PImage( LIGHT_BULB_GLASS_MASK );

            // rendering order
            addChild( maskNode );
            addChild( glassNode );
            addChild( baseNode );

            // layout
            double x = -baseNode.getFullBoundsReference().getWidth() / 2;
            double y = -baseNode.getFullBoundsReference().getHeight();
            baseNode.setOffset( x, y );
            x = baseNode.getFullBoundsReference().getCenterX() - ( glassNode.getFullBoundsReference().getWidth() / 2 );
            y = baseNode.getFullBoundsReference().getMinY() - glassNode.getFullBoundsReference().getHeight();
            glassNode.setOffset( x, y );
            maskNode.setOffset( glassNode.getOffset() );
        }

        public void setGlassTransparency( float transparency ) {
            glassNode.setTransparency( transparency );
        }
    }

    /*
    * Battery, origin at left center.
    */
    protected static class BatteryNode extends PComposite {
        public BatteryNode() {
            PImage imageNode = new PImage( BATTERY );
            addChild( imageNode );
            double x = 0;
            double y = -imageNode.getFullBoundsReference().getHeight() / 2;
            imageNode.setOffset( x, y );
        }
    }

    /*
    * Probe, origin at bottom center.
    */
    private static class ProbeNode extends PhetPNode {

        public ProbeNode( Dimension2D size, Color color, String label, Color labelColor ) {

            PPath pathNode = new PPath( new Rectangle2D.Double( -size.getWidth() / 2, -size.getHeight(), size.getWidth(), Math.abs( size.getHeight() ) ) );
            pathNode.setStroke( PROBE_STROKE );
            pathNode.setStrokePaint( PROBE_STROKE_COLOR );
            pathNode.setPaint( color );
            addChild( pathNode );

            PText labelNode = new PText( label );
            labelNode.setTextPaint( labelColor );
            labelNode.setFont( PROBE_LABEL_FONT );
            addChild( labelNode );

            double x = pathNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
            double y = pathNode.getFullBoundsReference().getMaxY() - labelNode.getFullBoundsReference().getHeight() - 3;
            labelNode.setOffset( x, y );
        }
    }

    /*
    * Wire that is drawn as a straight line.
    */
    private static class StraightWireNode extends PPath {

        public StraightWireNode( Color color ) {
            super();
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            setPathTo( new Line2D.Double( 0, 0, BULB_TO_BATTERY_WIRE_LENGTH, 0 ) );
        }
    }

    /*
     * Wire that is drawn using a cubic parametric curve.
     */
    private static class CubicWireNode extends PPath {

        private final double controlPointDx, controlPointDy;

        public CubicWireNode( Color color, double controlPointDx, double controlPointDy ) {
            this.controlPointDx = controlPointDx;
            this.controlPointDy = controlPointDy;
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            Point2D ctrl1 = new Point2D.Double( startPoint.getX() + controlPointDx, startPoint.getY() );
            Point2D ctrl2 = new Point2D.Double( endPoint.getX(), endPoint.getY() + controlPointDy );
            setPathTo( new CubicCurve2D.Double( startPoint.getX(), startPoint.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), endPoint.getX(), endPoint.getY() ) );
        }
    }

    /*
    * Displays the model's brightness value, for debugging.
    */
    private static class ValueNode extends PText {

        private static final DecimalFormat FORMAT = new DecimalFormat( "0.000" );

        public ValueNode() {
            this( 0 );
        }

        public ValueNode( double brightness ) {
            setTextPaint( Color.RED );
            setFont( new PhetFont( 16 ) );
            setValue( brightness );
        }

        public void setValue( double brightness ) {
            setText( "brightness=" + FORMAT.format( brightness ) ); // no i18n needed, this is a dev feature
        }
    }

    // Drag handler for probes, handles model-view transform, constrains dragging to vertical.
    private static class ProbeDragHandler extends PBasicInputEventHandler {

        private final ProbeNode probeNode;
        private final ModelViewTransform transform;
        private final Function0<Point2D> getModelLocation;
        private final VoidFunction1<Point2D> setModelLocation;

        private Point2D.Double relativeGrabPoint; // where the mouse grabbed relative to the probe, in view coordinates

        ProbeDragHandler( ModelViewTransform transform, ProbeNode probeNode, Function0<Point2D> getModelLocation, VoidFunction1<Point2D> setModelLocation ) {
            this.transform = transform;
            this.probeNode = probeNode;
            this.getModelLocation = getModelLocation;
            this.setModelLocation = setModelLocation;
        }

        // Set the relative grab point when the mouses is pressed.
        @Override public void mousePressed( PInputEvent event ) {
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D pProbe = transform.modelToView( getModelLocation.apply() );
            relativeGrabPoint = new Point2D.Double( pMouse.getX() - pProbe.getX(), pMouse.getY() - pProbe.getY() );
        }

        // Forget the relative grab point when the mouse is released.
        @Override public void mouseReleased( PInputEvent event ) {
            relativeGrabPoint = null;
        }

        // Update the model as the mouse is dragged.
        @Override public void mouseDragged( PInputEvent event ) {
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D pProbe = transform.viewToModel( pMouse.getX() - relativeGrabPoint.getX(), pMouse.getY() - relativeGrabPoint.getY() );
            setModelLocation.apply( new Point2D.Double( getModelLocation.apply().getX(), pProbe.getY() ) );
        }
    }
}