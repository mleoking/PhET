/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;



/**
 * BSConfig describes a configuration of the "Bound States" simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConfig implements BSSerializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSGlobalConfig _globalConfig;
    private BSModuleConfig _moduleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public BSConfig() {
        _globalConfig = new BSGlobalConfig();
        _moduleConfig = new BSModuleConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setGlobalConfig( BSGlobalConfig globalConfig ) {
        this._globalConfig = globalConfig;
    }
    
    public BSGlobalConfig getGlobalConfig() {
        return _globalConfig;
    }    
    
    public void setModuleConfig( BSModuleConfig qtConfig ) {
        this._moduleConfig = qtConfig;
    }
    
    public BSModuleConfig getModuleConfig() {
        return _moduleConfig;
    }
}
