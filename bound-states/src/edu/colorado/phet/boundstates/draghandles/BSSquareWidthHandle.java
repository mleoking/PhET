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
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSSquareWidthHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWidthHandle extends AbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private BSSquarePotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareWidthHandle( BSSquarePotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( AbstractHandle.HORIZONTAL );
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        setPotential( potential );
        updateDragBounds();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSSquarePotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateView();
    }
    
    public BSSquarePotential getPotential() {
        return _potential;
    }
    
    //----------------------------------------------------------------------------
    // Bounds
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        
        final int n = _potential.getNumberOfWells();
        final double center = _potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
        final double minWidth = _potentialSpec.getWidthRange().getMin();
        final double maxWidth = _potentialSpec.getWidthRange().getMax();
        
        // position -> x coordinates
        final double minPosition = center + ( minWidth / 2 );
        final double maxPosition = center + ( maxWidth / 2 );
        final double minX = _chartNode.positionToNode( minPosition );
        final double maxX = _chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        final double minEnergy = _chartNode.getCombinedChart().getEnergyPlot().getRangeAxis().getRange().getLowerBound();
        final double maxEnergy =  _chartNode.getCombinedChart().getEnergyPlot().getRangeAxis().getRange().getUpperBound();
        final double minY = _chartNode.energyToNode( maxEnergy );
        final double maxY = _chartNode.energyToNode( minEnergy );
        
        // bounds, local coordinates
        final double w = maxX - minX;
        final double h = maxY - minY;
        Rectangle2D dragBounds = new Rectangle2D.Double( minX, minY, w, h );
//        System.out.println( "BSSquareWidthHandle.updateDragBounds dragBounds=" + dragBounds );//XXX

        // Convert to global coordinates
        dragBounds = _chartNode.localToGlobal( dragBounds );

        setDragBounds( dragBounds );
        updateView();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------
    
    protected void updateModel() {
        _potential.deleteObserver( this );
        {
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
            final double currentX = localNodePoint.getX();
            
            final int n = _potential.getNumberOfWells();
            final double previousCenter = _potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
            final double previousWidth = _potential.getWidth(); 
            final double previousX = _chartNode.positionToNode( previousCenter + ( previousWidth / 2 ) );
            
            final double originX = _chartNode.positionToNode( 0 );
            
            final double deltaWidth = 2 * ( _chartNode.nodeToPosition( originX + ( currentX - previousX ) ) );
            
            final double width = previousWidth + deltaWidth;
            
//            System.out.println( "BSSquareWidthHandle.updateModel x=" + currentX + " width=" + width );//XXX
            _potential.setWidth( width );
            setValueDisplay( width );
        }
        _potential.addObserver( this );
    }

    protected void updateView() {
        removePropertyChangeListener( this );
        {
            final int n = _potential.getNumberOfWells();
            final double center = _potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
            final double width = _potential.getWidth();
            final double height = _potential.getHeight();
            final double offset = _potential.getOffset();
 
            final double position = center + ( width / 2 ); // right edge of the well
            final double energy = offset + ( height / 2 ); // half way up the side of the well
            Point2D modelPoint = new Point2D.Double( position, energy );
            Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
//            System.out.println( "BSSquareWidthHandle.updateView width=" + width + " x=" + globalNodePoint.getX() );//XXX
            setGlobalPosition( globalNodePoint );
            setValueDisplay( width );
        }
        addPropertyChangeListener( this );
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
        updateView();
    }
}
