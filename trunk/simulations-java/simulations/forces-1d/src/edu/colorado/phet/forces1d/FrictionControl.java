package edu.colorado.phet.forces1d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Created by: Sam
 * Sep 10, 2008 at 9:21:04 AM
 */
public class FrictionControl extends JPanel {
    public FrictionControl( final Forces1DModule module ) {
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton onButton = new JRadioButton( Force1DResources.get( "SimpleControlPanel.friction-on" ) );
        onButton.setHorizontalTextPosition( JRadioButton.LEFT );
        JRadioButton offButton = new JRadioButton( Force1DResources.get( "SimpleControlPanel.friction-off" ) );
        offButton.setHorizontalTextPosition( JRadioButton.LEFT );
        buttonGroup.add( onButton );
        buttonGroup.add( offButton );

        onButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setFrictionEnabled( true );
            }
        } );
        offButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setFrictionEnabled( false );
            }
        } );
        add(new JLabel(Force1DResources.get( "SimpleControlPanel.friction" )));
        add( onButton );
        add( offButton );
    }
}
