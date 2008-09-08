/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SnowfallNode is the visual representation of snowfall.
 * A white gradient is drawn in a shape that represents the atmosphere.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SnowfallNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BASE_COLOR = new Color( 255, 255, 255 );
    private static final Color NO_SNOW_COLOR = ColorUtils.createColor( BASE_COLOR, 0 /* alpha */ );
    private static final int MAX_ALPHA = 255; // opaque to simulate "white out" conditions at highest elevation
    private static final double ELEVATION_WHERE_FADE_BEGINS = 4800; // elevation where gradient paint starts fading out (meters), "white-out" above this 
    private static final double ELEVATION_WHERE_FADE_ENDS = 1500; // elevation where gradient paint has fully faded out (meters)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ClimateListener _climateListener;
    private final ModelViewTransform _mvt;
    private final Rectangle2D _worldBounds; // world bounds in model coordinates (meters x meters)
    private final PPath _pathNode;
    private final GeneralPath _path;
    private final Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SnowfallNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _glacier = glacier;
        _mvt = mvt;
        _worldBounds = new Rectangle2D.Double( 0, 0, 1, 1 );
        _path = new GeneralPath();
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        _pathNode = new PhetPPath(); // use PhetPPath for Gradient Paint workaround on Mac OS 10.4
        _pathNode.setStroke( null );
        addChild( _pathNode );
        
        _glacierListener = new GlacierAdapter() {
            // ice thickness determines the shape used to draw the snowfall
            public void iceThicknessChanged() {
                updateShape();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _climateListener = new ClimateAdapter() {
            // climate determines the color used to draw the snowfall
            public void snowfallChanged() {
                updatePaint();
            }
        };
        _glacier.getClimate().addClimateListener( _climateListener );
        
        updateShape();
        updatePaint();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        _glacier.getClimate().removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * When this node becomes visible, update it.
     */
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                updateShape();
                updatePaint();
            }
        }
    }
    
    /**
     * Sets the world bounds.
     * <p>
     * The semantics of this rectangle are a bit odd, and match those of Viewport.
     * (x,y) is the upper-left corner of the rectangle, and width and height 
     * are distances to the right and down respectively.
     * 
     * @param worldBounds
     * @param maxSnowfallElevation
     */
    public void setWorldBounds( Rectangle2D worldBounds ) {
        if ( !worldBounds.equals( _worldBounds ) ) {
            // height is adjusted to match where the gradient paint fades to NO_SNOW_COLOR.
            // This ensures that we don't see an abrupt edge at the bottom of the gradient (if our bounds are too short),
            // and we don't draw more than we need to (if our bounds are too tall).
            final double height = worldBounds.getY() - ELEVATION_WHERE_FADE_ENDS;
            _worldBounds.setFrame( worldBounds.getX(), worldBounds.getY(), worldBounds.getWidth(), height );
            updateShape();
            updatePaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the shape to match the world bounds, and the profile of the glacier's surface.
     * Snowfall appears above the surface of the glaciers and the valley floor;
     * ie, at all points that are in the atmosphere.
     */
    private void updateShape() {
        
        if ( getVisible() ) {

            // constants
            final double dx = IceNode.getDx();
            final double minX = _worldBounds.getMinX() - dx; // go one sample further than we really need to
            final double maxX = _worldBounds.getMaxX() + dx; // go one sample further than we really need to
            final double maxY = _worldBounds.getY();

            _path.reset();
            
            // top right
            _pModel.setLocation( maxX, maxY );
            _mvt.modelToView( _pModel, _pView );
            _path.moveTo( (float) _pView.getX(), (float) _pView.getY() );
            
            // top left
            _pModel.setLocation( minX, maxY );
            _mvt.modelToView( _pModel, _pView );
            _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            
            // draw the ice and valley surface
            double x = minX;
            double y = 0;
            while ( x <= maxX ) {
                y = _glacier.getSurfaceElevation( x );
                _pModel.setLocation( x, y );
                _mvt.modelToView( _pModel, _pView );
                _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                x += dx;
            }
            
            // connect to top-right
            _path.closePath();

            _pathNode.setPathTo( _path );
        }
    }
    
    /*
     * Updates the gradient paint to match the climate's snowfall.
     */
    private void updatePaint() {
        
        assert ( ELEVATION_WHERE_FADE_BEGINS > ELEVATION_WHERE_FADE_ENDS );
        
        if ( getVisible() ) {

            // get the snowfall value
            final double snowfall = _glacier.getClimate().getSnowfall();
            assert ( GlaciersConstants.SNOWFALL_RANGE.contains( snowfall ) );

            // convert snowfall to alpha channel value.
            // alpha channel is varies linearly.
            final double min = GlaciersConstants.SNOWFALL_RANGE.getMin();
            final double max = GlaciersConstants.SNOWFALL_RANGE.getMax();
            final double scale = ( snowfall - min ) / ( max - min );
            final int alpha = (int) ( scale * MAX_ALPHA );

            // create gradient paint.
            // The gradient starts to fade at ELEVATION_WHERE_FADE_BEGINS, and is fully transparent at ELEVATION_WHERE_FADE_ENDS.
            Rectangle2D modelBounds = new Rectangle2D.Double( 
                    _worldBounds.getX(), ELEVATION_WHERE_FADE_BEGINS, 
                    _worldBounds.getWidth(), ELEVATION_WHERE_FADE_BEGINS - ELEVATION_WHERE_FADE_ENDS );
            Rectangle2D viewBounds = _mvt.modelToView( modelBounds );
            Color maxSnowColor = ColorUtils.createColor( BASE_COLOR, alpha );
            GradientPaint paint = new GradientPaint( 
                    (float) viewBounds.getX(), (float) viewBounds.getMinY(), maxSnowColor, 
                    (float) viewBounds.getX(), (float) viewBounds.getMaxY(), NO_SNOW_COLOR );
            _pathNode.setPaint( paint );
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Icon createIcon() {
        return new ImageIcon( GlaciersImages.SNOWFLAKE );
    }
}
