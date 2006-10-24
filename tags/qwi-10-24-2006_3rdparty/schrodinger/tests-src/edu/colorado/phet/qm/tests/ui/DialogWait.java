package edu.colorado.phet.qm.tests.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogWait extends JFrame implements ActionListener {

    public DialogWait() {
        JButton button = new JButton( "Start Long Running Task" );
        button.addActionListener( this );
        getContentPane().add( button );
    }

    public void actionPerformed( ActionEvent e ) {
        JDialog dialog = new JDialog( this, "Please Wait Test", true );
        dialog.getContentPane().add( new JLabel( "Please wait for 5 seconds..." ) );
        dialog.pack();
        dialog.setLocationRelativeTo( this );

        Thread longRunningTask = new MyThread( dialog );
        longRunningTask.start();

        dialog.setVisible( true );

        dispose();
    }

    class MyThread extends Thread {
        JDialog dialog;

        public MyThread( JDialog dialog ) {
            this.dialog = dialog;
            setPriority( Thread.MAX_PRIORITY );
        }

        public void run() {
            long start = System.currentTimeMillis();
            long current = start;

            // Tight loop to hog CPU

            while( current - start < 5000 ) {
                current = System.currentTimeMillis();
                Thread.yield();
            }

            System.out.println( "Long task finished" );
            dialog.setVisible( false );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new DialogWait();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
}
