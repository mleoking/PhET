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

import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSHarmonicOscillatorAngularFrequencyHandle is the drag handle used to 
 * control the angular frequency attribute of a potential composed of a 
 * single harmonic oscillator well.
 * <p>
 * The handle is placed at a height V0 up the left side of the well.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorAngularFrequencyHandle extends BSPotentialHandle {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Handle is placed at this height up the left edge of the well.
    private static final double V0 = 4; // eV
    
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
    public BSHarmonicOscillatorAngularFrequencyHandle( BSHarmonicOscillatorPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potential, potentialSpec, chartNode, BSAbstractHandle.HORIZONTAL );
        
        int significantDecimalPlaces = potentialSpec.getAngularFrequencyRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( SimStrings.get( "drag.angularFrequency" ) );
        
        updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        
        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();
        
        assert( potential.getNumberOfWells() == 1 ); // single well only!
        assert( potential.getCenter() == 0 ); // center at zero
        
        // position -> x coordinates
        final double minAngularFrequency = spec.getAngularFrequencyRange().getMin();
        final double maxAngularFrequency = spec.getAngularFrequencyRange().getMax();
        final double mass = potential.getParticle().getMass();
        final double minPosition = -( angularFrequencyToWidth( minAngularFrequency, mass ) / 2 );
        final double maxPosition = -( angularFrequencyToWidth( maxAngularFrequency, mass ) / 2 );
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
//        System.out.println( "BSHarmonicOscillatorWidthHandle.updateDragBounds dragBounds=" + dragBounds );//XXX

        // Convert to global coordinates
        dragBounds = chartNode.localToGlobal( dragBounds );

        setDragBounds( dragBounds );
        updateView();
    }
    
    /**
     * Updates the model to match the drag handle.
     */
    protected void updateModel() {

        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        
        potential.deleteObserver( this );
        {
            // Convert the drag handle's location to model coordinates
            Point2D viewPoint = getGlobalPosition();
            Point2D modelPoint = viewToModel( viewPoint );
            final double handlePosition = modelPoint.getX();
            
            // Calculate the angular frequency
            final double width = 2 * Math.abs( handlePosition );
            final double mass = potential.getParticle().getMass();
            double angularFrequency = widthToAngularFrequency( width, mass );
            final int numberOfSignicantDecimalPlaces = spec.getAngularFrequencyRange().getSignificantDecimalPlaces();
            angularFrequency = round( angularFrequency, numberOfSignicantDecimalPlaces );
            
            potential.setAngularFrequency( angularFrequency );
            setValueDisplay( angularFrequency );
        }
        potential.addObserver( this );
    }

    /**
     * Updates the drag handle to match the model.
     */
    protected void updateView() {

        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential)getPotential();
        
        removePropertyChangeListener( this );
        {
            // Some potential attributes that we need...
            final double angularFrequency = potential.getAngularFrequency();
            final double mass = potential.getParticle().getMass();
            final double width = angularFrequencyToWidth( angularFrequency, mass );
            final double offset = potential.getOffset();
            
            // Calculate the handle's model coordinates
            final double handlePosition = -( width / 2 ); // right edge of the well
            final double handleEnergy = offset + V0;  // fixed height above the offset

            // Convert to view coordinates
            Point2D modelPoint = new Point2D.Double( handlePosition, handleEnergy );
            Point2D viewPoint = modelToView( modelPoint );
            
            setGlobalPosition( viewPoint );
            setValueDisplay( angularFrequency );
        }
        addPropertyChangeListener( this );
    }

    //----------------------------------------------------------------------------
    // Conversion utilities
    //----------------------------------------------------------------------------
    
    /*
     * Converts angular frequency to width.
     * 
     * @param af
     * @return
     */
    private static double angularFrequencyToWidth( double af, double mass ) {
        final double xd = Math.sqrt( ( 2 * V0 ) / ( mass * af * af ) );
        return 2 * xd;
    }
    
    /*
     * Converts width to angular frequency.
     * 
     * @param width
     * @return
     */
    private static double widthToAngularFrequency( double width, double mass ) {
        final double xd = width / 2;
        return Math.sqrt( ( 2 * V0 ) / ( mass * xd * xd ) );
    }
}
