/**
 * Class: TestAction
 * Package: edu.colorado.phet.javadetector
 * Author: Another Guy
 * Date: Dec 12, 2003
 */
package edu.colorado.phet.javadetector;

import com.zerog.ia.api.pub.*;

import java.util.Vector;

public class FindVmOnWindowsAction extends CustomCodeAction {
    private boolean acceptableJvmFound;
    private String[] acceptableJVMNames = new String[] {
        "j2sdk1.4",
        "j2re1.4"
    };


    public void install( InstallerProxy installerProxy ) throws InstallException {
        try {
            Thread.sleep( 2000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        InstallerResources ir = (InstallerResources)installerProxy.getService( InstallerResources.class );
        Vector vms = ir.getJavaVMList();
        for( int i = 0; i < vms.size() && acceptableJvmFound == false; i++ ) {
            Object o = (Object)vms.elementAt( i );
            String s = o.toString();
            for( int j = 0; j < acceptableJVMNames.length && acceptableJvmFound == false; j++ ) {
                String acceptableJVMName = acceptableJVMNames[j];
                if( s.indexOf( acceptableJVMName ) != -1 ) {
                    acceptableJvmFound = true;
                }
            }
        }
        System.out.println( "Acceptable JVM found: " + acceptableJvmFound );
        installerProxy.setVariable( "AcceptableJvmFound", new Boolean(acceptableJvmFound).toString() );
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
