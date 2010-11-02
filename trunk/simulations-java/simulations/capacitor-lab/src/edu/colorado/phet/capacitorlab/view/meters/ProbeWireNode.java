/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
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
    
    private static final Color WIRE_COLOR = Color.BLACK;
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
    
    private final PNode bodyNode,  probeNode;
    private final Point2D bodyControlPointOffset, probeControlPointOffset;
    private final Point2D probeConnectionOffset;
    
    public ProbeWireNode( PNode bodyNode, PNode probeNode, Point2D bodyControlPointOffset, Point2D probeControlPointOffset, Point2D probeConnectionOffset ) {
        setStroke( WIRE_STROKE );
        setStrokePaint( WIRE_COLOR );
        
        this.bodyNode = bodyNode;
        this.probeNode = probeNode;
        this.bodyControlPointOffset = bodyControlPointOffset;
        this.probeControlPointOffset = probeControlPointOffset;
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
    
    private void update() {
        
        Point2D pBody = getBodyConnectionPoint();
        Point2D pProbe = getProbeConnectionPoint();
        
        // control points 
        Point2D ctrl1 = new Point2D.Double( pBody.getX() + bodyControlPointOffset.getX(), pBody.getY() + bodyControlPointOffset.getY() );
        Point2D ctrl2 = new Point2D.Double( pProbe.getX() + probeControlPointOffset.getX(), pProbe.getY() + probeControlPointOffset.getY() );
        
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
        double x = probeNode.getXOffset() + probeConnectionOffset.getX();
        double y = probeNode.getYOffset() + probeConnectionOffset.getY();
        // rotate the connection point to match the probe's rotation
        AffineTransform t = AffineTransform.getRotateInstance( probeNode.getRotation(), probeNode.getXOffset(), probeNode.getYOffset() );
        return t.transform( new Point2D.Double( x, y ), null );
    }
}
