/**
 * Class: DetectFlashAction
 * Package: edu.colorado.phet.javadetector
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.javadetector;

import com.zerog.ia.api.pub.CustomCodeAction;
import com.zerog.ia.api.pub.InstallerProxy;
import com.zerog.ia.api.pub.InstallException;
import com.zerog.ia.api.pub.UninstallerProxy;

import javax.swing.*;
import java.io.IOException;
import java.io.File;

public class DetectFlashAction extends CustomCodeAction {
    public void install( InstallerProxy installerProxy ) throws InstallException {
        try {
            String installDir = installerProxy.getVariable( "USER_INSTALL_DIR" ).toString();
//            String s = System.getProperty( installDir  );
            JOptionPane.showMessageDialog( null, installDir );
            File dir = new File( installDir );
//            File dir = new File( s );
            File launchFile = new File( dir, "flash_detection_as.swf");
            Runtime.getRuntime().exec( launchFile.getAbsolutePath() );
            JOptionPane.showMessageDialog( null, "ran detector without exception" );
        }
        catch( IOException e ) {
            JOptionPane.showMessageDialog( null, "Exception: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    public void uninstall( UninstallerProxy uninstallerProxy ) throws InstallException {
    }

    public String getInstallStatusMessage() {
        return null;
    }

    public String getUninstallStatusMessage() {
        return null;
    }

    public static void main( String[] args ) {

        try {
            String installDir = "C:\\Program files\\TEST";
//            String s = System.getProperty( installDir  );
            JOptionPane.showMessageDialog( null, installDir );
            File dir = new File( installDir );
//            File dir = new File( s );
            File launchFile = new File( dir, "flash_detection_as.swf");
            System.out.println( launchFile.getAbsolutePath() );
            Runtime.getRuntime().exec( launchFile.getAbsolutePath() );
            JOptionPane.showMessageDialog( null, "ran detector without exception" );
        }
        catch( IOException e ) {
            JOptionPane.showMessageDialog( null, "Exception: " + e.getMessage() );
            System.out.println( e.getCause() );
            e.printStackTrace();
        }
    }
}
