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
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BSCoulomb1DHandleManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DHandleManager extends BSAbstractHandleManager {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSCoulomb1DSpacingMarkers _spacingMarkers;
    private BSCoulomb1DSpacingHandle _spacingHandle;
    private BSCoulomb1DOffsetHandle _offsetHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulomb1DHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSCoulomb1DPotential potential ) {
        clear();
        if ( potential != null ) {
            
            if ( !_potentialSpec.getSpacingRange().isZero() ) {
                
                _spacingMarkers = new BSCoulomb1DSpacingMarkers( potential, _chartNode );
                addChild( _spacingMarkers );
                
                _spacingHandle = new BSCoulomb1DSpacingHandle( potential, _potentialSpec, _chartNode );
                addChild( _spacingHandle );
            }
            
            if ( !_potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSCoulomb1DOffsetHandle( potential, _potentialSpec, _chartNode );
                addChild( _offsetHandle );
            }
        }
    }
    
    private void clear() {
        removeAllChildren();
        _offsetHandle = null;
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
   
    public void updateDragBounds() {
        if ( _spacingMarkers != null ) {
            _spacingMarkers.updateView();
        }
        if ( _spacingHandle != null ) {
            _spacingHandle.updateDragBounds();
        }
        if ( _offsetHandle != null ) {
            _offsetHandle.updateDragBounds();
        }
    }
    
    public PNode getHelpNode() {
        PNode node = null;
        if ( _offsetHandle != null ) {
            node = _offsetHandle;
        }
        else {
            node = _spacingHandle;
        }
        return node;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        if ( _spacingMarkers != null ) {
            _spacingMarkers.setColorScheme( colorScheme );
        }
        if ( _spacingHandle != null ) {
            _spacingHandle.setColorScheme( colorScheme );
        }
        if ( _offsetHandle != null ) {
            _offsetHandle.setColorScheme( colorScheme );
        }
    }
}
