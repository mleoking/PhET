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

import edu.colorado.phet.quantumtunneling.enum.PotentialType;
import edu.colorado.phet.quantumtunneling.enum.WaveType;


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
        
        private double _te;
        
        private PotentialType _potentialType;
        private double[] _pes;
        private double[] _positions;
 
        private boolean _realSelected;
        private boolean _imaginarySelected;
        private boolean _magnitudeSelected;
        private boolean _phaseSelected;
        private boolean _separateSelected;
        private boolean _leftToRightSelected;
        private WaveType _waveType;
        private double _packetWidth;
        private double _packetCenter;
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public ModuleConfig() {}
        
        public boolean isImaginarySelected() {
            return _imaginarySelected;
        }
        
        public void setImaginarySelected( boolean imaginarySelected ) {
            _imaginarySelected = imaginarySelected;
        }
        public boolean isLeftToRightSelected() {
            return _leftToRightSelected;
        }
        
        public void setLeftToRightSelected( boolean leftToRightSelected ) {
            _leftToRightSelected = leftToRightSelected;
        }
        
        public boolean isMagnitudeSelected() {
            return _magnitudeSelected;
        }
        
        public void setMagnitudeSelected( boolean magnitudeSelected ) {
            _magnitudeSelected = magnitudeSelected;
        }
        
        public double getPacketCenter() {
            return _packetCenter;
        }
        
        public void setPacketCenter( double packetCenter ) {
            _packetCenter = packetCenter;
        }
        
        public double getPacketWidth() {
            return _packetWidth;
        }
        
        public void setPacketWidth( double packetWidth ) {
            _packetWidth = packetWidth;
        }
        
        public double[] getPes() { 
            return _pes;
        }
        
        public void setPes( double[] pes ) {
            _pes = pes;
        }
        
        public boolean isPhaseSelected() {
            return _phaseSelected;
        }
        
        public void setPhaseSelected( boolean phaseSelected ) {
            _phaseSelected = phaseSelected;
        }
        
        public double[] getPositions() {
            return _positions;
        }
        
        public void setPositions( double[] positions ) {
            _positions = positions;
        }
        
        public boolean isRealSelected() {
            return _realSelected;
        }
        
        public void setRealSelected( boolean realSelected ) {
            _realSelected = realSelected;
        }
        
        public boolean isSeparateSelected() {
            return _separateSelected;
        }
        
        public void setSeparateSelected( boolean separateSelected ) {
            _separateSelected = separateSelected;
        }
        
        public double getTe() {
            return _te;
        }
        
        public void setTe( double te ) {
            _te = te;
        }
        
        public WaveType getWaveType() {
            return _waveType;
        }
        
        public void setWaveType( WaveType waveType ) {
            _waveType = waveType;
        }
        
        public PotentialType getPotentialType() {
            return _potentialType;
        }
        
        public void setPotentialType( PotentialType potentialType ) {
            _potentialType = potentialType;
        }
    }
}
