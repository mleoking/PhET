/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
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
 * A white gradient is drawn in a rectangle that covers the entire world.
 * This rectangle is clipped so that only the portion above the surface of
 * the ice or valley floor (whichever is higher) is drawn.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SnowfallNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BASE_COLOR = new Color( 255, 255, 255 );
    private static final Color NO_SNOW_COLOR = ColorUtils.createColor( BASE_COLOR, 0 );
    private static final int MAX_ALPHA = 255; // opaque to simulate "white out" conditions at highest elevation
    private static final double ELEVATION_WHERE_FADE_BEGINS = 4500; // elevation where gradient paint starts fading out (meters), "white-out" above this 
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
        
        _pathNode = new PPath();
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
     * Sets the world bounds.
     * <p>
     * The semantics of this rectangle are a bit odd, and match those of Viewport.
     * (x,y) is the upper-left corner of the rectangle, and width and height 
     * are distances to the right and down respectively.
     * 
     * @param worldBounds
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
     * Updates the shape to match the world bounds, clipped to the region above the ice and valley floor.
     */
    private void updateShape() {
        Rectangle2D viewBounds = _mvt.modelToView( _worldBounds ); 
        _pathNode.setPathTo( viewBounds );
    }
    
    /*
     * Updates the gradient paint to match the climate's snowfall.
     */
    private void updatePaint() {
        
        assert( ELEVATION_WHERE_FADE_BEGINS > ELEVATION_WHERE_FADE_ENDS );
        
        // get the snowfall value
        final double snowfall = _glacier.getClimate().getSnowfall();
        assert( GlaciersConstants.SNOWFALL_RANGE.contains( snowfall ) );
        
        // convert snowfall to alpha channel value.
        // alpha channel is varies linearly.
        final double min = GlaciersConstants.SNOWFALL_RANGE.getMin();
        final double max = GlaciersConstants.SNOWFALL_RANGE.getMax();
        final double scale = ( snowfall - min ) / ( max - min );
        final int alpha = (int) ( scale * MAX_ALPHA );
        
        // create gradient paint.
        // The gradient starts to fade at ELEVATION_WHERE_FADE_BEGINS, and is fully transparent at ELEVATION_WHERE_FADE_ENDS.
        Rectangle2D modelBounds = new Rectangle2D.Double( _worldBounds.getX(), ELEVATION_WHERE_FADE_BEGINS, 
                                                          _worldBounds.getWidth(), ELEVATION_WHERE_FADE_BEGINS - ELEVATION_WHERE_FADE_ENDS );
        Rectangle2D viewBounds = _mvt.modelToView( modelBounds );
        Color maxSnowColor = ColorUtils.createColor( BASE_COLOR, alpha );
        GradientPaint paint = new GradientPaint( (float)viewBounds.getX(), (float)viewBounds.getMinY(), maxSnowColor, 
                                                 (float)viewBounds.getX(), (float)viewBounds.getMaxY(), NO_SNOW_COLOR );
        _pathNode.setPaint( paint );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Icon createIcon() {
        return new ImageIcon( GlaciersImages.SNOWFLAKE );
    }
}
