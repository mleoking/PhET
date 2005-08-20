/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.photoelectric.view.CurrentVsVoltageGraph;
import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.PhotoelectricApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;
import java.awt.*;

import com.sun.java.swing.SwingUtilities2;

/**
 * AmmeterDataCollector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterDataCollector {
    private Frame frame;
    private Ammeter ammeter;
    private AbstractClock clock;
    private CurrentVsVoltageGraph graph;
    private JDialog dlg;

    public AmmeterDataCollector( Frame frame, Ammeter ammeter, AbstractClock clock ) {
        this.frame = frame;
        this.ammeter = ammeter;
        this.clock = clock;
    }

    public double getCurrent() {

        ammeter.clear();
        new MakeEmWait().start();
        dlg = new JDialog( frame, true );
        dlg.setUndecorated( true );
        dlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
        JPanel pnl = new JPanel();
        pnl.setPreferredSize( new Dimension( 100, 60 ) );
        pnl.add( new JLabel( "Collecting data") );
        dlg.setContentPane( pnl );
        dlg.pack();
        dlg.setLocationRelativeTo( frame );
        dlg.setVisible( true );

        return ammeter.getCurrent();
    }

    /**
     * Kills the modal dialog after the simulation clock has ticked through
     * the ammeter's simulation time window
     */
    private class MakeEmWait extends Thread {

        public void run() {
            double startTime = clock.getRunningTime();
            double currTime = startTime;
            do {
                try {
                    Thread.sleep( 100 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                currTime = clock.getRunningTime();
            } while( currTime - startTime < ammeter.getSimulationTimeWindow() );
            dlg.setVisible( false);
        }
    }
}
