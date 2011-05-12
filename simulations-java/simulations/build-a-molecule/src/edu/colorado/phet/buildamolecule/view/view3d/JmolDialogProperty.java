//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view.view3d;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;

/**
 * Represents an optional dialog, with a convenience method for hiding the dialog if it is shown
 */
public class JmolDialogProperty extends Property<Option<JmolDialog>> {
    public JmolDialogProperty() {
        super( new None<JmolDialog>() );
    }

    public void hideDialogIfShown() {
        if ( get().isSome() ) {
            get().get().dispose();
            set( new Option.None<JmolDialog>() );
        }
    }
}
