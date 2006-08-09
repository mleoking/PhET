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

import org.jfree.chart.axis.ValueAxis;

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
        final double separation = potential.getSeparation();
        final double minWidth = spec.getWidthRange().getMin();
        final double maxWidth = spec.getWidthRange().getMax();
        
        // position -> x coordinates
        final double minPosition = ( ( n * minWidth ) + ( ( n - 1 ) * separation ) ) / 2;
        final double maxPosition = ( ( n * maxWidth ) + ( ( n - 1 ) * separation ) ) / 2;
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

        potential.deleteObserver( this );
        {
            // Convert the drag handle's location to model coordinates
            Point2D viewPoint = getGlobalPosition();
            Point2D modelPoint = viewToModel( viewPoint );
            final double handlePosition = modelPoint.getX();

            // Calculate the width.
            // handlePosition = ( n*width + (n-1)*separation ) / 2
            final int n = potential.getNumberOfWells();
            final double separation = potential.getSeparation();
            double width = ( ( 2 * handlePosition ) - ( ( n - 1 ) * separation ) ) / n;
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
        
        removePropertyChangeListener( this );
        {
            // Some potential attributes that we need...
            final int n = potential.getNumberOfWells();
            final double center = potential.getCenter( n - 1 ); // center of the right-most well
            final double width = potential.getWidth();
            final double height = potential.getHeight();
 
            // Calculate the handle's model coordinates
            final double handlePosition = center + ( width / 2 ); // right edge of the well
            final double handleEnergy = potential.getEnergyAt( handlePosition + 0.001 ) - ( height / 2 ); // half way up the side of the well
            
            // Convert to view coordinates
            Point2D modelPoint = new Point2D.Double( handlePosition, handleEnergy );
            Point2D viewPoint = modelToView( modelPoint );
            
            setGlobalPosition( viewPoint );
            setValueDisplay( width );
        }
        addPropertyChangeListener( this );
    }
}
