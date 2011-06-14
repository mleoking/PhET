// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Wire that connects a probe to the body of a meter.
 * The wire is a cubic curve.
 */
public class ProbeWireNode extends PPath {

    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );

    private final PNode bodyNode, probeNode;
    private final Point2D bodyControlPointOffset, probeControlPointOffset;
    private final Point2D bodyConnectionOffset, probeConnectionOffset;

    /**
     * Constructor.
     *
     * @param bodyNode
     * @param probeNode
     * @param bodyControlPointOffset  cubic control point for the end of the wire that connects to the body
     * @param probeControlPointOffset cubic control point for the end of the wire that connects to the probe
     * @param bodyConnectionOffset    wire connection point on the body, relative to the body's origin
     * @param probeConnectionOffset   wire connection point on the probe, relative to the probe's origin
     * @param color                   wire color
     */
    public ProbeWireNode( PNode bodyNode, PNode probeNode, Point2D bodyControlPointOffset, Point2D probeControlPointOffset, Point2D bodyConnectionOffset, Point2D probeConnectionOffset, Color color ) {
        setPickable( false );
        setStroke( WIRE_STROKE );
        setStrokePaint( color );

        this.bodyNode = bodyNode;
        this.probeNode = probeNode;
        this.bodyControlPointOffset = bodyControlPointOffset;
        this.probeControlPointOffset = probeControlPointOffset;
        this.bodyConnectionOffset = bodyConnectionOffset;
        this.probeConnectionOffset = probeConnectionOffset;

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

    // updates the wire's path
    private void update() {

        Point2D pBody = getConnectionPoint( bodyNode, bodyConnectionOffset );
        Point2D pProbe = getConnectionPoint( probeNode, probeConnectionOffset );

        // control points
        Point2D ctrl1 = new Point2D.Double( pBody.getX() + bodyControlPointOffset.getX(), pBody.getY() + bodyControlPointOffset.getY() );
        Point2D ctrl2 = new Point2D.Double( pProbe.getX() + probeControlPointOffset.getX(), pProbe.getY() + probeControlPointOffset.getY() );

        // path
        setPathTo( new CubicCurve2D.Double( pBody.getX(), pBody.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), pProbe.getX(), pProbe.getY() ) );
    }

    /*
     * Gets the point where the wire connects to a specified node.
     * The offset is used to account for rotation, and is the offset for the *unrotated* node.
     */
    private Point2D getConnectionPoint( PNode node, Point2D connectionOffset ) {
        // create a transform that matches the node's rotation
        AffineTransform t = AffineTransform.getRotateInstance( node.getRotation(), node.getXOffset(), node.getYOffset() );
        // rotate the connection point to match the node's rotation
        double x = node.getXOffset() + connectionOffset.getX();
        double y = node.getYOffset() + connectionOffset.getY();
        return t.transform( new Point2D.Double( x, y ), null );
    }
}
