/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule;

import edu.colorado.phet.common.examples.hellophet.model.HelloPhetModel;
import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.examples.hellophet.model.MessageData;
import edu.colorado.phet.common.model.command.commands.AddModelElementCommand;
import edu.colorado.phet.common.view.components.clockgui.ClockDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 9:11:20 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class MessageModuleControlPanel extends JPanel {
    HelloPhetModel m;
    private JFrame parent;
    private ClockDialog cd;

    public MessageModuleControlPanel( final HelloPhetModel m, final JFrame parent ) {
        this.m = m;
        this.parent = parent;
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        JLabel label = new JLabel( "Height" );
        add( label );
//        final JSlider js = new JSlider();
//        add(js);
//        js.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                int y = js.getValue();
//                MoveMessageCommand mmc = new MoveMessageCommand(m.getMessage(), y);
//                m.execute(mmc);
////                mmc.doIt();
//            }
//        });

//        JButton jb = new JButton("Show FixedClock Dialog");
//        jb.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (cd == null)
//                    cd = new ClockDialog(parent, m.getClock());
//                cd.setVisible(true);
//            }
//        });
//        add(jb);

        JButton addButton = new JButton( "Add Message" );
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Message element = new Message( new MessageData( 200, 200 ), 3 );
                AddModelElementCommand amec = new AddModelElementCommand( m, element );
                amec.doItLater();
//
//                //This is wrong.
//                m.executeQueue();
//                m.updateObservers();
            }
        } );
        add( addButton );
    }
}
