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
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSIntegerRange;

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

    private BSIntegerRange _numberOfWellsRange;
    private BSDoubleRange _massMultiplierRange;
    private BSDoubleRange _offsetRange;
    private BSDoubleRange _depthRange;
    private BSDoubleRange _widthRange;
    private BSDoubleRange _spacingRange;
    private BSDoubleRange _separationRange;
    private BSDoubleRange _angularFrequencyRange;

    public BSAbstractModuleSpec() {}
    
    public BSDoubleRange getAngularFrequencyRange() {
        return _angularFrequencyRange;
    }
  
    protected void setAngularFrequencyRange( BSDoubleRange angularFrequencyRange ) {
        _angularFrequencyRange = angularFrequencyRange;
    }

    public BSDoubleRange getDepthRange() {
        return _depthRange;
    }

    protected void setDepthRange( BSDoubleRange depthRange ) {
        _depthRange = depthRange;
    }
    
    public BSDoubleRange getMassMultiplierRange() {
        return _massMultiplierRange;
    }
    
    protected void setMassMultiplierRange( BSDoubleRange massMultiplierRange ) {
        _massMultiplierRange = massMultiplierRange;
    }
    
    public BSIntegerRange getNumberOfWellsRange() {
        return _numberOfWellsRange;
    }
    
    protected void setNumberOfWellsRange( BSIntegerRange numberOfWellsRange ) {
        _numberOfWellsRange = numberOfWellsRange;
    }
    
    public BSDoubleRange getOffsetRange() {
        return _offsetRange;
    }
    
    protected void setOffsetRange( BSDoubleRange offsetRange ) {
        _offsetRange = offsetRange;
    }
    
    public BSDoubleRange getSeparationRange() {
        return _separationRange;
    }
    
    protected void setSeparationRange( BSDoubleRange separationRange ) {
        _separationRange = separationRange;
    }
    
    public BSDoubleRange getSpacingRange() {
        return _spacingRange;
    }
    
    protected void setSpacingRange( BSDoubleRange spacingRange ) {
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
    
    public BSDoubleRange getWidthRange() {
        return _widthRange;
    }
    
    protected void setWidthRange( BSDoubleRange widthRange ) {
        _widthRange = widthRange;
    }

    public BSWellType getDefaultWellType() {
        return _defaultWellType;
    }
    
    protected void setDefaultWellType( BSWellType defaultWellType ) {
        _defaultWellType = defaultWellType;
    }
}
