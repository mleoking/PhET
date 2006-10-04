/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.Color;


/**
 * QTColorScheme is a custom color scheme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTColorScheme {

    private Color _chartColor;
    private Color _tickColor;
    private Color _gridlineColor;
    private Color _annotationColor;
    private Color _regionMarkerColor;
    private Color _totalEnergyColor;
    private Color _potentialEnergyColor;
    private Color _realColor;
    private Color _imaginaryColor;
    private Color _magnitudeColor;
    private Color _probabilityDensityColor;
    
    public QTColorScheme( QTColorScheme colorScheme ) {
        _chartColor = colorScheme.getChartColor();
        _tickColor = colorScheme.getTickColor();
        _gridlineColor = colorScheme.getGridlineColor();
        _annotationColor = colorScheme.getAnnotationColor();
        _regionMarkerColor = colorScheme.getRegionMarkerColor();
        _totalEnergyColor = colorScheme.getTotalEnergyColor();
        _potentialEnergyColor = colorScheme.getPotentialEnergyColor();
        _realColor = colorScheme.getRealColor();
        _imaginaryColor = colorScheme.getImaginaryColor();
        _magnitudeColor = colorScheme.getMagnitudeColor();
        _probabilityDensityColor = colorScheme.getProbabilityDensityColor();
    }
    
    protected QTColorScheme() {
    }
    
    public Color getChartColor() {
        return _chartColor;
    }
    
    public void setChartColor( Color chartColor ) {
        _chartColor = chartColor;
    }
    
    public Color getGridlineColor() {
        return _gridlineColor;
    }
    
    public void setGridlineColor( Color gridlineColor ) {
        _gridlineColor = gridlineColor;
    }
    
    public Color getAnnotationColor() {
        return _annotationColor;
    }
    
    public void setAnnotationColor( Color annotationColor ) {
        _annotationColor = annotationColor;
    }
    
    public Color getImaginaryColor() {
        return _imaginaryColor;
    }
    
    public void setImaginaryColor( Color imaginaryColor ) {
        _imaginaryColor = imaginaryColor;
    }
    
    public Color getMagnitudeColor() {
        return _magnitudeColor;
    }
    
    public void setMagnitudeColor( Color magnitudeColor ) {
        _magnitudeColor = magnitudeColor;
    }
    
    public Color getPotentialEnergyColor() {
        return _potentialEnergyColor;
    }
    
    public void setPotentialEnergyColor( Color potentialEnergyColor ) {
        _potentialEnergyColor = potentialEnergyColor;
    }
    
    public Color getProbabilityDensityColor() {
        return _probabilityDensityColor;
    }
    
    public void setProbabilityDensityColor( Color probabilityDensityColor ) {
        _probabilityDensityColor = probabilityDensityColor;
    }
    
    public Color getRealColor() {
        return _realColor;
    }
    
    public void setRealColor( Color realColor ) {
        _realColor = realColor;
    }
    
    public Color getRegionMarkerColor() {
        return _regionMarkerColor;
    }
    
    public void setRegionMarkerColor( Color regionMarkerColor ) {
        _regionMarkerColor = regionMarkerColor;
    }
    
    public Color getTickColor() {
        return _tickColor;
    }
    
    public void setTickColor( Color tickColor ) {
        _tickColor = tickColor;
    }
    
    public Color getTotalEnergyColor() {
        return _totalEnergyColor;
    }
    
    public void setTotalEnergyColor( Color totalEnergyColor ) {
        _totalEnergyColor = totalEnergyColor;
    }
}
