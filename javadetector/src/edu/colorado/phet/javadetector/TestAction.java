/**
 * Class: TestAction
 * Package: edu.colorado.phet.javadetector
 * Author: Another Guy
 * Date: Dec 12, 2003
 */
package edu.colorado.phet.javadetector;

import com.zerog.ia.api.pub.CustomCodeAction;
import com.zerog.ia.api.pub.InstallerProxy;
import com.zerog.ia.api.pub.InstallException;
import com.zerog.ia.api.pub.UninstallerProxy;

public class TestAction extends CustomCodeAction{
    public void install( InstallerProxy installerProxy ) throws InstallException {
        try {
            Thread.sleep( 2000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        System.out.println( "Test Action" );
    }

    public void uninstall( UninstallerProxy uninstallerProxy ) throws InstallException {
    }

    public String getInstallStatusMessage() {
        return null;
    }

    public String getUninstallStatusMessage() {
        return null;
    }
}
