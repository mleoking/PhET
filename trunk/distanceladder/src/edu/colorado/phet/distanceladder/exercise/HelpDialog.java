/**
 * Class: HelpDialog
 * Package: edu.colorado.phet.distanceladder.exercise
 * Author: Another Guy
 * Date: Apr 27, 2004
 */
package edu.colorado.phet.distanceladder.exercise;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HelpDialog extends JDialog {

    public HelpDialog( JFrame owner, JTextComponent questionPane ) {
        super( owner, false );
        // Don't let the user close the dialog with the icon in the upper right corner
//        this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );
        this.setResizable( true );
        this.setUndecorated( true );
        this.getRootPane().setWindowDecorationStyle( JRootPane.INFORMATION_DIALOG );

        // Add the question
        Container container = this.getContentPane();
        container.setLayout( new BorderLayout() );
        questionPane.setEditable( false );
        JScrollPane jScrollPane = new JScrollPane( questionPane );
        jScrollPane.setAutoscrolls( true );
        jScrollPane.setPreferredSize( new Dimension( 400, 200 ) );
        container.add( jScrollPane, BorderLayout.NORTH );

        JButton submitBtn = new JButton( new AbstractAction( "Close" ) {
            public void actionPerformed( ActionEvent e ) {
                HelpDialog.this.setVisible( false );
                HelpDialog.this.dispose();
            }
        } );
        JPanel buttonPane = new JPanel();
        buttonPane.add( submitBtn );
        container.add( buttonPane, BorderLayout.SOUTH );
        this.pack();

//        this.setLocation( 50, 50 );
//        this.setVisible( true );
    }
}
