/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Test app for fine tuning behavior of the wire that connects the field detector probe to the body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDetectorWire extends JFrame {
    
    private static final double YAW = Math.toRadians( 45 ); // rotation about the vertical axis, creates pseudo-3D perspective
    
    // wire is a cubic curve, these are the control point deltas
    private static final double WIRE_CONTROL_POINT_DX = -25;
    private static final double WIRE_CONTROL_POINT_DY = 100;
    
    // field detector body, simplified
    private static class BodyNode extends PPath {
        public BodyNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, 200, 100 ) );
            setPaint( Color.BLACK );
        }
    }
    
    // field detector probe, rotated so that it's aligned with the pseudo-3D perspective
    private static class ProbeNode extends PhetPNode {
        public ProbeNode() {
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = -( 0.078 * imageNode.getFullBoundsReference().getHeight() ); // multiplier is dependent on where crosshairs appear in image file
            imageNode.setOffset( x, y );
            imageNode.scale( 0.65 );
            imageNode.rotate( YAW );
        }
    }
    
    // wire that connects the body and probe
    private static class WireNode extends PPath {
        
        private final BodyNode bodyNode;
        private final ProbeNode probeNode;
        
        public WireNode( BodyNode bodyNode, ProbeNode probeNode ) {
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( Color.RED );
            
            this.bodyNode = bodyNode;
            this.probeNode = probeNode;
            
            // update wire when body or probe moves
            {
                PropertyChangeListener fullBoundsListener = new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent event ) {
                        if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                            update();
                        }
                    }
                };
                bodyNode.addPropertyChangeListener( fullBoundsListener );
                probeNode.addPropertyChangeListener( fullBoundsListener );
            }
        }
        
        private void update() {
            // connect to left center of body
            double x = bodyNode.getFullBoundsReference().getMinX();
            double y = bodyNode.getFullBoundsReference().getCenterY();
            Point2D pBody = new Point2D.Double( x, y );
            // connect to end of probe
            x = probeNode.getFullBoundsReference().getCenterX(); //XXX does not account for probe rotation
            y = probeNode.getFullBoundsReference().getMaxY(); //XXX does not account for probe rotation
            Point2D pProbe = new Point2D.Double( x, y );
            // control points 
            Point2D ctrl1 = new Point2D.Double( pBody.getX() + WIRE_CONTROL_POINT_DX, pBody.getY() );
            Point2D ctrl2 = new Point2D.Double( pProbe.getX(), pProbe.getY() + WIRE_CONTROL_POINT_DY );
            // path
            setPathTo( new CubicCurve2D.Double( pBody.getX(), pBody.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), pProbe.getX(), pProbe.getY() ) );
        }
    }
    
    private static class MyCanvas extends PhetPCanvas {
        
        public MyCanvas() {
            setPreferredSize( new Dimension( 700, 500 ) );
            
            BodyNode bodyNode = new BodyNode() {{
                addInputEventListener( new PDragEventHandler() );
                addInputEventListener( new CursorHandler() );
            }};
            
            ProbeNode probeNode = new ProbeNode() {{
                addInputEventListener( new PDragEventHandler() );
                addInputEventListener( new CursorHandler() );
            }};
            
            WireNode wireNode = new WireNode( bodyNode, probeNode );

            // rendering order
            {
                getLayer().addChild( bodyNode );
                getLayer().addChild( wireNode );
                getLayer().addChild( probeNode );
            }
            
            // layout
            {
                bodyNode.setOffset( 300, 250 );
                probeNode.setOffset( 200, 250 );
            }
        }
    }
    
    public TestDetectorWire() {
        super( TestDetectorWire.class.getName() );
        setContentPane( new MyCanvas() );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDetectorWire();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
