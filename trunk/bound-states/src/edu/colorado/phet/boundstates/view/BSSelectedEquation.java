/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.geom.AffineTransform;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSSelectedEquation displays the wave function equation of the selected eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSelectedEquation extends BSAbstractWaveFunctionEquation implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _xform; // reusable transform
    private String _superpositionString;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSelectedEquation() {
        super();
        _xform = new AffineTransform();
        _superpositionString = SimStrings.get( "label.equation.superposition" );
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractWaveFunctionEquation implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme.
     * The color of the text is set to the color used for the selected eigenstate.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        setHTMLColor( colorScheme.getEigenstateSelectionColor() );
    }
    
    /*
     * Updates the display to match the model.
     * Subclasses should call this from their update method.
     */
    protected void updateDisplay() {
        
        if ( getModel() != null ) {
            
            // Determine which eigenstate...
            int eigenstateSubscript = getEigenstateSubscript();

            // Set the text...
            String text = null;
            if ( eigenstateSubscript == EIGENSTATE_SUBSCRIPT_NONE ) {
                text = "";
            }
            else if ( eigenstateSubscript == EIGENSTATE_SUBSCRIPT_SUPERPOSITION ) {
                text = _superpositionString;
            }
            else {
                if ( getMode() == BSBottomPlotMode.WAVE_FUNCTION ) {
                    text = "<html>" + BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)</html>";
                }
                else if ( getMode() == BSBottomPlotMode.PROBABILITY_DENSITY ) {
                    text = "<html>|" + BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)|<sup>2</sup></html>";
                }
                else {
                    throw new UnsupportedOperationException( "unsupported mode: " + getMode() );
                }
            }
            setHTML( text );

            // Translate so the upper right corner is at the location...
            _xform.setToIdentity();
            _xform.translate( getLocation().getX(), getLocation().getY() );
            _xform.translate( -getFullBounds().getWidth(), 0 ); // upper right
            setTransform( _xform );
        }
    }
    
    //----------------------------------------------------------------------------
    // private methods
    //----------------------------------------------------------------------------
    
    /*
     * Gets the eigenstate subscript.
     * If one eigenstate is selected, then its subscript is returned.
     * If no eigenstate is selected, then EIGENSTATE_SUBSCRIPT_NONE is returned.
     * If more than one eigenstate is selected, then EIGENSTATE_SUBSCRIPT_SUPERPOSITION is returned.
     */
    private int getEigenstateSubscript() {
        
        BSModel model = getModel();
        
        // Determine which superposition coefficient is non-zero...
        BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
        final int numberOfCoefficients = coefficients.getNumberOfCoefficients();
        int coefficientIndex = -1;
        int count = 0;
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            double energy = coefficients.getCoefficient( i );
            if ( energy > 0 ) {
                if ( count == 0 ) {
                    coefficientIndex = i;
                }
                count++;
            }
        }
        
        // Map to an eigenstate subscript...
        int subscript = EIGENSTATE_SUBSCRIPT_NONE; // count==0
        if ( count == 1 ) {
            BSEigenstate eigenstate = model.getEigenstate( coefficientIndex );
            subscript = eigenstate.getSubscript();
        }
        else if ( count > 1 ) {
            subscript = EIGENSTATE_SUBSCRIPT_SUPERPOSITION;
        }
        
        return subscript;
    }
}
