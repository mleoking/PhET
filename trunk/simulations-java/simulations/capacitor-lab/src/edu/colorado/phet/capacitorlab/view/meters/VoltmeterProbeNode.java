// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.drag.LocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for voltmeter probes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class VoltmeterProbeNode extends PhetPNode {

    private final Voltmeter voltmeter;
    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body
    
    public VoltmeterProbeNode( Image image, Voltmeter voltmeter, CLModelViewTransform3D mvt ) {
        
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
        addDragHandler( voltmeter, mvt );
        
        addLocationObserver( voltmeter, mvt );
    }
    
    protected Voltmeter getVoltmeter() {
        return voltmeter;
    }
    
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }
    
    protected abstract void addDragHandler( Voltmeter voltmeter, CLModelViewTransform3D mvt );
    
    protected abstract void addLocationObserver( Voltmeter voltmeter, CLModelViewTransform3D mvt );
    
    /**
     * Positive voltmeter probe.
     */
    public static class PositiveVoltmeterProbeNode extends VoltmeterProbeNode {
        
        public PositiveVoltmeterProbeNode( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            super( CLImages.RED_VOLTMETER_PROBE, voltmeter, mvt );
        }
        
        @Override
        protected void addDragHandler( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            addInputEventListener( new LocationDragHandler( this, mvt ) {
                
                protected Point3D getModelLocation() {
                    return voltmeter.getPositiveProbeLocationReference();
                }
                
                protected void setModelLocation( Point3D location ) {
                    voltmeter.setPositiveProbeLocation( location );
                }
            });
        }

        @Override
        protected void addLocationObserver( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            voltmeter.addPositiveProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( voltmeter.getPositiveProbeLocationReference() ) );
                }
            });
        }
    }

    /**
     * Negative voltmeter probe.
     */
    public static class NegativeVoltmeterProbeNode extends VoltmeterProbeNode {
        
        public NegativeVoltmeterProbeNode( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            super( CLImages.BLACK_VOLTMETER_PROBE, voltmeter, mvt );
        }
        
        @Override
        protected void addDragHandler( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            addInputEventListener( new LocationDragHandler( this, mvt ) {
                
                protected Point3D getModelLocation() {
                    return voltmeter.getNegativeProbeLocationReference();
                }
                
                protected void setModelLocation( Point3D location ) {
                    voltmeter.setNegativeProbeLocation( location );
                }
            });
        }

        @Override
        protected void addLocationObserver( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            voltmeter.addNegativeProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( voltmeter.getNegativeProbeLocationReference() ) );
                }
            });
        }
    }
}
