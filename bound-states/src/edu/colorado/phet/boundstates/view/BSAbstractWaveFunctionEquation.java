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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.nodes.HTMLNode;

/**
 * BSAbstractWaveFunctionEquation displays a wave function equation.
 * The subscript on the equation corresponds to some eigenstate, as 
 * determined by the subclass' implementation of getEigenstateSubscript.
 * If we're in a superposition state, the label is "Superposition".
 * The display varies depending on whether we're in "wave function" or 
 * "probability density" mode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractWaveFunctionEquation extends HTMLNode implements Observer {

    protected static final int EIGENSTATE_SUBSCRIPT_NONE = -1;
    protected static final int EIGENSTATE_SUBSCRIPT_SUPERPOSITION = Integer.MIN_VALUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private Point2D _location;
    private AffineTransform _xform; // reusable transform
    private String _superpositionString;
    private int _mode; // BSBottomPlot.MODE_PROBABILITY_DENSITY or BSBottomPlot.MODE_WAVE_FUNCTION
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSAbstractWaveFunctionEquation() {
        setFont( BSConstants.WAVE_FUNCTION_EQUATION_FONT );
        setHTMLColor( Color.BLACK );
        _location = new Point2D.Double();
        _xform = new AffineTransform();
        _superpositionString = SimStrings.get( "label.equation.superposition" );
        _mode = BSBottomPlot.MODE_PROBABILITY_DENSITY;
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
    
    /*
     * Gets the model, for use by subclasses.
     */
    protected BSModel getModel() {
        return _model;
    }
    
    /**
     * Sets the mode.
     * 
     * @param mode BSBottomPlot.MODE_PROBABILITY_DENSITY, BSBottomPlot.MODE_WAVE_FUNCTION
     */
    public void setMode( int mode ) {
        if ( mode != BSBottomPlot.MODE_PROBABILITY_DENSITY && mode != BSBottomPlot.MODE_WAVE_FUNCTION ) {
            throw new IllegalArgumentException( "invalid mode: " + mode );
        }
        _mode = mode;
        updateDisplay();
    }
    
    /**
     * Sets the color scheme.
     * @param colorScheme
     */
    public abstract void setColorScheme( BSColorScheme colorScheme );

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
     * Updates the display when the hilite eigenstate changes.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == getModel() ) {
            updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Gets the eigenstate subscript.
     * If one eigenstate, then its subscript is returned.
     * If no eigenstate, then EIGENSTATE_SUBSCRIPT_NONE is returned.
     * If more than one eigenstate, then EIGENSTATE_SUBSCRIPT_SUPERPOSITION is returned.
     */
    protected abstract int getEigenstateSubscript();
    
    /*
     * Updates the display to match the model.
     * Subclasses should call this from their update method.
     */
    protected void updateDisplay() {
        
        if ( _model != null ) {
            
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
                if ( _mode == BSBottomPlot.MODE_WAVE_FUNCTION ) {
                    text = "<html>" + BSConstants.PSI + "<sub>" + eigenstateSubscript + "</sub>(x)</html>";
                }
                else {
                    text = "<html>|" + BSConstants.PSI + "<sub>" + eigenstateSubscript + "</sub>(x)|<sup>2</sup></html>";
                }
            }
            setHTML( text );

            // Translate so the upper right corner is at the location...
            _xform.setToIdentity();
            _xform.translate( _location.getX(), _location.getY() );
            _xform.translate( -getFullBounds().getWidth(), 0 ); // upper right
            setTransform( _xform );
        }
    }
}
