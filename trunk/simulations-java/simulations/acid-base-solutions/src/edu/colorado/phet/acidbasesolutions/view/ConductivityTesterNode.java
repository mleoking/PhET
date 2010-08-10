/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

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
    private static final Stroke WIRE_STROKE = new BasicStroke( 2f );
    
    // positive wire properties
    private static final Color POSITIVE_WIRE_COLOR = POSITIVE_PROBE_FILL_COLOR;
    
    // negative wire properties
    private static final Color NEGATIVE_WIRE_COLOR = NEGATIVE_PROBE_FILL_COLOR;
    
    private final ConductivityTester tester;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    private final WireNode positiveWireNode, negativeWireNode;
    private final ValueNode valueNode;
    private final PImage batteryImageNode;
    private final PImage bulbImageNode;
    private static final double COMPONENT_WIRE_LENGTH = 50;
    private static final double FRACTION_WIRE_ATTACH_ON_BULB = 0.12;

    public ConductivityTesterNode( final ConductivityTester tester ) {
        
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
                updateValue();
            }
        });
        
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
                //TODO map y from view to model coordinate frame
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
                //TODO map y from view to model coordinate frame
                tester.setNegativeProbeLocation( tester.getNegativeProbeLocationReference().getX(), y );
            }
        } );
        
        // positive wire
        positiveWireNode = new WireNode.Right( POSITIVE_WIRE_COLOR );

        // negative wire
        negativeWireNode = new WireNode.Left( NEGATIVE_WIRE_COLOR );
        
        //XXX circuit body
        batteryImageNode = new PImage( ABSImages.BATTERY );
        
        bulbImageNode = new PImage(ABSImages.LIGHT_BULB);
        bulbImageNode.setScale( 0.4 );
        bulbImageNode.setOffset( -bulbImageNode.getFullBoundsReference().getWidth()/2, -bulbImageNode.getFullBoundsReference().getHeight() );
        
        batteryImageNode.setOffset( bulbImageNode.getFullBoundsReference().getCenterX()+COMPONENT_WIRE_LENGTH, bulbImageNode.getFullBounds().getMaxY()-batteryImageNode.getFullBounds().getHeight()/2);
        
        // value
        valueNode = new ValueNode();
        double x = batteryImageNode.getFullBoundsReference().getMinX();
        double y = batteryImageNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight() - 2;
        valueNode.setOffset( x, y );
        
        // rendering order
        addChild( positiveWireNode );
        addChild( negativeWireNode );
        addChild( positiveProbeNode );
        addChild( negativeProbeNode );
        addChild( bulbImageNode );
        addChild( batteryImageNode );
        addChild( valueNode );
        
        // layout 
        //XXX mvt needed here?
        setOffset( tester.getLocationReference() );
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
        
        setVisible( tester.isVisible() );
    }
    
    private void updatePositiveProbeLocation() {
        
        // probe
        double x = tester.getPositiveProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getPositiveProbeLocationReference().getY() - tester.getLocationReference().getY();
        positiveProbeNode.setOffset( x, y );
        
        // wire
        Point2D componentConnectionPoint = new Point2D.Double( batteryImageNode.getFullBoundsReference().getMaxX(), batteryImageNode.getFullBoundsReference().getCenterY() );
        Point2D probeConnectionPoint = new Point2D.Double( x, y - tester.getProbeSizeReference().getHeight() );
        positiveWireNode.setEndPoints( componentConnectionPoint, probeConnectionPoint );
    }
    
    private void updateNegativeProbeLocation() {
        
        // probe
        double x = tester.getNegativeProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getNegativeProbeLocationReference().getY() - tester.getLocationReference().getY();
        negativeProbeNode.setOffset( x, y );
        
        // wire
        Point2D componentConnectionPoint = new Point2D.Double( bulbImageNode.getFullBoundsReference().getCenterX(), bulbImageNode.getFullBoundsReference().getMaxY()- bulbImageNode.getFullBoundsReference().getHeight()*FRACTION_WIRE_ATTACH_ON_BULB);
        Point2D probeConnectionPoint = new Point2D.Double( x, y - tester.getProbeSizeReference().getHeight() );
        negativeWireNode.setEndPoints( componentConnectionPoint, probeConnectionPoint );
    }
    
    private void updateValue() {
        valueNode.setValue( tester.getBrightness() );
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
    
    private static abstract class WireNode extends PPath {
        private final int CONTROL_POINT_DY = 50;
        private static final int CONTROL_POINT_DX = 50;

        public WireNode( Color color ) {
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints(Point2D componentConnectionPoint, Point2D probeConnectionPoint) {
            Point2D.Double ctrl1 = new Point2D.Double(componentConnectionPoint.getX() + getControlPointDX(), componentConnectionPoint.getY());
            Point2D.Double ctrl2 = new Point2D.Double(probeConnectionPoint.getX(), probeConnectionPoint.getY() + getControlPointDY());
            setPathTo(new CubicCurve2D.Double(componentConnectionPoint.getX(), componentConnectionPoint.getY(),
                    ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(),
                    probeConnectionPoint.getX(), probeConnectionPoint.getY()));
        }

        protected abstract double getControlPointDX();

        public int getControlPointDY(){
            return -CONTROL_POINT_DY;
        }

        private static class Right extends WireNode{

            public Right(Color color) {
                super(color);
            }

            @Override
            protected double getControlPointDX() {
                return CONTROL_POINT_DX;
            }
        }
        private static class Left extends WireNode{
            public Left(Color color) {
                super(color);
            }

            @Override
            protected double getControlPointDX() {
                return -CONTROL_POINT_DX;
            }
        }
    }
    
    private static class ValueNode extends PText {
        
        public ValueNode() {
            this( 0 );
        }
        
        public ValueNode( double brightness ) {
            setTextPaint( Color.RED );
            setFont( new PhetFont( 20 ) );
            setValue( brightness );
        }
        
        public void setValue( double brightness ) {
            setText( "brightness=" + String.valueOf( brightness ) );
        }
    }
}
