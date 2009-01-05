package edu.colorado.phet.build.gui;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class PhetBuildGUITask extends Task {
    final Object blocker = new Object();

    public void execute() throws BuildException {
        super.execute();
        new PhetBuildGUI( getProject().getBaseDir() ).start();
        System.out.println( "started phet build gui" );
        //avoid closing ant until we've finished with this application
        synchronized( blocker ) {
            try {
                int hours = 1;
                blocker.wait( 1000 * 60 * 60 * hours );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
