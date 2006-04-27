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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.nodes.HTMLNode;

/**
 * BSWaveFunctionEquation displays the wave function name of the selected eigenstate.
 * If we're in a superposition state, the label is "Superposition".
 * If nothing is selected, the label is blank.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWaveFunctionEquation extends HTMLNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private Point2D _location;
    private AffineTransform _xform; // reusable transform
    private String _superpositionString;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSWaveFunctionEquation() {
        setFont( BSConstants.WAVE_FUNCTION_EQUATION_FONT );
        setHTMLColor( Color.BLACK );
        _location = new Point2D.Double();
        _xform = new AffineTransform();
        _superpositionString = SimStrings.get( "label.equation.superposition" );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the model.
     * @param model
     */
    public void setModel( BSModel model ) {
        if ( _model != null ) {
            _model.deleteObserver( this );
        }
        _model = model;
        _model.addObserver( this );
        updateDisplay();
    }
    
    /**
     * Sets the color scheme.
     * The color of the text is set to the color used for the selected eigenstate.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        setHTMLColor( colorScheme.getEigenstateSelectionColor() );
    }

    /**
     * Sets the "location" of this node.
     * This is a hack so that we can maintain right justification as the text changes.
     * 
     * @param x
     * @param y
     */
    public void setLocation( double x, double y ) {
        _location.setLocation( x, y );
        updateDisplay();
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
        if ( o == _model && arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS ) {
            updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the display to match the model.
     */
    private void updateDisplay() {
        
        // Determine how many coefficients are non-zero...
        BSSuperpositionCoefficients coefficients = _model.getSuperpositionCoefficients();
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
        
        // Set the text...
        String text = "";  // when count==0
        if ( count == 1 ) {
            // Map the coefficient index to an eigenstate subscript...
            BSEigenstate eigenstate = _model.getEigenstate( coefficientIndex );
            final int subscript = eigenstate.getSubscript();
            text = "<html>" + BSConstants.PSI + "<sub>" + subscript + "</sub>(x)";
        }
        else if ( count > 1 ) {
            text = _superpositionString;
        }
        setHTML( text );

        // Translate so the upper right corner is at the location...
        _xform.setToIdentity();
        _xform.translate( _location.getX(), _location.getY() );
        _xform.translate( -getFullBounds().getWidth(), 0 ); // upper right
        setTransform( _xform );
    }
}
