/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.persistence;

import java.awt.Color;
import java.io.Serializable;


/**
 * QTConfig describes a configuration of the "Quantum Tunneling" simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTConfig implements Serializable {

    private GlobalConfig _globalConfig;
    private ModuleConfig _moduleConfig;
    
    //----------------------------------------------------------------------------
    // Application-level configuration
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public QTConfig() {
        _globalConfig = new GlobalConfig();
        _moduleConfig = new ModuleConfig();
    }

    public void setGlobalConfig( GlobalConfig globalConfig ) {
        this._globalConfig = globalConfig;
    }
    
    public GlobalConfig getGlobalConfig() {
        return _globalConfig;
    }    
    
    public void setModuleConfig( ModuleConfig qtConfig ) {
        this._moduleConfig = qtConfig;
    }
    
    public ModuleConfig getModuleConfig() {
        return _moduleConfig;
    }
    
    //----------------------------------------------------------------------------
    // Global-level configuration, applies to all modules
    //----------------------------------------------------------------------------

    public class GlobalConfig implements Serializable {

        private String _versionNumber;
        private String _cvsTag;
        private boolean _showValues;

        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public GlobalConfig() {}
        
        public String getVersionNumber() {
            return _versionNumber;
        }
        
        public void setVersionNumber( String versionNumber ) {
            _versionNumber = versionNumber;
        }
        
        public String getCvsTag() {
            return _cvsTag;
        }
        
        public void setCvsTag( String buildNumber ) {
            _cvsTag = buildNumber;
        }
           
        public boolean getShowValues() {
            return _showValues;
        }
        
        public void setShowValues( boolean showValues ) {
            _showValues = showValues;
        }
    }
    
    //----------------------------------------------------------------------------
    // Module configuration, for the sole module
    //----------------------------------------------------------------------------
    
    public class ModuleConfig implements Serializable {
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public ModuleConfig() {}
        
    }
}
