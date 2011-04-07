// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.test;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H5Cl;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H5OH;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H6;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH3OH;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CS2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.Cl2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.F2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2S;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HCl;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HF;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.OF2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.P4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PCl3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PCl5;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PF3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SO3;
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
public class TestMoleculeNodes extends JFrame {

    private static final Color CANVAS_COLOR = BCEConstants.CANVAS_BACKGROUND;
    private static final Color TEXT_COLOR = Color.BLACK;

    public TestMoleculeNodes() {
        super( TestMoleculeNodes.class.getName() );

        PhetPCanvas canvas = new PhetPCanvas( BCEConstants.CANVAS_RENDERING_SIZE );
        canvas.setBackground( CANVAS_COLOR );
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        // parent node of all molecule nodes
        PNode parent = new PNode();
        canvas.addWorldChild( parent );

        parent.addChild( new LabeledMoleculeNode( new CMolecule() ) );
        parent.addChild( new LabeledMoleculeNode( new C2H2() ) );
        parent.addChild( new LabeledMoleculeNode( new C2H4() ) );
        parent.addChild( new LabeledMoleculeNode( new C2H5Cl() ) );
        parent.addChild( new LabeledMoleculeNode( new C2H5OH() ) );
        parent.addChild( new LabeledMoleculeNode( new C2H6() ) );
        parent.addChild( new LabeledMoleculeNode( new CH2O() ) );
        parent.addChild( new LabeledMoleculeNode( new CH3OH() ) );
        parent.addChild( new LabeledMoleculeNode( new CH4() ) );
        parent.addChild( new LabeledMoleculeNode( new Cl2() ) );
        parent.addChild( new LabeledMoleculeNode( new CO() ) );
        parent.addChild( new LabeledMoleculeNode( new CO2() ) );
        parent.addChild( new LabeledMoleculeNode( new CS2() ) );
        parent.addChild( new LabeledMoleculeNode( new F2() ) );
        parent.addChild( new LabeledMoleculeNode( new H2() ) );
        parent.addChild( new LabeledMoleculeNode( new H2O() ) );
        parent.addChild( new LabeledMoleculeNode( new H2S() ) );
        parent.addChild( new LabeledMoleculeNode( new HCl() ) );
        parent.addChild( new LabeledMoleculeNode( new HF() ) );
        parent.addChild( new LabeledMoleculeNode( new N2() ) );
        parent.addChild( new LabeledMoleculeNode( new N2O() ) );
        parent.addChild( new LabeledMoleculeNode( new NH3() ) );
        parent.addChild( new LabeledMoleculeNode( new NO() ) );
        parent.addChild( new LabeledMoleculeNode( new NO2() ) );
        parent.addChild( new LabeledMoleculeNode( new O2() ) );
        parent.addChild( new LabeledMoleculeNode( new OF2() ) );
        parent.addChild( new LabeledMoleculeNode( new P4() ) );
        parent.addChild( new LabeledMoleculeNode( new PCl3() ) );
        parent.addChild( new LabeledMoleculeNode( new PCl5() ) );
        parent.addChild( new LabeledMoleculeNode( new PF3() ) );
        parent.addChild( new LabeledMoleculeNode( new PH3() ) );
        parent.addChild( new LabeledMoleculeNode( new SMolecule() ) );
        parent.addChild( new LabeledMoleculeNode( new SO2() ) );
        parent.addChild( new LabeledMoleculeNode( new SO3() ) );

        // control for changing canvas color
        PSwing pswing = new PSwing( new CanvasColorControl( this, canvas ) );
        canvas.addWorldChild( pswing );

        // layout
        final int columns = 8;
        final int xSpacing = 100;
        final int ySpacing = 100;
        final int margin = 50;
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            double x = margin + ( ( i % columns ) ) * xSpacing;
            double y = margin + ( ( i / columns ) ) * ySpacing;
            child.setOffset( x, y );
        }
        pswing.setOffset( margin, parent.getFullBoundsReference().getMaxY() + ySpacing );

        setContentPane( canvas );
        pack();
    }

    private static class LabeledMoleculeNode extends PComposite {
        public LabeledMoleculeNode( Molecule molecule ) {
            PNode moleculeNode = new PImage( molecule.getImage() );
            addChild( moleculeNode );
            HTMLNode labelNode = new HTMLNode( molecule.getSymbol() );
            labelNode.setHTMLColor( TEXT_COLOR );
            addChild( labelNode );
            double x = moleculeNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
            double y = moleculeNode.getFullBoundsReference().getMaxY() + 2;
            labelNode.setOffset( x, y );
        }
    }

    private static class CanvasColorControl extends JPanel {
        public CanvasColorControl( JFrame parentFrame, final PCanvas canvas ) {
            setBorder( new CompoundBorder( new LineBorder( Color.WHITE), new LineBorder( Color.BLACK ) ) );
            final ColorControl colorControl = new ColorControl( parentFrame, "play area color:", canvas.getBackground() );
            add( colorControl );
            SwingUtils.setBackgroundDeep( this, Color.WHITE );
            colorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    canvas.setBackground( colorControl.getColor() );
                }
            } );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestMoleculeNodes();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
