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
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.ACSource;


/**
 * ACSourceGraphic is the graphical representation of an alternating current source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSourceGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ACSource _acSourceModel;
    private PhetImageGraphic _acSourceGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors and finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param acSourceModel
     */
    public ACSourceGraphic( Component component, ACSource acSourceModel ) {
        
        super( component );
        
        assert( component != null );
        assert( acSourceModel != null );
        
        _acSourceModel = acSourceModel;
        _acSourceModel.addObserver( this );
        
        // AC panel
        {
            _acSourceGraphic = new PhetImageGraphic( component, FaradayConfig.AC_SOURCE_IMAGE );
            addGraphic( _acSourceGraphic );

            // Registration point is the bottom center of the image.
            int rx = _acSourceGraphic.getImage().getWidth() / 2;
            int ry = _acSourceGraphic.getImage().getHeight();
            _acSourceGraphic.setRegistrationPoint( rx, ry );

            _acSourceGraphic.scale( 0.40 ); // XXX
        }
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _acSourceModel.removeObserver( this );
        _acSourceModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _acSourceModel.isEnabled() );
        if ( isVisible() ) {
            // XXX 
            repaint();
        }
    }
}
