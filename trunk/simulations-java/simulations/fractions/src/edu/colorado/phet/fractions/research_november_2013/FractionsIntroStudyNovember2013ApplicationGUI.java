// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.simsharing.Log;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroStudyNovember2013ApplicationGUI {

    public static void main( final String[] args ) {
        final Analysis report = new Analysis();
        SimSharingManager.initListeners.add( new VoidFunction1<SimSharingManager>() {
            public void apply( SimSharingManager simSharingManager ) {
                simSharingManager.addLog( new Log() {
                    public void addMessage( SimSharingMessage message ) throws IOException {
                        report.addMessage( message.toString() );
                    }

                    public String getName() {
                        return "Fractions Intro Study November 2013";
                    }

                    public void shutdown() {

                    }
                } );
            }
        } );
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyNovember2013Application.class );

        new Timer( 60, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                report.sync();
            }
        } ).start();
    }
}