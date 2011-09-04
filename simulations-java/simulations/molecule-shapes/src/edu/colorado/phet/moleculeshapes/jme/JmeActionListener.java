// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An action listener that puts execution back into the JME3 thread
 */
public class JmeActionListener implements ActionListener {
    private Runnable runnable;

    public JmeActionListener( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void actionPerformed( ActionEvent e ) {
        JmeUtils.invoke( runnable );
    }
}
