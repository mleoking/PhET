// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.quantumtunneling.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;


/**
 * QTConfig describes a configuration of the "Quantum Tunneling" simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class QTConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTGlobalConfig _globalConfig;
    private QTModuleConfig _moduleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public QTConfig() {
        _globalConfig = new QTGlobalConfig();
        _moduleConfig = new QTModuleConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setGlobalConfig( QTGlobalConfig globalConfig ) {
        _globalConfig = globalConfig;
    }
    
    public QTGlobalConfig getGlobalConfig() {
        return _globalConfig;
    }    
    
    public void setModuleConfig( QTModuleConfig moduleConfig ) {
        _moduleConfig = moduleConfig;
    }
    
    public QTModuleConfig getModuleConfig() {
        return _moduleConfig;
    }
}
