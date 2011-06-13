// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that contains one button to remove salt (if there is any salt) and another button for sugar.
 *
 * @author Sam Reid
 */
public class RemoveSoluteControlNode extends PNode {
    public RemoveSoluteControlNode( final ISugarAndSaltModel model ) {

        //Button to remove salt, only shown if there is any salt
        TextButtonNode saltButton = new TextButtonNode( "Remove salt", SugarAndSaltSolutionsCanvas.CONTROL_FONT ) {{
            setBackground( SugarAndSaltSolutionsCanvas.BUTTON_COLOR );
            model.getSaltMoles().greaterThan( 0 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeSalt();
                }
            } );
        }};
        addChild( saltButton );

        //Button to remove sugar, only shown if there is any sugar
        TextButtonNode sugarButton = new TextButtonNode( "Remove sugar", SugarAndSaltSolutionsCanvas.CONTROL_FONT ) {{
            setBackground( SugarAndSaltSolutionsCanvas.BUTTON_COLOR );
            model.getSugarMoles().greaterThan( 0 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeSugar();
                }
            } );
        }};
        addChild( sugarButton );

        //Put the buttons next to each other, leaving the origin at (0,0) so it can be positioned easily by the client
        sugarButton.setOffset( saltButton.getFullBounds().getMaxX() + SugarAndSaltSolutionsCanvas.INSET, 0 );
    }
}
