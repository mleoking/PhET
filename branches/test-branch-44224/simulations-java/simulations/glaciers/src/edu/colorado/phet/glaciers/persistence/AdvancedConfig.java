/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;


/**
 * AdvancedConfig is a Java Bean compliant configuration of AdvancedModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedConfig extends IntroConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _iceFlowVectorsSelected;
    private boolean _coordinatesSelected;
    private boolean _glacierLengthVersusTimeChartSelected;
    private boolean _elaVersusTimeChartSelected;
    private boolean _glacialBudgetVersusElevationChartSelected;
    private boolean _temperatureVerusElevationChartSelected;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public AdvancedConfig() {}

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isIceFlowVectorsSelected() {
        return _iceFlowVectorsSelected;
    }

    public void setIceFlowVectorsSelected( boolean selected ) {
        _iceFlowVectorsSelected = selected;
    }

    
    public boolean isCoordinatesSelected() {
        return _coordinatesSelected;
    }

    
    public void setCoordinatesSelected( boolean selected ) {
        _coordinatesSelected = selected;
    }

    
    public boolean isGlacierLengthVersusTimeChartSelected() {
        return _glacierLengthVersusTimeChartSelected;
    }

    
    public void setGlacierLengthVersusTimeChartSelected( boolean selected ) {
        _glacierLengthVersusTimeChartSelected = selected;
    }

    
    public boolean isELAVersusTimeChartSelected() {
        return _elaVersusTimeChartSelected;
    }

    
    public void setELAVersusTimeChartSelected( boolean selected ) {
        _elaVersusTimeChartSelected = selected;
    }

    
    public boolean isGlacialBudgetVersusElevationChartSelected() {
        return _glacialBudgetVersusElevationChartSelected;
    }

    
    public void setGlacialBudgetVersusElevationChartSelected( boolean selected ) {
        _glacialBudgetVersusElevationChartSelected = selected;
    }

    
    public boolean isTemperatureVerusElevationChartSelected() {
        return _temperatureVerusElevationChartSelected;
    }

    
    public void setTemperatureVerusElevationChartSelected( boolean selected ) {
        _temperatureVerusElevationChartSelected = selected;
    }
    
}
