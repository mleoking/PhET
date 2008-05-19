/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Debris;
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
    
    private static final double BOULDER_RADIUS = 1; // pixels
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Debris _debris;
    private final DebrisListener _debrisListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pModel, _pView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DebrisNode( Debris debris, ModelViewTransform mvt ) {
        super();
        
        _debris = debris;
        _mvt = mvt;
        
        _debrisListener = new DebrisAdapter() {
            public void positionChanged() {
                updatePosition();
            }
        };
        _debris.addDebrisListener( _debrisListener );
        
        BoulderNode boulderNode = new BoulderNode( BOULDER_RADIUS );
        addChild( boulderNode );
        
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
        _pModel.setLocation( _debris.getX(), _debris.getY() );
        _mvt.modelToView( _pModel, _pView );
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
