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
    private String[] acceptableJVMNames = Config.acceptableJVMNames;


    public void install( InstallerProxy installerProxy ) throws InstallException {
        InstallerResources ir = (InstallerResources)installerProxy.getService( InstallerResources.class );
        Vector vms = ir.getJavaVMList();

        // Print the JVMs we found to the console
        for( int i = 0; i < vms.size(); i++ ) {
            Object o = vms.elementAt( i );
            System.out.println( o );
        }

        // Check for an acceptable JVM
        for( int i = 0; i < vms.size() && acceptableJvmFound == false; i++ ) {
            Object o = (Object)vms.elementAt( i );
            String s = o.toString();
            for( int j = 0; j < acceptableJVMNames.length && acceptableJvmFound == false; j++ ) {
                String acceptableJVMName = acceptableJVMNames[j];
                System.out.println( "acceptableJVMName = " + acceptableJVMName );
                if( s.indexOf( acceptableJVMName ) != -1
                    && s.endsWith( "java.exe" ) ) {
                    acceptableJvmFound = true;
                }
                System.out.println( "acceptableJvmFound = " + acceptableJvmFound );
            }
        }

        // Set a variable that the installer can test
        installerProxy.setVariable( "AcceptableJvmFound", new Boolean( acceptableJvmFound ) );
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
