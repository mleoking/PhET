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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Electron;
import edu.colorado.phet.faraday.model.ElectronPathDescriptor;


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
    
    // The electron that this graphic represents.
    private Electron _electronModel;
    
    // The parent graphic.
    private CompositePhetGraphic _parent;
    
    // Foreground image.
    private BufferedImage _foregroundImage;
    
    // Background image.
    private BufferedImage _backgroundImage;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
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
        
        try {
            _foregroundImage = ImageLoader.loadBufferedImage( FaradayConfig.ELECTRON_FOREGROUND_IMAGE );
            _backgroundImage = ImageLoader.loadBufferedImage( FaradayConfig.ELECTRON_BACKGROUND_IMAGE );
        }
        catch ( IOException e ) {
            // Bail if this happens.
            throw new RuntimeException( e );
        }
        setImage( _foregroundImage );
        
        int rx = getImage().getWidth() / 2;
        int ry = getImage().getHeight() / 2;
        setRegistrationPoint( rx, ry );
        
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
            int x = (int)_electronModel.getLocation().getX();
            int y = (int)_electronModel.getLocation().getY();
            setLocation( x, y );
            
            repaint();
        }
    }
}
