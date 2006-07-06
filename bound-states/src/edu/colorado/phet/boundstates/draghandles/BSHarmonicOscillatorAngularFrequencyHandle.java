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

import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSHarmonicOscillatorAngularFrequencyHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorAngularFrequencyHandle extends AbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double V0 = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private BSHarmonicOscillatorPotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorAngularFrequencyHandle( BSHarmonicOscillatorPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( AbstractHandle.HORIZONTAL );
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        setPotential( potential );
        updateDragBounds();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSHarmonicOscillatorPotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateView();
    }
    
    public BSHarmonicOscillatorPotential getPotential() {
        return _potential;
    }
    
    private double angularFrequencyToWidth( double af ) {
        final double m = _potential.getParticle().getMass();
        final double xd = Math.sqrt( ( 2 * V0 ) / ( m * af * af ) );
        return 2 * xd;
    }
    
    private double widthToAngularFrequency( double width ) {
        final double m = _potential.getParticle().getMass();
        final double xd = width / 2;
        return Math.sqrt( ( 2 * V0 ) / ( m * xd * xd ) );
    }
    
    //----------------------------------------------------------------------------
    // Bounds
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        assert( _potential.getNumberOfWells() == 1 ); // single well only!
        assert( _potential.getCenter() == 0 ); // center at zero
        
        // position -> x coordinates
        final double minAngularFrequency = _potentialSpec.getAngularFrequencyRange().getMin();
        final double maxAngularFrequency = _potentialSpec.getAngularFrequencyRange().getMax();
        final double minPosition = -( angularFrequencyToWidth( minAngularFrequency ) / 2 );
        final double maxPosition = -( angularFrequencyToWidth( maxAngularFrequency ) / 2 );
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
//        System.out.println( "BSHarmonicOscillatorWidthHandle.updateDragBounds dragBounds=" + dragBounds );//XXX

        // Convert to global coordinates
        dragBounds = _chartNode.localToGlobal( dragBounds );

        setDragBounds( dragBounds );
        updateView();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------
    
    protected void updateModel() {
        assert( _potential.getNumberOfWells() == 1 ); // single well only!
        assert( _potential.getCenter() == 0 ); // center at zero
        
        _potential.deleteObserver( this );
        {
            Point2D globalNodePoint = getGlobalPosition();
            Point2D localNodePoint = _chartNode.globalToLocal( globalNodePoint );
            Point2D modelPoint = _chartNode.nodeToEnergy( localNodePoint );
            
            final double width = 2 * Math.abs( modelPoint.getX() );
            final double angularFrequency = widthToAngularFrequency( width );
            
//            System.out.println( "BSHarmonicOscillatorWidthHandle.updateModel x=" + globalNodePoint.getX() + " width=" + width + " angularFrequency=" + angularFrequency );//XXX
            _potential.setAngularFrequency( angularFrequency );
            setValueDisplay( angularFrequency );
        }
        _potential.addObserver( this );
    }

    protected void updateView() {
        assert( _potential.getNumberOfWells() == 1 ); // single well only!
        assert( _potential.getCenter() == 0 ); // center at zero
        
        removePropertyChangeListener( this );
        {
            final double angularFrequency = _potential.getAngularFrequency();
            final double width = angularFrequencyToWidth( angularFrequency );
 
            final double position = -( width / 2 ); // right edge of the well
            final double offset = _potential.getOffset();
            Point2D modelPoint = new Point2D.Double( position, offset + V0 );
            Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
//            System.out.println( "BSHarmonicOscillatorWidthHandle.updateView angularFrequency=" + angularFrequency + " width=" + width + " x=" + globalNodePoint.getX() );//XXX
            setGlobalPosition( globalNodePoint );
            setValueDisplay( angularFrequency );
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
