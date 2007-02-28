/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.javadetector;

import com.zerog.ia.api.pub.*;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Locale;

/**
 * TestFinVmOnWindowsAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestFinVmOnWindowsAction {
    public static void main( String[] args ) {
        ResourceAccess resourceAccess = new ResourceAccess() {
            public URL getResource( String string ) {
                return null;
            }

            public File getTempDirectory() throws IOException {
                return null;
            }

            public File saveURLContentToFile( URL url ) throws IOException {
                return null;
            }
        };
        VariableAccess variableAccess = new VariableAccess() {
            public Object getVariable( String string ) {
                return null;
            }

            public String substitute( String string ) {
                return null;
            }

            public Object setVariable( String string, Object object ) {
                return null;
            }
        };
        I18NAccess i18NAccess = new I18NAccess() {
            public String getValue( String string, Locale locale ) {
                return null;
            }

            public String getValue( String string ) {
                return null;
            }
        };
        InstallerAccess installerAccess = new InstallerAccess() {
            public DataOutput getLogOutput() {
                return null;
            }
        };
        InstallerControl installerControl = new InstallerControl() {
            public void abortInstallation( int i ) {

            }
        };
//        InstallerProxy installerProxy = new CustomCodeConsoleProxy( );
        InstallerProxy installerProxy = new InstallerProxy( resourceAccess,
                                                            variableAccess,
                                                            i18NAccess,
                                                            installerAccess,
                                                            installerControl );
        try {
            new FindVmOnWindowsAction().install( installerProxy );
        }
        catch( InstallException e ) {
            e.printStackTrace();
        }
    }

}
