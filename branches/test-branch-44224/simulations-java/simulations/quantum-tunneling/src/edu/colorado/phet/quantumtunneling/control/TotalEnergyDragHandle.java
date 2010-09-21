/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChartNode;


/**
 * TotalEnergyControl is a drag handle used to control total energy.
 * It is superimposed on top of a QTCombinedChartNode, which manages
 * rendering of the energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyDragHandle extends AbstractDragHandle implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _totalEnergy;
    private QTCombinedChartNode _chartNode;
    private double _xAxisPosition;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param chartNode the chart node that contains the energy chart
     */
    public TotalEnergyDragHandle( QTCombinedChartNode chartNode ) {
        super( AbstractDragHandle.VERTICAL );
        setValueFormat( QTConstants.ENERGY_FORMAT );
        
        _totalEnergy = null;
        _chartNode = chartNode;
        _xAxisPosition = 0;
        
        addPropertyChangeListener( this );
        updateDragBounds();
    }
   
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
            _totalEnergy = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the total energy model that this drag handle controls.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updatePosition();
        updateText();
    }
    
    /**
     * Sets the drag handle's position on the energy chart's x axis.
     * 
     * @param position position, in chart coordinates
     */
    public void setXAxisPosition( double position ) {
        _xAxisPosition = position;
        updatePosition();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds and position of the drag handle.
     */
    public void updateDragBounds() {

        Point2D xMin = _chartNode.energyToNode( new Point2D.Double( QTConstants.POSITION_RANGE.getLowerBound(), 0 ) );
        Point2D xMax = _chartNode.energyToNode( new Point2D.Double( QTConstants.POSITION_RANGE.getUpperBound(), 0 ) );
        // +y is down!
        Point2D yMax = _chartNode.energyToNode( new Point2D.Double( 0, QTConstants.ENERGY_RANGE.getLowerBound() ) );
        Point2D yMin = _chartNode.energyToNode( new Point2D.Double( 0, QTConstants.ENERGY_RANGE.getUpperBound() ) );
        
        double x = xMin.getX();
        double y = yMin.getY();
        double w = xMax.getX() - xMin.getX();
        double h = yMax.getY() - yMin.getY();
        Rectangle2D dragBounds = new Rectangle2D.Double( x, y, w, h );
        
        // Convert to global coordinates
        dragBounds = _chartNode.localToGlobal( dragBounds );
        
        setDragBounds( dragBounds );
        updatePosition();
    }
    
    /*
     * Updates the drag handle's position based on the total energy.
     */
    private void updatePosition() {
        if ( _totalEnergy != null ) {
            Point2D chartPoint = new Point2D.Double( _xAxisPosition, _totalEnergy.getEnergy() );
            Point2D localNodePoint = _chartNode.energyToNode( chartPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
            removePropertyChangeListener( this );
            setGlobalPosition( globalNodePoint );
            addPropertyChangeListener( this );
        }
    }

    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------

    /*
     * Gets the value, in model coordinates, that is represented by
     * the drag handle's current location.
     * 
     * @return
     */
    protected double getModelValue() {
        double energy = 0;
        if ( _totalEnergy != null ) {
            energy = _totalEnergy.getEnergy();
        }
        return energy;
    }
    
    /*
     * Updates the total energy based on the drag handle's position.
     */
    protected void updateModel() {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
            Point2D chartPoint = _chartNode.nodeToEnergy( localNodePoint );
            _totalEnergy.setEnergy( chartPoint.getY() );
            _totalEnergy.addObserver( this );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag handle position to match the total energy.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _totalEnergy ) {
            updatePosition();
            updateText();
        }
    }
}
