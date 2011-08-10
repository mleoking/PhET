// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;


/**
 * PNode that contains the back and forward buttons and, optionally, the title node
 *
 * @author Sam Reid
 */
public class KitControlNode extends PNode {
    /**
     * Creates a node that contains back and forward buttons for use in a KitSelectionNode.
     *
     * @param numKits     the number of kits that are available, for purposes of hiding/showing the "next" button
     * @param selectedKit model for which kit is selected, this control sets and observes the value
     * @param titleNode   optional title to be displayed between the buttons
     * @param inset       space between the arrows and the title (or 2x inset = distance between arrows if no title)
     */
    public KitControlNode( final int numKits, final Property<Integer> selectedKit, Option<PNode> titleNode, double inset ) {

        //Buttons for scrolling previous/next
        //Place the kit "previous" and "next" buttons above the kit to save horizontal space.  In Sugar and Salt Solution, they are moved up next to the title
        final PNode nextButton = new ForwardButton() {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selectedKit.set( selectedKit.get() + 1 );
                }
            } );
            selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setVisible( selectedKit.get() < numKits - 1 );
                }
            } );
        }};
        addChild( nextButton );

        BackButton backButton = new BackButton() {{

            //Make sure the previous and next buttons don't overlap, useful to handle long i18n strings
            if ( getFullBounds().getMaxX() > nextButton.getFullBounds().getMinX() ) {
                setOffset( nextButton.getFullBounds().getMinX() - 2, getOffset().getY() );
            }
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selectedKit.set( selectedKit.get() - 1 );
                }
            } );
            selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setVisible( selectedKit.get() > 0 );
                }
            } );
        }};
        addChild( backButton );
        if ( titleNode.isSome() ) {
            PNode title = titleNode.get();
            addChild( title );
            title.setOffset( backButton.getFullBounds().getMaxX() + inset, backButton.getFullBounds().getCenterY() - title.getFullBounds().getHeight() / 2 );
            nextButton.setOffset( title.getFullBounds().getMaxX() + inset, 0 );
        }
        else {
            nextButton.setOffset( backButton.getFullBounds().getMaxX() + inset * 2, 0 );
        }
    }
}