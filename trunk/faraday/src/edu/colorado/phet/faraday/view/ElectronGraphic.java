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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.CurveDescriptor;
import edu.colorado.phet.faraday.model.Electron;


/**
 * ElectronGraphic is the graphic represention of an electron.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectronGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Composite with alpha, used when electron is on the background layer.
    private static final Composite COMPOSITE = 
        AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The electron that this graphic represents.
    private Electron _electronModel;
    
    // The parent graphic.
    private CompositePhetGraphic _parent;
    
    // Is this electron on the background layer?
    private boolean _onBackground;
    
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
        super( component, FaradayConfig.ELECTRON_IMAGE );
        
        assert( component != null );
        assert( parent != null );
        assert( electronModel != null );

        _electronModel = electronModel;
        _electronModel.addObserver( this );
        
        _parent = parent;
        _onBackground = false;
        
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
            
            CurveDescriptor cd = _electronModel.getCurveDescriptor();
            
            // Are we on the background layer?
            _onBackground = ( cd.getLayer() == CurveDescriptor.BACKGROUND );
                
            // Jump between foreground and background.
            CompositePhetGraphic parent = cd.getParent();
            if ( parent != _parent ) {
                _parent.removeGraphic( this );
                parent.addGraphic( this );
                _parent = parent;
            }
            
            // Set the electron's location.
            int x = (int)_electronModel.getLocation().getX();
            int y = (int)_electronModel.getLocation().getY();
            setLocation( x, y );
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetImageGraphic overrides
    //----------------------------------------------------------------------------

    /**
     * Draws the electron.  
     * If transparency is enabled, use alpha compositing to make the electron transparent.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            if ( _onBackground ) {
                Composite oldComposite = g2.getComposite(); // save
                g2.setComposite( COMPOSITE );
                super.paint( g2 );
                g2.setComposite( oldComposite ); // restore
            }
            else {
                super.paint( g2 );
            }
        }   
    }
}
