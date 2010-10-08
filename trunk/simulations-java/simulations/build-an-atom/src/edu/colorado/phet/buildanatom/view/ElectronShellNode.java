package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ElectronShellNode extends PNode {

    // Color Test Set 03 - Blue background with gray atoms
    private static final Color BACKGROUND_COLOR = new Color( 185, 205, 229 );
    private static final Color INNER_SHELL_BASE_COLOR = Color.DARK_GRAY;
    private static final Color INNER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
    private static final Color OUTER_SHELL_BASE_COLOR = Color.DARK_GRAY;
    private static final Color OUTER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;

    private static final Color OUTER_SHELL_BASE_COLOR_4_ELECTRONS = new Color(
            OUTER_SHELL_BASE_COLOR.getRed(),
            OUTER_SHELL_BASE_COLOR.getGreen(),
            OUTER_SHELL_BASE_COLOR.getBlue(),
            200);
    private static final Color OUTER_SHELL_FADE_TO_COLOR_4_ELECTRONS = new Color(
            OUTER_SHELL_FADE_TO_COLOR.getRed(),
            OUTER_SHELL_FADE_TO_COLOR.getGreen(),
            OUTER_SHELL_FADE_TO_COLOR.getBlue(),
            100);

    // Stroke for drawing the electron shells.
    private static final Stroke ELECTRON_SHELL_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                                                                         new float[] { 3, 3 }, 0 );
    private static final Paint ELECTRON_SHELL_STROKE_PAINT = new Color( 0, 0, 255, 100 );

    public ElectronShellNode( final ModelViewTransform2D mvt, final MutableBoolean viewOrbitals, final Atom atom, Double shellRadius ) {
        Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -shellRadius,
                -shellRadius,
                shellRadius * 2,
                shellRadius * 2 ) );
        final PNode electronShellNode = new PhetPPath( electronShellShape, ELECTRON_SHELL_STROKE, ELECTRON_SHELL_STROKE_PAINT ) {{
            setOffset( atom.getPosition() );
            final SimpleObserver updateVisibility = new SimpleObserver() {
                public void update() {
                    setVisible( viewOrbitals.getValue() );
                }
            };
            viewOrbitals.addObserver( updateVisibility );
        }};
        addChild( electronShellNode );

//        Paint outerShell4ElectronsPaint = new RoundGradientPaint( 0, 0, OUTER_SHELL_BASE_COLOR_4_ELECTRONS,
//                new Point2D.Double( LARGE_SHELL_RADIUS * 0.75, LARGE_SHELL_RADIUS * 0.75), OUTER_SHELL_FADE_TO_COLOR_4_ELECTRONS );
        Paint outerShell4ElectronsPaint = new RoundGradientPaint( electronShellShape.getBounds2D().getCenterX(), electronShellShape.getBounds2D().getCenterY(),
                                                                  OUTER_SHELL_BASE_COLOR_4_ELECTRONS,
                                                                  new Point2D.Double( electronShellShape.getBounds2D().getWidth()/2, electronShellShape.getBounds2D().getHeight()/2 ),
                                                                  OUTER_SHELL_FADE_TO_COLOR_4_ELECTRONS);
        PhetPPath fuzzyView = new PhetPPath( electronShellShape,outerShell4ElectronsPaint){{
            final SimpleObserver updateVisibility = new SimpleObserver() {
                public void update() {
                    setVisible( !viewOrbitals.getValue() );
                }
            };
            viewOrbitals.addObserver( updateVisibility );
        }};
        viewOrbitals.setValue( false );//XXX
        addChild( fuzzyView );
    }
}
