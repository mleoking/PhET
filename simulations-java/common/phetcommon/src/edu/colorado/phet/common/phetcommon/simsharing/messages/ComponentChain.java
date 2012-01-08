// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Use this class when a single componentID is ambiguous, such as when there are multiple file->save buttons in the sim (for saving different things)
 * This makes the text appear in the form: simSharingLog.fileSaveButton
 */
public class ComponentChain implements UserComponent {
    private final UserComponent[] components;

    public ComponentChain( UserComponent... components ) {
        this.components = components;
    }

    @Override public String toString() {
        return new ObservableList<UserComponent>( components ).mkString( "." );
    }

    //Provide an index name for a component, such as "the 3rd track" would be track.3
    public static ComponentChain chain( UserComponent component, int index ) {
        return chain( component, new IntegerUserComponent( index ) );
    }

    public static ComponentChain chain( UserComponent... components ) {
        return new ComponentChain( components );
    }
}
