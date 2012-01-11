// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Use this class when a single componentID is ambiguous, such as when there are multiple file->save buttons in the sim (for saving different things)
 * This makes the text appear in the form: simSharingLog.fileSaveButton
 */
public class ComponentChain implements IUserComponent {
    private final IUserComponent[] components;

    public ComponentChain( IUserComponent... components ) {
        this.components = components;
    }

    @Override public String toString() {
        return new ObservableList<IUserComponent>( components ).mkString( "." );
    }

    //Provide an index name for a component, such as "the 3rd track" would be track.3
    public static ComponentChain chain( IUserComponent component, int index ) {
        return chain( component, new UserComponentId( index ) );
    }

    public static ComponentChain chain( IUserComponent... components ) {
        return new ComponentChain( components );
    }

    public static IUserComponent chain( IUserComponent userComponent, String name ) {
        return new ComponentChain( userComponent, new UserComponentId( name ) );
    }
}