/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.application.modules.compositetestmodule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 9, 2003
 * Time: 8:21:35 PM
 * Copyright (c) Jun 9, 2003 by Sam Reid
 */
public class CompositeTestControlPanel extends JPanel {
    private CompositeTestModule module;

    public CompositeTestControlPanel( final CompositeTestModule module ) {
        this.module = module;
        JButton addButton = new JButton( "Add a Composite Test Message" );
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addMessage();
            }
        } );
        add( addButton );
    }
}
