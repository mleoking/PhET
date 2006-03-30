// Copyright (C) 2001-2003 Jon A. Maxwell (JAM)
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


package netx.jnlp.runtime;

import java.util.*;
import java.util.List;
import java.io.*;
import java.net.*;
import java.security.*;

import netx.jnlp.*;
import netx.jnlp.cache.*;
import netx.jnlp.util.*;
import netx.jnlp.runtime.*;
import netx.jnlp.services.*;

/**
 * This is the main entry point for the JNLP client.  The main
 * method parses the command line parameters and loads a JNLP
 * file into the secure runtime environment.  This class is meant
 * to be called from the command line or file association; to
 * initialize the netx engine from other code invoke the
 * <code>JNLPRuntime.initialize</code> method after configuring
 * the runtime.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public final class Boot implements PrivilegedAction {

    // todo: decide whether a spawned netx (external launch)
    // should inherit the same options as this instance (store argv?)
    
    private static String R(String key) { return JNLPRuntime.getMessage(key); }
    private static String R(String key, Object param) { return JNLPRuntime.getMessage(key, new Object[] {param}); }

    private static final String version = "0.5";

    /** the JNLP file to open to display the network-based about window */
    private static final String aboutFile =
        "http://jnlp.sourceforge.net/netx/about/netx"+version+".jnlp";

    /** the text to display before launching the about link */
    private static final String aboutMessage = ""
        + "netx v"+version+" - (C)2001-2003 Jon A. Maxwell (jmaxwell@users.sourceforge.net)\n"
        + "\n"
        + R("BLaunchAbout");

    /** the JNLP file to open if -jnlp not specified (null for no default) */
    private static final String defaultFile = "jar:"
        + Boot.class.getProtectionDomain().getCodeSource().getLocation()
        + "!/default.jnlp";

    private static final String miniLicense = "\n"
        + "   netx - an open-source JNLP client.\n"
        + "   Copyright (C) 2001-2003 Jon A. Maxwell (JAM)\n"
        + "\n"
        + "   // This library is free software; you can redistribute it and/or\n"
        + "   modify it under the terms of the GNU Lesser General Public\n"
        + "   License as published by the Free Software Foundation; either\n"
        + "   version 2.1 of the License, or (at your option) any later version.\n"
        + "\n"
        + "   This library is distributed in the hope that it will be useful,\n"
        + "   but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
        + "   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"
        + "   Lesser General Public License for more details.\n"
        + "\n"
        + "   You should have received a copy of the GNU Lesser General Public\n"
        + "   License along with this library; if not, write to the Free Software\n"
        + "   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.\n"
        + "\n";

    private static final String helpMessage = "\n"
        + R("BOUsage")+"\n"
        + "  -basedir dir          "+R("BOBasedir")+"\n"
        + "  -jnlp location        "+R("BOJnlp")+"\n"
        + "  -arg arg              "+R("BOArg")+"\n"
        + "  -param name=value     "+R("BOParam")+"\n"
        + "  -property name=value  "+R("BOProperty")+"\n"
        + "  -update seconds       "+R("BOUpdate")+"\n"
        + "  -license              "+R("BOLicense")+"\n"
        + "  -verbose              "+R("BOVerbose")+"\n"
        + "  -about                "+R("BOAbout")+"\n"
        + "  -nosecurity           "+R("BONosecurity")+"\n"
        + "  -noupdate             "+R("BONoupdate")+"\n"
        + "  -headless             "+R("BOHeadless")+"\n"
        + "  -strict               "+R("BOStrict")+"\n"
        + "  -help                 "+R("BOHelp")+"\n";

    private static final String doubleArgs = "-basedir -jnlp -arg -param -property -update";

    private static String args[]; // avoid the hot potato


    /**
     * Launch the JNLP file specified by the command-line arguments.
     */
    public static void main(String[] argsIn) {
        args = argsIn;

        if (null != getOption("-license")) {
            System.out.println(miniLicense);
            System.exit(0);
        }

        if (null != getOption("-help")) {
            System.out.println(helpMessage);
            System.exit(0);
        }

        if (null != getOption("-about"))
            System.out.println(aboutMessage);

        if (null != getOption("-verbose"))
            JNLPRuntime.setDebug(true);

        if (null != getOption("-update")) {
            int value = Integer.parseInt(getOption("-update"));
            JNLPRuntime.setDefaultUpdatePolicy(new UpdatePolicy(value*1000l));
        }

        if (null != getOption("-headless"))
            JNLPRuntime.setHeadless(true);

        if (null != getOption("-noupdate"))
            JNLPRuntime.setDefaultUpdatePolicy(UpdatePolicy.NEVER);

        // do in a privileged action to clear the security context of
        // the Boot13 class, which doesn't have any privileges in
        // JRE1.3; JRE1.4 works without Boot13 or this PrivilegedAction.
        AccessController.doPrivileged(new Boot());

        args = null; // might save a couple bytes...
    }

    /**
     * The privileged part (jdk1.3 compatibility).
     */
    public Object run() {
        JNLPRuntime.setBaseDir(getBaseDir());
        JNLPRuntime.setSecurityEnabled(null == getOption("-nosecurity"));
        JNLPRuntime.initialize();

        try {
            new Launcher().launch(getFile());
        }
        catch (LaunchException ex) {
            // default handler prints this
        }
        catch (Exception ex) {
            if (JNLPRuntime.isDebug())
                ex.printStackTrace();

            fatalError(ex.getMessage());
        }

        return null;
    }

    private static void fatalError(String message) {
        System.err.println("netx: "+message);
        System.exit(1);
    }

    /**
     * Returns the file to open; does not return if no file was
     * specified.
     */
    private static JNLPFile getFile() throws ParseException, MalformedURLException, IOException {
        String location = getOption("-jnlp");

        // override -jnlp with aboutFile
        if (null != getOption("-about"))
            location = aboutFile;

        // still null, use default
        if (location == null)
            location = defaultFile;

        if (location == null)
            fatalError(R("BNeedsFile")+helpMessage);

        if (JNLPRuntime.isDebug())
            System.out.println(R("BFileLoc")+": "+location);

        URL url = null;
        if (new File(location).exists())
            url = new File(location).toURL(); // Why use file.getCanonicalFile?
        else 
            url = new URL(ServiceUtil.getBasicService().getCodeBase(), location);

        boolean strict = (null != getOption("-strict"));

        JNLPFile file = new JNLPFile(url, strict);

        // add in extra params from command line
        addProperties(file);

        if (file.isApplet())
            addParameters(file);

        if (file.isApplication())
            addArguments(file);

        if (JNLPRuntime.isDebug()) {
            if (getOption("-arg") != null)
                if (file.isInstaller() || file.isApplet())
                    System.out.println(R("BArgsNA"));

            if (getOption("-param") != null)
                if (file.isApplication())
                    System.out.println(R("BParamNA"));
        }

        return file;
    }

    /**
     * Add the properties to the JNLP file.
     */
    private static void addProperties(JNLPFile file) {
        String props[] = getOptions("-property");
        ResourcesDesc resources = file.getResources();

        for (int i=0; i < props.length; i++) {
            // allows empty property, not sure about validity of that.
            int equals = props[i].indexOf("=");
            if (equals == -1)
                fatalError(R("BBadProp", props[i]));

            String key = props[i].substring(0, equals);
            String value = props[i].substring(equals+1, props[i].length());

            resources.addResource(new PropertyDesc(key, value));
        }
    }

    /**
     * Add the params to the JNLP file; only call if file is
     * actually an applet file.
     */
    private static void addParameters(JNLPFile file) {
        String params[] = getOptions("-param");
        AppletDesc applet = file.getApplet();

        for (int i=0; i < params.length; i++) {
            // allows empty param, not sure about validity of that.
            int equals = params[i].indexOf("=");
            if (equals == -1)
                fatalError(R("BBadParam", params[i]));

            String name = params[i].substring(0, equals);
            String value = params[i].substring(equals+1, params[i].length());

            applet.addParameter(name, value);
        }
    }

    /**
     * Add the arguments to the JNLP file; only call if file is
     * actually an application (not installer).
     */
    private static void addArguments(JNLPFile file) {
        String args[] = getOptions("-arg");  // FYI args also global variable
        ApplicationDesc app = file.getApplication();

        for (int i=0; i < args.length; i++) {
            app.addArgument(args[i]);
        }
    }

    /**
     * Return value of the first occurence of the specified
     * option, or null if the option is not present.  If the
     * option is a flag (0-parameter) and is present then the
     * option name is returned.
     */
    private static String getOption(String option) {
        String result[] = getOptions(option);

        if (result.length == 0)
            return null;
        else
            return result[0];
    }

    /**
     * Return all the values of the specified option, or an empty
     * array if the option is not present.  If the option is a
     * flag (0-parameter) and is present then the option name is
     * returned once for each occurrence.
     */
    private static String[] getOptions(String option) {
        List result = new ArrayList();

        for (int i=0; i < args.length; i++) {
            if (option.equals(args[i])) {
                if (-1 == doubleArgs.indexOf(option))
                    result.add(option);
                else
                    if (i+1 < args.length)
                        result.add(args[i+1]);
            }

            if (args[i].startsWith("-") && -1 != doubleArgs.indexOf(args[i]))
                i++;
        }

        return (String[]) result.toArray( new String[result.size()] );
    }

    /**
     * Return the base dir.  If the base dir parameter is not set
     * the value is read from the "${user.home}/.netxrc" file (as
     * defined by JNLPRuntime).  If that file does not exist, an
     * install dialog is displayed to select the base directory.
     */
    private static File getBaseDir() {
        if (getOption("-basedir") != null) {
            File basedir = new File(getOption("-basedir"));

            if (!basedir.exists() || !basedir.isDirectory())
                fatalError(R("BNoDir", basedir));

            return basedir;
        }

        // check .netxrc, display dialog
        File basedir = JNLPRuntime.getDefaultBaseDir();
        if (basedir == null)
            fatalError(R("BNoBase"));

        return basedir;
    }

}

