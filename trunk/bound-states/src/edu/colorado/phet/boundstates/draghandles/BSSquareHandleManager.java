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

/**
 * BSSquareHandleManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareHandleManager extends BSAbstractHandleManager {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSSquareSeparationMarkers _separationMarkers;
    private BSSquareSeparationHandle _separationHandle;
    private BSSquareOffsetHandle _offsetHandle;
    private BSSquareHeightHandle _heightHandle;
    private BSSquareWidthHandle _widthHandle;
    
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
        clear();      
        if ( potential != null ) {
            
            if ( !_potentialSpec.getSeparationRange().isZero() ) {
                _separationMarkers = new BSSquareSeparationMarkers( potential, _chartNode );
                addChild( _separationMarkers );

                _separationHandle = new BSSquareSeparationHandle( potential, _potentialSpec, _chartNode );
                addChild( _separationHandle );
            }

            if ( !_potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSSquareOffsetHandle( potential, _potentialSpec, _chartNode );
                addChild( _offsetHandle );
            }

            if ( !_potentialSpec.getHeightRange().isZero() ) {
                _heightHandle = new BSSquareHeightHandle( potential, _potentialSpec, _chartNode );
                addChild( _heightHandle );
            }

            if ( !_potentialSpec.getWidthRange().isZero() ) {
                _widthHandle = new BSSquareWidthHandle( potential, _potentialSpec, _chartNode );
                addChild( _widthHandle );
            }
        }
    }
    
    private void clear() {
        removeAllChildren();
        _separationMarkers = null;
        _separationHandle = null;
        _offsetHandle = null;
        _heightHandle = null;
        _widthHandle = null;
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
       
    public void updateDragBounds() {
        if ( _separationMarkers != null ) {
            _separationMarkers.updateView();
        }
        if ( _separationHandle != null ) {
            _separationHandle.updateDragBounds();
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
    }

    public PNode getHelpNode() {
        return _widthHandle;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        if ( _separationMarkers != null ) {
            _separationMarkers.setColorScheme( colorScheme );
        }
        if ( _separationHandle != null ) {
            _separationHandle.setColorScheme( colorScheme );
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
    }
}
