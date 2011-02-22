// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MobileAtom;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents an atom and that labels it with the chemical symbol
 * and atomic number so that isotopes of the same element can be distinguished
 * from one another.
 *
 * @author John Blanco
 */
public class LabeledIsotopeNode extends PNode {

    private final SphericalNode sphericalNode;

    /**
     * Constructor.
     */
    public LabeledIsotopeNode( final ModelViewTransform2D mvt, final MobileAtom isotope, final Color baseColor ) {

        // Create a gradient that gives the particles a 3D look.  The numbers
        // used were empirically determined.
        double radius = isotope.getRadius();
        Paint particlePaint = new RoundGradientPaint( -radius / 1.5, -radius / 1.5,
                ColorUtils.brighterColor( baseColor, 0.8 ), new Point2D.Double( radius, radius ),
                baseColor );
        sphericalNode = new SphericalNode( mvt.modelToViewDifferentialX( radius * 2 ), particlePaint, false );
        addChild( sphericalNode );

        // Create, scale, and add the label.
        HTMLNode labelNode = new HTMLNode(){{
            setHTML( "<html><sup>" + isotope.getAtomConfiguration().getMassNumber() + "</sup>" + isotope.getAtomConfiguration().getSymbol() + "</html>" );
            setFont( new PhetFont(12, true) );
            setScale( sphericalNode.getFullBoundsReference().width * 0.65 / getFullBoundsReference().width );
            if ( 0.30 * baseColor.getRed() + 0.59 * baseColor.getGreen() + 0.11 * baseColor.getBlue() < 125 ){
                setHTMLColor( Color.WHITE );
            }
            setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
        }};
        sphericalNode.addChild( labelNode );

        // TODO: Add the code for hooking the node's position to the model unit's position here.
        sphericalNode.setOffset( mvt.modelToViewDouble( isotope.getPosition() ) );

        // Add a cursor handler to signal to the user that this is movable.
        addInputEventListener( new CursorHandler() );

        /*
         * TODO: variations on the following commented-out code will be needed to handle
         * positioning and dragging.
         *
        subatomicParticle.addPositionListener( new SimpleObserver() {
            public void update() {
                updatePosition();
            }
        } );

        addInputEventListener( new PDragEventHandler() {

            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                subatomicParticle.setUserControlled( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                Point2D modelDelta = mvt.viewToModelDifferential( delta.width, delta.height );
                subatomicParticle.setPositionAndDestination( subatomicParticle.getPosition().getX() + modelDelta.getX(),
                        subatomicParticle.getPosition().getY() + modelDelta.getY() );
            }

            @Override
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                subatomicParticle.setUserControlled( false );
            }
        } );
         */
    }

    public static void main( String[] args ) {

        // Canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );

        // Add the item being testing.
        LabeledIsotopeNode isotopeNode = new LabeledIsotopeNode( new ModelViewTransform2D(),
                new MobileAtom(1, 0, 20, new Point2D.Double( 0, 0 )), Color.RED );
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
