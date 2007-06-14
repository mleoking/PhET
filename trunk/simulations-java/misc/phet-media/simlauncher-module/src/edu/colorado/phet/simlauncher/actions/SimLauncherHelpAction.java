/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/SimLauncherHelpAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/28 23:50:37 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimLauncher;
import edu.colorado.phet.simlauncher.util.HtmlViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * SimLauncherHelpAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public class SimLauncherHelpAction extends AbstractAction {

    public void actionPerformed( ActionEvent e ) {
        if( true ) {
            HtmlViewer viewer = new HtmlViewer();
            viewer.view( "file://C:/phet/simlauncher-module/data/help/introduction.htm" );
            return;
        }
        try {
            JDialog dlg = new JDialog( SimLauncher.instance().getFrame(), false );
            InputStream in = getClass().getResourceAsStream( "help/introduction.htm" );
            FileInputStream fis = new FileInputStream( "C:/phet/simlauncher-module/data/help/introduction.htm");
            BufferedReader in2 = new BufferedReader( new InputStreamReader( fis ) );
            String str;
            StringBuffer sb = new StringBuffer();
            while( ( str = in2.readLine() ) != null ) {
                sb.append( str );
            }
            in2.close();
            JTextArea textArea = new JTextArea( sb.toString() );
//            JEditorPane jep = new JEditorPane( sb.toString() );

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
