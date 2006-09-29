/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * AbstractProbabilityNode is the base class for ReflectionProbabilityNode 
 * and TransmissionProbabilityNode.
 * <p>
 * Updates to this node occur when the the associated wave changes.
 * This is a bit wasteful, since what we really care about is changes to the total energy,
 * potential energy, or wave direction.  But it simplifies the implementation and the 
 * performance hit is negligible -- the calculation is trivial and we only update the 
 * node's text if the value actually changes.
 * <p>
 * ReflectionProbabilityNode is used to display the reflection probability.
 * <p>
 * TransmissionProbabilityNode is used to display the transmission probability.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractProbabilityNode extends PText implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final DecimalFormat FORMAT = new DecimalFormat( "0.00" );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Font FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 18 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractWave _wave;
    private String _label;
    private double _value;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    private AbstractProbabilityNode( String label ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        setFont( FONT );
        setTextPaint( DEFAULT_COLOR );
        _label = label;
        setValue( -1 );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display whenever the wave changes.
     */
    public void update( Observable o, Object arg ) {
        update();
    }
    
    //----------------------------------------------------------------------------
    // Mutators & Accessors
    //----------------------------------------------------------------------------
    
    /*
     * Synchronizes the display with the model.
     */
    public abstract void update();
    
    /**
     * Sets the wave, registers this node as an observer.
     * 
     * @param totalEnergy
     */
    public void setWave( AbstractWave wave ) {
        if ( _wave != null ) {
            _wave.deleteObserver( this );
        }
        _wave = wave;
        _wave.addObserver( this );
        update();
    }

    /**
     * Sets the color scheme.
     * This node uses the "annotation" property of the color scheme,
     * which is the color used for any text is overlayed on a chart.
     * 
     * @param colorScheme
     */
    public void setColorScheme( QTColorScheme colorScheme ) {
        setTextPaint( colorScheme.getAnnotationColor() );
    }
    
    /**
     * Sets the node's visibility.
     * If we're changing from invisible to visible, force an update,
     * since updates are not typcially done while the node is invisible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible && isInitialized() ) {
            update();
        }
    }
    
    //----------------------------------------------------------------------------
    // Protected, for use by subclasses
    //----------------------------------------------------------------------------
    
    /*
     * Gets the wave.
     */
    protected AbstractWave getWave() {
        return _wave;
    }
    
    /*
     * Sets the value and rebuild's the node's text.
     */
    protected void setValue( double value ) {
        if ( value != _value ) {
            _value = value;
            String s = null;
            if ( _value < 0  ) {
                s = _label + "=?";
            }
            else {
                s = _label + "=" + FORMAT.format( _value );
            }
            setText( s );
        }
    }
    
    /*
     * Gets the value.
     */
    protected double getValue() {
        return _value;
    }
    
    //----------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------
    
    /*
     * Determines whether the node is initialized.
     * If all the appropriate setters haven't been called, the node is 
     * not capable of displaying a valid value.
     */
    private boolean isInitialized() {
        return ( _wave != null );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ReflectionProbabilityNode displays the probability of reflection.
     */
    public static class ReflectionProbabilityNode extends AbstractProbabilityNode {

        public ReflectionProbabilityNode() {
            super( SimStrings.get( "label.reflectionProbability" ) );
        }

        public void update() {
            if ( getVisible() ) {
                double value = getWave().getReflectionProbability();
                setValue( value );
            }
        }
    }
    
    /**
     * TransmissionProbabilityNode displays the probability of transmission.
     */
    public static class TransmissionProbabilityNode extends AbstractProbabilityNode {

        public TransmissionProbabilityNode() {
            super( SimStrings.get( "label.transmissionProbability" ) );
        }

        public void update() {
            if ( getVisible() ) {
                double value = getWave().getTransmissionProbability();
                setValue( value );
            }
        }
    }
}
