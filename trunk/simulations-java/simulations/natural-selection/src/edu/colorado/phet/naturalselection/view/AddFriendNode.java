package edu.colorado.phet.naturalselection.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class AddFriendNode extends PNode {

    public JButton addFriendButton;

    public AddFriendNode( final NaturalSelectionModel model ) {
        addFriendButton = new JButton( "Add a friend" );
        PSwing holder = new PSwing( addFriendButton );
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
    }
}
