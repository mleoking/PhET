/**
 * Class: TestResult
 * Package: edu.colorado.phet.javadetector
 * Author: Another Guy
 * Date: Dec 12, 2003
 */
package edu.colorado.phet.javadetector;

import com.zerog.ia.api.pub.*;

public class TestResult extends CustomCodeAction {

    public void install( InstallerProxy installerProxy ) throws InstallException {
        Object o = installerProxy.getVariable( "AcceptableJvmFound" );
        System.out.println( "---> class: " + o.getClass() + "   value: " + o + "   toString: " + o.toString() );
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
