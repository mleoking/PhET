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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSHilitedEquation displays the wave function equation of the hilited eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHilitedEquation extends BSAbstractWaveFunctionEquation {

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
    public BSHilitedEquation() {
        super();
        _xform = new AffineTransform();
        _superpositionString = SimStrings.get( "label.equation.superposition" );
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractWaveFunctionEquation implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme.
     * The color of the text is set to the color used for the hilited eigenstate.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        setHTMLColor( colorScheme.getEigenstateHiliteColor() );
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
                    text = "<html>" + BSConstants.LOWERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x)</html>";
                }
                else if ( getMode() == BSBottomPlotMode.PROBABILITY_DENSITY || getMode() == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
                    text = "<html>|" + BSConstants.LOWERCASE_PSI + "<sub>" + eigenstateSubscript + "</sub>(x)|<sup>2</sup></html>";
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
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when the hilite eigenstate changes.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == getModel() ) {
            if ( arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
                updateDisplay();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // private methods
    //----------------------------------------------------------------------------

    /*
     * Gets the hilited eigenstate's subscript.
     * If an eigenstate is hilited, then its subscript is returned.
     * If no eigenstate is hilited, then EIGENSTATE_SUBSCRIPT_NONE is returned.
     */
    private int getEigenstateSubscript() {
        BSModel model = getModel();
        final int hiliteIndex = model.getHilitedEigenstateIndex();
        int subscript = EIGENSTATE_SUBSCRIPT_NONE;
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            // map to an eigenstate subscript...
            BSEigenstate eigenstate = model.getEigenstate( hiliteIndex );
            subscript = eigenstate.getSubscript();
        }
        return subscript;
    }
}
