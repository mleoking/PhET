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

import edu.colorado.phet.simlauncher.SimLauncher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * SimLauncherHelpAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncherHelpAction extends AbstractAction {

    public void actionPerformed( ActionEvent e ) {
        try {
            JDialog dlg = new JDialog( SimLauncher.instance().getFrame(), false );
            InputStream in = getClass().getResourceAsStream( "help/introduction.htm" );
            BufferedReader in2 = new BufferedReader( new InputStreamReader( in ) );
            String str;
            int i;
            StringBuffer sb = new StringBuffer();
            while( ( str = in2.readLine() ) != null ) {
                sb.append( str );
            }
            in.close();
            JTextArea textArea = new JTextArea( str );

            dlg.setContentPane( textArea );
            dlg.pack();
            dlg.setVisible( true );
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }

    }
}
