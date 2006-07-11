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

import java.awt.Color;
import java.awt.geom.Rectangle2D;

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
    
    private BSSquareSeparationMarkers _separationMarkers;
    private BSSquareSeparationHandle _separationHandle;
    private BSSquareOffsetHandle _offsetHandle;
    private BSSquareHeightHandle _heightHandle;
    private BSSquareWidthHandle _widthHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSSquarePotential potential ) {
        clear();      
        if ( potential != null ) {
            
            BSPotentialSpec potentialSpec = getPotentialSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( !potentialSpec.getSeparationRange().isZero() ) {
                _separationMarkers = new BSSquareSeparationMarkers( potential, chartNode );
                addChild( _separationMarkers );

                _separationHandle = new BSSquareSeparationHandle( potential, potentialSpec, chartNode );
                addChild( _separationHandle );
            }

            if ( !potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSSquareOffsetHandle( potential, potentialSpec, chartNode );
                addChild( _offsetHandle );
            }

            if ( !potentialSpec.getHeightRange().isZero() ) {
                _heightHandle = new BSSquareHeightHandle( potential, potentialSpec, chartNode );
                addChild( _heightHandle );
            }

            if ( !potentialSpec.getWidthRange().isZero() ) {
                _widthHandle = new BSSquareWidthHandle( potential, potentialSpec, chartNode );
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
       
    public void updateLayout() {
        updateClip();
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
