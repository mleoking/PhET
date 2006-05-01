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

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;

/**
 * BSSelectedEquation displays the wave function equation of the selected eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSelectedEquation extends BSAbstractWaveFunctionEquation implements Observer {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSelectedEquation() {
        super();
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
     * Gets the eigenstate subscript.
     * If one eigenstate is selected, then its subscript is returned.
     * If no eigenstate is selected, then EIGENSTATE_SUBSCRIPT_NONE is returned.
     * If more than one eigenstate is selected, then EIGENSTATE_SUBSCRIPT_SUPERPOSITION is returned.
     */
    protected int getEigenstateSubscript() {
        
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
        else {
            subscript = EIGENSTATE_SUBSCRIPT_SUPERPOSITION;
        }
        
        return subscript;
    }
}
