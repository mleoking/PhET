// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.rutherfordscattering.model.Gun;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamNode is the beam the comes out of the gun.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private PPath _pathNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BeamNode( Dimension size, Gun gun ) {
        super();
        
        _gun = gun;
        
        setPickable( false );
        setChildrenPickable( false );
        
        _pathNode = new PPath( new Rectangle2D.Double( 0, 0, size.width, size.height ) );
        _pathNode.setStroke( null );
        addChild( _pathNode );

        updateAll();
        _gun.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the beam to match the gun model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _gun ) {
            updateAll();
        }
    }
    
    private void updateAll() {
        setVisible( _gun.isEnabled() );
        _pathNode.setPaint( _gun.getBeamColor() );
    }
}
