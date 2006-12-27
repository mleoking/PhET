/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 9:11:20 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class HelloPhetSimpleControlPanel extends JPanel {
    private MessageModule module;

    public HelloPhetSimpleControlPanel( final MessageModule module ) {
        this.module = module;
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        final JSlider speed = new JSlider( 1, 10, 2 );
        speed.setBorder( BorderFactory.createTitledBorder( "Speed" ) );
        add( speed );

        final JSlider ypos = new JSlider( 50, 450, 200 );
        ypos.setBorder( BorderFactory.createTitledBorder( "YPosition" ) );
        add( ypos );

        JButton addButton = new JButton( "Add Message" );
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addNewMessage( speed.getValue(), ypos.getValue() );
            }
        } );
        add( addButton );

        JButton removeButton = new JButton( "Remove Last" );
        removeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.removeLastMessage();
            }
        } );
        add( removeButton );

//        JButton increaseDt=new JButton("Increase DT");
//        increaseDt.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });
    }
}
