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

import org.jfree.chart.axis.ValueAxis;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.boundstates.view.BSEnergyPlot;

/**
 * BSSquareHeightHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareHeightHandle extends AbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSSquarePotential _potential;
    private BSCombinedChartNode _chartNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareHeightHandle( BSSquarePotential potential, BSCombinedChartNode chartNode ) {
        super( AbstractHandle.VERTICAL );
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
        
        BSEnergyPlot energyPlot = _chartNode.getCombinedChart().getEnergyPlot();
        ValueAxis positionAxis = energyPlot.getDomainAxis();
        ValueAxis energyAxis = energyPlot.getRangeAxis();

        // Determine the drag bounds, in local node coordinates
        Point2D xMin = _chartNode.energyToNode( new Point2D.Double( positionAxis.getLowerBound(), 0 ) );
        Point2D xMax = _chartNode.energyToNode( new Point2D.Double( positionAxis.getUpperBound(), 0 ) );
        // +y is down!
        Point2D yMax = _chartNode.energyToNode( new Point2D.Double( 0, _potential.getOffset() ) );
        Point2D yMin = _chartNode.energyToNode( new Point2D.Double( 0, energyAxis.getUpperBound() ) );
        double x = xMin.getX();
        double y = yMin.getY();
        double w = xMax.getX() - xMin.getX();
        double h = yMax.getY() - yMin.getY();
        Rectangle2D dragBounds = new Rectangle2D.Double( x, y, w, h );

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
            Point2D modelPoint = _chartNode.nodeToEnergy( localNodePoint );
            final double height = modelPoint.getY() - _potential.getOffset();
//            System.out.println( "BSSquareHeightHandle.updateModel globalNodePoint=" + globalNodePoint + " height=" + height );//XXX
            _potential.setHeight( height );
            setValueDisplay( height );
        }
        _potential.addObserver( this );
    }

    protected void updateView() {
        removePropertyChangeListener( this );
        {
            final int n = _potential.getNumberOfWells();
            final double width = _potential.getWidth();
            final double separation = _potential.getSeparation();
            final double position = _potential.getCenter( n - 1 ) + ( width / 2 ) + ( separation /  2 );
            final double height = _potential.getHeight();
            final double offset = _potential.getOffset();
            Point2D modelPoint = new Point2D.Double( position, offset + height );
            Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
//            System.out.println( "BSSquareHeightHandle.updateView position=" + position + " height=" + height + " globalNodePoint=" + globalNodePoint );//XXX
            setGlobalPosition( globalNodePoint );
            setValueDisplay( height );
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
