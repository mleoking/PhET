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

    public class GlobalConfig implements Serializable {

        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------
        
        private String _versionNumber;
        private String _cvsTag;
        private boolean _showValues;

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
        
        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------
        
        private double _totalEnergy;
        
        private String _potentialTypeName;
        private double _minRegionWidth;
        private Region[] _regions;
 
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
        
        public Region[] getRegions() { 
            return _regions;
        }
        
        public void setRegions( Region[] regions ) {
            _regions = regions;
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
        
        public String getIRViewName() {
            return _irViewName;
        }
        
        public void setIRViewName( String irViewName ) {
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
            setIRViewName( irView.getName() );
        }
        
        public IRView loadIRView() {
            return IRView.getByName( getIRViewName() );
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
        
        /**
         * Breaks an AbstractPotential into the pieces that we need to save
         * (potential type, min region width, regions).
         * 
         * @param potentialEnergy
         */
        public void savePotentialEnergy( AbstractPotential potentialEnergy ) {
            
            // Potential type
            PotentialType potentialType = null;
            if ( potentialEnergy instanceof ConstantPotential ) {
                potentialType = PotentialType.CONSTANT;
            }
            else if ( potentialEnergy instanceof StepPotential ) {
                potentialType = PotentialType.STEP;
            }
            else if ( potentialEnergy instanceof SingleBarrierPotential ) {
                potentialType = PotentialType.SINGLE_BARRIER;
            }
            else if ( potentialEnergy instanceof DoubleBarrierPotential ) {
                potentialType = PotentialType.DOUBLE_BARRIER;
            }
            else {
                throw new IllegalArgumentException( "unsupported potential type: " + potentialEnergy.getClass().getName() );
            }
            savePotentialType( potentialType );
            
            // Min region width 
            _minRegionWidth = potentialEnergy.getMinRegionWidth();
            
            // Regions
            PotentialRegion[] potentialRegions = potentialEnergy.getRegions();
            Region[] regions = new Region[ potentialRegions.length ];
            for ( int i = 0; i < potentialRegions.length; i++ ) {
                Region region = new Region( potentialRegions[i] );
                regions[i] = region;
            }
            setRegions( regions );
        }
        
        /**
         * Creates an AbstractPotential from the pieces that we saved
         * (potential type, min region width, regions).
         * 
         * @param potentialEnergy
         */
        public AbstractPotential loadPotentialEnergy() {
            
            AbstractPotential pe = null;
            
            // Potential type
            PotentialType potentialType = loadPotentialType();
            if ( potentialType == PotentialType.CONSTANT ) {
                pe = new ConstantPotential();
            }
            else if ( potentialType == PotentialType.STEP ) {
                pe = new StepPotential();
            }
            else if ( potentialType == PotentialType.SINGLE_BARRIER ) {
                pe = new SingleBarrierPotential();
            }
            else if ( potentialType == PotentialType.DOUBLE_BARRIER ) {
                pe = new DoubleBarrierPotential();
            }
            else {
                throw new IllegalArgumentException( "unsupported potential type: " + _potentialTypeName );
            }
            
            // Min region width
            pe.setMinRegionWidth( _minRegionWidth );
            
            // Regions
            PotentialRegion[] potentialRegions = new PotentialRegion[ _regions.length ];
            for ( int i = 0; i < _regions.length; i++ ) {
                potentialRegions[i] = _regions[i].toPotentialRegion();
            }
            pe.setRegions( potentialRegions );
            
            return pe;
        }

    }
    
    //----------------------------------------------------------------------------
    // Misc config
    //----------------------------------------------------------------------------
    
    /**
     * A simplified data structure for saving region information.
     */
    public static class Region {
        private double _start;
        private double _end;
        private double _energy;
        
        public Region() {
            this( 0, 0, 0 );
        }
          
        public Region( double start, double end, double energy ) {
            _start = start;
            _end = end;
            _energy = energy;
        }
        
        public Region( PotentialRegion region ) {
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
        
        public double getWidth() {
            return _end - _start;
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
