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

import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.enums.IRView;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;
import edu.colorado.phet.quantumtunneling.enums.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;


/**
 * QTModuleConfig is a JavaBean-compliant data structure that stores
 * configuration information related to QTModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModuleConfig implements QTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Clock
    private boolean _clockRunning;
    
    // Model properties
    private double _totalEnergy;
    private double _minRegionWidth;
    private QTRegionConfig[] _constantRegions;
    private QTRegionConfig[] _stepRegions;
    private QTRegionConfig[] _singleBarrierRegions;
    private QTRegionConfig[] _doubleBarrierRegions;

    // Control properties
    private String _potentialTypeName;
    private boolean _showValuesSelected;
    private boolean _rtpSelected;
    private boolean _realSelected;
    private boolean _imaginarySelected;
    private boolean _magnitudeSelected;
    private boolean _phaseSelected;
    private String _irViewName;
    private String _directionName;
    private String _waveTypeName;
    private double _packetWidth;
    private double _packetCenter;
    private int _waveFunctionZoomIndex;
    private int _probabilityDensityZoomIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public QTModuleConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public boolean isClockRunning() {
        return _clockRunning;
    }
    
    public QTRegionConfig[] getConstantRegions() {
        return _constantRegions;
    }
    
    public void setConstantRegions( QTRegionConfig[] constantRegions ) {
        _constantRegions = constantRegions;
    }
    
    public QTRegionConfig[] getDoubleBarrierRegions() {
        return _doubleBarrierRegions;
    }
    
    public void setDoubleBarrierRegions( QTRegionConfig[] doubleBarrierRegions ) {
        _doubleBarrierRegions = doubleBarrierRegions;
    }
    
    public QTRegionConfig[] getSingleBarrierRegions() {
        return _singleBarrierRegions;
    }
    
    public void setSingleBarrierRegions( QTRegionConfig[] singleBarrierRegions ) {
        _singleBarrierRegions = singleBarrierRegions;
    }
    
    public QTRegionConfig[] getStepRegions() {
        return _stepRegions;
    }
    
    public void setStepRegions( QTRegionConfig[] stepRegions ) {
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
    
    public int getWaveFunctionZoomIndex() {
        return _waveFunctionZoomIndex;
    }
    
    public void setWaveFunctionZoomIndex( int waveFunctionZoomIndex ) {
        _waveFunctionZoomIndex = waveFunctionZoomIndex;
    }
    
    public int getProbabilityDensityZoomIndex() {
        return _probabilityDensityZoomIndex;
    }
    
    public void setProbabilityDensityZoomIndex( int probabilityDensityZoomIndex ) {
        _probabilityDensityZoomIndex = probabilityDensityZoomIndex;
    }
    
    public boolean isShowValuesSelected() {
        return _showValuesSelected;
    }
    
    public void setShowValuesSelected( boolean showValuesSelected ) {
        _showValuesSelected = showValuesSelected;
    }
    
    public boolean isRtpSelected() {
        return _rtpSelected;
    }

    public void setRtpSelected( boolean rtpSelected ) {
        _rtpSelected = rtpSelected;
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
    
    private QTRegionConfig[] toRegionConfigs( PotentialRegion[] potentialRegions ) {
        QTRegionConfig[] regionConfigs = new QTRegionConfig[ potentialRegions.length ];
        for ( int i = 0; i < potentialRegions.length; i++ ) {
            QTRegionConfig region = new QTRegionConfig( potentialRegions[i] );
            regionConfigs[i] = region;
        }
        return regionConfigs;
    }
    
    private PotentialRegion[] toPotentialRegions( QTRegionConfig[] regionConfigs ) {
        PotentialRegion[] potentialRegions = new PotentialRegion[ regionConfigs.length ];
        for ( int i = 0; i < regionConfigs.length; i++ ) {
            potentialRegions[i] = regionConfigs[i].toPotentialRegion();
        }
        return potentialRegions;
    }
}
