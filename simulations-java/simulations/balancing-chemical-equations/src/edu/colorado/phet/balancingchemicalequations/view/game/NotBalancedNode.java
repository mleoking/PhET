// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.control.BalancedRepresentationChoiceNode.BalancedRepresentation;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.MakeAmmoniaEquation;
import edu.colorado.phet.balancingchemicalequations.view.BalanceScalesNode;
import edu.colorado.phet.balancingchemicalequations.view.BarChartsNode;
import edu.colorado.phet.balancingchemicalequations.view.HorizontalAligner;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that equation is not balanced, by any definition of balanced.
 * Frowny face, big "X" and text.
 * Optionally displays a bar chart or balance scale.
 */
public class NotBalancedNode extends GameResultNode {

    public NotBalancedNode( final Equation equation, final boolean showChartsAndScalesInGame, final BalancedRepresentation balancedRepresentation, final HorizontalAligner aligner ) {
        super( false /* smile */, new Function1<PhetFont, PNode>() {
            public PNode apply( PhetFont font ) {
                PNode parentNode = new PNode();

                PNode iconAndTextNode = new PNode();
                parentNode.addChild( iconAndTextNode );

                PImage iconNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
                iconAndTextNode.addChild( iconNode );

                PText textNode = new PText( BCEStrings.NOT_BALANCED );
                textNode.setFont( font );
                iconAndTextNode.addChild( textNode );

                PNode balanceRepresentationNode = null;
                if ( showChartsAndScalesInGame ) {
                    if ( balancedRepresentation != null ) {
                        if ( balancedRepresentation == BalancedRepresentation.BALANCE_SCALES ) {
                            balanceRepresentationNode = new BalanceScalesNode( new Property<Equation>( equation ), aligner );
                        }
                        else if ( balancedRepresentation == BalancedRepresentation.BAR_CHARTS ) {
                            balanceRepresentationNode = new BarChartsNode( new Property<Equation>( equation ), aligner );
                        }
                    }
                }

                // layout: icon to left of text, optional bar charts or balance scales center underneath
                iconNode.setOffset( 0, 0 );
                double x = iconNode.getFullBoundsReference().getMaxX() + 2;
                double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                textNode.setOffset( x, y );
                if ( balanceRepresentationNode != null ) {
                    x = iconAndTextNode.getFullBoundsReference().getCenterX() - ( balanceRepresentationNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( balanceRepresentationNode );
                    y = parentNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( balanceRepresentationNode ) + 25;
                    balanceRepresentationNode.setOffset( x, y );
                    parentNode.addChild( balanceRepresentationNode );
                }

                return parentNode;
            }
        } );
    }

    public static void main( String[] args ) {
        PhetPCanvas canvas = new PhetPCanvas( BCEConstants.CANVAS_RENDERING_SIZE );
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        Equation equation = new MakeAmmoniaEquation();
        EquationTerm[] reactants = equation.getReactants();
        for ( EquationTerm term : reactants ) {
            term.setActualCoefficient( 15 );
        }
        EquationTerm[] products = equation.getProducts();
        for ( EquationTerm term : products ) {
            term.setActualCoefficient( 15 );
        }

        NotBalancedNode node = new NotBalancedNode( equation, true, BalancedRepresentation.BAR_CHARTS, new HorizontalAligner( new Dimension( 475, 400 ), 90 ) );
        node.setOffset( 200, 200 );
        canvas.addWorldChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible( true );
    }
}