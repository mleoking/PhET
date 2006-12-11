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

import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSAsymmetricWidthHandle is the drag handle used to control the 
 * width attribute of a potential composed of a single asymmetric well.
 * <p>
 * The handle is attached half way up the left side of the well.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWidthHandle extends BSPotentialHandle {
    
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
    public BSAsymmetricWidthHandle( BSAsymmetricPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
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
        
        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();
        
        if ( potential.getCenter() != 0 || potential.getNumberOfWells() != 1 ) {
            throw new UnsupportedOperationException( "this implementation only supports 1 well centered at 0" );
        }
        
        // position -> x coordinates
        final double minPosition = -( spec.getWidthRange().getMax() / 2 );
        final double maxPosition = -( spec.getWidthRange().getMin() / 2 );
        final double minX = chartNode.positionToNode( minPosition );
        final double maxX = chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        final double minEnergy = potential.getOffset();
        final double maxEnergy = potential.getOffset() + potential.getHeight();
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

        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        
        potential.deleteObserver( this );
        {
            // Convert the drag handle's location to model coordinates
            Point2D viewPoint = getGlobalPosition();
            Point2D modelPoint = viewToModel( viewPoint );
            final double handlePosition = modelPoint.getX();
            
            // Calculate the width
            double width = Math.abs( 2 * handlePosition );
            final int numberOfSignicantDecimalPlaces = spec.getWidthRange().getSignificantDecimalPlaces();
            width = round( width, numberOfSignicantDecimalPlaces );
            
            potential.setWidth( width );
            setValueDisplay( width );
        }
        potential.addObserver( this );
    }

    /**
     * Updates the drag handle to match the model.
     */
    protected void updateView() {

        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        
        removePropertyChangeListener( this );
        {
            // Some potential attributes that we need...
            final double width = potential.getWidth();
            final double height = potential.getHeight();
            final double offset = potential.getOffset();
            
            // Calculate the handle's model coordinates
            final double handlePosition = -( width / 2 ); // left edge of well
            final double handleEnergy = offset + ( height / 2 ); // half way up edge of well

            // Convert to view coordinates
            Point2D modelPoint = new Point2D.Double( handlePosition, handleEnergy );
            Point2D viewPoint = modelToView( modelPoint );
            
            setGlobalPosition( viewPoint );
            setValueDisplay( width );
        }
        addPropertyChangeListener( this );
    }
}
