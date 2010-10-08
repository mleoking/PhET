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
 * Node that represents an electron shell, aka "orbit", in the view.  This
 * node is able to switch between different representations of a shell.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ElectronShellNode extends PNode {

    // Color Test Set 03 - Blue background with gray atoms
//    private static final Color CENTER_COLOR = Color.DARK_GRAY;
//    private static final Color OUTER_COLOR = new Color( 185, 205, 100 );
    private static final Color CENTER_COLOR = new Color(0, 0, 255, 100);
    private static final Color OUTER_COLOR = new Color(0, 0, 255, 0);

    // Stroke for drawing the electron orbits.
    private static final Stroke ELECTRON_SHELL_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 );

    // Paint for the electron orbitals.
    private static final Paint ELECTRON_SHELL_STROKE_PAINT = new Color( 0, 0, 255, 100 );

    /**
     * Constructor.
     */
    public ElectronShellNode( final ModelViewTransform2D mvt, final MutableBoolean viewOrbitals, final Atom atom, Double shellRadius ) {

        Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -shellRadius,
                -shellRadius,
                shellRadius * 2,
                shellRadius * 2 ) );

        // Create and add the node that will depict the shell as a circular
        // orbit.
        final PNode electronOrbitNode = new PhetPPath( electronShellShape, ELECTRON_SHELL_STROKE, ELECTRON_SHELL_STROKE_PAINT ) { {
                setOffset( atom.getPosition() );
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
            } };
        addChild( electronOrbitNode );

        // Create and add the nodes that will be used when depicting the
        // electrons as a fuzzy cloud.
        Paint outerShell4ElectronsPaint = new RoundGradientPaint(
                electronShellShape.getBounds2D().getCenterX(),
                electronShellShape.getBounds2D().getCenterY(),
                CENTER_COLOR,
//                new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 2, electronShellShape.getBounds2D().getHeight() / 2 ),
                new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 3, electronShellShape.getBounds2D().getHeight() / 3 ),
                OUTER_COLOR );
        PhetPPath electronCloudNode = new PhetPPath( electronShellShape, outerShell4ElectronsPaint ) {
            {
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( !viewOrbitals.getValue() );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
            }
        };
        viewOrbitals.setValue( false );//XXX
        addChild( electronCloudNode );
    }
}
