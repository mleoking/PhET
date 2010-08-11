/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the conductivity tester.
 * A simple circuit with a battery and a light bulb, and a probe at each end of the circuit.
 * When the probes are inserted in the solution, the circuit is completed, and the light bulb 
 * lights up based on the tester's brightness value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConductivityTesterNode extends PhetPNode {
    

    // general probe properties
    private static final Color PROBE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PROBE_STROKE = new BasicStroke( 1f );
    private static final Font PROBE_LABEL_FONT = new PhetFont( Font.BOLD, 24 );
    
    // positive probe properties
    private static final Color POSITIVE_PROBE_FILL_COLOR = Color.RED;
    private static final String POSITIVE_PROBE_LABEL = ABSSymbols.PLUS;
    private static final Color POSITIVE_PROBE_LABEL_COLOR = Color.WHITE;
    
    // negative probe properties
    private static final Color NEGATIVE_PROBE_FILL_COLOR = Color.BLACK;
    private static final String NEGATIVE_PROBE_LABEL = ABSSymbols.MINUS;
    private static final Color NEGATIVE_PROBE_LABEL_COLOR = Color.WHITE;
    
    // general wire properties
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
    
    // positive wire properties
    private static final Color POSITIVE_WIRE_COLOR = Color.BLACK;
    private static final int POSITIVE_WIRE_CONTROL_POINT_DX = 50;
    private static final int POSITIVE_WIRE_CONTROL_POINT_DY = -50;
    
    // negative wire properties
    private static final Color NEGATIVE_WIRE_COLOR = Color.BLACK;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DX = -POSITIVE_WIRE_CONTROL_POINT_DX;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DY = POSITIVE_WIRE_CONTROL_POINT_DY;
    
    // connector wire, connects bulb and battery
    private static final Color CONNECTOR_WIRE_COLOR = Color.BLACK;
    private static final double CONNECTOR_WIRE_LENGTH = 50;
    
    // light bulb
    private static final double PERCENT_LIGHT_BULB_ATTACHMENT = 0.12; // percent of light bulb's full height, from bottom of bulb, determines where to attach the probe wire
    private static final LinearFunction BRIGHTNESS_TO_ALPHA_FUNCTION = new LinearFunction( 0, 1, 0.85, 1 );
    
    private final ConductivityTester tester;

    private final LightBulbNode lightBulbNode;
    private final BatteryNode batteryNode;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    private final CubicWireNode positiveWireNode, negativeWireNode;
    private final ValueNode valueNode;
    
    public ConductivityTesterNode( final ConductivityTester tester, boolean dev ) {
        
        this.tester = tester;
        tester.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( tester.isVisible() );
            }
        });
        
        tester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {

            public void positiveProbeLocationChanged() {
                updatePositiveProbeLocation();
            }
            
            public void negativeProbeLocationChanged() {
                updateNegativeProbeLocation();
            }

            public void brightnessChanged() {
                updateBrightness();
            }
        });
        
        // light bulb
        lightBulbNode = new LightBulbNode();
        lightBulbNode.setScale( 0.4 ); //XXX scale image files
        
        // battery
        batteryNode = new BatteryNode();
        
        // wire that connects the light bulb to the battery
        StraightWireNode connectorWireNode = new StraightWireNode( CONNECTOR_WIRE_COLOR );
        Point2D lightBulbConnectionPoint = new Point2D.Double( 0, 0 );
        Point2D batteryConnectionPoint = new Point2D.Double( 0, CONNECTOR_WIRE_LENGTH );
        connectorWireNode.setEndPoints( lightBulbConnectionPoint, batteryConnectionPoint );
        
        // positive probe
        positiveProbeNode = new ProbeNode( tester.getProbeSizeReference(), POSITIVE_PROBE_FILL_COLOR, POSITIVE_PROBE_LABEL, POSITIVE_PROBE_LABEL_COLOR );
        positiveProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        positiveProbeNode.addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - tester.getPositiveProbeLocationReference().getY() + tester.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double y = pMouse.getY() - clickYOffset;
                y += tester.getLocationReference().getY();
                tester.setPositiveProbeLocation( tester.getPositiveProbeLocationReference().getX(), y );
            }
        } );

        // negative probe
        negativeProbeNode = new ProbeNode( tester.getProbeSizeReference(), NEGATIVE_PROBE_FILL_COLOR, NEGATIVE_PROBE_LABEL, NEGATIVE_PROBE_LABEL_COLOR );
        negativeProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        negativeProbeNode.addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - tester.getNegativeProbeLocationReference().getY()  + tester.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double y = pMouse.getY() + getYOffset() - clickYOffset;
                tester.setNegativeProbeLocation( tester.getNegativeProbeLocationReference().getX(), y );
            }
        } );
        
        // positive wire
        positiveWireNode = new CubicWireNode( POSITIVE_WIRE_COLOR, POSITIVE_WIRE_CONTROL_POINT_DX, POSITIVE_WIRE_CONTROL_POINT_DY );

        // negative wire
        negativeWireNode = new CubicWireNode( NEGATIVE_WIRE_COLOR, NEGATIVE_WIRE_CONTROL_POINT_DX, NEGATIVE_WIRE_CONTROL_POINT_DY );
        
        // brightness value, for debugging
        valueNode = new ValueNode();
        
        // rendering order
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
        x = lightBulbNode.getFullBoundsReference().getCenterX() + CONNECTOR_WIRE_LENGTH;
        y = lightBulbNode.getFullBounds().getMaxY();
        batteryNode.setOffset( x, y );
        connectorWireNode.setOffset( lightBulbNode.getFullBoundsReference().getCenterX(), lightBulbNode.getFullBoundsReference().getMaxY() );
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = batteryNode.getFullBoundsReference().getMaxY() + 3;
        valueNode.setOffset( x, y );
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
        
        setOffset( tester.getLocationReference() );
        setVisible( tester.isVisible() );
    }

    private void updatePositiveProbeLocation() {

        // probe
        double x = tester.getPositiveProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getPositiveProbeLocationReference().getY() - tester.getLocationReference().getY();
        positiveProbeNode.setOffset( x, y );

        // wire
        x = batteryNode.getFullBoundsReference().getMaxX();
        y = batteryNode.getFullBoundsReference().getCenterY();
        Point2D batteryConnectionPoint = new Point2D.Double( x, y );
        x = positiveProbeNode.getFullBoundsReference().getCenterX();
        y = positiveProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        positiveWireNode.setEndPoints( batteryConnectionPoint, probeConnectionPoint );
    }
    
    private void updateNegativeProbeLocation() {
        
        // probe
        double x = tester.getNegativeProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getNegativeProbeLocationReference().getY() - tester.getLocationReference().getY();
        negativeProbeNode.setOffset( x, y );

        // wire
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = lightBulbNode.getFullBoundsReference().getMaxY() - ( lightBulbNode.getFullBoundsReference().getHeight() * PERCENT_LIGHT_BULB_ATTACHMENT );
        Point2D componentConnectionPoint = new Point2D.Double( x, y );
        x = negativeProbeNode.getFullBoundsReference().getCenterX();
        y = negativeProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        negativeWireNode.setEndPoints( componentConnectionPoint, probeConnectionPoint );
    }
    
    private void updateBrightness() {
        lightBulbNode.setGlassTransparency( (float) BRIGHTNESS_TO_ALPHA_FUNCTION.evaluate( tester.getBrightness() ) );
        valueNode.setValue( tester.getBrightness() );
    }
    
    /*
     * Light bulb, origin at bottom center.
     */
    private static class LightBulbNode extends PComposite {
        
        private final PImage glassNode;
        
        public LightBulbNode() {
            
            PNode baseNode = new PImage( ABSImages.LIGHT_BULB_BASE );
            
            glassNode = new PImage( ABSImages.LIGHT_BULB_GLASS );
            
            PNode maskNode = new PImage( ABSImages.LIGHT_BULB_GLASS_MASK );
            
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
    private static class BatteryNode extends PComposite {
        public BatteryNode() {
            PImage imageNode = new PImage( ABSImages.BATTERY );
            addChild( imageNode );
            double x = 0;
            double y = -imageNode.getFullBoundsReference().getHeight() / 2;
            imageNode.setOffset( x, y );
        }
    }
    
    /*
     * Probes, origin at bottom center.
     */
    private static class ProbeNode extends PhetPNode {
        
        public ProbeNode( PDimension size, Color color, String label, Color labelColor ) {
            
            PPath pathNode = new PPath( new Rectangle2D.Double( -size.getWidth() / 2, -size.getHeight(), size.getWidth(), size.getHeight() ) );
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
    
    private interface IWire {

        public void setEndPoints( Point2D startPoint, Point2D endPoint );
    }
    
    /*
     * Wire that is drawn as a straight line.
     */
    private static class StraightWireNode extends PPath implements IWire {
        
        public StraightWireNode( Color color ) {
            super();
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }
        
        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            setPathTo( new Line2D.Double( 0, 0, CONNECTOR_WIRE_LENGTH, 0 ) );
        }
    }

    /*
     * Wire that is drawn using a cubic parametric curve.
     */
    private static class CubicWireNode extends PPath implements IWire {

        private final double controlPointDx, controlPointDy;

        public CubicWireNode( Color color, double controlPointDx, double controlPointDy ) {
            this.controlPointDx = controlPointDx;
            this.controlPointDy = controlPointDy;
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            Point2D.Double ctrl1 = new Point2D.Double( startPoint.getX() + controlPointDx, startPoint.getY() );
            Point2D.Double ctrl2 = new Point2D.Double( endPoint.getX(), endPoint.getY() + controlPointDy );
            setPathTo( new CubicCurve2D.Double( startPoint.getX(), startPoint.getY(), 
                    ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), 
                    endPoint.getX(), endPoint.getY() ) );
        }
    }
    
    /*
     * Displays the brightness value of the model, for debugging.
     */
    private static class ValueNode extends PText {
        
        public ValueNode() {
            this( 0 );
        }
        
        public ValueNode( double brightness ) {
            setTextPaint( Color.RED );
            setFont( new PhetFont( 16 ) );
            setValue( brightness );
        }
        
        public void setValue( double brightness ) {
            setText( "brightness=" + String.valueOf( brightness ) );
        }
    }
}
