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
import edu.colorado.phet.simlauncher.util.HtmlViewer;

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
