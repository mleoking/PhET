// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that contains one button to remove salt (if there is any salt) and another button for sugar.
 *
 * @author Sam Reid
 */
public class SeparateRemoveSaltSugarButtons extends PNode {
    public SeparateRemoveSaltSugarButtons( final IntroModel model ) {

        //Button to remove salt, only shown if there is any salt
        TextButtonNode saltButton = new TextButtonNode( "Remove salt" ) {{
            setBackground( Color.yellow );
            model.salt.moles.greaterThan( 0 ).addObserver( new VoidFunction1<Boolean>() {
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
        TextButtonNode sugarButton = new TextButtonNode( "Remove sugar" ) {{
            setBackground( Color.yellow );
            model.sugar.moles.greaterThan( 0 ).addObserver( new VoidFunction1<Boolean>() {
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
        saltButton.setOffset( sugarButton.getFullBounds().getMaxX() + SugarAndSaltSolutionsCanvas.INSET, 0 );
    }
}
