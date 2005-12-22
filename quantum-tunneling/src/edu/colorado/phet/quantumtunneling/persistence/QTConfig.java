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

import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.enum.IRView;
import edu.colorado.phet.quantumtunneling.enum.PotentialType;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;


/**
 * QTConfig describes a configuration of the "Quantum Tunneling" simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTConfig implements Serializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlobalConfig _globalConfig;
    private ModuleConfig _moduleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public QTConfig() {
        _globalConfig = new GlobalConfig();
        _moduleConfig = new ModuleConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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

    /**
     * GlobalConfig is a JavaBean-compliant data structure that stores
     * global configuration information.
     */
    public class GlobalConfig implements Serializable {

        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------
        
        private String _versionNumber;
        private String _cvsTag;
        private boolean _valuesVisible;

        //----------------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------------
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public GlobalConfig() {}
        
        //----------------------------------------------------------------------------
        // Accessors
        //----------------------------------------------------------------------------
        
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
           
        public boolean isValuesVisible() {
            return _valuesVisible;
        }
        
        public void setValuesVisible( boolean visible ) {
            _valuesVisible = visible;
        }
    }
    
    //----------------------------------------------------------------------------
    // Module configuration, for the sole module
    //----------------------------------------------------------------------------
    
    /**
     * ModuleConfig is a JavaBean-compliant data structure that stores
     * module configuration information.
     */
    public class ModuleConfig implements Serializable {
        
        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------
        
        // Model properties
        private double _totalEnergy;
        private double _minRegionWidth;
        private RegionConfig[] _constantRegions;
        private RegionConfig[] _stepRegions;
        private RegionConfig[] _singleBarrierRegions;
        private RegionConfig[] _doubleBarrierRegions;
 
        // Control properties
        private String _potentialTypeName;
        private boolean _realSelected;
        private boolean _imaginarySelected;
        private boolean _magnitudeSelected;
        private boolean _phaseSelected;
        private String _irViewName;
        private String _directionName;
        private String _waveTypeName;
        private double _packetWidth;
        private double _packetCenter;
        
        //----------------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------------
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public ModuleConfig() {}
        
        //----------------------------------------------------------------------------
        // Accessors
        //----------------------------------------------------------------------------
        
        public RegionConfig[] getConstantRegions() {
            return _constantRegions;
        }
        
        public void setConstantRegions( RegionConfig[] constantRegions ) {
            _constantRegions = constantRegions;
        }
        
        public RegionConfig[] getDoubleBarrierRegions() {
            return _doubleBarrierRegions;
        }
        
        public void setDoubleBarrierRegions( RegionConfig[] doubleBarrierRegions ) {
            _doubleBarrierRegions = doubleBarrierRegions;
        }
        
        public RegionConfig[] getSingleBarrierRegions() {
            return _singleBarrierRegions;
        }
        
        public void setSingleBarrierRegions( RegionConfig[] singleBarrierRegions ) {
            _singleBarrierRegions = singleBarrierRegions;
        }
        
        public RegionConfig[] getStepRegions() {
            return _stepRegions;
        }
        
        public void setStepRegions( RegionConfig[] stepRegions ) {
            _stepRegions = stepRegions;
        }
        
        public boolean isImaginarySelected() {
            return _imaginarySelected;
        }
        
        public void setImaginarySelected( boolean imaginarySelected ) {
            _imaginarySelected = imaginarySelected;
        }
        
        public String getDirectionName() {
            return _directionName;
        }
        
        public void setDirectionName( String directionName ) {
            _directionName = directionName;
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
        
        public boolean isPhaseSelected() {
            return _phaseSelected;
        }
        
        public void setPhaseSelected( boolean phaseSelected ) {
            _phaseSelected = phaseSelected;
        }
        
        public boolean isRealSelected() {
            return _realSelected;
        }
        
        public void setRealSelected( boolean realSelected ) {
            _realSelected = realSelected;
        }
        
        public String getIrViewName() {
            return _irViewName;
        }
        
        public void setIrViewName( String irViewName ) {
            _irViewName = irViewName;
        }
        
        public double getTotalEnergy() {
            return _totalEnergy;
        }
        
        public void setTotalEnergy( double totalEnergy ) {
            _totalEnergy = totalEnergy;
        }
        
        public String getWaveTypeName() {
            return _waveTypeName;
        }
        
        public void setWaveTypeName( String waveTypeName ) {
            _waveTypeName = waveTypeName;
        }
        
        public String getPotentialTypeName() {
            return _potentialTypeName;
        }
        
        public void setPotentialTypeName( String potentialTypeName ) {
            _potentialTypeName = potentialTypeName;
        }
        
        public double getMinRegionWidth() {
            return _minRegionWidth;
        }
        
        public void setMinRegionWidth( double minRegionWidth ) {
            _minRegionWidth = minRegionWidth;
        }
        
        //----------------------------------------------------------------------------
        // Convenience methods for non-JavaBean objects
        //----------------------------------------------------------------------------
       
        public void savePotentialType( PotentialType potentialType ) {
            setPotentialTypeName( potentialType.getName() );
        }
        
        public PotentialType loadPotentialType() {
            return PotentialType.getByName( getPotentialTypeName() );
        }
        
        public void saveIRView( IRView irView ) {
            setIrViewName( irView.getName() );
        }
        
        public IRView loadIRView() {
            return IRView.getByName( getIrViewName() );
        }
        
        public void saveDirection( Direction direction ) {
            setDirectionName( direction.getName() );
        }
        
        public Direction loadDirection() {
            return Direction.getByName( getDirectionName() );
        }
        
        public void saveWaveType( WaveType waveType ) {
            setWaveTypeName( waveType.getName() );
        }
        
        public WaveType loadWaveType() {
            return WaveType.getByName( getWaveTypeName() );
        }
        
        public void saveTotalEnergy( TotalEnergy te ) {
            setTotalEnergy( te.getEnergy() );
        }
        
        public TotalEnergy loadTotalEnergy() {
            return new TotalEnergy( getTotalEnergy() );
        }
        
        public void saveConstantPotential( ConstantPotential pe ) {
            setConstantRegions( toRegionConfigs( pe.getRegions() ) );
        }
        
        public ConstantPotential loadConstantPotential() {
            ConstantPotential pe = new ConstantPotential();
            pe.setMinRegionWidth( _minRegionWidth );
            pe.setRegions( toPotentialRegions( _constantRegions ) );
            return pe;
        }
        
        public void saveStepPotential( StepPotential pe ) {
            setStepRegions( toRegionConfigs( pe.getRegions() ) );
        }
        
        public StepPotential loadStepPotential() {
            StepPotential pe = new StepPotential();
            pe.setMinRegionWidth( _minRegionWidth );
            pe.setRegions( toPotentialRegions( _stepRegions ) );
            return pe;
        }
        
        public void saveSingleBarrierPotential( SingleBarrierPotential pe ) {
            setSingleBarrierRegions( toRegionConfigs( pe.getRegions() ) );
        }
        
        public SingleBarrierPotential loadSingleBarrierPotential() {
            SingleBarrierPotential pe = new SingleBarrierPotential();
            pe.setMinRegionWidth( _minRegionWidth );
            pe.setRegions( toPotentialRegions( _singleBarrierRegions ) );
            return pe;
        }
        
        public void saveDoubleBarrierPotential( DoubleBarrierPotential pe ) {
            setDoubleBarrierRegions( toRegionConfigs( pe.getRegions() ) );
        }
        
        public DoubleBarrierPotential loadDoubleBarrierPotential() {
            DoubleBarrierPotential pe = new DoubleBarrierPotential();
            pe.setMinRegionWidth( _minRegionWidth );
            pe.setRegions( toPotentialRegions( _doubleBarrierRegions ) );
            return pe;
        }
        
        private RegionConfig[] toRegionConfigs( PotentialRegion[] potentialRegions ) {
            RegionConfig[] regionConfigs = new RegionConfig[ potentialRegions.length ];
            for ( int i = 0; i < potentialRegions.length; i++ ) {
                RegionConfig region = new RegionConfig( potentialRegions[i] );
                regionConfigs[i] = region;
            }
            return regionConfigs;
        }
        
        private PotentialRegion[] toPotentialRegions( RegionConfig[] regionConfigs ) {
            PotentialRegion[] potentialRegions = new PotentialRegion[ regionConfigs.length ];
            for ( int i = 0; i < regionConfigs.length; i++ ) {
                potentialRegions[i] = regionConfigs[i].toPotentialRegion();
            }
            return potentialRegions;
        }
    }
    
    //----------------------------------------------------------------------------
    // Misc config
    //----------------------------------------------------------------------------
    
    /**
     * A JavaBean-compliant data structure for saving region information.
     */
    public static class RegionConfig {
        private double _start;
        private double _end;
        private double _energy;
        
        public RegionConfig() {
            this( 0, 0, 0 );
        }
          
        public RegionConfig( double start, double end, double energy ) {
            _start = start;
            _end = end;
            _energy = energy;
        }
        
        public RegionConfig( PotentialRegion region ) {
            this( region.getStart(), region.getEnd(), region.getEnergy() );
        }
        
        public double getStart() {
            return _start;
        }
        
        public void setStart( double start ) {
            _start = start;
        }
        
        public double getEnd() {
            return _end;
        }
        
        public void setEnd( double end ) {
            _end = end;
        }
        
        public double getEnergy() {
            return _energy;
        }
        
        public void setEnergy( double energy ) {
            _energy = energy;
        }
        
        public PotentialRegion toPotentialRegion() {
            return new PotentialRegion( _start, _end, _energy );
        }
    }
}
