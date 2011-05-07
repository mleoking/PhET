// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents an atom and that labels it with the chemical symbol
 * and atomic number so that isotopes of the same element can be distinguished
 * from one another.
 *
 * @author John Blanco
 */
public class LabeledIsotopeNode extends PNode {

    private static final double MIN_RADIUS_FOR_LABEL = 10; // In screen units, which is roughly pixels.

    private final SphericalNode sphericalNode;

    /**
     * Constructor.
     */
    public LabeledIsotopeNode( final ModelViewTransform mvt, final MovableAtom isotope, final Color baseColor ) {

        // Create a gradient that gives the particles a 3D look.  The numbers
        // used were empirically determined.
        double radius = isotope.getRadius();
        double transformedRadius = mvt.modelToViewDeltaX( radius );
        Paint particlePaint = new RoundGradientPaint(
                transformedRadius / 2,
                -transformedRadius / 2,
                ColorUtils.brighterColor( baseColor, 0.8 ),
                new Point2D.Double( transformedRadius / 2, transformedRadius / 2 ),
                ColorUtils.darkerColor( baseColor, 0.2 ) );
        sphericalNode = new SphericalNode( mvt.modelToViewDeltaX( radius * 2 ), particlePaint, false );
        addChild( sphericalNode );

        // Create, scale, and add the label, assuming the isotope is large enough.
        if ( transformedRadius > MIN_RADIUS_FOR_LABEL ){
            HTMLNode labelNode = new HTMLNode(){{
                setHTML( "<sup>" + isotope.getAtomConfiguration().getMassNumber() + "</sup>" + isotope.getAtomConfiguration().getSymbol() );
                setFont( new PhetFont(12, true) );
                setScale( sphericalNode.getFullBoundsReference().width * 0.65 / getFullBoundsReference().width );
                if ( 0.30 * baseColor.getRed() + 0.59 * baseColor.getGreen() + 0.11 * baseColor.getBlue() < 125 ){
                    setHTMLColor( Color.WHITE );
                }
                setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
            }};
            sphericalNode.addChild( labelNode );
        }

        // Add the code for moving this node when the model element's position
        // changes.
        isotope.addPositionListener( new SimpleObserver(){
            public void update() {
                sphericalNode.setOffset( mvt.modelToView( isotope.getPosition().toPoint2D() ) );
            }
        });

        // Add a cursor handler to signal to the user that this is movable.
        addInputEventListener( new CursorHandler() );

        // Add a drag listener that will move the model element when the user
        // drags this node.
        addInputEventListener( new PDragEventHandler() {

            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                isotope.setUserControlled( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) );
                isotope.setPositionAndDestination( isotope.getPosition().getX() + modelDelta.getX(),
                        isotope.getPosition().getY() + modelDelta.getY() );
            }

            @Override
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                isotope.setUserControlled( false );
            }
        } );
    }

    public static void main( String[] args ) {

        // Canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );

        // Add the item being testing.
        LabeledIsotopeNode isotopeNode = new LabeledIsotopeNode( ModelViewTransform.createIdentity(),
                new MovableAtom(1, 0, 20, new Point2D.Double( 0, 0 ), new ConstantDtClock()), Color.RED );
        isotopeNode.setOffset( 100, 100 );
        canvas.addWorldChild( isotopeNode );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
