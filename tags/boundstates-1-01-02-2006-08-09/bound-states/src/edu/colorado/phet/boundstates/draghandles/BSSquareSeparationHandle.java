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
 * BSSquareSeparationHandle is the drag handle used to control the 
 * separation attribute of a potential composed of square wells.  
 * <p>
 * A vertical marker (instantiated by this handles drag manager)
 * places 2 vertical dashed lines through the right and left edges
 * of two adjacent wells.  The wells closest to the origin are used.
 * The handle is attached on the rightmost of these vertical lines,
 * just above the offset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareSeparationHandle extends BSPotentialHandle {
    
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
    public BSSquareSeparationHandle( BSSquarePotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potential, potentialSpec, chartNode, BSAbstractHandle.HORIZONTAL );
        
        int significantDecimalPlaces = potentialSpec.getSeparationRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( SimStrings.get( "drag.separation" ) );
        
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
        
        // position -> x coordinates
        final double minSeparation = spec.getSeparationRange().getMin();
        final double maxSeparation = spec.getSeparationRange().getMax();
        double minPosition = 0;
        double maxPosition = 0;
        final int n = potential.getNumberOfWells();
        if ( n % 2 == 0 ) {
            // even number of wells
            minPosition = minSeparation / 2;
            maxPosition = maxSeparation / 2;
        }
        else {
            // odd number of wells
            final double width = potential.getWidth();
            minPosition = ( width / 2 ) + minSeparation;
            maxPosition = ( width / 2 ) + maxSeparation;
        } 
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
        
        BSSquarePotential potential = (BSSquarePotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        
        potential.deleteObserver( this );
        {
            // Convert the drag handle's location to model coordinates
            Point2D viewPoint = getGlobalPosition();
            Point2D modelPoint = viewToModel( viewPoint );
            final double handlePosition = modelPoint.getX();

            // Calculate the separation
            final int n = potential.getNumberOfWells();
            double separation = 0;
            if ( n % 2 == 0 ) {
                // even number of wells
                separation = 2 * handlePosition;
            }
            else {
                // odd number of wells
                final double width = potential.getWidth();
                separation = handlePosition - ( width / 2 );
            }
            final int numberOfSignicantDecimalPlaces = spec.getSeparationRange().getSignificantDecimalPlaces();
            separation = round( separation, numberOfSignicantDecimalPlaces );

            potential.setSeparation( separation );
            setValueDisplay( separation );
        }
        potential.addObserver( this );
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
            final double separation = potential.getSeparation();
            final double offset = potential.getOffset();
            final double height = potential.getHeight();
            final double width = potential.getWidth();
            
            // Calculate the handle's model coordinates
            final double handleEnergy = offset + height + 1;
            double handlePosition = 0;
            if ( n % 2 == 0 ) {
                // even number of wells
                handlePosition = separation / 2;
            }
            else {
                // odd number of wells
                handlePosition = ( width / 2 )  + separation;
            }
            
            // Convert to view coordinates
            Point2D modelPoint = new Point2D.Double( handlePosition, handleEnergy );
            Point2D viewPoint = modelToView( modelPoint );
            
            setGlobalPosition( viewPoint );
            setValueDisplay( separation );
        }
        addPropertyChangeListener( this );
    }
}
