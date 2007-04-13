/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.persistence;

import java.awt.Color;

import edu.colorado.phet.quantumtunneling.color.BlackColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;


/**
 * QTColorSchemeConfig is a JavaBean-compliant data structure for saving a color scheme.
 * <p>
 * A QTColorScheme consists of a set of java.awt.Colors.  But Colors are not Java Bean
 * compliant (they are immutable and have no zero-arg constructor) so they cannot be
 * XMLEncoded. To workaround this, we serialize each java.awt.Color as an int[].
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTColorSchemeConfig implements QTSerializable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int[] _chartColor;
    private int[] _tickColor;
    private int[] _gridlineColor;
    private int[] _annotationColor;
    private int[] _regionMarkerColor;
    private int[] _totalEnergyColor;
    private int[] _potentialEnergyColor;
    private int[] _realColor;
    private int[] _imaginaryColor;
    private int[] _magnitudeColor;
    private int[] _probabilityDensityColor;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero arg constructor for Java Bean compliance.
     */
    public QTColorSchemeConfig() {}
    
    /**
     * Constructor that converts a QTColorScheme to something that can be XMLEncoded.
     * 
     * @param colorScheme
     */
    public QTColorSchemeConfig( QTColorScheme colorScheme ) {
        setChartColor( toArray( colorScheme.getChartColor() ) );
        setTickColor( toArray( colorScheme.getTickColor() ) );
        setGridlineColor( toArray( colorScheme.getGridlineColor() ) );
        setAnnotationColor( toArray( colorScheme.getAnnotationColor() ) );
        setRegionMarkerColor( toArray( colorScheme.getRegionMarkerColor() ) );
        setTotalEnergyColor( toArray( colorScheme.getTotalEnergyColor() ) );
        setPotentialEnergyColor( toArray( colorScheme.getPotentialEnergyColor() ) );
        setRealColor( toArray( colorScheme.getRealColor() ) );
        setImaginaryColor( toArray( colorScheme.getImaginaryColor() ) );
        setMagnitudeColor( toArray( colorScheme.getMagnitudeColor() ) );
        setProbabilityDensityColor( toArray( colorScheme.getProbabilityDensityColor() ) );
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

    public int[] getTotalEnergyColor() {
        return _totalEnergyColor;
    }

    public void setTotalEnergyColor( int[] totalEnergyColor ) {
        _totalEnergyColor = totalEnergyColor;
    }
    
    //----------------------------------------------------------------------------
    // Conversion utilities
    //----------------------------------------------------------------------------
    
    /**
     * Converts to a QTColorScheme object.
     * 
     * @return
     */
    public QTColorScheme toQTColorScheme() {
        
        QTColorScheme colorScheme = new QTColorScheme( new BlackColorScheme() );
        
        colorScheme.setChartColor( toColor( getChartColor() ) );
        colorScheme.setTickColor( toColor( getTickColor() ) );
        colorScheme.setGridlineColor( toColor( getGridlineColor() ) );
        colorScheme.setAnnotationColor( toColor( getAnnotationColor() ) );
        colorScheme.setRegionMarkerColor( toColor( getRegionMarkerColor() ) );
        colorScheme.setTotalEnergyColor( toColor( getTotalEnergyColor() ) );
        colorScheme.setPotentialEnergyColor( toColor( getPotentialEnergyColor() ) );
        colorScheme.setRealColor( toColor( getRealColor() ) );
        colorScheme.setImaginaryColor( toColor( getImaginaryColor() ) );
        colorScheme.setMagnitudeColor( toColor( getMagnitudeColor() ) );
        colorScheme.setProbabilityDensityColor( toColor( getProbabilityDensityColor() ) );
        
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
