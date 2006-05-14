/* Copyright 2005, University of Colorado */

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
import edu.colorado.phet.boundstates.view.BSBottomPlot;


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
    
    // Model
    //XXX
    
    // Controls
    private String _bottomPlotMode;
    private boolean _magnifyingGlassSelected;
    private boolean _realSelected;
    private boolean _imaginarySelected;
    private boolean _magnitudeSelected;
    private boolean _phaseSelected;
    //XXX
    
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

    public BSBottomPlotMode getBottomPlotMode() {
        return BSBottomPlotMode.getByName( _bottomPlotMode );
    }
    
    public void setBottomPlotMode( BSBottomPlotMode bottomPlotMode ) {
        _bottomPlotMode = bottomPlotMode.getName();
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

}
