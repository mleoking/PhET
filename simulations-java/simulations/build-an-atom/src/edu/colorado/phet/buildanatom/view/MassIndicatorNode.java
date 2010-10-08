package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows a scale with a numeric readout of the atom weight.  Origin is the top left of the scale body (not the platform).
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MassIndicatorNode extends PNode {
    private static final double WIDTH = 90; // The image will define the aspect ratio and therefore the height.

    public MassIndicatorNode( final Atom atom ) {

        final PImage weighScaleImageNode = new PImage( BuildAnAtomResources.getImage( "atom_builder_scale.png" ) );
        weighScaleImageNode.setScale( WIDTH / weighScaleImageNode.getFullBoundsReference().width );
        addChild ( weighScaleImageNode );

        final PText readoutPText = new PText() {{
            setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );
            setTextPaint( Color.red );
        }};
        addChild( readoutPText );

        SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                readoutPText.setText( atom.getAtomicMassNumber() + "" );
                readoutPText.setOffset( weighScaleImageNode.getFullBounds().getCenterX() - readoutPText.getFullBounds().getWidth() / 2, weighScaleImageNode.getFullBounds().getMaxY() - readoutPText.getFullBounds().getHeight() );
            }
        };
        atom.addObserver( updateText );
        updateText.update();

        //from 9/30/2010 meeting
        //will students think the atom on the scale is an electron?
        //use small icon of orbits/cloud instead of cloud

        //TODO: copied from BuildAnAtomCanvas, should be factored out into something like ElectronShellNode
        final PNode atomNode = new PNode();
        //Make it small enough so it looks to scale, but also so we don't have to indicate atomic substructure
        Stroke stroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.5f, 1.5f }, 0 );
        double scale = 1.0/5;
        ModelViewTransform2D mvt = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 1, 1 ), new Rectangle2D.Double( 0, 0, scale,scale), false );
        for ( ElectronShell electronShell : atom.getElectronShells() ) {
            double shellRadius = electronShell.getRadius();
            Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                    -shellRadius,
                    -shellRadius,
                    shellRadius * 2,
                    shellRadius * 2 ) );
            PNode electronShellNode = new PhetPPath( electronShellShape, stroke, Color.BLUE );
            atomNode.addChild( electronShellNode );
        }
        double nucleusWidth=1;
        atomNode.addChild( new PhetPPath( new Ellipse2D.Double( -nucleusWidth / 2, -nucleusWidth / 2, nucleusWidth, nucleusWidth ), Color.red ) {{
            setOffset( atomNode.getFullBounds().getCenter2D() );
            final SimpleObserver updateNucleusNode = new SimpleObserver() {
                public void update() {
                    setVisible( atom.getNumProtons() + atom.getNumNeutrons() > 0 );
                    if ( atom.getNumProtons() > 0 ) {
                        setPaint( Color.red );//if any protons, it should look red
                    }
                    else {
                        setPaint( Color.gray );
                    } //if no protons, but some neutrons, should look neutron colored
                }
            };
            atom.addObserver( updateNucleusNode );
            updateNucleusNode.update();
        }} );
        addChild( atomNode );

        // Position the atom and the scale such that the (0,0) position is the
        // upper left corner of the whole assembly.
        atomNode.setOffset( weighScaleImageNode.getFullBoundsReference().getCenterX(),
                atomNode.getFullBoundsReference().height / 2);
        // There is a tweak factor here to set the vertical relationship between
        // the atom and scale.
        weighScaleImageNode.setOffset( 0, atomNode.getFullBoundsReference().height * 0.75 );
    }
}
