// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_2O2_CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.balancingchemicalequations.view.HorizontalAligner;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that an equation is not balanced, by any definition of balanced.
 * This looks like a dialog, and contains:
 * <ul>
 * <li>a frowny face
 * <li>big "X" for "not balanced"
 * <li>"Show Why" button for showing an additional representation
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NotBalancedTerseNode extends GamePopupNode {

    /**
     * Convenience constructor.
     * @param equation
     * @param globalProperties
     * @param whyButtonListener
     * @param aligner
     */
    public NotBalancedTerseNode( final Equation equation, BCEGlobalProperties globalProperties, final ActionListener whyButtonListener, final HorizontalAligner aligner ) {
        this( equation, globalProperties.popupsCloseButtonVisible.getValue(), globalProperties.popupsTitleBarVisible.getValue(),
                globalProperties.popupsWhyButtonVisible.getValue(), whyButtonListener, aligner );
    }

    /*
     * @param equation the equation
     * @param closeButtonVisible
     * @param titleBarVisible
     * @param whyButtonVisible
     * @param aligner specifies horizontal layout, for aligning with other user-interface components
     */
    private NotBalancedTerseNode( final Equation equation, boolean closeButtonVisible, boolean titleBarVisible, final boolean whyButtonVisible,//REVIEW: why is argument equation unused?
            final ActionListener whyButtonListener, final HorizontalAligner aligner ) {//REVIEW: why is argument aligner unused?
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

                // "Show Why" button
                ButtonNode showWhyButton = null;
                if ( whyButtonVisible ) {
                    showWhyButton = new ButtonNode( BCEStrings.SHOW_WHY, 18, Color.WHITE );
                    showWhyButton.addActionListener( whyButtonListener );
                }

                // layout
                {
                    // icon to left of text
                    iconNode.setOffset( 0, 0 );
                    double x = iconNode.getFullBoundsReference().getMaxX() + 2;
                    double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                    textNode.setOffset( x, y );
                    // button centered under icon and text
                    if ( showWhyButton != null ) {
                        x = iconAndTextNode.getFullBoundsReference().getCenterX() - ( showWhyButton.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( showWhyButton );
                        y = parentNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( showWhyButton ) + 25;
                        showWhyButton.setOffset( x, y );
                        parentNode.addChild( showWhyButton );
                    }
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

        ActionListener whyButtonListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Show Why button pressed" );
            }
        };
        NotBalancedTerseNode node = new NotBalancedTerseNode( equation, false, false, true, whyButtonListener, new HorizontalAligner( new Dimension( 475, 400 ), 90 ) );
        node.setOffset( 20, 200 );
        canvas.addWorldChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible( true );
    }
}