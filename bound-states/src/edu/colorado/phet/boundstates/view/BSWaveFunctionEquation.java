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
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
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

    public BSModel _model;
    private Point2D _location;
    
    public BSWaveFunctionEquation() {
        setFont( BSConstants.WAVE_FUNCTION_EQUATION_FONT );
        setHTMLColor( Color.BLACK );
        _location = new Point2D.Double();
    }
    
    public void setModel( BSModel model ) {
        if ( _model != null ) {
            _model.deleteObserver( this );
        }
        _model = model;
        _model.addObserver( this );
        updateDisplay();
    }
    
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
    
    public void update( Observable o, Object arg ) {
        if ( o == _model && arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS ) {
            updateDisplay();
        }
    }
    
    private void updateDisplay() {
        
        // Determine how many coefficients are non-zero...
        BSSuperpositionCoefficients coefficients = _model.getSuperpositionCoefficients();
        final int numberOfCoefficients = coefficients.getNumberOfCoefficients();
        int selectedEigenstateIndex = BSEigenstate.INDEX_UNDEFINED;
        int count = 0;
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            double energy = coefficients.getCoefficient( i );
            if ( energy > 0 ) {
                if ( count == 0 ) {
                    selectedEigenstateIndex = i;
                }
                count++;
            }
        }
        
        // Set the text...
        String text = "";
        if ( count == 1 ) {
            text = "<html>" + BSConstants.PSI + "<sub>" + selectedEigenstateIndex + "</sub>(x)";
        }
        else if ( count > 1 ) {
            text = "Superposition"; //XXX
        }
        setHTML( text );

        // Translate so that it's upper right corner is at the location...
        AffineTransform xform = new AffineTransform();
        xform.translate( _location.getX(), _location.getY() );
        xform.translate( -getFullBounds().getWidth(), 0 ); // upper right
        setTransform( xform );
    }
}
