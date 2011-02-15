// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH2O;
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
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


public class TestMoleculeNodes extends JFrame {

    public TestMoleculeNodes() {
        super( TestMoleculeNodes.class.getName() );

        PhetPCanvas canvas = new PhetPCanvas( BCEConstants.CANVAS_RENDERING_SIZE );
        canvas.setBackground( BCEConstants.CANVAS_BACKGROUND );
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        rootNode.addChild( new LabeledMoleculeNode( new CMolecule() ) );
        rootNode.addChild( new LabeledMoleculeNode( new C2H2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new C2H4() ) );
        rootNode.addChild( new LabeledMoleculeNode( new CH2O() ) );
        rootNode.addChild( new LabeledMoleculeNode( new CH4() ) );
        rootNode.addChild( new LabeledMoleculeNode( new Cl2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new CO() ) );
        rootNode.addChild( new LabeledMoleculeNode( new CO2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new CS2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new F2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new H2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new H2O() ) );
        rootNode.addChild( new LabeledMoleculeNode( new H2S() ) );
        rootNode.addChild( new LabeledMoleculeNode( new HCl() ) );
        rootNode.addChild( new LabeledMoleculeNode( new HF() ) );
        rootNode.addChild( new LabeledMoleculeNode( new N2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new N2O() ) );
        rootNode.addChild( new LabeledMoleculeNode( new NH3() ) );
        rootNode.addChild( new LabeledMoleculeNode( new NO() ) );
        rootNode.addChild( new LabeledMoleculeNode( new NO2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new O2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new OF2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new P4() ) );
        rootNode.addChild( new LabeledMoleculeNode( new PCl3() ) );
        rootNode.addChild( new LabeledMoleculeNode( new PCl5() ) );
        rootNode.addChild( new LabeledMoleculeNode( new PF3() ) );
        rootNode.addChild( new LabeledMoleculeNode( new PH3() ) );
        rootNode.addChild( new LabeledMoleculeNode( new SMolecule() ) );
        rootNode.addChild( new LabeledMoleculeNode( new SO2() ) );
        rootNode.addChild( new LabeledMoleculeNode( new SO3() ) );

        // layout
        final int columns = 8;
        final int xSpacing = 100;
        final int ySpacing = 100;
        for ( int i = 0; i < rootNode.getChildrenCount(); i++ ) {
            PNode child = rootNode.getChild( i );
            double x = ( ( i % columns ) + 1 ) * xSpacing;
            double y = ( ( i / columns ) + 1 ) * ySpacing;
            child.setOffset( x, y );
        }

        setContentPane( canvas );
        pack();
    }

    private static class LabeledMoleculeNode extends PComposite {
        public LabeledMoleculeNode( Molecule molecule ) {
            PNode moleculeNode = new PImage( molecule.getImage() );
            addChild( moleculeNode );
            PNode labelNode = new HTMLNode( molecule.getSymbol() );
            addChild( labelNode );
            double x = moleculeNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
            double y = moleculeNode.getFullBoundsReference().getMaxY() + 2;
            labelNode.setOffset( x, y );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestMoleculeNodes();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
