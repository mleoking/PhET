/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Dimension;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.faraday.view.CompassGridGraphic;


/**
 * FaradayModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FaradayModule extends Module implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CompassGridGraphic _gridGraphic;
    
    public FaradayModule( String title ) {
        super( title );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setCompassGridGraphic( CompassGridGraphic gridGraphic ) {
        assert( gridGraphic != null );
        _gridGraphic = gridGraphic;
    }
    
    public CompassGridGraphic getCompassGridGraphic() {
        return _gridGraphic;
    }
    
    //----------------------------------------------------------------------------
    // ICompassGridModule implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridSpacing(int, int)
     */
    public void setGridSpacing( int xSpacing, int ySpacing ) {
        _gridGraphic.setSpacing( xSpacing, ySpacing );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridXSpacing()
     */
    public int getGridXSpacing() {
        return _gridGraphic.getXSpacing();
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridYSpacing()
     */
    public int getGridYSpacing() {
        return _gridGraphic.getYSpacing();
    }  
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridNeedleSize(Dimension)
     */
    public void setGridNeedleSize( Dimension size ) {
        _gridGraphic.setNeedleSize( size );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridNeedleSize()
     */
    public Dimension getGridNeedleSize() {
        return _gridGraphic.getNeedleSize();
    }
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setAlphaEnabled(boolean)
     */
    public void setAlphaEnabled( boolean enabled ) {
        _gridGraphic.setAlphaEnabled( enabled );
    }
}
