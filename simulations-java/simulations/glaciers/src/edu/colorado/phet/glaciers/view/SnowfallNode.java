/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.umd.cs.piccolo.nodes.PImage;


public class SnowfallNode extends PhetPNode {
    
    private static final double MIN_SCALE = 1;
    private static final double MAX_SCALE = MIN_SCALE * 5;
    
    private final Climate _climate;
    private final ModelViewTransform _mvt;
    private PImage _imageNode;
    
    public SnowfallNode( Climate climate, ModelViewTransform mvt ) {
        
        _climate = climate;
        _climate.addClimateListener( new ClimateAdapter() {
            public void snowfallChanged() {
                update();
            }
        });
        
        _mvt = mvt;
        
        _imageNode = new PImage( GlaciersImages.SNOWFLAKE );
        addChild( _imageNode );
        
        Point2D p = _mvt.modelToView( 2500, 5000 );//XXX
        _imageNode.setOffset( p );//XXX shouldn't apply offset to imageNode
        
        update();
    }
    
    private void update() {
        final double snowfall = _climate.getSnowfall();
        final double min = GlaciersConstants.SNOWFALL_RANGE.getMin();
        final double max = GlaciersConstants.SNOWFALL_RANGE.getMax();
        final double scale = MIN_SCALE + ( MAX_SCALE - MIN_SCALE ) * ( snowfall - min ) / ( max - min );
        Point2D offset = _imageNode.getOffset();
        _imageNode.getTransformReference( true ).setToScale( scale, scale );
        _imageNode.setOffset( offset );
    }

    public static Icon createIcon() {
        return new ImageIcon( GlaciersImages.SNOWFLAKE );
    }
}
