// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.developer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.model.Molecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.*;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays the visual representation of all molecules used in the "Balancing Chemical Equations" sim.
 * This was used for development of the Piccolo code that draws the molecules,
 * and for early colorblindness testing of the atom colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PreviewMoleculesCanvas extends PhetPCanvas {

    /**
     * Constructor that displays the nodes used in "Balancing Chemical Equations".
     *
     * @param parentFrame
     */
    public PreviewMoleculesCanvas( Frame parentFrame ) {
        this( parentFrame, BCEConstants.CANVAS_BACKGROUND, 8, 100, 100, 20,
              new Molecule[] {
                      new CMolecule(),
                      new C2H2(),
                      new C2H4(),
                      new C2H5Cl(),
                      new C2H5OH(),
                      new C2H6(),
                      new CH2O(),
                      new CH3OH(),
                      new CH4(),
                      new Cl2(),
                      new CO(),
                      new CO2(),
                      new CS2(),
                      new F2(),
                      new H2(),
                      new H2O(),
                      new H2S(),
                      new HCl(),
                      new HF(),
                      new N2(),
                      new N2O(),
                      new NH3(),
                      new NO(),
                      new NO2(),
                      new O2(),
                      new OF2(),
                      new P4(),
                      new PCl3(),
                      new PCl5(),
                      new PF3(),
                      new PH3(),
                      new SMolecule(),
                      new SO2(),
                      new SO3()
              } );
    }

    /**
     * Constructor that displays any set of molecules.
     * Molecules are displayed in an grid, in row-major order.
     * Canvas is automatically sized so that all nodes are visible.
     *
     * @param parentFrame color chooser dialog will be parented to this frame
     * @param background  background color of the canvas
     * @param columns     number of columns in the grid
     * @param xSpacing    horizontal spacing between molecules
     * @param ySpacing    vertical spacing between molecules
     * @param margin      margin around the edge of the play area
     * @param molecules   molecules to display
     */
    public PreviewMoleculesCanvas( Frame parentFrame, Color background, int columns, int xSpacing, int ySpacing, int margin, Molecule[] molecules ) {
        super();
        setBackground( background );

        // parent node of all molecule nodes
        PNode parent = new PNode();
        addWorldChild( parent );

        // molecule nodes
        for ( Molecule molecule : molecules ) {
            parent.addChild( new LabeledMoleculeNode( molecule ) );
        }

        // control for changing canvas color
        PSwing colorControl = new PSwing( new CanvasColorControl( parentFrame, this ) );
        addWorldChild( colorControl );

        // layout
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            double x = margin + ( ( i % columns ) ) * xSpacing;
            double y = margin + ( ( i / columns ) ) * ySpacing;
            child.setOffset( x, y );
        }
        colorControl.setOffset( margin, parent.getFullBoundsReference().getMaxY() + 10 );

        // compute preferred size
        int width = (int) parent.getFullBoundsReference().getMaxX() + margin;
        int height = (int) colorControl.getFullBoundsReference().getMaxY() + margin;
        setPreferredSize( new Dimension( width, height ) );
    }

    /*
     * Displays a molecule as an image, with a label centered below it.
     */
    private static class LabeledMoleculeNode extends PComposite {
        public LabeledMoleculeNode( Molecule molecule ) {

            // image
            PNode moleculeNode = new PImage( molecule.getImage() );
            addChild( moleculeNode );

            // label
            HTMLNode labelNode = new HTMLNode( molecule.getSymbol() );
            labelNode.setHTMLColor( Color.BLACK );
            addChild( labelNode );

            // layout: label centered below image
            double x = moleculeNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
            double y = moleculeNode.getFullBoundsReference().getMaxY() + 2;
            labelNode.setOffset( x, y );
        }
    }

    /*
     * Control for changing the color of the canvas background.
     * Clicking on the color chip opens a color chooser dialog.
     */
    private static class CanvasColorControl extends JPanel {
        public CanvasColorControl( Frame parentFrame, final PCanvas canvas ) {
            setBorder( new CompoundBorder( new LineBorder( Color.WHITE ), new LineBorder( Color.BLACK ) ) );
            final ColorControl colorControl = new ColorControl( parentFrame, "background color:", canvas.getBackground() );
            add( colorControl );
            SwingUtils.setBackgroundDeep( this, Color.WHITE );
            colorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    canvas.setBackground( colorControl.getColor() );
                }
            } );
        }
    }

    // test the canvas
    public static void main( String[] args ) {
        JFrame frame = new JFrame( PreviewMoleculesCanvas.class.getName() ) {{
            setContentPane( new PreviewMoleculesCanvas( this ) );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
