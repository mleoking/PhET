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

import java.awt.Color;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;


/**
 * BSColorSchemeConfig is a JavaBean-compliant data structure for saving a color scheme.
 * <p>
 * A BSColorScheme consists of a set of java.awt.Colors.  But Colors are not Java Bean
 * compliant (they are immutable and have no zero-arg constructor) so they cannot be
 * XMLEncoded. To workaround this, we serialize each java.awt.Color as an int[].
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSColorSchemeConfig implements BSSerializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int[] _chartColor;
    private int[] _tickColor;
    private int[] _gridlineColor;
    private int[] _annotationColor;
    private int[] _regionMarkerColor;
    private int[] _eigenstateNormalColor;
    private int[] _eigenstateHiliteColor;
    private int[] _eigenstateSelectionColor;
    private int[] _potentialEnergyColor;
    private int[] _realColor;
    private int[] _imaginaryColor;
    private int[] _magnitudeColor;
    private int[] _probabilityDensityColor;
    private int[] _magnifyingGlassBezelColor;
    private int[] _magnifyingGlassHandleColor;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero arg constructor for Java Bean compliance.
     */
    public BSColorSchemeConfig() {}
    
    /**
     * Constructor that converts a BSColorScheme to something that can be XMLEncoded.
     * 
     * @param colorScheme
     */
    public BSColorSchemeConfig( BSColorScheme colorScheme ) {
        setChartColor( toArray( colorScheme.getChartColor() ) );
        setTickColor( toArray( colorScheme.getTickColor() ) );
        setGridlineColor( toArray( colorScheme.getGridlineColor() ) );
        setEigenstateNormalColor( toArray( colorScheme.getEigenstateNormalColor() ) );
        setEigenstateHiliteColor( toArray( colorScheme.getEigenstateHiliteColor() ) );
        setEigenstateSelectionColor( toArray( colorScheme.getEigenstateSelectionColor() ) );
        setPotentialEnergyColor( toArray( colorScheme.getPotentialEnergyColor() ) );
        setRealColor( toArray( colorScheme.getRealColor() ) );
        setImaginaryColor( toArray( colorScheme.getImaginaryColor() ) );
        setMagnitudeColor( toArray( colorScheme.getMagnitudeColor() ) );
        setMagnifyingGlassBezelColor( toArray( colorScheme.getMagnifyingGlassBezelColor() ) );
        setMagnifyingGlassHandleColor( toArray( colorScheme.getMagnifyingGlassHandleColor() ) );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public int[] getAnnotationColor() {
        return _annotationColor;
    }

    public void setAnnotationColor( int[] annotationColor ) {
        _annotationColor = annotationColor;
    }

    public int[] getChartColor() {
        return _chartColor;
    }

    public void setChartColor( int[] chartColor ) {
        _chartColor = chartColor;
    }

    public int[] getGridlineColor() {
        return _gridlineColor;
    }

    public void setGridlineColor( int[] gridlineColor ) {
        _gridlineColor = gridlineColor;
    }

    public int[] getImaginaryColor() {
        return _imaginaryColor;
    }

    public void setImaginaryColor( int[] imaginaryColor ) {
        _imaginaryColor = imaginaryColor;
    }

    public int[] getMagnitudeColor() {
        return _magnitudeColor;
    }

    public void setMagnitudeColor( int[] magnitudeColor ) {
        _magnitudeColor = magnitudeColor;
    }

    public int[] getPotentialEnergyColor() {
        return _potentialEnergyColor;
    }

    public void setPotentialEnergyColor( int[] potentialEnergyColor ) {
        _potentialEnergyColor = potentialEnergyColor;
    }

    public int[] getProbabilityDensityColor() {
        return _probabilityDensityColor;
    }

    public void setProbabilityDensityColor( int[] probabilityDensityColor ) {
        _probabilityDensityColor = probabilityDensityColor;
    }

    public int[] getRealColor() {
        return _realColor;
    }

    public void setRealColor( int[] realColor ) {
        _realColor = realColor;
    }

    public int[] getRegionMarkerColor() {
        return _regionMarkerColor;
    }

    public void setRegionMarkerColor( int[] regionMarkerColor ) {
        _regionMarkerColor = regionMarkerColor;
    }

    public int[] getTickColor() {
        return _tickColor;
    }

    public void setTickColor( int[] tickColor ) {
        _tickColor = tickColor;
    }
    
    public int[] getEigenstateHiliteColor() {
        return _eigenstateHiliteColor;
    }
    
    public void setEigenstateHiliteColor( int[] eigenstateHiliteColor ) {
        _eigenstateHiliteColor = eigenstateHiliteColor;
    }
    
    public int[] getEigenstateNormalColor() {
        return _eigenstateNormalColor;
    }
   
    public void setEigenstateNormalColor( int[] eigenstateNormalColor ) {
        _eigenstateNormalColor = eigenstateNormalColor;
    }
    
    public int[] getEigenstateSelectionColor() {
        return _eigenstateSelectionColor;
    }

    public void setEigenstateSelectionColor( int[] eigenstateSelectionColor ) {
        _eigenstateSelectionColor = eigenstateSelectionColor;
    }
    
    public int[] getMagnifyingGlassBezelColor() {
        return _magnifyingGlassBezelColor;
    }
  
    public void setMagnifyingGlassBezelColor( int[] magnifyingGlassBezelColor ) {
        _magnifyingGlassBezelColor = magnifyingGlassBezelColor;
    }
    
    public int[] getMagnifyingGlassHandleColor() {
        return _magnifyingGlassHandleColor;
    }
    
    public void setMagnifyingGlassHandleColor( int[] magnifyingGlassHandleColor ) {
        _magnifyingGlassHandleColor = magnifyingGlassHandleColor;
    }
    
    //----------------------------------------------------------------------------
    // Conversion utilities
    //----------------------------------------------------------------------------

    /**
     * Converts to a BSColorScheme object.
     * 
     * @return a BSColorScheme
     */
    public BSColorScheme toBSColorScheme() {
        
        BSColorScheme colorScheme = new BSColorScheme( new BSBlackColorScheme() );
        
        colorScheme.setChartColor( toColor( getChartColor() ) );
        colorScheme.setTickColor( toColor( getTickColor() ) );
        colorScheme.setGridlineColor( toColor( getGridlineColor() ) );
        colorScheme.setEigenstateNormalColor( toColor( getEigenstateNormalColor() ) );
        colorScheme.setEigenstateHiliteColor( toColor( getEigenstateHiliteColor() ) );
        colorScheme.setEigenstateSelectionColor( toColor( getEigenstateSelectionColor() ) );
        colorScheme.setPotentialEnergyColor( toColor( getPotentialEnergyColor() ) );
        colorScheme.setRealColor( toColor( getRealColor() ) );
        colorScheme.setImaginaryColor( toColor( getImaginaryColor() ) );
        colorScheme.setMagnitudeColor( toColor( getMagnitudeColor() ) );
        colorScheme.setMagnifyingGlassBezelColor( toColor( getMagnifyingGlassBezelColor() ) );
        colorScheme.setMagnifyingGlassHandleColor( toColor( getMagnifyingGlassHandleColor() ) );
        
        return colorScheme;
    }
    
    /*
     * Converts an array of RGB components to a Color.
     * @param components
     * @return Color
     */
    private Color toColor( int[] components ) {
        return new Color( components[0], components[1], components[2] );
    }
    
    /*
     * Converts a Color to an array of RBG components.
     * @param color
     * @return int[]
     */
    private int[] toArray( Color color ) {
        int[] components = new int[3];
        components[0] = color.getRed();
        components[1] = color.getGreen();
        components[2] = color.getBlue();
        return components;
    }
}
