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

import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSSquareWidthHandle is the drag handle used to control the width attribute
 * of a potential composed of square wells.  
 * <p>
 * The handle is placed half way up the right edge of the rightmost well.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWidthHandle extends BSPotentialHandle {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potential
     * @param potentialSpec used to get the range of the attribute controlled
     * @param chartNode
     */
    public BSSquareWidthHandle( BSSquarePotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potential, potentialSpec, chartNode, BSAbstractHandle.HORIZONTAL );

        int significantDecimalPlaces = potentialSpec.getWidthRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( SimStrings.get( "drag.width" ) );
        
        updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        
        BSSquarePotential potential = (BSSquarePotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();
        
        if ( potential.getCenter() != 0 ) {
            throw new UnsupportedOperationException( "this implementation only supports potentials centered at 0" );
        }
        
        final int n = potential.getNumberOfWells();
        final double center = potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
        final double minWidth = spec.getWidthRange().getMin();
        final double maxWidth = spec.getWidthRange().getMax();
        
        // position -> x coordinates
        final double minPosition = center + ( minWidth / 2 );
        final double maxPosition = center + ( maxWidth / 2 );
        final double minX = chartNode.positionToNode( minPosition );
        final double maxX = chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        ValueAxis yAxis = chartNode.getEnergyPlot().getRangeAxis();
        final double minEnergy = yAxis.getLowerBound();
        final double maxEnergy = yAxis.getUpperBound();
        final double minY = chartNode.energyToNode( maxEnergy );
        final double maxY = chartNode.energyToNode( minEnergy );
        
        // bounds, local coordinates
        final double w = maxX - minX;
        final double h = maxY - minY;
        Rectangle2D dragBounds = new Rectangle2D.Double( minX, minY, w, h );

        // Convert to global coordinates
        dragBounds = chartNode.localToGlobal( dragBounds );

        setDragBounds( dragBounds );
        updateView();
    }

    /**
     * Updates the model to match the drag handle.
     */
    protected void updateModel() {

        BSSquarePotential potential = (BSSquarePotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();

        potential.deleteObserver( this );
        {
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = chartNode.globalToLocal( globalNodePoint );
            Point2D modelPoint = chartNode.nodeToEnergy( localNodePoint );
            final double d = modelPoint.getX();

            final int n = potential.getNumberOfWells();
            final double s = potential.getSeparation();
            double width = 0;
            
            if ( n % 2 == 0 ) {
                // even number of wells
                width = ( 2.0 / n ) * ( d - ( ( ( n / 2.0 ) - 1 ) * s ) - ( 0.5 * s ) );
            }
            else {
                // odd number of wells
                final int m = n - 1;
                width = ( 2.0 / ( m + 1 ) ) * ( d - ( ( m / 2.0 ) * s ) );
            }
            final int numberOfSignicantDecimalPlaces = spec.getWidthRange().getSignificantDecimalPlaces();
            width = round( width, numberOfSignicantDecimalPlaces );

            potential.setWidth( width );
            setValueDisplay( width );
        }
        potential.addObserver( this );
        updateDragBounds();
    }
    
    /**
     * Updates the drag handle to match the model.
     */
    protected void updateView() {
        
        BSSquarePotential potential = (BSSquarePotential)getPotential();
        BSCombinedChartNode chartNode = getChartNode();
        
        removePropertyChangeListener( this );
        {
            final int n = potential.getNumberOfWells();
            final double center = potential.getCenter( n - 1 ); // center of the well that we're attaching the handle to
            final double width = potential.getWidth();
            final double height = potential.getHeight();
            final double offset = potential.getOffset();
 
            final double position = center + ( width / 2 ); // right edge of the well
            final double energy = offset + ( height / 2 ); // half way up the side of the well
            Point2D modelPoint = new Point2D.Double( position, energy );
            Point2D localNodePoint = chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = chartNode.localToGlobal( localNodePoint );
//            System.out.println( "BSSquareWidthHandle.updateView width=" + width + " x=" + globalNodePoint.getX() );//XXX
            setGlobalPosition( globalNodePoint );
            setValueDisplay( width );
        }
        addPropertyChangeListener( this );
    }
}
