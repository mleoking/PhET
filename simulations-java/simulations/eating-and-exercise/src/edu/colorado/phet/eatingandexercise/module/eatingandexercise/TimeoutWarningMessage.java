// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.view.LabelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class TimeoutWarningMessage extends PNode {
    private long lastVisibilityRestart;

    public TimeoutWarningMessage( String message ) {
        LabelNode labelNode = new LabelNode( message );
        addChild( labelNode );

        Timer timer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
                updateVisibility();
            }
        } );
        timer.start();
    }

    protected void update() {
    }

    protected void updateVisibility() {
        setVisible( System.currentTimeMillis() - lastVisibilityRestart < 5000 );
    }

    public long getLastVisibilityRestart() {
        return lastVisibilityRestart;
    }

    protected void resetVisibleTime() {
        this.lastVisibilityRestart = System.currentTimeMillis();
        updateVisibility();
    }

}