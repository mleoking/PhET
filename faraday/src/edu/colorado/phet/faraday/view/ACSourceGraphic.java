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
import edu.colorado.phet.faraday.model.ACSource;


/**
 * ACSourceGraphic is the graphical representation of an alternating current source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSourceGraphic extends PhetImageGraphic implements SimpleObserver {

    private ACSource _acSourceModel;
    
    public ACSourceGraphic( Component component, ACSource acSourceModel ) {
        
        super( component, FaradayConfig.AC_SOURCE_IMAGE );
        assert( component != null );
        assert( acSourceModel != null );
        
        _acSourceModel = acSourceModel;
        _acSourceModel.addObserver( this );
        
        // Registration point is the bottom center of the image.
        setRegistrationPoint( getImage().getWidth() / 2, getImage().getHeight() );
        
        scale( 0.40 ); // XXX
        
        update();
    }
    
    public void finalize() {
        _acSourceModel.removeObserver( this );
        _acSourceModel = null;
    }

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
