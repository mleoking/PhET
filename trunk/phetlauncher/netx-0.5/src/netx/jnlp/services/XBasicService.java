// Copyright (C) 2001 Jon A. Maxwell (JAM)
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


package netx.jnlp.services;

import java.io.*;
import java.net.*;
import javax.jnlp.*;
import javax.swing.*;
import netx.jnlp.*;
import netx.jnlp.util.*;
import netx.jnlp.runtime.*;

/**
 * The BasicService JNLP service.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
class XBasicService implements BasicService {

    /** command used to exec the native browser */
    private String command = null;

    /** whether the command was loaded / prompted for */
    private boolean initialized = false;


    protected XBasicService() {
    }

    /**
     * Returns the codebase of the application, applet, or
     * installer.  If the codebase was not specified in the JNLP
     * element then the main JAR's location is returned.  If no main
     * JAR was specified then the location of the JAR containing the
     * main class is returned.
     */
    public URL getCodeBase() {
        ApplicationInstance app = JNLPRuntime.getApplication();

        if (app != null) {
            JNLPFile file = app.getJNLPFile();

            // return the codebase.
            if (file.getCodeBase() != null)
                return file.getCodeBase();

            // else return the main JAR's URL.
            JARDesc mainJar = file.getResources().getMainJAR();
            if (mainJar != null)
                return mainJar.getLocation();

            // else find JAR where main class was defined.
            //
            // JNLPFile file = app.getJNLPFile();
            // String mainClass = file.getLaunchInfo().getMainClass()+".class";
            // URL jarUrl = app.getClassLoader().getResource(mainClass);
            // go through list of JARDesc to find one matching jarUrl
        }

        return null;
    }

    /**
     * Return true if the Environment is Offline
     */
    public boolean isOffline() {
        return false;
    }

    /**
     * Return true if a Web Browser is Supported
     */
    public boolean isWebBrowserSupported() {
        initialize();

        return command != null;
    }

    /**
     * Show a document.
     *
     * @return whether the document was opened 
     */
    public boolean showDocument(URL url)  {
        initialize();

        if (url.toString().endsWith(".jnlp")) {
            try {
                new Launcher().launchExternal(url);
                return true;
            }
            catch (Exception ex) {
                return false;
            }
        }

        if (command != null) {
            try {
                // this is bogus because the command may require options;
                // should use a StreamTokenizer or similar to get tokens
                // outside of quotes.
                Runtime.getRuntime().exec(command + url.toString());
                //Runtime.getRuntime().exec(new String[]{command,url.toString()});

                return true;
            }
            catch(IOException ex){
                if (JNLPRuntime.isDebug())
                    ex.printStackTrace();
            }
        }

        return false;
    }

    private void initialize() {
        if (initialized)
            return;
        initialized = true;

        if(isWindows()) {
            command = "rundll32 url.dll,FileProtocolHandler ";
        }
        else {
            PropertiesFile props = JNLPRuntime.getProperties();
            command = props.getProperty("browser.command");

            if(command == null) { // prompt & store
                command = promptForCommand(null);

                if(command != null) {
                    props.setProperty("browser.command", command);
                    props.store();
                }
            }
        }
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name");
        if(os != null && os.startsWith("Windows"))
            return true;
        else
            return false;
    }

    private String promptForCommand(String cmd) {
        return JOptionPane.showInputDialog(new JPanel(),
                                           "Browser Location:",
                                           "Specify Browser Location",
                                           JOptionPane.PLAIN_MESSAGE 
                                          );
    }

}


