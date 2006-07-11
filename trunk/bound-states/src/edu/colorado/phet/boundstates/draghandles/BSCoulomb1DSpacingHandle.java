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
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSCoulomb1DSpacingHandle is the drag handle used to control the 
 * spacing attribute of a potential composed of 1D Coulomb wells.
 * <p>
 * A vertical marker (instantiated by this handles drag manager)
 * places 2 vertical dashed lines through the centers of the 
 * 2 wells that are closest to the origin.
 * The handle is attached on the rightmost of these vertical lines,
 * just below the offset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DSpacingHandle extends BSPotentialHandle {
    
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
    public BSCoulomb1DSpacingHandle( BSCoulomb1DPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potential, potentialSpec, chartNode, BSAbstractHandle.HORIZONTAL );
        
        int significantDecimalPlaces = potentialSpec.getSpacingRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( SimStrings.get( "drag.spacing" ) );
        
        updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();
        
        if ( potential.getCenter() != 0 ) {
            throw new UnsupportedOperationException( "this implementation only supports potentials centered at 0" );
        }
        
        final int n = potential.getNumberOfWells();
        double minSpacing = spec.getSpacingRange().getMin();
        double maxSpacing = spec.getSpacingRange().getMax();
        double minPosition = 0;
        double maxPosition = 0;
        if ( n % 2 == 0 ) {
            // even number of wells
            minPosition = minSpacing / 2;
            maxPosition = maxSpacing / 2;
        }
        else {
            // odd number of wells
            minPosition = minSpacing;
            maxPosition = maxSpacing;
        }
        
        // position -> x coordinates
        final double minX = chartNode.positionToNode( minPosition );
        final double maxX = chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        ValueAxis yAxis = chartNode.getEnergyPlot().getRangeAxis();
        final double minEnergy = yAxis.getLowerBound();
        final double maxEnergy =  yAxis.getUpperBound();
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
       
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential)getPotential();
        BSCombinedChartNode chartNode = getChartNode();

        potential.deleteObserver( this );
        {
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = chartNode.globalToLocal( globalNodePoint );
            Point2D modelPoint = chartNode.nodeToEnergy( localNodePoint );
            final double d = modelPoint.getX();

            double spacing = 0;
            final int n = potential.getNumberOfWells();
            if ( n % 2 == 0 ) {
                // even number of wells
                spacing = 2 * d;
            }
            else { 
                // odd number of wells
                spacing = d;
            }
            
            potential.setSpacing( spacing );
            setValueDisplay( spacing );
        }
        potential.addObserver( this );
        updateDragBounds();
    }
    
    /**
     * Updates the drag handle to match the model.
     */
    protected void updateView() {

        BSCoulomb1DPotential potential = (BSCoulomb1DPotential)getPotential();
        BSCombinedChartNode chartNode = getChartNode();
        
        removePropertyChangeListener( this );
        {
            final double spacing = potential.getSpacing();
            final int n = potential.getNumberOfWells();
            double position = 0;
            if ( n % 2 == 0 ) {
                // even number of wells
                position = spacing / 2;
            }
            else {
                // odd number of wells
                position = spacing;
            }
            
            final double energy = potential.getOffset() - 5;
            Point2D modelPoint = new Point2D.Double( position, energy );
            Point2D localNodePoint = chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = chartNode.localToGlobal( localNodePoint );
            setGlobalPosition( globalNodePoint );
            setValueDisplay( spacing );
        }
        addPropertyChangeListener( this );
    }
}
