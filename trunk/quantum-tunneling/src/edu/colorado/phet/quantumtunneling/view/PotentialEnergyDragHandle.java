/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.quantumtunneling.model.AbstractPotentialSpace;
import edu.colorado.phet.quantumtunneling.model.PotentialRegion;
import edu.umd.cs.piccolo.PNode;


/**
 * PotentialEnergyDragHandler
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialEnergyDragHandle extends DragHandle implements Observer, PropertyChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotentialSpace _potentialEnergy;
    private int _regionIndex;
    private QTCombinedChartNode _chartNode;
    private double _xAxisPosition;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PotentialEnergyDragHandle( QTCombinedChartNode chartNode ) {
        super( DragHandle.VERTICAL );
        
        _potentialEnergy = null;
        _regionIndex = -1;
        _chartNode = chartNode;
        _xAxisPosition = 0;
        
        addPropertyChangeListener( this );
        updateDragBounds();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
            _potentialEnergy = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the region of potential energy that this drag handle controls.
     * 
     * @param potentialEnergy
     * @param regionIndex
     */
    public void setPotentialEnergy( AbstractPotentialSpace potentialEnergy, int regionIndex ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _regionIndex = regionIndex;
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        updatePosition();
    }
    
    /**
     * Sets the drag handle's position on the energy chart's x axis.
     * @param value
     */
    private void setXAxisPosition( double value ) {
        _xAxisPosition = value;
        updatePosition();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds and position of the drag handle.
     */
    public void updateDragBounds() {
        if ( _potentialEnergy != null ) {
            // Determine the bounds of the energy chart.
            Rectangle2D energyPlotBounds = _chartNode.getEnergyPlotBounds();

            // Determine the region's drag bounds
            PotentialRegion region = _potentialEnergy.getRegion( _regionIndex );
            Point2D start = _chartNode.energyToNode( new Point2D.Double( region.getStart(), 0 ) );
            Point2D end = _chartNode.energyToNode( new Point2D.Double( region.getEnd(), 0 ) );
            double x = start.getX();
            double y = energyPlotBounds.getY();
            double w = end.getX() - start.getX();
            double h = energyPlotBounds.getHeight();
            Rectangle2D regionBounds = new Rectangle2D.Double( x, y, w, h );
            
            // Convert to global coordinates
            regionBounds = _chartNode.localToGlobal( regionBounds );

            setDragBounds( regionBounds );
            updatePosition();
        }
    }
    
    /*
     * Updates the drag handle's position based on the region's
     * potential energy and width.
     */
    private void updatePosition() {
        if ( _potentialEnergy != null ) {
            PotentialRegion region = _potentialEnergy.getRegion( _regionIndex );
            double position = region.getMiddle();
            double energy = region.getEnergy();
            Point2D chartPoint = new Point2D.Double( position, energy );
            Point2D localNodePoint = _chartNode.energyToNode( chartPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
            removePropertyChangeListener( this );
            setGlobalPosition( globalNodePoint );
            addPropertyChangeListener( this );
        }
    }
    
    /*
     * Updates the region's potential energy based on the drag handle's position.
     */
    private void updatePotentialEnergy() {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
            Point2D chartPoint = _chartNode.nodeToEnergy( localNodePoint );
            _potentialEnergy.setEnergy( _regionIndex, chartPoint.getY() );
            _potentialEnergy.addObserver( this );
        }
    }
    
    //----------------------------------------------------------------------------
    // PropertChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the region's potential energy whenever the drag handle is moved.
     * 
     * @param event
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getSource() == this ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updatePotentialEnergy();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag handle position to match the
     * region's potential energy and width.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _potentialEnergy ) {
            updateDragBounds();
        }
    }
}
