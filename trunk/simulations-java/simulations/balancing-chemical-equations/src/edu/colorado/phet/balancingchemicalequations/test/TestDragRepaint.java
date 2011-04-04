// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_2H2_O2_2H2O;
import edu.colorado.phet.balancingchemicalequations.view.EquationNode;
import edu.colorado.phet.balancingchemicalequations.view.HorizontalAligner;
import edu.colorado.phet.balancingchemicalequations.view.game.GamePopupNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Dragging the Game popups over the Equation causes the equation's read-only coefficients (PText) to
 * disappear and reappear.  Dragging right causes them to disappear, dragging left causes them to reappear.
 * This occurs on both Mac and Windows, first observed in version 0.00.20.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDragRepaint extends JFrame {

    private static class TestEquationNode extends PhetPNode {
        public TestEquationNode() {

            PText c1 = new PText( "1" );
            PText c2 = new PText( "2" );
            PText c3 = new PText( "3" );
            HTMLNode s1 = new HTMLNode( "H<sub>2</sub>O" );
            HTMLNode s2 = new HTMLNode( "H<sub>2</sub>O" );
            HTMLNode s3 = new HTMLNode( "H<sub>2</sub>O" );
            PText plusNode = new PText( "+" );
            PText equalsNode = new PText( "=" );

            addChild( c1 );
            addChild( c2 );
            addChild( c3 );
            addChild( s1 );
            addChild( s2 );
            addChild( s3 );
            addChild( plusNode );
            addChild( equalsNode );

            // font
            PhetFont font = new PhetFont( 42 );
            for ( PNode node : getChildren() ) {
                if ( node instanceof PText ) {
                    ( (PText) node ).setFont( font );
                }
                else if ( node instanceof HTMLNode ) {
                    ( (HTMLNode) node ).setFont( font );
                }
            }

            // layout
            final double coefficientSpacing = 8;
            final double termSpacing = 15;
            c1.setOffset( 0, 0 );
            s1.setOffset( c1.getFullBounds().getMaxX() + coefficientSpacing, 0 );
            plusNode.setOffset( s1.getFullBounds().getMaxX() + termSpacing, 0 );
            c2.setOffset( plusNode.getFullBounds().getMaxX() + termSpacing, 0 );
            s2.setOffset( c2.getFullBounds().getMaxX() + coefficientSpacing, 0 );
            equalsNode.setOffset( s2.getFullBounds().getMaxX() + termSpacing, 0 );
            c3.setOffset( equalsNode.getFullBounds().getMaxX() + termSpacing, 0 );
            s3.setOffset( c3.getFullBounds().getMaxX() + coefficientSpacing, 0 );
        }
    }

    public static class TestPopupNode extends GamePopupNode {
        public TestPopupNode() {
            super( true, false, false, new Function1<PhetFont, PNode>() {
                public PNode apply( PhetFont font ) {
                    return new PNode();
                }
            } );
        }
    }

    public TestDragRepaint() {
        super( "TestDragRepaint" );

        Dimension canvasSize = new Dimension( 1024, 768 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        canvas.setPreferredSize( canvasSize );

        PNode rootNode = new PNode();
        canvas.addWorldChild( rootNode );

//        TestEquationNode testEquationNode = new TestEquationNode();
//        rootNode.addChild( testEquationNode );
//        testEquationNode.setOffset( 300, 300 );

        Property<Equation> equationProperty = new Property<Equation>( new Synthesis_2H2_O2_2H2O() );
        IntegerRange coefficientRange = new IntegerRange( 0, 7 );
        HorizontalAligner aligner = new HorizontalAligner( new Dimension( 475, 220 ), 90 );
        EquationNode equationNode = new EquationNode( equationProperty, coefficientRange, aligner );
        equationNode.setEditable( false );
        rootNode.addChild( equationNode );
        equationNode.setOffset( 300, 300 );

        TestPopupNode popupNode = new TestPopupNode();
        rootNode.addChild( popupNode );
        popupNode.setOffset( 400, 400 );

        setContentPane( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDragRepaint();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
