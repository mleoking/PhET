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
import edu.colorado.phet.boundstates.model.BSCoulomb3DPotential;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSCoulomb3DHandleManager extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSCombinedChartNode _chartNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulomb3DHandleManager( BSCombinedChartNode chartNode ) {
        super();
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSCoulomb3DPotential potential ) {

        removeAllChildren();
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        //XXX
    }
       
    public void updateDragBounds() {
    }
}
