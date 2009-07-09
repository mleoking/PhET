package edu.colorado.phet.buildtools.test.gui;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task so the PhET Build GUI can be run from Ant
 */
public class TestGUITask extends Task {
    final Object blocker = new Object();

    public void execute() throws BuildException {
        super.execute();
        new TestGUI( getProject().getBaseDir().getParentFile() ).start();
        System.out.println( "started test build gui" );
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