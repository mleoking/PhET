/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.capacitorlab.model.World;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Probe for the voltmeter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class VoltmeterProbeNode extends PhetPNode {

    public static class PositiveVoltmeterProbeNode extends VoltmeterProbeNode {
        
        public PositiveVoltmeterProbeNode( final Voltmeter voltmeter, World world, final ModelViewTransform mvt ) {
            super( CLImages.RED_PROBE, voltmeter, world, mvt );
            voltmeter.addPositiveProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( voltmeter.getPositiveProbeLocationReference() ) );
                }
            });
        }
        
        public Point3D getProbeLocationReference() {
            return getVoltmeter().getPositiveProbeLocationReference();
        }
        
        public void setProbeLocation( Point3D location ) {
            getVoltmeter().setPositiveProbeLocation( location );
        }
    }

    public static class NegativeVoltmeterProbeNode extends VoltmeterProbeNode {
        
        public NegativeVoltmeterProbeNode( final Voltmeter voltmeter, World world, final ModelViewTransform mvt ) {
            super( CLImages.BLACK_PROBE, voltmeter, world, mvt );
            voltmeter.addNegativeProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( voltmeter.getNegativeProbeLocationReference() ) );
                }
            });
        }
        
        public Point3D getProbeLocationReference() {
            return getVoltmeter().getNegativeProbeLocationReference();
        }
        
        public void setProbeLocation( Point3D location ) {
            getVoltmeter().setNegativeProbeLocation( location );
        }
    }
    
    private final Voltmeter voltmeter;
    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body
    
    public VoltmeterProbeNode( Image image, final Voltmeter voltmeter, World world, final ModelViewTransform mvt ) {
        
        this.voltmeter = voltmeter;
        
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        imageNode.setOffset( x, y );
        
        connectionOffset = new Point2D.Double( 0, imageNode.getFullBoundsReference().getHeight() ); // connect wire to bottom center
        
        // rotate after computing the connection offset
        rotate( -mvt.getYaw() );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new ProbeDragHandler( this, world, mvt ) );
    }
    
    protected Voltmeter getVoltmeter() {
        return voltmeter;
    }
    
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }
    
    protected abstract Point3D getProbeLocationReference();
    
    protected abstract void setProbeLocation( Point3D location );
    
    private static class ProbeDragHandler extends PDragSequenceEventHandler {
        
        private final VoltmeterProbeNode probeNode;
        private final World world;
        private final ModelViewTransform mvt;
        
        private double clickXOffset, clickYOffset;
        
        public ProbeDragHandler( VoltmeterProbeNode probeNode, World world, ModelViewTransform mvt ) {
            this.probeNode = probeNode;
            this.world = world;
            this.mvt = mvt;
        }
        
        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D pOrigin = mvt.modelToViewDelta( probeNode.getProbeLocationReference() );
            clickXOffset = pMouse.getX() - pOrigin.getX();
            clickYOffset = pMouse.getY() - pOrigin.getY();
        }
        
        @Override
        protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double yView = pMouse.getY() - clickYOffset;
            Point3D pModel = new Point3D.Double( mvt.viewToModel( xView, yView ) );
            // prevent probe from being dragged outside world bounds
            if ( world.contains( pModel ) ) {
                probeNode.setProbeLocation( pModel );
            }
        }
    }
}

