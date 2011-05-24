// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
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
     *
     * @param equation
     * @param globalProperties
     * @param whyButtonListener
     * @param aligner
     */
    public NotBalancedTerseNode( BCEGlobalProperties globalProperties, final ActionListener whyButtonListener ) {
        this( globalProperties.popupsCloseButtonVisible.get(), globalProperties.popupsTitleBarVisible.get(),
              globalProperties.popupsWhyButtonVisible.get(), whyButtonListener );
    }

    /*
     * @param closeButtonVisible
     * @param titleBarVisible
     * @param whyButtonVisible
     */
    private NotBalancedTerseNode( boolean closeButtonVisible, boolean titleBarVisible, final boolean whyButtonVisible, final ActionListener whyButtonListener ) {
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
                HTMLImageButtonNode showWhyButton = null;
                if ( whyButtonVisible ) {
                    showWhyButton = new HTMLImageButtonNode( BCEStrings.SHOW_WHY, new PhetFont( Font.BOLD, 18 ), Color.WHITE );
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

        ActionListener whyButtonListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Show Why button pressed" );
            }
        };
        NotBalancedTerseNode node = new NotBalancedTerseNode( false, false, true, whyButtonListener );
        node.setOffset( 20, 200 );
        canvas.addWorldChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}