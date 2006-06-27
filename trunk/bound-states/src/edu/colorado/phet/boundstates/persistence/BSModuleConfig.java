/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;


/**
 * BSModuleConfig is a JavaBean-compliant data structure that stores
 * module configuration information for BSModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSModuleConfig implements BSSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Clock
    private boolean _clockRunning;
    private int _clockIndex;
    
    // Model
    private BSParticle _particle;
    private BSAsymmetricWell _asymmetricWell;
    private BSCoulomb1DWells _coulomb1DWells;
    private BSCoulomb3DWell _coulomb3DWell;
    private BSHarmonicOscillatorWell _harmonicOscillatorWell;
    private BSSquareWells _squareWells;
    private int _hiliteEigenstateIndex;
    private String _selectedWellTypeName;
    private double[] _superpositionCoefficients;
    
    // Controls
    private String _bottomPlotModeName;
    private boolean _magnifyingGlassSelected;
    private boolean _realSelected;
    private boolean _imaginarySelected;
    private boolean _magnitudeSelected;
    private boolean _phaseSelected;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSModuleConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public boolean isClockRunning() {
        return _clockRunning;
    }

    public void setClockIndex( int clockIndex ) {
        _clockIndex = clockIndex;
    }
    
    public int getClockIndex() {
        return _clockIndex;
    }
    
    public String getBottomPlotModeName() {
        return _bottomPlotModeName;
    }
    
    public void setBottomPlotModeName( String bottomPlotModeName ) {
        _bottomPlotModeName = bottomPlotModeName;
    }
    
    public boolean isRealSelected() {
        return _realSelected;
    }
    
    public void setRealSelected( boolean realSelected ) {
        _realSelected = realSelected;
    }
    
    public boolean isImaginarySelected() {
        return _imaginarySelected;
    }
    
    public void setImaginarySelected( boolean imaginarySelected ) {
        _imaginarySelected = imaginarySelected;
    }
    
    public boolean isMagnitudeSelected() {
        return _magnitudeSelected;
    }
    
    public void setMagnitudeSelected( boolean magnitudeSelected ) {
        _magnitudeSelected = magnitudeSelected;
    }
    
    public boolean isPhaseSelected() {
        return _phaseSelected;
    }
    
    public void setPhaseSelected( boolean phaseSelected ) {
        _phaseSelected = phaseSelected;
    }

    public boolean isMagnifyingGlassSelected() {
        return _magnifyingGlassSelected;
    }
  
    public void setMagnifyingGlassSelected( boolean magnifyingGlassSelected ) {
        _magnifyingGlassSelected = magnifyingGlassSelected;
    }

    public BSParticle getParticle() {
        return _particle;
    }
    
    public void setParticle( BSParticle particle ) {
        _particle = particle;
    }

    public BSAsymmetricWell getAsymmetricWell() {
        return _asymmetricWell;
    }
    
    public void setAsymmetricWell( BSAsymmetricWell asymmetricWell ) {
        _asymmetricWell = asymmetricWell;
    }
    
    public BSCoulomb1DWells getCoulomb1DWells() {
        return _coulomb1DWells;
    }
    
    public void setCoulomb1DWells( BSCoulomb1DWells coulomb1DWells ) {
        _coulomb1DWells = coulomb1DWells;
    }
    
    public BSCoulomb3DWell getCoulomb3DWell() {
        return _coulomb3DWell;
    }
    
    public void setCoulomb3DWell( BSCoulomb3DWell coulomb3DWell ) {
        _coulomb3DWell = coulomb3DWell;
    }
    
    public BSHarmonicOscillatorWell getHarmonicOscillatorWell() {
        return _harmonicOscillatorWell;
    }
    
    public void setHarmonicOscillatorWell( BSHarmonicOscillatorWell harmonicOscillatorWell ) {
        _harmonicOscillatorWell = harmonicOscillatorWell;
    }
    
    public int getHiliteEigenstateIndex() {
        return _hiliteEigenstateIndex;
    }
    
    public void setHiliteEigenstateIndex( int hiliteEigenstateIndex ) {
        _hiliteEigenstateIndex = hiliteEigenstateIndex;
    }
    
    public String getSelectedWellTypeName() {
        return _selectedWellTypeName;
    }
    
    public void setSelectedWellTypeName( String selectedWellTypeName ) {
        _selectedWellTypeName = selectedWellTypeName;
    }

    public BSSquareWells getSquareWells() {
        return _squareWells;
    }
    
    public void setSquareWells( BSSquareWells squareWells ) {
        _squareWells = squareWells;
    }
    
    public double[] getSuperpositionCoefficients() {
        return _superpositionCoefficients;
    }
    
    public void setSuperpositionCoefficients( double[] superpositionCoefficients ) {
        _superpositionCoefficients = superpositionCoefficients;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for non-JavaBean objects
    //----------------------------------------------------------------------------

    public BSBottomPlotMode loadBottomPlotMode() {
        return BSBottomPlotMode.getByName( _bottomPlotModeName );
    }
    
    public void saveBottomPlotMode( BSBottomPlotMode bottomPlotMode ) {
        setBottomPlotModeName( bottomPlotMode.getName() );
    }
    
    public BSWellType loadSelectedWellType() {
        return BSWellType.getByName( _selectedWellTypeName );
    }
    
    public void saveSelectedWellType( BSWellType wellType ) {
        setSelectedWellTypeName( wellType.getName() );
    }
}
