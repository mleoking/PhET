// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Key for a surface's color scheme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SurfaceColorKeyNode extends PComposite {

    private static final Dimension SIZE = new Dimension( 400, 20 );
    private static final Font TITLE_FONT = new PhetFont( 12 );
    private static final Font RANGE_FONT = new PhetFont( 12 );
    private static final double X_INSET = 0;
    private static final double Y_SPACING = 2;

    // Color key for "electron density" surface representation.
    public static class ElectronDensityColorKeyNode extends SurfaceColorKeyNode {
        public ElectronDensityColorKeyNode() {
            super( MPColors.BW_GRADIENT, MPStrings.ELECTRON_DENSITY, MPStrings.LESS, MPStrings.MORE );
        }
    }

    // Color key for primary "electrostatic potential" surface representation.
    public static class ElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public ElectrostaticPotentialColorKeyNode() {
            super( MPColors.RWB_GRADIENT, MPStrings.ELECTROSTATIC_POTENTIAL, MPStrings.POSITIVE, MPStrings.NEGATIVE );
        }
    }

    // Color key for secondary "electrostatic potential" surface representation.
    public static class RainbowElectrostaticPotentialColorKeyNode extends SurfaceColorKeyNode {
        public RainbowElectrostaticPotentialColorKeyNode() {
            super( MPColors.ROYGB_GRADIENT, MPStrings.ELECTROSTATIC_POTENTIAL, MPStrings.POSITIVE, MPStrings.NEGATIVE );
        }
    }

    /**
     * Constructor
     *
     * @param colors     colors used for the gradient, in left-to-right order
     * @param leftLabel
     * @param rightLabel
     */
    public SurfaceColorKeyNode( Color[] colors, String title, String leftLabel, String rightLabel ) {

        // spectrum, composed of multiple segments, because Java 1.5 doesn't include LinearGradientPaint
        final double segmentWidth = SIZE.width / (double) ( colors.length - 1 );
        final Shape spectrumShape = new Rectangle2D.Double( 0, 0, SIZE.width, SIZE.height );
        PPath spectrumNode = new PPath( spectrumShape ) {{
            setStroke( null );
        }};
        double x = 0;
        for ( int i = 0; i < colors.length - 1; i++ ) {
            final Paint gradient = new GradientPaint( (float) x, 0f, colors[i], (float) ( x + segmentWidth ), 0f, colors[i + 1] );
            spectrumNode.addChild( new PPath( new Rectangle2D.Double( x, 0, segmentWidth + 1, SIZE.height ) ) {{ // +1 to workaround visible seams between PPaths
                setPaint( gradient );
                setStroke( null );
            }} );
            x += segmentWidth;
        }

        // put an outline on top, because outlining spectrumNode looks incorrect
        spectrumNode.addChild( new PPath( spectrumShape ) );

        // labels
        PText titleNode = new PText( title ) {{
            setFont( TITLE_FONT );
        }};
        PText leftLabelNode = new PText( leftLabel ) {{
            setFont( RANGE_FONT );
        }};
        PText rightLabelNode = new PText( rightLabel ) {{
            setFont( RANGE_FONT );
        }};

        // rendering order
        addChild( spectrumNode );
        addChild( titleNode );
        addChild( leftLabelNode );
        addChild( rightLabelNode );

        // layout
        spectrumNode.setOffset( 0, 0 );
        titleNode.setOffset( spectrumNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                             spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        leftLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMinX() + X_INSET,
                                 spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        rightLabelNode.setOffset( spectrumNode.getFullBoundsReference().getMaxX() - rightLabelNode.getFullBoundsReference().getWidth() - X_INSET,
                                  spectrumNode.getFullBoundsReference().getMaxY() + Y_SPACING );
    }
}
