/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.view.NeedleColorStrategy.AlphaColorStrategy;

/**
 * 
 * BFieldInsideGraphic is the view of the B-field inside the bar magnet.
 * This implementation is a bit hard-wired.
 * If you change the bar magnet image, you'll need to change X_SPACING, Y_SPACING and COLUMNS.
 * <p>
 * NOTE: This graphic, by request, is not connected to the Options>Field Controls feature.
 * The size of the needles and their spacing is fixed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BFieldInsideGraphic extends AbstractBFieldGraphic implements SimpleObserver {

    private static final int X_SPACING = 34;
    private static final int Y_SPACING = 20;
    private static final int COLUMNS = 7; // this must be an odd number!
    
    private BarMagnet _barMagnetModel;
    private Point2D _barMagnetLocation;
    
    /**
     * Constructor.
     * 
     * @param component
     * @param barMagnetModel
     */
    public BFieldInsideGraphic( Component component, BarMagnet barMagnetModel ) {
        super( component, barMagnetModel, X_SPACING, Y_SPACING, true /* inMagnetPlane */ );
        
        setNeedleColorStrategy( new AlphaColorStrategy() ); // need to use alpha because we're drawing this on top of the magnet
        
        _barMagnetModel = barMagnetModel;
        _barMagnetModel.addObserver( this );
        
        _barMagnetLocation = new Point2D.Double();
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _barMagnetModel.removeObserver( this );
        _barMagnetModel = null;
    }
    
    /**
     * Updates the graphic when it becomes visible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }
    
    /*
     * Creates the points in the grid.
     * There are 2 rows and COLUMNS columns of points, with one column centered.
     */
    protected ArrayList createGridPoints() {
        
        assert( COLUMNS % 2 == 1 ); // must be odd!
        
        _barMagnetModel.getLocation( _barMagnetLocation );
        
        ArrayList gridPoints = new ArrayList();
        
        GridPoint gridPoint;
        final double xSpacing = getXSpacing();
        final double ySpacing = getYSpacing();
        double x, y;
        
        for ( int i = 0; i <= ( COLUMNS / 2 ); i++ ) {
            
            // above center
            y = _barMagnetLocation.getY() - ( ySpacing  / 2 );
            
            x = _barMagnetLocation.getX() + ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
            
            x = _barMagnetLocation.getX() - ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );

            // below center
            y = _barMagnetLocation.getY() + ( ySpacing / 2 );
            
            x = _barMagnetLocation.getX() + ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
            
            x = _barMagnetLocation.getX() - ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
        }
        
        return gridPoints;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the magnet changes, update the needle descriptors and this graphic's bounds.
     */
    public void update() {
        if ( isVisible() ) {
            updateGridBounds();
            updateGridPoints();
        }
    }
   
    /*
     * Updates the grid bounds to match the bounds of the bar magnet model.
     */
    private void updateGridBounds() {
        System.out.println( "BFieldInsideGraphic.updateGridBounds" );//XXX
        setGridBounds( 
                (int)( _barMagnetModel.getX() - ( _barMagnetModel.getWidth() / 2 ) ), 
                (int)( _barMagnetModel.getY() - ( _barMagnetModel.getHeight() / 2 ) ), 
                (int) _barMagnetModel.getWidth(),
                (int) _barMagnetModel.getHeight() );
    }
}
