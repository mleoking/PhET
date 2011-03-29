// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
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
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Test app for fine tuning behavior of the wire that connects the field detector probe to the body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDetectorWire extends JFrame {

    private static final double YAW = Math.toRadians( 45 ); // rotation about the vertical axis, creates pseudo-3D perspective

    // wire is a cubic curve, these are the control point offsets
    private static final Point2D BODY_CONTROL_POINT_OFFSET = new Point2D.Double( -25, 0 );
    private static final Point2D PROBE_CONTROL_POINT_OFFSET = new Point2D.Double( -80, 100 );

    // field detector body, simplified.
    // origin at upper-left of bounding rectangle.
    private static class TestBodyNode extends PPath {
        public TestBodyNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, 200, 100 ) );
            setPaint( Color.BLACK );
        }
    }

    /*
     * Field detector probe, rotated so that it's aligned with the pseudo-3D perspective.
     * Origin at center of image crosshairs.
     * Connection point is precomputed because we'll be rotating this node.
     */
    private static class TestProbeNode extends PhetPNode {

        private final Point2D connectionOffset;

        public TestProbeNode() {
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            imageNode.scale( 0.65 ); // scale before setting offset!
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = -( 0.078 * imageNode.getFullBoundsReference().getHeight() ); // multiplier is dependent on where crosshairs appear in image file
            imageNode.setOffset( x, y );

            connectionOffset = new Point2D.Double( 0, imageNode.getFullBoundsReference().getHeight() + y ); // connect wire to bottom center
        }

        public Point2D getConnectionOffsetReference() {
            return connectionOffset;
        }
    }

    // wire that connects the body and probe
    private static class TestWireNode extends PPath {

        private final TestBodyNode bodyNode;
        private final TestProbeNode probeNode;

        public TestWireNode( TestBodyNode bodyNode, TestProbeNode probeNode ) {
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

            Point2D pBody = getBodyConnectionPoint();
            Point2D pProbe = getProbeConnectionPoint();

            // control points
            Point2D ctrl1 = new Point2D.Double( pBody.getX() + BODY_CONTROL_POINT_OFFSET.getX(), pBody.getY() + BODY_CONTROL_POINT_OFFSET.getY() );
            Point2D ctrl2 = new Point2D.Double( pProbe.getX() + PROBE_CONTROL_POINT_OFFSET.getX(), pProbe.getY() + PROBE_CONTROL_POINT_OFFSET.getY() );

            // path
            setPathTo( new CubicCurve2D.Double( pBody.getX(), pBody.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), pProbe.getX(), pProbe.getY() ) );
        }

        // connect to left center of body
        private Point2D getBodyConnectionPoint() {
            double x = bodyNode.getFullBoundsReference().getMinX();
            double y = bodyNode.getFullBoundsReference().getCenterY();
            return new Point2D.Double( x, y );
        }

        // connect to end of probe handle, account for probe rotation
        private Point2D getProbeConnectionPoint() {
            // unrotated connection point
            double x = probeNode.getXOffset() + probeNode.getConnectionOffsetReference().getX();
            double y = probeNode.getYOffset() + probeNode.getConnectionOffsetReference().getY();
            // rotate the connection point to match the probe's rotation
            AffineTransform t = AffineTransform.getRotateInstance( probeNode.getRotation(), probeNode.getXOffset(), probeNode.getYOffset() );
            return t.transform( new Point2D.Double( x, y ), null );
        }
    }

    // Adjust offset when a node or any of its children are dragged.
    private static class TestDragHandler extends PDragEventHandler {

        private final PNode dragNode;

        public TestDragHandler( PNode dragNode ) {
            this.dragNode = dragNode;
        }

        @Override
        protected void drag( final PInputEvent event ) {
            final PDimension d = event.getDeltaRelativeTo( dragNode );
            dragNode.localToParent( d );
            dragNode.setOffset( dragNode.getXOffset() + d.getWidth(), dragNode.getYOffset() + d.getHeight() );
        }
    }

    private static class TestCanvas extends PhetPCanvas {

        public TestCanvas() {
            setPreferredSize( new Dimension( 700, 500 ) );

            TestBodyNode bodyNode = new TestBodyNode() {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new TestDragHandler( this ) );
            }};

            final TestProbeNode probeNode = new TestProbeNode() {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new TestDragHandler( this ) );
            }};
            probeNode.rotate( YAW );

            TestWireNode wireNode = new TestWireNode( bodyNode, probeNode );

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
        setContentPane( new TestCanvas() );
        pack();
    }

    public static void main( String[] args ) {
        PDebug.debugBounds = true;
        JFrame frame = new TestDetectorWire();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
