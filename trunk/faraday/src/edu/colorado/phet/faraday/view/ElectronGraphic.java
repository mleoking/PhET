/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Electron;


/**
 * ElectronGraphic is the graphic represention of an electron.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectronGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Electron _electronModel;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param electronModel the electron being displayed
     */
    public ElectronGraphic( Component component, Electron electronModel ) {
        super( component, FaradayConfig.ELECTRON_IMAGE );
        
        assert( component != null );
        assert( electronModel != null );

        _electronModel = electronModel;
        _electronModel.addObserver( this );
        
        int rx = getImage().getWidth() / 2;
        int ry = getImage().getHeight() / 2;
        setRegistrationPoint( rx, ry );
        
        scale( 0.25 ); // XXX
        
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _electronModel.removeObserver( this );
        _electronModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _electronModel.isEnabled() );
        if ( isVisible() ) {
            int x = (int)_electronModel.getLocation().getX();
            int y = (int)_electronModel.getLocation().getY();
            setLocation( x, y );
            repaint();
        }
    }
}
