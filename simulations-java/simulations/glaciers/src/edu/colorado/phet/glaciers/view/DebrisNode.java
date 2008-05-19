/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Debris;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
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
    
    private static final double BOULDER_RADIUS = 1; // pixels
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Debris _debris;
    private final MovableListener _movableListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DebrisNode( Debris debris, ModelViewTransform mvt ) {
        super();
        
        _debris = debris;
        _mvt = mvt;
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updatePosition();
            }
        };
        _debris.addMovableListener( _movableListener );
        
        BoulderNode boulderNode = new BoulderNode( BOULDER_RADIUS );
        addChild( boulderNode );
        
        _pView = new Point2D.Double();
        updatePosition();
    }
    
    public void cleanup() {
        _debris.removeMovableListener( _movableListener );
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _mvt.modelToView( _debris.getPositionReference(), _pView );
        setOffset( _pView );
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
