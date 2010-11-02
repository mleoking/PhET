/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.World;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Probe for the E-Field Detector.
 * Origin is in the center of the probe's crosshairs, and the location of the crosshairs 
 * is dependent on the image file.  The only way to align the crosshairs and the origin
 * is via visual inspection. Running with -dev will add an additional node that allows 
 * you to visually check alignment.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorProbeNode extends PhetPNode {
    
    private final Point2D connectionOffset;
    
    public EFieldDetectorProbeNode( final EFieldDetector detector, World world, final ModelViewTransform mvt, boolean dev ) {
        super();
        
        PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
        addChild( imageNode );
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -( 0.078 * imageNode.getFullBoundsReference().getHeight() ); // multiplier is dependent on where crosshairs appear in image file
        imageNode.setOffset( x, y );
        
        connectionOffset = new Point2D.Double( 0, imageNode.getFullBoundsReference().getHeight() + y ); // connect wire to bottom center
        
        // rotate after computing the connection offset
        rotate( -mvt.getYaw() );
        
        // Put a '+' at origin to check that probe image is offset properly.
        if ( dev ) {
            PlusNode plusNode = new PlusNode( 12, 2, Color.GRAY );
            addChild( plusNode );
        }
        
        detector.addProbeLocationListener( new SimpleObserver() {
            public void update() {
                setOffset( mvt.modelToView( detector.getProbeLocationReference() ) );
            }
        });
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new ProbeDragHandler( this, detector, world, mvt ) );
    }
    
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }
    
    private static class ProbeDragHandler extends PDragSequenceEventHandler {
        
        private final PNode probeNode;
        private final EFieldDetector detector;
        private final World world;
        private final ModelViewTransform mvt;
        
        private double clickXOffset, clickYOffset;
        
        public ProbeDragHandler( PNode probeNode, EFieldDetector detector, World world, ModelViewTransform mvt ) {
            this.probeNode = probeNode;
            this.detector = detector;
            this.world = world;
            this.mvt = mvt;
        }
        
        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D pOrigin = mvt.modelToViewDelta( detector.getProbeLocationReference() );
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
                detector.setProbeLocation( pModel );
            }
        }
    }
}


