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
import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BSAsymmetricHandleManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricHandleManager extends BSAbstractHandleManager {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSAsymmetricOffsetHandle _offsetHandle;
    private BSAsymmetricHeightHandle _heightHandle;
    private BSAsymmetricWidthHandle _widthHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAsymmetricHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setPotential( BSAsymmetricPotential potential ) {
        clear();
        if ( potential != null ) {
            
            if ( !_potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSAsymmetricOffsetHandle( potential, _potentialSpec, _chartNode );
                addChild( _offsetHandle );
            }

            if ( !_potentialSpec.getHeightRange().isZero() ) {
                _heightHandle = new BSAsymmetricHeightHandle( potential, _potentialSpec, _chartNode );
                addChild( _heightHandle );
            }

            if ( !_potentialSpec.getWidthRange().isZero() ) {
                _widthHandle = new BSAsymmetricWidthHandle( potential, _potentialSpec, _chartNode );
                addChild( _widthHandle );
            }
        }
    }
    
    private void clear() {
        removeAllChildren();
        _offsetHandle = null;
        _heightHandle = null;
        _widthHandle = null;
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
    
    public void updateDragBounds() {
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
        return _offsetHandle;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
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
