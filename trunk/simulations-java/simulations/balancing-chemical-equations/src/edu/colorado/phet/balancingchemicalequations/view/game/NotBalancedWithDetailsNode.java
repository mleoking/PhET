// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_2O2_CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.balancingchemicalequations.view.BalanceScalesNode;
import edu.colorado.phet.balancingchemicalequations.view.BalancedRepresentation;
import edu.colorado.phet.balancingchemicalequations.view.BarChartsNode;
import edu.colorado.phet.balancingchemicalequations.view.HorizontalAligner;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that an equation is not balanced, by any definition of balanced.
 * This looks like a dialog, and contains a frowny face, big "X" for "not balanced".
 * Optionally displays a bar chart or balance scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NotBalancedWithDetailsNode extends GamePopupNode {

    /**
     * Constructor.
     *
     * @param equation the equation
     * @param closeButtonVisible
     * @param titleBarVisible
     * @param whyButtonVisible
     * @param balancedRepresentation which representation of "balanced" should we show?
     * @param aligner specifies horizontal layout, for aligning with other user-interface components
     */
    public NotBalancedWithDetailsNode( final Equation equation, boolean closeButtonVisible, boolean titleBarVisible, boolean whyButtonVisible,
            final BalancedRepresentation balancedRepresentation, final HorizontalAligner aligner ) {
        super( false /* smile */, closeButtonVisible, titleBarVisible, new Function1<PhetFont, PNode>() {
            public PNode apply( PhetFont font ) {
                PNode parentNode = new PNode();

                // icon and text
                PNode iconAndTextNode = new PNode();
                parentNode.addChild( iconAndTextNode );
                PImage iconNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
                iconAndTextNode.addChild( iconNode );
                PText textNode = new PText( BCEStrings.NOT_BALANCED );
                textNode.setFont( font );
                iconAndTextNode.addChild( textNode );

                // representation of "balanced"
                PNode balanceRepresentationNode = null;
                if ( balancedRepresentation == BalancedRepresentation.BALANCE_SCALES ) {
                    balanceRepresentationNode = new BalanceScalesNode( new Property<Equation>( equation ), aligner );
                }
                else if ( balancedRepresentation == BalancedRepresentation.BAR_CHARTS ) {
                    balanceRepresentationNode = new BarChartsNode( new Property<Equation>( equation ), aligner );
                }
                else {
                    // BalancedRepresentation.NONE, show nothing
                }

                // layout: icon to left of text, optional bar charts or balance scales centered underneath
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

    // test
    public static void main( String[] args ) {
        PhetPCanvas canvas = new PhetPCanvas( BCEConstants.CANVAS_RENDERING_SIZE );
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        Equation equation = new Displacement_CH4_2O2_CO2_2H2O();
        EquationTerm[] reactants = equation.getReactants();
        for ( EquationTerm term : reactants ) {
            term.setUserCoefficient( 15 );
        }
        EquationTerm[] products = equation.getProducts();
        for ( EquationTerm term : products ) {
            term.setUserCoefficient( 15 );
        }

        NotBalancedWithDetailsNode node = new NotBalancedWithDetailsNode( equation, false, false, true, BalancedRepresentation.BAR_CHARTS, new HorizontalAligner( new Dimension( 475, 400 ), 90 ) );
        node.setOffset( 20, 200 );
        canvas.addWorldChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible( true );
    }
}