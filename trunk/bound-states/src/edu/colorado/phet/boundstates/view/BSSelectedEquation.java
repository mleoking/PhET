/* Copyright 2006, University of Colorado */

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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
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

    // maximum number of terms in superposition equation
    private static final int MAX_TERMS = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _xform; // reusable transform
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSelectedEquation() {
        super();
        _xform = new AffineTransform();
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

        BSModel model = getModel();
        
        if ( model != null ) {
            
            BSSuperpositionCoefficients superpositionCoefficients = model.getSuperpositionCoefficients();
            final int numberOfNonZeroCoefficients = superpositionCoefficients.getNumberOfNonZeroCoefficients();
            
            // Set the text...
            String text = null;
            if ( numberOfNonZeroCoefficients == 0 ) {
                text = "";
            }
            else if ( numberOfNonZeroCoefficients == 1 ) {
                text = createSimpleString();
            }
            else {
                if ( getMode() == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
                    text = createAverageProbabilityDensityString();
                }
                else {
                    text = createSuperpositionString();
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
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when the superposition coefficients change.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == getModel() ) {
            if ( arg == null || 
                 arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES  ) {
                updateDisplay();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // String creation
    //----------------------------------------------------------------------------
    
    /*
     * Creates an equation that involves a single coefficient.
     * This is used when we're NOT in a superposition state.
     */
    private String createSimpleString() {

        BSModel model = getModel();

        // Find the first non-zero coefficient...
        BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
        final int numberOfCoefficients = coefficients.getNumberOfCoefficients();
        int coefficientIndex = -1;
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            double coefficient = coefficients.getCoefficient( i );
            if ( coefficient > 0 ) {
                coefficientIndex = i;
                break;
            }
        }
        assert ( coefficientIndex != -1 );

        // Map to an eigenstate subscript...
        BSEigenstate eigenstate = model.getEigenstate( coefficientIndex );
        final int eigenstateSubscript = eigenstate.getSubscript();

        // Construct a text string...
        String text = null;
        if ( getMode() == BSBottomPlotMode.WAVE_FUNCTION ) {
            text = "<html>" + BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)</html>";
        }
        else if ( getMode() == BSBottomPlotMode.PROBABILITY_DENSITY || getMode() == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
            text = "<html>|" + BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)|<sup>2</sup></html>";
        }
        else {
            throw new UnsupportedOperationException( "unsupported mode: " + getMode() );
        }
        return text;
    }
    
    /*
     * Creates an equation that involves multiple coefficients.
     * This is used when we're in a superposition state.
     */
    private String createSuperpositionString() {

        BSModel model = getModel();
        String text = "";

        // Create sum of terms...
        BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
        int numberOfTerms = 0;
        final int numberOfCoefficients = coefficients.getNumberOfCoefficients();
        for ( int i = 0; i < numberOfCoefficients && numberOfTerms < MAX_TERMS; i++ ) {
            double coefficient = coefficients.getCoefficient( i );
            if ( coefficient > 0 ) {
                if ( numberOfTerms > 0 ) {
                    text += "+";
                }
                BSEigenstate eigenstate = model.getEigenstate( i );
                final int eigenstateSubscript = eigenstate.getSubscript();
                text += BSConstants.COEFFICIENT_FORMAT.format( coefficient );
                text += BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)";
                numberOfTerms++;
            }
        }

        // Add ellipsis if there are more non-zero terms that aren't shown...
        final int numberOfNonZeroCoefficients = coefficients.getNumberOfNonZeroCoefficients();
        if ( numberOfNonZeroCoefficients > MAX_TERMS ) {
            text += "+...";
        }

        // Modify equation for probabilty density
        if ( getMode() == BSBottomPlotMode.PROBABILITY_DENSITY ) {
            text = "|" + text + "|<sup>2</sup>";
        }

        // Convert to html
        String html = "<html>" + text + "<html>";
        return html;
    }
    
    /*
     * Creates the string that is displayed when the bottom chart is in 
     * "Average Probability Density" mode.
     */
    private String createAverageProbabilityDensityString() {
        BSModel model = getModel();
        String text = "";

        // Create sum of absolute squares of terms...
        BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
        int numberOfTerms = 0;
        final int numberOfCoefficients = coefficients.getNumberOfCoefficients();
        text = "(";
        for ( int i = 0; i < numberOfCoefficients && numberOfTerms < MAX_TERMS; i++ ) {
            double coefficient = coefficients.getCoefficient( i );
            if ( coefficient > 0 ) {
                if ( numberOfTerms > 0 ) {
                    text += "+";
                }
                BSEigenstate eigenstate = model.getEigenstate( i );
                final int eigenstateSubscript = eigenstate.getSubscript();
                text += "|" + BSConstants.UPPERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x,t)|<sup>2</sup>";
                numberOfTerms++;
            }
        }

        // Add ellipsis if there are more non-zero terms that aren't shown...
        final int numberOfNonZeroCoefficients = coefficients.getNumberOfNonZeroCoefficients();
        if ( numberOfNonZeroCoefficients > MAX_TERMS ) {
            text += "+...";
        }

        text += ")/" + numberOfNonZeroCoefficients;

        // Convert to html
        String html = "<html>" + text + "<html>";
        return html;
    }
}
