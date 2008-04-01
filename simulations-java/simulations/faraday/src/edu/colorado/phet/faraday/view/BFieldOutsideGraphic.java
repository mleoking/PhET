/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.Dimension;

import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.faraday.model.AbstractMagnet;

/**
 * BFieldOutsideGraphic is the B-field outside the magnet, which fills the apparatus panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BFieldOutsideGraphic extends CompassGridGraphic implements ApparatusPanel2.ChangeListener{

    /**
     * Constructor.
     * 
     * @param component
     * @param magnetModel
     * @param xSpacing
     * @param ySpacing
     */
    public BFieldOutsideGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing) {
        super( component, magnetModel, xSpacing, ySpacing );
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the grid bounds whenever the apparatus panel's canvas size changes.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        Dimension parentSize = event.getCanvasSize();
        setGridBounds( 0, 0, parentSize.width, parentSize.height );
        super.setBoundsDirty();
        resetSpacing(); 
    }
}
