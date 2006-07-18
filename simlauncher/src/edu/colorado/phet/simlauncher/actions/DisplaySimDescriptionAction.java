/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.util.LauncherUtil;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import net.sf.wraplog.SystemLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * DisplaySimDescriptionAction
 * <p/>
 * Displays the abstract for a simulation in a JOptionPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DisplaySimDescriptionAction extends AbstractAction {
    private SimContainer simContainer;
    private Component parent;
    private Process process;

    public DisplaySimDescriptionAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent event ) {
        JTextArea textArea = new JTextArea( simContainer.getSimulation().getDescription(), 10, 50 );
        textArea.setPreferredSize( new Dimension( 200, 100 ));
        textArea.setLineWrap( true );
//        JEditorPane jep = new JEditorPane( "text",
//                                           simContainer.getSimulation().getDescription() );
        JOptionPane.showMessageDialog( parent,
                                       textArea,
                                       "Simulation description",
                                       JOptionPane.PLAIN_MESSAGE );
//        JOptionPane.showMessageDialog( parent,
//                                       "Not yet implemented",
//                                       "Simulation description",
//                                       JOptionPane.PLAIN_MESSAGE );

        if( true) return;

        // The preferred method for using the BrowserLauncher2 api is to create an
        // instance of BrowserLauncher (edu.stanford.ejalbert.BrowserLauncher) and
        // invoke the method: public void openURLinBrowser(String urlString).
        if( PhetUtilities.isMacintosh() ) {
            String[]commands = new String[]{"open", "-a", "/Applications/Safari.app",};
            for( int i = 0; i < commands.length; i++ ) {
                System.out.println( "commands[i] = " + commands[i] );
            }
            try {
                process = Runtime.getRuntime().exec( commands );
            }
            catch( IOException ioe ) {
                ioe.printStackTrace();
            }
            // Get the input stream and read from it
            new Thread( new LauncherUtil.OutputRedirection( process.getInputStream() ) ).start();
        }
        else {
            try {
                BrowserLauncher browserLauncher = new BrowserLauncher( new SystemLogger() );
                java.util.List list = browserLauncher.getBrowserList();
//                if( DEBUG ) {
//                    System.out.println( "list = " + list );
//                }
                if( list.size() > 1 ) {
                    String path = simContainer.getSimulation().getDescriptionResource().getLocalFile().getAbsolutePath();
                    browserLauncher.openURLinBrowser( list.get( 1 ).toString(), "file://" + path );
                }
            }
            catch( BrowserLaunchingInitializingException e ) {
                e.printStackTrace();
            }
            catch( UnsupportedOperatingSystemException e ) {
                e.printStackTrace();
            }
            catch( BrowserLaunchingExecutionException e ) {
                e.printStackTrace();
            }
        }

    }
}
