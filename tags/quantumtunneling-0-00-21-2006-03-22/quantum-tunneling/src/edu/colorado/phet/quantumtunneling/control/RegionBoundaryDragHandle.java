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
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChartNode;


/**
 * RegionBoundaryDragHandle is a drag handle used to control the position of
 * the boundary between regions. It is superimposed on top of a QTCombinedChartNode,
 * which manages rendering of the energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RegionBoundaryDragHandle extends AbstractDragHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotential _potentialEnergy;
    private int _regionIndex;
    private QTCombinedChartNode _chartNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param chartNode
     */
    public RegionBoundaryDragHandle( QTCombinedChartNode chartNode ) {
        super( AbstractDragHandle.HORIZONTAL );
        setValueFormat( QTConstants.POSITION_FORMAT );
        
        _potentialEnergy = null;
        _regionIndex = -1;
        _chartNode = chartNode;
        
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
     * @param regionIndex region whose right edge is moved
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy, int regionIndex ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _regionIndex = regionIndex;
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        updatePosition();
        updateText();
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
            
            // Determine the drag bounds, in local node coordinates
            double x = 0;
            final double y = energyPlotBounds.getY();
            double w = 0;
            final double h = energyPlotBounds.getHeight();
            double minRegionWidth = _potentialEnergy.getMinRegionWidth();
            if ( _regionIndex == 0 ) {
                double startX = _potentialEnergy.getStart( _regionIndex ) + minRegionWidth;
                double endX = _potentialEnergy.getEnd( _regionIndex + 1 ) - minRegionWidth;
                Point2D start = _chartNode.energyToNode( new Point2D.Double( startX, 0 ) );
                Point2D end = _chartNode.energyToNode( new Point2D.Double( endX, 0 ) );
                x = start.getX();
                w = end.getX() - start.getX();
            }
            else {
                double startX = _potentialEnergy.getEnd( _regionIndex - 1 ) + minRegionWidth;
                double endX = _potentialEnergy.getEnd( _regionIndex + 1 ) - minRegionWidth;
                Point2D start = _chartNode.energyToNode( new Point2D.Double( startX, 0 ) );
                Point2D end = _chartNode.energyToNode( new Point2D.Double( endX, 0 ) );
                x = start.getX();
                w = end.getX() - start.getX();
            }
            Rectangle2D regionBounds = new Rectangle2D.Double( x, y, w, h );
            
            // Convert to global coordinates
            regionBounds = _chartNode.localToGlobal( regionBounds );

            setDragBounds( regionBounds );
            updatePosition();
        }
    }
    
    /*
     * Updates the drag handle's position based on the region boundary.
     * The handle corresponds to the end of the region.
     */
    private void updatePosition() {
        if ( _potentialEnergy != null ) {
            double position = _potentialEnergy.getEnd( _regionIndex );
            double energy1 = _potentialEnergy.getEnergy( _regionIndex );
            double energy2 = _potentialEnergy.getEnergy( _regionIndex + 1 );
            double y = Math.min( energy1, energy2 ) + ( Math.max( energy1, energy2 ) - Math.min( energy1, energy2 ) ) / 2;
            Point2D chartPoint = new Point2D.Double( position, y );
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
        double position = 0;
        if ( _potentialEnergy != null ) {
            position = _potentialEnergy.getEnd( _regionIndex );
        }
        return position;
    }
    
    /*
     * Updates the region's boundary based on the drag handle's position.
     */
    protected void updateModel() {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
            Point2D chartPoint = _chartNode.nodeToEnergy( localNodePoint );
            double position = chartPoint.getX();

            boolean success = _potentialEnergy.adjustBoundary( _regionIndex, position );
            if ( !success ) {
                System.out.println( "WARNINING: RegionBoundaryDragHandle.updatePotentialEnergy failed, " +
                        "regionIndex=" + _regionIndex + " position=" + position );
            }
            _potentialEnergy.addObserver( this );
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
            updateText();
        }
    }
}
