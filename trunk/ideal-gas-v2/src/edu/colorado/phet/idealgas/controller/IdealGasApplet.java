/**
 * Class: IdealGasApplet
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Apr 11, 2003
 */
package edu.colorado.phet.idealgas.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class IdealGasApplet extends JApplet {
    private IdealGasApplication  application;

    public void init() {

        Container contentPane = getContentPane();
        JPanel appletPanel = new JPanel( new BorderLayout() );
        contentPane.add( appletPanel );

        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton( "Start" );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runApplication();
            }
        } );
        buttonPanel.add( startButton );

        JButton closeButton = new JButton( "Close" );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.getPhetFrame().hide();
            }
        } );
        buttonPanel.add( closeButton );
        appletPanel.add( buttonPanel, BorderLayout.SOUTH );

        JTextArea instructions = new JTextArea( instructionText );
        instructions.setLineWrap( true );
        instructions.setWrapStyleWord( true );
        appletPanel.add( instructions, BorderLayout.CENTER );
    }

    private void runApplication() {
        if( application == null ) {
            application = new IdealGasApplication();
            application.start();
        }
    }

    public void start() {
        if( application != null ) {
            application.run();
        }
    }

    public void stop() {
        if( application == null ) {
            application.stop();
        }
    }

    //
    // Static fields and methods
    //
    private String instructionText =
            "To start the Ideal Gas System, press the Start button below. " +
            "To close the Ideal Gas System, press the Close button.";
}
