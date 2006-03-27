/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.color;

import java.awt.Color;


/**
 * BSColorScheme is a custom color scheme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSColorScheme {

    private Color _chartColor;
    private Color _tickColor;
    private Color _gridlineColor;
    private Color _annotationColor;
    private Color _regionMarkerColor;
    
    private Color _eigenstateNormalColor;
    private Color _eigenstateHiliteColor;
    private Color _eigenstateSelectionColor;
    private Color _potentialEnergyColor;
    
    private Color _realColor;
    private Color _imaginaryColor;
    private Color _magnitudeColor;
    
    public BSColorScheme( BSColorScheme colorScheme ) {
        _chartColor = colorScheme.getChartColor();
        _tickColor = colorScheme.getTickColor();
        _gridlineColor = colorScheme.getGridlineColor();
        _annotationColor = colorScheme.getAnnotationColor();
        _regionMarkerColor = colorScheme.getRegionMarkerColor();
        
        _eigenstateNormalColor = colorScheme.getEigenstateNormalColor();
        _eigenstateHiliteColor = colorScheme.getEigenstateHiliteColor();
        _eigenstateSelectionColor = colorScheme.getEigenstateSelectionColor();
        _potentialEnergyColor = colorScheme.getPotentialEnergyColor();
        
        _realColor = colorScheme.getRealColor();
        _imaginaryColor = colorScheme.getImaginaryColor();
        _magnitudeColor = colorScheme.getMagnitudeColor();
    }
    
    protected BSColorScheme() {
    }
    
    public Color getEigenstateHiliteColor() {
        return _eigenstateHiliteColor;
    }

    
    public void setEigenstateHiliteColor( Color eigenstateHiliteColor ) {
        _eigenstateHiliteColor = eigenstateHiliteColor;
    }

    
    public Color getEigenstateNormalColor() {
        return _eigenstateNormalColor;
    }

    
    public void setEigenstateNormalColor( Color eigenstateNormalColor ) {
        _eigenstateNormalColor = eigenstateNormalColor;
    }

    
    public Color getEigenstateSelectionColor() {
        return _eigenstateSelectionColor;
    }

    
    public void setEigenstateSelectionColor( Color eigenstateSelectionColor ) {
        _eigenstateSelectionColor = eigenstateSelectionColor;
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
}
