/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;

/**
 * BSAbstractModuleSpec contains the information needed to set up a module,
 * including ranges, flags, combo box choices, etc.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractModuleSpec {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _id;
    
    private BSWellType[] _wellTypes;
    private BSWellType _defaultWellType;

    private boolean _offsetControlSupported;
    private boolean _superpositionControlsSupported;
    private boolean _particleControlsSupported;
    private boolean _magnifyingGlassSupported;
    private boolean _magnifyingGlassSelected;
    private DoubleRange _massMultiplierRange;
    private boolean _averageProbabilityDensitySupported;
    
    private IntegerRange _numberOfWellsRange;
    
    private DoubleRange _fieldConstantRange;
    
    private BSPotentialSpec _asymmetricSpec;
    private BSPotentialSpec _coulomb1DSpec;
    private BSPotentialSpec _coulomb3DSpec;
    private BSPotentialSpec _harmonicOscillatorSpec;
    private BSPotentialSpec _squareSpec;

    private double _magnification;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAbstractModuleSpec() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public String getId() {
        return _id;
    }
    
    public void setId( String id ) {
        _id = id;
    }
    
    public boolean isOffsetControlSupported() {
        return _offsetControlSupported;
    }
    
    protected void setOffsetControlSupported( boolean offsetControlSupported ) {
        _offsetControlSupported = offsetControlSupported;
    }

    public DoubleRange getMassMultiplierRange() {
        return _massMultiplierRange;
    }
    
    protected void setMassMultiplierRange( DoubleRange massMultiplierRange ) {
        _massMultiplierRange = massMultiplierRange;
    }
    
    public IntegerRange getNumberOfWellsRange() {
        return _numberOfWellsRange;
    }
    
    public void setNumberOfWellsRange( IntegerRange numberOfWellsRange ) {
        _numberOfWellsRange = numberOfWellsRange;
    }

    public void setFieldConstantRange( DoubleRange range ) {
        _fieldConstantRange = range;
    }
    
    public DoubleRange getFieldConstantRange() {
        return _fieldConstantRange;
    }
    
    public boolean isParticleControlsSupported() {
        return _particleControlsSupported;
    }
    
    protected void setParticleControlsSupported( boolean supportsParticleControls ) {
        _particleControlsSupported = supportsParticleControls;
    }
    
    public boolean isSuperpositionControlsSupported() {
        return _superpositionControlsSupported;
    }
    
    protected void setSuperpositionControlsSupported( boolean supportsSuperpositionControls ) {
        _superpositionControlsSupported = supportsSuperpositionControls;
    }
    
    public BSWellType[] getWellTypes() {
        return _wellTypes;
    }
    
    protected void setWellTypes( BSWellType[] wellTypes ) {
        _wellTypes = wellTypes;
    }

    public BSWellType getDefaultWellType() {
        return _defaultWellType;
    }
    
    protected void setDefaultWellType( BSWellType defaultWellType ) {
        _defaultWellType = defaultWellType;
    }
    
    public void setAverageProbabilityDensityIsSupported( boolean supported ) {
        _averageProbabilityDensitySupported = supported;
    }
    
    public boolean isAverageProbabilityDensitySupported() {
        return _averageProbabilityDensitySupported;
    }
    
    public boolean isMagnifyingGlassSupported() {
        return _magnifyingGlassSupported;
    }
    
    protected void setMagnifyingGlassSupported( boolean magnifyingGlassSupported ) {
        _magnifyingGlassSupported = magnifyingGlassSupported;
    }

    public boolean isMagnifyingGlassSelected() {
        return _magnifyingGlassSelected;
    }
    
    protected void setMagnifyingGlassSelected( boolean magnifyingGlassSelected ) {
        _magnifyingGlassSelected = magnifyingGlassSelected;
    }
    
    public double getMagnification() {
        return _magnification;
    }

    protected void setMagnification( double magnification ) {
        _magnification = magnification;
    }
    
    public BSPotentialSpec getAsymmetricSpec() {
        return _asymmetricSpec;
    }
    
    protected void setAsymmetricSpec( BSPotentialSpec asymmetricRangeSpec ) {
        _asymmetricSpec = asymmetricRangeSpec;
    }
    
    public BSPotentialSpec getCoulomb1DSpec() {
        return _coulomb1DSpec;
    }
    
    protected void setCoulomb1DSpec( BSPotentialSpec coulomb1DRangeSpec ) {
        _coulomb1DSpec = coulomb1DRangeSpec;
    }
    
    public BSPotentialSpec getCoulomb3DSpec() {
        return _coulomb3DSpec;
    }
    
    protected void setCoulomb3DSpec( BSPotentialSpec coulomb3DRangeSpec ) {
        _coulomb3DSpec = coulomb3DRangeSpec;
    }
    
    public BSPotentialSpec getHarmonicOscillatorSpec() {
        return _harmonicOscillatorSpec;
    }
    
    protected void setHarmonicOscillatorSpec( BSPotentialSpec harmonicOscillatorRangeSpec ) {
        _harmonicOscillatorSpec = harmonicOscillatorRangeSpec;
    }

    public BSPotentialSpec getSquareSpec() {
        return _squareSpec;
    }
    
    protected void setSquareSpec( BSPotentialSpec squareRangeSpec ) {
        _squareSpec = squareRangeSpec;
    }
    
    public BSPotentialSpec getRangeSpec( BSWellType wellType ) {
        BSPotentialSpec rangeSpec = null;
        if ( wellType == BSWellType.ASYMMETRIC ) {
            rangeSpec = _asymmetricSpec;
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            rangeSpec = _coulomb1DSpec;
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            rangeSpec = _coulomb3DSpec;
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            rangeSpec = _harmonicOscillatorSpec;
        }
        else if ( wellType == BSWellType.SQUARE ) {
            rangeSpec = _squareSpec;
        }
        else {
            throw new UnsupportedOperationException( "unsupported well type: " + wellType );
        }
        return rangeSpec;
    }
}
