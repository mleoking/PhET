// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.common.view.Colors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.sequentialFill;

/**
 * @author Sam Reid
 */
public class DefaultLevelSelectionScreen extends AbstractLevelSelectionNode {
    public static Color[] colors = new Color[] { Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow };

    public DefaultLevelSelectionScreen( final String title, final BuildAFractionCanvas canvas2 ) {
        super( title, createInfo(), canvas2 );
    }

    private static List<List<LevelInfo>> createInfo() {
        return List.list( List.list( toShapeLevelInfo( 1, Pattern.pie( 1 ) ),
                                     toShapeLevelInfo( 2, Pattern.verticalBars( 2 ) ),
                                     toShapeLevelInfo( 3 ),
                                     toShapeLevelInfo( 4 ),
                                     toShapeLevelInfo( 5 ) ),
                          List.list( createNumberLevel( 1 ),
                                     createNumberLevel( 2 ),
                                     createNumberLevel( 3 ),
                                     createNumberLevel( 4 ),
                                     createNumberLevel( 5 ) ) );
    }

    private static LevelInfo toShapeLevelInfo( final int level ) {
        return toShapeLevelInfo( level, Pattern.polygon( 80, level ) );
    }

    private static LevelInfo toShapeLevelInfo( final int level, Pattern pattern ) {
        final int levelIndex = level - 1;
        return new LevelInfo( "Level " + level, new PatternNode( sequentialFill( pattern, level ), colors[levelIndex % colors.length] ), 0, 3, levelIndex, LevelType.SHAPES );
    }

    private static LevelInfo createNumberLevel( int level ) {return new LevelInfo( "Level " + level, createLevelIcon( level ), 0, 3, level - 1, LevelType.NUMBERS );}

    private static List<BufferedImage> images = List.list( NUMBER_0, NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4, NUMBER_5, NUMBER_6, NUMBER_7, NUMBER_8, NUMBER_9 ).map( new F<BufferedImage, BufferedImage>() {
        @Override public BufferedImage f( final BufferedImage bufferedImage ) {
            return BufferedImageUtils.multiScaleToHeight( bufferedImage, 100 );
        }
    } );

    private static PNode createLevelIcon( final int level ) {
        return new PNode() {{
            final int width = 110;
//            Color prototype = colors[( level - 1 ) % colors.length];
//            RoundGradientPaint paint = new RoundGradientPaint( width / 2, width / 2, new Color( prototype.getRed(), prototype.getGreen(), prototype.getBlue(), 100 ), new Point2D.Double( width / 3, width / 3 ), new Color( prototype.getRed(), prototype.getGreen(), prototype.getBlue(), 0 ) );
//            final PhetPPath circle = new PhetPPath( new Ellipse2D.Double( 0, 0, width, width ), paint );
//            addChild( circle );
            final PImage image = new PImage( images.index( level ) );
            addChild( image );
//            image.centerFullBoundsOnPoint( circle.getFullBounds().getCenterX(), circle.getFullBounds().getCenterY() );
        }};
    }
}