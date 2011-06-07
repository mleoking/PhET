// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;

/**
 * Button that shows "Remove salt/sugar" as a single button, even if there is no sugar in the sim.  Shown only if there is anything to be removed.
 * Kelly decided on May 24 to delete this variant since the separate buttons are better, but I'm keeping this around in case she is overriden or we change our minds
 *
 * @author Sam Reid
 */
public class RemoveSaltSugarButton extends HTMLImageButtonNode {
    public RemoveSaltSugarButton( final MacroModel model ) {
        super( "Remove salt/sugar", SugarAndSaltSolutionsCanvas.BUTTON_COLOR );

        //Remove all solutes from the model when pressed
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.removeSaltAndSugar();
            }
        } );

        //Only show if there is anything to be removed
        model.anySolutes.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}
