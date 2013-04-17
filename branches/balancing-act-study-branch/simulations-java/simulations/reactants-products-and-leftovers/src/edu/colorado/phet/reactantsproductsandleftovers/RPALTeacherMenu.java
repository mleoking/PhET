// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;

/**
 * "Teacher" menu for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALTeacherMenu extends TeacherMenu {

    public RPALTeacherMenu() {

        Property<Boolean> whiteBackground = new Property<Boolean>( false );
        whiteBackground.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean whiteBackground ) {
                if ( whiteBackground ) {
                    RPALColors.COLOR_SCHEME.set( RPALColors.WORKSHEET_COLOR_SCHEME );
                }
                else {
                    RPALColors.COLOR_SCHEME.set( RPALColors.NORMAL_COLOR_SCHEME );
                }
            }
        } );

        addWhiteBackgroundMenuItem( whiteBackground );
    }
}
