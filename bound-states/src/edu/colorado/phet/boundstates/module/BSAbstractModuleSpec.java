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

    private IntegerRange _numberOfWellsRange;
    private DoubleRange _massMultiplierRange;
    private DoubleRange _offsetRange;
    private DoubleRange _depthRange;
    private DoubleRange _widthRange;
    private DoubleRange _spacingRange;
    private DoubleRange _separationRange;
    private DoubleRange _angularFrequencyRange;

    public BSAbstractModuleSpec() {}
    
    public DoubleRange getAngularFrequencyRange() {
        return _angularFrequencyRange;
    }
  
    protected void setAngularFrequencyRange( DoubleRange angularFrequencyRange ) {
        _angularFrequencyRange = angularFrequencyRange;
    }

    public DoubleRange getDepthRange() {
        return _depthRange;
    }

    protected void setDepthRange( DoubleRange depthRange ) {
        _depthRange = depthRange;
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
    
    protected void setNumberOfWellsRange( IntegerRange numberOfWellsRange ) {
        _numberOfWellsRange = numberOfWellsRange;
    }
    
    public DoubleRange getOffsetRange() {
        return _offsetRange;
    }
    
    protected void setOffsetRange( DoubleRange offsetRange ) {
        _offsetRange = offsetRange;
    }
    
    public DoubleRange getSeparationRange() {
        return _separationRange;
    }
    
    protected void setSeparationRange( DoubleRange separationRange ) {
        _separationRange = separationRange;
    }
    
    public DoubleRange getSpacingRange() {
        return _spacingRange;
    }
    
    protected void setSpacingRange( DoubleRange spacingRange ) {
        _spacingRange = spacingRange;
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
    
    public DoubleRange getWidthRange() {
        return _widthRange;
    }
    
    protected void setWidthRange( DoubleRange widthRange ) {
        _widthRange = widthRange;
    }

    public BSWellType getDefaultWellType() {
        return _defaultWellType;
    }
    
    protected void setDefaultWellType( BSWellType defaultWellType ) {
        _defaultWellType = defaultWellType;
    }
}
