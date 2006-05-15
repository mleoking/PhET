/* Copyright 2005, University of Colorado */

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

    private BSWellType[] _wellTypes;
    private BSWellType _defaultWellType;

    private boolean _superpositionControlsSupported;
    private boolean _particleControlsSupported;
    private boolean _magnifyingGlassSupported;
    private boolean _magnifyingGlassSelected;

    private DoubleRange _massMultiplierRange;
    
    private IntegerRange _numberOfWellsRange;
    
    private BSWellRangeSpec _asymmetricRangeSpec;
    private BSWellRangeSpec _coulomb1DRangeSpec;
    private BSWellRangeSpec _coulomb3DRangeSpec;
    private BSWellRangeSpec _harmonicOscillatorRangeSpec;
    private BSWellRangeSpec _squareRangeSpec;

    public BSAbstractModuleSpec() {}
    
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
    
    public BSWellRangeSpec getAsymmetricRangeSpec() {
        return _asymmetricRangeSpec;
    }
    
    protected void setAsymmetricRangeSpec( BSWellRangeSpec asymmetricRangeSpec ) {
        _asymmetricRangeSpec = asymmetricRangeSpec;
    }
    
    public BSWellRangeSpec getCoulomb1DRangeSpec() {
        return _coulomb1DRangeSpec;
    }
    
    protected void setCoulomb1DRangeSpec( BSWellRangeSpec coulomb1DRangeSpec ) {
        _coulomb1DRangeSpec = coulomb1DRangeSpec;
    }
    
    public BSWellRangeSpec getCoulomb3DRangeSpec() {
        return _coulomb3DRangeSpec;
    }
    
    protected void setCoulomb3DRangeSpec( BSWellRangeSpec coulomb3DRangeSpec ) {
        _coulomb3DRangeSpec = coulomb3DRangeSpec;
    }
    
    public BSWellRangeSpec getHarmonicOscillatorRangeSpec() {
        return _harmonicOscillatorRangeSpec;
    }
    
    protected void setHarmonicOscillatorRangeSpec( BSWellRangeSpec harmonicOscillatorRangeSpec ) {
        _harmonicOscillatorRangeSpec = harmonicOscillatorRangeSpec;
    }

    public BSWellRangeSpec getSquareRangeSpec() {
        return _squareRangeSpec;
    }
    
    protected void setSquareRangeSpec( BSWellRangeSpec squareRangeSpec ) {
        _squareRangeSpec = squareRangeSpec;
    }
    
    public BSWellRangeSpec getRangeSpec( BSWellType wellType ) {
        BSWellRangeSpec rangeSpec = null;
        if ( wellType == BSWellType.ASYMMETRIC ) {
            rangeSpec = _asymmetricRangeSpec;
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            rangeSpec = _coulomb1DRangeSpec;
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            rangeSpec = _coulomb3DRangeSpec;
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            rangeSpec = _harmonicOscillatorRangeSpec;
        }
        else if ( wellType == BSWellType.SQUARE ) {
            rangeSpec = _squareRangeSpec;
        }
        else {
            throw new UnsupportedOperationException( "unsupported well type: " + wellType );
        }
        return rangeSpec;
    }
}
