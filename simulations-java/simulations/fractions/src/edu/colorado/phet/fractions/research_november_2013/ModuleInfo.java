// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Created by Sam on 10/18/13.
 */
public class ModuleInfo {
    boolean running;
    private long startTime;
    private long elapsedTime = 0;
    private int clicks;
    private Module module;

    public ModuleInfo( Module module ) {
        this.module = module;
        this.module.getSimulationPanel().addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {

            }

            public void mousePressed( MouseEvent e ) {
                clicks++;
            }

            public void mouseReleased( MouseEvent e ) {

            }

            public void mouseEntered( MouseEvent e ) {

            }

            public void mouseExited( MouseEvent e ) {

            }
        } );
    }

    public long getElapsedTime() {
        if ( running ) {
            return ( System.currentTimeMillis() - startTime ) + elapsedTime;
        }
        return elapsedTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning( boolean running ) {
        if ( running != this.running ) {
            this.running = running;

            if ( this.running ) {
                startTime = System.currentTimeMillis();
            }
            else {
                elapsedTime += ( System.currentTimeMillis() - startTime );
            }
        }
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks( int clicks ) {
        this.clicks = clicks;
    }
}
