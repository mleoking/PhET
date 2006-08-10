/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSPotentialHandle is the base class for all drag handles used to
 * control a potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSPotentialHandle extends BSAbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractPotential _potential;
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potential
     * @param potentialSpec
     * @param chartNode
     * @param orientation HORIZONTAL or VERTICAL
     */
    public BSPotentialHandle( BSAbstractPotential potential, BSPotentialSpec potentialSpec, 
            BSCombinedChartNode chartNode, int orientation ) {
        super( orientation );
        
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        
        setPotential( potential );
        
        // Y-axis may have a zoom control, so listen for axis changes.
        ValueAxis yAxis = chartNode.getEnergyPlot().getRangeAxis();
        yAxis.addChangeListener( new AxisChangeListener() {
            // If the y axis is changed, update the drag bounds.
            public void axisChanged( AxisChangeEvent event ) {
                updateDragBounds();
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /*
     * Sets the potential that this handle is controlling.
     */
    protected void setPotential( BSAbstractPotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateView();
    }
    
    /*
     * Gets the potential that this handle is controlling.
     */
    protected BSAbstractPotential getPotential() {
        return _potential;
    }
    
    /*
     * Gets the specification that defines the ranges of 
     * the potential's attributes.
     */
    protected BSPotentialSpec getPotentialSpec() {
        return _potentialSpec;
    }
    
    /*
     * Gets the chart node that is displaying the potential.
     */
    protected BSCombinedChartNode getChartNode() {
        return _chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Coordinate system translators
    //----------------------------------------------------------------------------
    
    /*
     * Converts a point in model coordinates to a point in view (global) coordinates.
     * 
     * @param modelPoint
     * @return
     */
    protected Point2D modelToView( Point2D modelPoint ) {
        Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
        Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
        return globalNodePoint;
    }
    
    /*
     * Converts a point in view (global) coordinates to a point in model coordinates.
     * 
     * @param viewPoint
     * @return
     */
    protected Point2D viewToModel( Point2D viewPoint ) {
        Point2D localNodePoint = _chartNode.globalToLocal( viewPoint );
        Point2D modelPoint = _chartNode.nodeToEnergy( localNodePoint );
        return modelPoint;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view when the model changes.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        assert( o == _potential );
        updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------------------
    
    /**
     * Rounds a double value to a specified number of decimal places.
     * 
     * @param value
     * @param numberOfSignificantDecimalPlaces
     * @return double
     */
    public static double round( double value, double numberOfSignificantDecimalPlaces ) {
        final double multiplier = Math.pow( 10, numberOfSignificantDecimalPlaces );
        double adder = 0.49 / multiplier;
        if ( value < 0 ) {
            adder *= -1;
        }
        int intValue = (int)( ( value + adder ) * multiplier );
        return ( intValue / multiplier );
    }
}
