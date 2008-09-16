/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.GlaciersModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class CoordinatesNode extends PhetPNode {
    
    private static final Color AXIS_COLOR = Color.BLACK;

    private final ModelViewTransform _mvt;
    private final ElevationAxisNode _leftElevationAxisNode, _rightElevationAxisNode;
    private final DistanceAxisNode _distanceAxisNode;
    
    public CoordinatesNode( GlaciersModel model, ModelViewTransform mvt, boolean englishUnits ) {
        _mvt = mvt;
        _leftElevationAxisNode = new ElevationAxisNode( mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, false, englishUnits );
        addChild( _leftElevationAxisNode );
        _rightElevationAxisNode = new ElevationAxisNode( mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, true, englishUnits );
        addChild( _rightElevationAxisNode );
        _distanceAxisNode = new DistanceAxisNode( model.getValley(), mvt, englishUnits );
        addChild( _distanceAxisNode  );
    }
    
    public void setEnglishUnits( boolean englishUnits ) {
        _leftElevationAxisNode.setEnglishUnits( englishUnits );
        _rightElevationAxisNode.setEnglishUnits( englishUnits );
        _distanceAxisNode.setEnglishUnits( englishUnits );
    }
    
    /*
     * Moves the elevation (y) axes to the left & right edges of the zoomed viewport.
     * Rebuilds the distance (x) axis.
     */
    public void update( Rectangle2D zoomedViewportBounds ) {
        
        Rectangle2D rView = _mvt.modelToView( zoomedViewportBounds );
        
        // rebuild the horizontal (distance) axis
        final int minX = (int) Math.max( 0, zoomedViewportBounds.getX() );
        final int maxX = (int) Math.max( 1, zoomedViewportBounds.getX() + zoomedViewportBounds.getWidth() );
        _distanceAxisNode.setRange( minX, maxX );
        
        // reposition the vertical (elevation) axes
        final double margin = 15; // pixels
        _leftElevationAxisNode.setOffset( rView.getMinX() + margin, _rightElevationAxisNode.getYOffset() );
        _rightElevationAxisNode.setOffset( rView.getMaxX() - margin, _rightElevationAxisNode.getYOffset() );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------

    /**
     * Creates an icon to represent a set of x-y axes.
     * @return Icon
     */
    public static Icon createIcon() {
        
        PNode parentNode = new PNode();
        
        // constants
        final double xStart = -10;
        final double xEnd = 20;
        final double yStart = -10;
        final double yEnd = 10;
        final double tickSpacing = 5;
        final double tickLength = 2;
        final Stroke stroke = new BasicStroke( 1f );
        
        // x axis with tick marks
        PPath xAxisNode = new PPath( new Line2D.Double( xStart, 0, xEnd, 0 ) );
        xAxisNode.setStroke( stroke );
        xAxisNode.setStrokePaint( AXIS_COLOR );
        parentNode.addChild( xAxisNode );
        for ( double x = ( xStart + tickSpacing ); x < xEnd; x += tickSpacing ) {
            PPath tickNode = new PPath( new Line2D.Double( x, -(tickLength/2), x, (tickLength/2) ) );
            tickNode.setStroke( stroke );
            tickNode.setStrokePaint( AXIS_COLOR );
            parentNode.addChild( tickNode );
        }
        
        // y axis with tick marks
        PPath yAxisNode = new PPath( new Line2D.Double( 0, yStart, 0, yEnd ) );
        yAxisNode.setStroke( stroke );
        yAxisNode.setStrokePaint( AXIS_COLOR );
        parentNode.addChild( yAxisNode );
        for ( double y = ( yStart + tickSpacing ); y < yEnd; y += tickSpacing ) {
            PPath tickNode = new PPath( new Line2D.Double( -(tickLength/2), y, (tickLength/2), y ) );
            tickNode.setStroke( stroke );
            tickNode.setStrokePaint( AXIS_COLOR );
            parentNode.addChild( tickNode );
        }
        
        Image image = parentNode.toImage();
        return new ImageIcon( image );
    }
}
