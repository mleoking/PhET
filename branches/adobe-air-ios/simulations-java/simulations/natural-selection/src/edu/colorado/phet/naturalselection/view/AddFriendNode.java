// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays the "Add Friend" button
 *
 * @author Jonathan Olson
 */
public class AddFriendNode extends PNode {

    public JButton addFriendButton;

    private boolean cachedVisible = true;
    private final PSwing holder;

    public AddFriendNode( final NaturalSelectionModel model ) {
        addFriendButton = new JButton( NaturalSelectionStrings.ADD_A_FRIEND );
        holder = new PSwing( addFriendButton );
        addChild( holder );

        addFriendButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.addFriend();
                setVisible( false );
            }
        } );

    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        addFriendButton.setVisible( visible );
        if ( visible != cachedVisible ) {
            cachedVisible = visible;
            if ( visible ) {
                addChild( holder );
            }
            else {
                removeChild( holder );
            }
        }
    }

    public void updateLayout( int width, int height ) {
        setOffset( 75, height - 2 * addFriendButton.getHeight() );
    }
}
