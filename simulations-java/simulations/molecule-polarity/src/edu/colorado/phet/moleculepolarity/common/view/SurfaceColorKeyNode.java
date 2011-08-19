// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Explains the color scheme used for a surface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SurfaceColorKeyNode extends PComposite {

    private static final Dimension SIZE = new Dimension( 400, 25 );
    private static final Font FONT = new PhetFont( 16 );
    private static final double Y_SPACING = 5;
    private static final double Y_INSET = 5;

    // Color key for "electron density" surface representation.
    public static class ElectronDensityColorKeyNode extends SurfaceColorKeyNode {
        public ElectronDensityColorKeyNode() {
            super( new Color[] { Color.WHITE, Color.BLACK },
                   MPStrings.LESS, MPStrings.MORE );
        }
    }

    // Color key for primary "electrostatic potential" surface representation.
    public static class ElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public ElectrostaticPotentialColorKeyNode() {
            super( new Color[] { Color.BLUE, Color.WHITE, Color.RED },
                   MPStrings.POSITIVE, MPStrings.NEGATIVE );
        }
    }

    // Color key for secondary "electrostatic potential" surface representation.
    //TODO white seams are visible between the segments in this color key
    public static class RainbowElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public RainbowElectrostaticPotentialColorKeyNode() {
            super( new Color[] { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, VisibleColor.INDIGO, VisibleColor.VIOLET },
                   "Positive", "Negative" );//TODO i18n
        }
    }

    public SurfaceColorKeyNode( Color[] colors, String leftLabel, String rightLabel ) {

        final double segmentWidth = SIZE.width / (double) ( colors.length - 1 );

        // spectrum, composed of multiple PPath's because Java 1.5 doesn't include LinearGradientPaint
        PPath spectrumNode = new PPath( new Rectangle2D.Double( 0, 0, SIZE.width, SIZE.height ) );
        for ( int i = 0; i < colors.length - 1; i++ ) {
            Color leftColor = colors[i];
            Color rightColor = colors[i + 1];
            double x = i * segmentWidth;
            final Paint gradient = new GradientPaint( (float) x, 0f, leftColor, (float) ( x + segmentWidth ), 0f, rightColor );
            spectrumNode.addChild( new PPath( new Rectangle2D.Double( x, 0, segmentWidth, SIZE.height ) ) {{
                setPaint( gradient );
                setStroke( null );
            }} );
        }

        // labels
        PText leftLabelNode = new PText( leftLabel ) {{
            setFont( FONT );
        }};
        PText rightLabelNode = new PText( rightLabel ) {{
            setFont( FONT );
        }};

        // rendering order
        addChild( spectrumNode );
        addChild( leftLabelNode );
        addChild( rightLabelNode );

        // layout
        leftLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMinX() + Y_INSET,
                                 spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        rightLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMaxX() - rightLabelNode.getFullBoundsReference().getWidth() - Y_INSET,
                                  spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
    }
}
