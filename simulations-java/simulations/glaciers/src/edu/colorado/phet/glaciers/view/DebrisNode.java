/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Debris;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Debris.DebrisAdapter;
import edu.colorado.phet.glaciers.model.Debris.DebrisListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * DebrisNode is the visual representation of debris moving in and on the glacier ice.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebrisNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_3D = false;  // see debug3D method
    private static final boolean DEBUG_ON_VALLEY_FLOOR = true; // see debugOnValleyFloor method
    
    private static final double BOULDER_RADIUS = 1; // pixels
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Debris _debris;
    private final Glacier _glacier;
    private BoulderNode _boulderNode;
    private final DebrisListener _debrisListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pModel, _pView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DebrisNode( Debris debris, Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        _debris = debris;
        _glacier = glacier;
        _mvt = mvt;
        
        _debrisListener = new DebrisAdapter() {
            public void positionChanged() {
                updatePosition();
            }
        };
        _debris.addDebrisListener( _debrisListener );
        
        _boulderNode = new BoulderNode( BOULDER_RADIUS );
        addChild( _boulderNode );

        // only debris in the cross-section is initially visible
        setVisible( _debris.getZ() == 0 );

        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        updatePosition();
    }
    
    public void cleanup() {
        _debris.removeDebrisListener( _debrisListener );
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        
        final double surfaceElevation = _glacier.getSurfaceElevation( _debris.getX() );
        if ( _debris.getY() >= surfaceElevation ) {
            // when debris comes to the surface, add perspective to its position and make it visible
            _pModel.setLocation( _debris.getX(), _debris.getY() + _debris.getZ() );
            setVisible( true );
        }
        else {
            _pModel.setLocation( _debris.getX(), _debris.getY() );
            setVisible( _debris.getZ() == 0 );
        }
        _mvt.modelToView( _pModel, _pView );
        setOffset( _pView );
        
        if ( DEBUG_3D ) {
            debug3D();
        }
        else if (DEBUG_ON_VALLEY_FLOOR ) {
            debugOnValleyFloor();
        }
    }
    
    /*
     * In debug mode, debris is always visible.
     * Any debris with a non-zero z coordinate is shown in red.
     */
    private void debug3D() {
        setVisible( true );
        if ( _debris.getZ() > 0 ) {
            _boulderNode.setPaint( Color.RED );
        }
    }
    
    /*
     * In debug mode, debris on valley floor is red.
     */
    private void debugOnValleyFloor() {
        if ( _debris.isOnValleyFloor() ) {
            _boulderNode.setPaint( Color.GREEN );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * A very simple boulder.
     */
    private static class BoulderNode extends PPath {
        
        private static final Color FILL_COLOR = Color.BLACK;
        
        public BoulderNode( double radius ) {
            super();
            Shape shape = new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius );
            setPathTo( shape );
            setStroke( null );
            setPaint( FILL_COLOR );
        }
    }
}
