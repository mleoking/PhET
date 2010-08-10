/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSResources;
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


public class ConductivityTesterNode extends PhetPNode {
    
    // probe properties
    private static final Color POSITIVE_PROBE_FILL_COLOR = Color.RED;
    private static final Color NEGATIVE_PROBE_FILL_COLOR = Color.BLACK;
    private static final Color PROBE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PROBE_STROKE = new BasicStroke( 1f );
    private static final String POSITIVE_PROBE_LABEL = "+"; //XXX i18n
    private static final String NEGATIVE_PROBE_LABEL = "-"; //XXX i18n
    private static final Color POSITIVE_PROBE_LABEL_COLOR = Color.WHITE;
    private static final Color NEGATIVE_PROBE_LABEL_COLOR = Color.WHITE;
    private static final Font PROBE_LABEL_FONT = new PhetFont( Font.BOLD, 24 );
    
    // wire properties
    private static final Color WIRE_COLOR = Color.BLACK;
    private static final Stroke WIRE_STROKE = new BasicStroke( 2f );

    private final ConductivityTester tester;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    private final WireNode positiveWireNode, negativeWireNode;
    private final ValueNode valueNode;
    private final PImage imageNode;
    
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
        addChild( positiveProbeNode );
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
        addChild( negativeProbeNode );
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
        positiveWireNode = new WireNode();
        addChild( positiveWireNode );

        // negative wire
        negativeWireNode = new WireNode();
        addChild( negativeWireNode );
        
        //XXX circuit body
        imageNode = new PImage( ABSResources.getBufferedImage( "uncleMalley.png" ) ); //XXX
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, 0 );
        
        // value
        valueNode = new ValueNode();
        addChild( valueNode );
        double x = imageNode.getFullBoundsReference().getMinX();
        double y = imageNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight() - 2;
        valueNode.setOffset( x, y );
        
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
        Point2D p1 = new Point2D.Double( -imageNode.getFullBoundsReference().getWidth() / 2, imageNode.getFullBoundsReference().getHeight() / 2 );
        Point2D p2 = new Point2D.Double( x, y - tester.getProbeSizeReference().getHeight() );
        positiveWireNode.setEndPoints( p1, p2 );
    }
    
    private void updateNegativeProbeLocation() {
        
        // probe
        double x = tester.getNegativeProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getNegativeProbeLocationReference().getY() - tester.getLocationReference().getY();
        negativeProbeNode.setOffset( x, y );
        
        // wire
        Point2D p1 = new Point2D.Double( imageNode.getFullBoundsReference().getWidth() / 2, imageNode.getFullBoundsReference().getHeight() / 2 );
        Point2D p2 = new Point2D.Double( x, y - tester.getProbeSizeReference().getHeight() );
        negativeWireNode.setEndPoints( p1, p2 );
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
    
    private static class WireNode extends PPath {
        
        private final Line2D line;
        
        public WireNode() {
            this.line = new Line2D.Double();
            setStroke( WIRE_STROKE );
            setStrokePaint( WIRE_COLOR );
        }
        
        public void setEndPoints( Point2D p1, Point2D p2 ) {
            line.setLine( p1, p2 );
            setPathTo( line );
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
