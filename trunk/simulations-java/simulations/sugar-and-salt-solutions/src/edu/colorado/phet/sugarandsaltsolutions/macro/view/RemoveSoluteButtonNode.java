// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.BUTTON_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * Button that allows the user to remove a particular type of solute, only shown if the sim contains the solute.
 *
 * @author Sam Reid
 */
public class RemoveSoluteButtonNode extends TextButtonNode {
    public RemoveSoluteButtonNode( String text, ObservableProperty<Boolean> visible, final VoidFunction0 remove ) {
        super( text, CONTROL_FONT );
        setBackground( BUTTON_COLOR );

        //Only show the button if there is solute to be removed
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        //When the user presses the button ,clear the solute
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                remove.apply();
            }
        } );
    }
}
