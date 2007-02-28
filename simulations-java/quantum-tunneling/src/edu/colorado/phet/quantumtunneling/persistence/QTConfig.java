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

import java.io.Serializable;

import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.enums.IRView;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;
import edu.colorado.phet.quantumtunneling.enums.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;


/**
 * QTConfig describes a configuration of the "Quantum Tunneling" simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTConfig implements QTSerializable {

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
