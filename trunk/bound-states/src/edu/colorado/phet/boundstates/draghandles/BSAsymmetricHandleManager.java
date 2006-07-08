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


public class BSAsymmetricHandleManager extends BSAbstractHandleManager {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
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

        removeAllChildren();
        
        if ( potential != null ) {
            
            _offsetHandle = new BSAsymmetricOffsetHandle( potential, _potentialSpec, _chartNode );
            _offsetHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _offsetHandle );
            _offsetHandle.setVisible( _potentialSpec.getOffsetRange().getMin() != _potentialSpec.getOffsetRange().getMax() );

            _heightHandle = new BSAsymmetricHeightHandle( potential, _potentialSpec, _chartNode );
            _heightHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _heightHandle );
            _heightHandle.setVisible( _potentialSpec.getHeightRange().getMin() != _potentialSpec.getHeightRange().getMax() );

            _widthHandle = new BSAsymmetricWidthHandle( potential, _potentialSpec, _chartNode );
            _widthHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _widthHandle );
            _widthHandle.setVisible( _potentialSpec.getWidthRange().getMin() != _potentialSpec.getWidthRange().getMax() );
        }
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
        //XXX
    }
       
}
