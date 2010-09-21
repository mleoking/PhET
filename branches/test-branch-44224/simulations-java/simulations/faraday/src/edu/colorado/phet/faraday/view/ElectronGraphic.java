/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.model.Electron;
import edu.colorado.phet.faraday.model.ElectronPathDescriptor;


/**
 * ElectronGraphic is the graphic represention of an electron.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronGraphic extends PhetImageGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The electron that this graphic represents.
    private Electron _electronModel;
    
    // The parent graphic.
    private CompositePhetGraphic _parent;
    
    // Foreground image.
    private BufferedImage _foregroundImage;
    
    // Background image.
    private BufferedImage _backgroundImage;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param electronModel the electron that this graphic represents
     */
    public ElectronGraphic( Component component, CompositePhetGraphic parent, Electron electronModel ) {
        super( component );
        
        assert( component != null );
        assert( parent != null );
        assert( electronModel != null );

        _electronModel = electronModel;
        _electronModel.addObserver( this );

        _foregroundImage = FaradayResources.getImage( FaradayConstants.ELECTRON_FOREGROUND_IMAGE );
        _backgroundImage = FaradayResources.getImage( FaradayConstants.ELECTRON_BACKGROUND_IMAGE );
        setImage( _foregroundImage );
        
        int rx = getImage().getWidth() / 2;
        int ry = getImage().getHeight() / 2;
        setRegistrationPoint( rx, ry );
        
        update();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _electronModel.removeObserver( this );
        _electronModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the view to match the model.
     * Handles moving the electron from one segment of the coil to the next.
     * Changes the "look" of the electron as it moves between the foreground 
     * and background of the coil.
     */
    public void update() {
        setVisible( _electronModel.isEnabled() );
        if ( isVisible() ) {
            
            ElectronPathDescriptor descriptor = _electronModel.getPathDescriptor();
                
            // Jump between foreground and background.
            CompositePhetGraphic parent = descriptor.getParent();
            if ( parent != _parent ) {
                
                // Change the parent.
                if ( _parent != null ) {
                    _parent.removeGraphic( this );
                }
                parent.addGraphic( this );
                _parent = parent;
                
                // Change the image.
                if ( descriptor.getLayer() == ElectronPathDescriptor.BACKGROUND ) {
                    setImage( _backgroundImage );
                }
                else { 
                    setImage( _foregroundImage );
                }
            }
            
            // Set the electron's location.
            int x = (int)_electronModel.getX();
            int y = (int)_electronModel.getY();
            setLocation( x, y );
            
            repaint();
        }
    }
}
