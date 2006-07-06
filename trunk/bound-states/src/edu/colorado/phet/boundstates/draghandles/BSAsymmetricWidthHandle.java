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
import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSAsymmetricWidthHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWidthHandle extends AbstractHandle implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private BSAsymmetricPotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAsymmetricWidthHandle( BSAsymmetricPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( AbstractHandle.HORIZONTAL );
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        setPotential( potential );
        updateDragBounds();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSAsymmetricPotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateView();
    }
    
    public BSAsymmetricPotential getPotential() {
        return _potential;
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
        final double minPosition = -( _potentialSpec.getWidthRange().getMax() / 2 );
        final double maxPosition = -( _potentialSpec.getWidthRange().getMin() / 2 );
        final double minX = _chartNode.positionToNode( minPosition );
        final double maxX = _chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        final double minEnergy = _potential.getOffset();
        final double maxEnergy = _potential.getOffset() + _potential.getHeight();
        final double minY = _chartNode.energyToNode( maxEnergy );
        final double maxY = _chartNode.energyToNode( minEnergy );
        
        // bounds, local coordinates
        final double w = maxX - minX;
        final double h = maxY - minY;
        Rectangle2D dragBounds = new Rectangle2D.Double( minX, minY, w, h );

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
            final double width = Math.abs( 2 * modelPoint.getX() );
//            System.out.println( "BSAsymmetricWidthHandle.updateModel x=" + globalNodePoint.getX() + " width=" + width );//XXX
            _potential.setWidth( width );
            setValueDisplay( width );
        }
        _potential.addObserver( this );
    }

    protected void updateView() {
        assert( _potential.getNumberOfWells() == 1 ); // single well only!
        assert( _potential.getCenter() == 0 ); // center at zero
        removePropertyChangeListener( this );
        {
            final double width = _potential.getWidth();
            final double position = -( width / 2 ); // handle on left edge of well
            final double height = _potential.getHeight();
            final double offset = _potential.getOffset();
            Point2D modelPoint = new Point2D.Double( position, offset + ( height / 2 ) ); // handle half way up edge of well
            Point2D localNodePoint = _chartNode.energyToNode( modelPoint );
            Point2D globalNodePoint = _chartNode.localToGlobal( localNodePoint );
//            System.out.println( "BSAsymmetricWidthHandle.updateView width=" + width + " x=" + globalNodePoint.getX() );//XXX
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
