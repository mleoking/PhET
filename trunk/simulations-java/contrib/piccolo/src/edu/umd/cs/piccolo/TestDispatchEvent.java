/* Copyright 2007, University of Colorado */
package edu.umd.cs.piccolo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;

public class TestDispatchEvent {


    public static void main( String[] args ) {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            new AWTEventListener() {
                public void eventDispatched( AWTEvent awtEvent ) {
                    System.out.println("Caught event: " + awtEvent );
                }
            },
            0xFFFFFFFF 
        );

        JFrame frame = new JFrame( );


        frame.dispatchEvent( new ActionEvent(new JLabel(), ActionEvent.ACTION_PERFORMED, ""));
        
    }
}
