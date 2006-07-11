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

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * BSAbstractHandleManager is the base class for all drag handle managers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractHandleManager extends PClip {

    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    public BSAbstractHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    public abstract void updateLayout();
    
    public abstract PNode getHelpNode();
    
    public abstract void setColorScheme( BSColorScheme colorScheme );
    
    protected BSPotentialSpec getPotentialSpec() {
        return _potentialSpec;
    }
    
    protected BSCombinedChartNode getChartNode() {
        return _chartNode;
    }
    
    public void setVisible( boolean visible ) {
        if ( visible ) {
            updateLayout();
        }
        super.setVisible( visible );
    }
    
    protected void updateClip() {
        Rectangle2D energyPlotBounds = _chartNode.getEnergyPlotBounds();
        Rectangle2D globalBounds = _chartNode.localToGlobal( energyPlotBounds );
        Rectangle2D localBounds = globalToLocal( globalBounds );
        setPathTo( localBounds );
    }
    
}
