// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.simsharing;

import java.util.List;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo combo box with data collection support.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingComboBoxNode<T> extends ComboBoxNode<T> {

    private final IUserComponent userComponent;
    private final Function1<T, String> itemToString; // function for converting a combo box item to a String, so that it can appear as a message parameter

    public SimSharingComboBoxNode( IUserComponent userComponent, final Function1<T, String> itemToString, T... items ) {
        super( items );
        this.userComponent = userComponent;
        this.itemToString = itemToString;
    }

    public SimSharingComboBoxNode( IUserComponent userComponent, final Function1<T, String> itemToString, List<T> items ) {
        super( items );
        this.userComponent = userComponent;
        this.itemToString = itemToString;
    }

    public SimSharingComboBoxNode( IUserComponent userComponent, final Function1<T, String> itemToString, final List<T> items, T initialItem, final Function1<T, PNode> nodeGenerator ) {
        super( items, initialItem, nodeGenerator );
        this.userComponent = userComponent;
        this.itemToString = itemToString;
    }

    @Override protected void itemSelected( T item ) {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.comboBox, UserActions.selected,
                                           Parameter.param( ParameterKeys.item, itemToString.apply( item ) ) );
    }
}
