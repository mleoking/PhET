// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents the electron shell in an isotope as a "cloud" that
 * grows and shrinks depending on the number of electrons that it contains.
 * This particular class implements behavior needed for the Isotopes
 * simulation, which is somewhat different from that needed for Build an
 * Atom.
 *
 * @author John Blanco
 */
public class IsotopeElectronCloudNode extends PNode {

    // Base color to use when drawing clouds.
    private static final Color CLOUD_BASE_COLOR = Color.BLUE;

    private static final double MIN_RADIUS = 80;
    private static final double MAX_RADIUS = 150;
    private static final int MAX_ELECTRONS = 18; // For argon.

    // Cloud version of the representation.
    private final PhetPPath electronCloudNode;

    /**
     * Constructor.
     */
    public IsotopeElectronCloudNode( final ModelViewTransform mvt, final OrbitalViewProperty orbitalView, final Atom atom ) {

        // Add a listener that will update the cloud's appearance when the
        // atom configuration changes.
        atom.addAtomListener( new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                Function.LinearFunction electronCountToRadiusFunction = new Function.LinearFunction( 1, MAX_ELECTRONS, MIN_RADIUS, MAX_RADIUS );
                double radius = electronCountToRadiusFunction.evaluate( atom.getNumElectrons() );
                double centerX = atom.getPosition().getX();
                double centerY = atom.getPosition().getY();
                final Shape electronShellShape = mvt.modelToView( new Ellipse2D.Double( centerX - radius,
                        centerY - radius, radius * 2, radius * 2 ) );
                electronCloudNode.setPathTo( electronShellShape );
                Function.LinearFunction electronCountToAlphaMapping = new Function.LinearFunction( 0, MAX_ELECTRONS, 90, 175 );//Map to alpha values
                int alpha = atom.getNumElectrons() == 0 ? 0 : (int) electronCountToAlphaMapping.evaluate( atom.getNumElectrons() );//But if there are no electrons, be transparent
                Paint shellGradientPaint = new RoundGradientPaint(
                        electronShellShape.getBounds2D().getCenterX(),
                        electronShellShape.getBounds2D().getCenterY(),
                        new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), alpha ),
                        new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 3, electronShellShape.getBounds2D().getHeight() / 3 ),
                        new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), 0 ) );
                electronCloudNode.setPaint( shellGradientPaint );
            }
        } );

        Paint initialPaint = new Color( 0, 0, 0, 0 );
        electronCloudNode = new PhetPPath( initialPaint ) {
            {
                orbitalView.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( orbitalView.getValue() == OrbitalView.ISOTOPES_RESIZING_CLOUD );
                    }
                } );
            }
        };

        addChild( electronCloudNode );
    }
}
