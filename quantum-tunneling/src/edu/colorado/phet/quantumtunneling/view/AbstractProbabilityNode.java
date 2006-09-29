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
 * AbstractProbabilityNode is the base class for ReflectionProbabilityNode and TransmissionProbabilityNode.
 * It creates its own AbstractPlaneWaveSolver, and uses it to update the displayed probability value. 
 * Updates occur when the total energy, potential energy, or direction of the wave changes.
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
    
    private AbstractPlaneSolver _solver;
    private TotalEnergy _totalEnergy;
    private AbstractPotential _potentialEnergy;
    private Direction _direction;
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
        
        // Initialize a value so that layout code will have valid bounds for the node
        _value = -1; // force an update by setting an invalid probabilty
        setValue( 0 );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display whenever the total energy or total energy changes.
     * Direction is set explicitly via setDirection.
     */
    public void update( Observable o, Object arg ) {
        if ( _solver != null ) {
            _solver.update();
            update();
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators & Accessors
    //----------------------------------------------------------------------------
    
    /*
     * Synchronizes the display with the model.
     */
    public abstract void update();
    
    /**
     * Sets the total energy, registers this node as an observer.
     * Results in the creation of a new solver.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updateSolver();
    }
    
    /**
     * Sets the potential energy, registers this node as an observer.
     * Results in the creation of a new solver.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        updateSolver();
    }
    
    /**
     * Sets the direction.
     * Results in the creation of a new solver.
     * 
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _direction = direction;
        updateSolver();
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
        if ( visible ) {
            update();
        }
    }
    
    //----------------------------------------------------------------------------
    // Protected, for use by subclasses
    //----------------------------------------------------------------------------
    
    /*
     * Sets the value and rebuild's the node's text.
     */
    protected void setValue( double value ) {
        if ( value != _value ) {
            _value = value;
            String s = _label + "=" + FORMAT.format( _value );
            setText( s );
        }
    }
    
    /*
     * Gets the reflection probability value from the solver.
     * Implemented in the base class so that subclasses don't need 
     * to know about the solver.
     */
    protected double getReflectionProbability() {
        double value = -1;
        if ( _solver != null ) {
            value = _solver.getReflectionProbability();
        }
        return value;
    }
    
    /*
     * Gets the transmission probability value from the solver.
     * Implemented in the base class so that subclasses don't need 
     * to know about the solver.
     */
    protected double getTransmissionProbability() {
        double value = -1;
        if ( _solver != null ) {
            value = _solver.getTransmissionProbability();
        }
        return value;
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
        return ( _totalEnergy != null && _potentialEnergy != null && _direction != null );
    }
    
    /*
     * Updates the solver to correspond to the instances of total energy,
     * potential energy and direction that are associated with this node.
     */
    private void updateSolver() {
        if ( isInitialized() ) {
            _solver = SolverFactory.createSolver( _totalEnergy, _potentialEnergy, _direction );
            _solver.update();
            update();
        }
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
                setValue( getReflectionProbability() );
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
                setValue( getTransmissionProbability() );
            }
        }
    }
}
