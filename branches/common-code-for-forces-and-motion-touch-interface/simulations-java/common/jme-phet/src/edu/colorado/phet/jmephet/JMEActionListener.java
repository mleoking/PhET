// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An action listener that puts execution back into the JME3 thread
 */
public class JMEActionListener implements ActionListener {
    private Runnable runnable;

    public JMEActionListener( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void actionPerformed( ActionEvent e ) {
        JMEUtils.invoke( runnable );
    }
}
