/* Copyright 2006, University of Colorado */

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

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Color _chartColor;
    private Color _tickColor;
    private Color _gridlineColor;
    
    private Color _eigenstateNormalColor;
    private Color _eigenstateHiliteColor;
    private Color _eigenstateSelectionColor;
    private Color _potentialEnergyColor;
    
    private Color _realColor;
    private Color _imaginaryColor;
    private Color _magnitudeColor;
    
    private Color _magnifyingGlassBezelColor;
    private Color _magnifyingGlassHandleColor;
    
    private Color _dragHandleColor;
    private Color _dragHandleHiliteColor;
    private Color _dragHandleValueColor;
    private Color _dragHandleMarkersColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a color scheme that is initialized using some existing
     * color scheme.
     * 
     * @param colorScheme
     */
    public BSColorScheme( BSColorScheme colorScheme ) {
        copy( colorScheme );
    }
    
    /*
     * Color schemes can only be created by using one of the pre-defined
     * subclasses as a starting point.  This constructor is provided 
     * for use by subclasses, who must fully initialize themselves.
     */
    protected BSColorScheme() {
    }
    
    /*
     * Copies a color scheme.
     * 
     * @param colorScheme
     */
    public void copy( BSColorScheme colorScheme ) {
        _chartColor = colorScheme.getChartColor();
        _tickColor = colorScheme.getTickColor();
        _gridlineColor = colorScheme.getGridlineColor();
        
        _eigenstateNormalColor = colorScheme.getEigenstateNormalColor();
        _eigenstateHiliteColor = colorScheme.getEigenstateHiliteColor();
        _eigenstateSelectionColor = colorScheme.getEigenstateSelectionColor();
        _potentialEnergyColor = colorScheme.getPotentialEnergyColor();
        
        _realColor = colorScheme.getRealColor();
        _imaginaryColor = colorScheme.getImaginaryColor();
        _magnitudeColor = colorScheme.getMagnitudeColor();
        
        _magnifyingGlassBezelColor = colorScheme.getMagnifyingGlassBezelColor();
        _magnifyingGlassHandleColor = colorScheme.getMagnifyingGlassHandleColor();
        
        _dragHandleColor = colorScheme.getDragHandleColor();
        _dragHandleHiliteColor = colorScheme.getDragHandleHiliteColor();
        _dragHandleValueColor = colorScheme.getDragHandleValueColor();
        _dragHandleMarkersColor = colorScheme.getDragHandleMarkersColor();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
    
    public Color getTickColor() {
        return _tickColor;
    }
    
    public void setTickColor( Color tickColor ) {
        _tickColor = tickColor;
    }
 
    public Color getMagnifyingGlassBezelColor() {
        return _magnifyingGlassBezelColor;
    }
  
    public void setMagnifyingGlassBezelColor( Color magnifyingGlassBezelColor ) {
        _magnifyingGlassBezelColor = magnifyingGlassBezelColor;
    }

    public Color getMagnifyingGlassHandleColor() {
        return _magnifyingGlassHandleColor;
    }
    
    public void setMagnifyingGlassHandleColor( Color magnifyingGlassHandleColor ) {
        _magnifyingGlassHandleColor = magnifyingGlassHandleColor;
    }
   
    public Color getDragHandleColor() {
        return _dragHandleColor;
    }
    
    public void setDragHandleColor( Color dragHandleColor ) {
        _dragHandleColor = dragHandleColor;
    }
   
    public Color getDragHandleHiliteColor() {
        return _dragHandleHiliteColor;
    }
    
    public void setDragHandleHiliteColor( Color dragHandleHiliteColor ) {
        _dragHandleHiliteColor = dragHandleHiliteColor;
    }
    
    public Color getDragHandleValueColor() {
        return _dragHandleValueColor;
    }

    public void setDragHandleValueColor( Color dragHandleValueColor ) {
        _dragHandleValueColor = dragHandleValueColor;
    }
    
    public Color getDragHandleMarkersColor() {
        return _dragHandleMarkersColor;
    }
    
    public void setDragHandleMarkersColor( Color dragHandleRegionMarkersColor ) {
        _dragHandleMarkersColor = dragHandleRegionMarkersColor;
    }
}
