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
    private BSModuleConfig _oneWellModuleConfig;
    private BSModuleConfig _twoWellsModuleConfig;
    private BSModuleConfig _manyWellsModuleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public BSConfig() {
        _globalConfig = new BSGlobalConfig();
        _oneWellModuleConfig = new BSModuleConfig();
        _twoWellsModuleConfig = new BSModuleConfig();
        _manyWellsModuleConfig = new BSModuleConfig();
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
    
    public void setOneWellModuleConfig( BSModuleConfig oneWellModuleConfig ) {
        _oneWellModuleConfig = oneWellModuleConfig;
    }
    
    public BSModuleConfig getOneWellModuleConfig() {
        return _oneWellModuleConfig;
    }
    
    public BSModuleConfig getTwoWellsModuleConfig() {
        return _twoWellsModuleConfig;
    }
    
    public void setTwoWellsModuleConfig( BSModuleConfig twoWellsModuleConfig ) {
        _twoWellsModuleConfig = twoWellsModuleConfig;
    }
    
    public BSModuleConfig getManyWellsModuleConfig() {
        return _manyWellsModuleConfig;
    }

    public void setManyWellsModuleConfig( BSModuleConfig manyWellsModuleConfig ) {
        _manyWellsModuleConfig = manyWellsModuleConfig;
    }
}
