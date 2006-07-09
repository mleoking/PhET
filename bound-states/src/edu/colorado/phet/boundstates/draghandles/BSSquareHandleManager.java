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

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSSquareHandleManager extends BSAbstractHandleManager {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSSquareSeparationMarkers _separationMarkers;
    private BSSquareOffsetHandle _offsetHandle;
    private BSSquareHeightHandle _heightHandle;
    private BSSquareWidthHandle _widthHandle;
    private BSSquareSeparationHandle _separationHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSSquarePotential potential ) {

        removeAllChildren();
        
        if ( potential != null ) {
            
            _separationMarkers = new BSSquareSeparationMarkers( potential, _chartNode );
            addChild( _separationMarkers );
            _separationMarkers.setVisible( _potentialSpec.getSeparationRange().getMin() != _potentialSpec.getSeparationRange().getMax() );
            
            _offsetHandle = new BSSquareOffsetHandle( potential, _potentialSpec, _chartNode );
            addChild( _offsetHandle );
            _offsetHandle.setVisible( _potentialSpec.getOffsetRange().getMin() != _potentialSpec.getOffsetRange().getMax() );

            _heightHandle = new BSSquareHeightHandle( potential, _potentialSpec, _chartNode );
            addChild( _heightHandle );
            _heightHandle.setVisible( _potentialSpec.getHeightRange().getMin() != _potentialSpec.getHeightRange().getMax() );
            
            _widthHandle = new BSSquareWidthHandle( potential, _potentialSpec, _chartNode );
            addChild( _widthHandle );
            _widthHandle.setVisible( _potentialSpec.getWidthRange().getMin() != _potentialSpec.getWidthRange().getMax() );
            
            _separationHandle = new BSSquareSeparationHandle( potential, _potentialSpec, _chartNode );
            addChild( _separationHandle );
            _separationHandle.setVisible( _potentialSpec.getSeparationRange().getMin() != _potentialSpec.getSeparationRange().getMax() );
        }
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
       
    public void updateDragBounds() {
        if ( _separationMarkers != null ) {
            _separationMarkers.updateView();
        }
        if ( _offsetHandle != null ) {
            _offsetHandle.updateDragBounds();
        }
        if ( _heightHandle != null ) {
            _heightHandle.updateDragBounds();
        }
        if ( _widthHandle != null ) {
            _widthHandle.updateDragBounds();
        }
        if ( _separationHandle != null ) {
            _separationHandle.updateDragBounds();
        }
    }

    public PNode getHelpNode() {
        return _widthHandle;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        if ( _separationMarkers != null ) {
            _separationMarkers.setColorScheme( colorScheme );
        }
        if ( _offsetHandle != null ) {
            _offsetHandle.setColorScheme( colorScheme );
        }
        if ( _heightHandle != null ) {
            _heightHandle.setColorScheme( colorScheme );
        }
        if ( _widthHandle != null ) {
            _widthHandle.setColorScheme( colorScheme );
        }
        if ( _separationHandle != null ) {
            _separationHandle.setColorScheme( colorScheme );
        }
    }
}
