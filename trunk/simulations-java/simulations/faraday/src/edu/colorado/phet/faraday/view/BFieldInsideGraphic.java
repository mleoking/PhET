/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
    
    private AffineTransform _transform; // reusable transform
    private Point2D _point2D; // reusable point
    private Rectangle _rect; // reusable rectangle
    
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
        _transform = new AffineTransform();
        _point2D = new Point2D.Double();
        _rect = new Rectangle();
        
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
    protected GridPoint[] createGridPoints() {
        
        assert( COLUMNS % 2 == 1 ); // must be odd!
        
        _barMagnetModel.getLocation( _barMagnetLocation );
        
        ArrayList gridPoints = new ArrayList();
        
        GridPoint gridPoint;
        final double xSpacing = getXSpacing();
        final double ySpacing = getYSpacing();
        final double bx = _barMagnetLocation.getX();
        final double by = _barMagnetLocation.getY();
        double x, y;
        
        // Create grid point for magnet with zero rotation.
        for ( int i = 0; i <= ( COLUMNS / 2 ); i++ ) {
            
            // above center
            y = by - ( ySpacing  / 2 );
            
            x = bx + ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
            
            x = bx - ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );

            // below center
            y = by + ( ySpacing / 2 );
            
            x = bx + ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
            
            x = bx - ( i * xSpacing );
            gridPoint = new GridPoint( x, y );
            gridPoints.add( gridPoint );
        }
        
        // Transform all grid points by the magnet's rotation angle.
        final double direction = _barMagnetModel.getDirection();
        if ( direction != 0 ) {
            Iterator i = gridPoints.iterator();
            while ( i.hasNext() ) {
                gridPoint = (GridPoint) i.next();
                _point2D.setLocation( gridPoint.getX(), gridPoint.getY() );
                _transform.setToIdentity();
                _transform.translate( bx, by );
                _transform.rotate( direction );
                _transform.translate( -bx, -by );
                _transform.transform( _point2D, _point2D );
                gridPoint.setLocation( _point2D.getX(), _point2D.getY() );
            }
        }
        
        // convert to array
        return (GridPoint[]) gridPoints.toArray( new GridPoint[gridPoints.size()] );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the magnet changes, update the grid bounds, which causes the grid to be rebuilt.
     */
    public void update() {
        if ( isVisible() ) {
            
            final double bx = _barMagnetModel.getX();
            final double by = _barMagnetModel.getY();
            
            // bounds of the magnet with zero rotation
            final int x = (int) ( bx - ( _barMagnetModel.getWidth() / 2 ) );
            final int y = (int) ( by - ( _barMagnetModel.getHeight() / 2 ) );
            _rect.setRect( x, y, (int) _barMagnetModel.getWidth(), (int) _barMagnetModel.getHeight() );
            
            // transform to match magnet's orientation
            Rectangle gridBounds = _rect;
            final double direction = _barMagnetModel.getDirection();
            if ( direction != 0 ) {
                _transform.setToIdentity();
                _transform.translate( bx, by );
                _transform.rotate( direction );
                _transform.translate( -bx, -by );
                Shape txShape = _transform.createTransformedShape( _rect );
                gridBounds = txShape.getBounds();
            }
            
            // set the grid bounds
            setGridBounds( gridBounds.x, gridBounds.y, gridBounds.width, gridBounds.height );
        }
    }
}
