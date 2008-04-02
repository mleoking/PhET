/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.faraday.model.AbstractMagnet;

/**
 * BFieldOutsideGraphic is the B-field outside the magnet, which fills the apparatus panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BFieldOutsideGraphic extends AbstractBFieldGraphic implements SimpleObserver, ApparatusPanel2.ChangeListener{

    private AbstractMagnet _magnetModel;
    
    /**
     * Constructor.
     * 
     * @param component
     * @param magnetModel
     * @param xSpacing
     * @param ySpacing
     * @param inMagnetPlane true=show field in magnet's 2D plane, false=show field slightly outside magnet's 2D plane
     */
    public BFieldOutsideGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing, boolean inMagnetPlane ) {
        super( component, magnetModel, xSpacing, ySpacing, inMagnetPlane );
        
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    /*
     * Creates the description of the needles (grid points) in the grid.
     * In this case, we fill the apparatus panel with grid points, based on
     * the bounds of the apparatus panel and the spacing of the points.
     */
    protected GridPoint[] createGridPoints() {
        
        ArrayList gridPoints = new ArrayList();
        
        Rectangle bounds = getGridBoundsReference();
        final int xSpacing = getXSpacing();
        final int ySpacing = getYSpacing();
        
        // Determine how many points are needed to fill the apparatus panel.
        final int xCount = (int) ( bounds.width / xSpacing ) + 1;
        final int yCount = (int) ( bounds.height / ySpacing ) + 1;
        
        // Create the grid points.
        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                double x = bounds.getX() + ( i * xSpacing );
                double y = bounds.getY() + ( j * ySpacing );
                gridPoints.add( new GridPoint( x, y ) );
            }
        }
        
        // convert to array
        return (GridPoint[]) gridPoints.toArray( new GridPoint[gridPoints.size()] );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the magnet changes, update the needle descriptors.
     */
    public void update() {
        updateStrengthAndOrientation();
        repaint();
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
    }
}
