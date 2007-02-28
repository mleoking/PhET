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

import netx.jnlp.DefaultLaunchHandler;
import netx.jnlp.LaunchHandler;
import netx.jnlp.cache.DefaultDownloadIndicator;
import netx.jnlp.cache.DownloadIndicator;
import netx.jnlp.cache.UpdatePolicy;
import netx.jnlp.services.XServiceManagerStub;
import netx.jnlp.util.PropertiesFile;

import javax.jnlp.ServiceManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.Policy;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Configure and access the runtime environment.  This class
 * stores global jnlp properties such as default download
 * indicators, the install/base directory, the default resource
 * update policy, etc.  Some settings, such as the base directory,
 * cannot be changed once the runtime has been initialized.<p>
 * <p/>
 * The JNLP runtime can be locked to prevent further changes to
 * the runtime environment except by a specified class.  If set,
 * only instances of the <i>exit class</i> can exit the JVM or
 * change the JNLP runtime settings once the runtime has been
 * initialized.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class JNLPRuntime {

    static {
        loadResources();
    }

    /**
     * the localized resource strings
     */
    private static ResourceBundle resources;

    /**
     * the security manager
     */
    private static JNLPSecurityManager security;

    /**
     * the security policy
     */
    private static JNLPPolicy policy;

    /**
     * the base dir for cache, etc
     */
    private static File baseDir;

    /**
     * a default launch handler
     */
    private static LaunchHandler handler = null;

    /**
     * default download indicator
     */
    private static DownloadIndicator indicator = null;

    /**
     * update policy that controls when to check for updates
     */
    private static UpdatePolicy updatePolicy = UpdatePolicy.ALWAYS;

    /**
     * netx window icon
     */
    private static Image windowIcon = null;

    /**
     * whether initialized
     */
    private static boolean initialized = false;

    /**
     * whether netx is in command-line mode (headless)
     */
    private static boolean headless = false;

    /**
     * whether the runtime uses security
     */
    private static boolean securityEnabled = true;

    /**
     * whether debug mode is on
     */
    private static boolean debug = false; // package access by Boot


    /**
     * Returns whether the JNLP runtime environment has been
     * initialized.  Once initialized, some properties such as the
     * base directory cannot be changed.  Before
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize the JNLP runtime environment by installing the
     * security manager and security policy, initializing the JNLP
     * standard services, etc.<p>
     * <p/>
     * This method cannot be called more than once.  Once
     * initialized, methods that alter the runtime can only be
     * called by the exit class.<p>
     *
     * @throws IllegalStateException if the runtime was previously initialized
     */
    public static void initialize() throws IllegalStateException {
        checkInitialized();
        initialized = true;

        if( headless == false ) {
            checkHeadless();
        }

        if( !headless && windowIcon == null ) {
            loadWindowIcon();
        }

        if( !headless && indicator == null ) {
            indicator = new DefaultDownloadIndicator();
        }

        if( handler == null ) {
            handler = new DefaultLaunchHandler();
        }

        if( baseDir == null ) {
            baseDir = getDefaultBaseDir();
        }

        if( baseDir == null ) {
            throw new IllegalStateException( JNLPRuntime.getMessage( "BNoBase" ) );
        }

        policy = new JNLPPolicy();
        security = new JNLPSecurityManager(); // side effect: create JWindow

        if( securityEnabled ) {
            Policy.setPolicy( policy ); // do first b/c our SM blocks setPolicy
            System.setSecurityManager( security );
        }

        ServiceManager.setServiceManagerStub( new XServiceManagerStub() ); // ignored if we're running under Web Start
    }

    /**
     * Returns the window icon.
     */
    public static Image getWindowIcon() {
        return windowIcon;
    }

    /**
     * Sets the window icon that is displayed in Java applications
     * and applets instead of the default Java icon.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setWindowIcon( Image image ) {
        checkExitClass();
        windowIcon = image;
    }

    /**
     * Returns whether the JNLP client will use any AWT/Swing
     * components.
     */
    public static boolean isHeadless() {
        return headless;
    }

    /**
     * Sets whether the JNLP client will use any AWT/Swing
     * components.  In headless mode, client features that use the
     * AWT are disabled such that the client can be used in
     * headless mode (<code>java.awt.headless=true</code>).
     *
     * @throws IllegalStateException if the runtime was previously initialized
     */
    public static void setHeadless( boolean enabled ) {
        checkInitialized();
        headless = enabled;
    }

    /**
     * Return the base directory containing the cache, persistence
     * store, etc.
     */
    public static File getBaseDir() {
        return baseDir;
    }

    /**
     * Sets the base directory containing the cache, persistence
     * store, etc.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setBaseDir( File baseDirectory ) {
        checkInitialized();
        baseDir = baseDirectory;
    }

    /**
     * Returns whether the secure runtime environment is enabled.
     */
    public static boolean isSecurityEnabled() {
        return securityEnabled;
    }

    /**
     * Sets whether to enable the secure runtime environment.
     * Disabling security can increase performance for some
     * applications, and can be used to use netx with other code
     * that uses its own security manager or policy.
     * <p/>
     * Disabling security is not recommended and should only be
     * used if the JNLP files opened are trusted.  This method can
     * only be called before initalizing the runtime.<p>
     *
     * @param enabled whether security should be enabled
     * @throws IllegalStateException if the runtime is already initialized
     */
    public static void setSecurityEnabled( boolean enabled ) {
        checkInitialized();
        securityEnabled = enabled;
    }

    /**
     * Returns the system default base dir for or if not set,
     * prompts the user for the location.
     *
     * @return the base dir, or null if the user canceled the dialog
     * @throws IOException if there was an io exception
     */
    public static File getDefaultBaseDir() {
        PropertiesFile props = JNLPRuntime.getProperties();

        loadWindowIcon();

        String baseStr = props.getProperty( "basedir" );
        if( baseStr != null ) {
            return new File( baseStr );
        }

        if( isHeadless() ) {
            return null;
        }

        File baseDir = InstallDialog.getInstallDir();
        if( baseDir == null ) {
            return null;
        }

        props.setProperty( "basedir", baseDir.toString() );
        props.store();

        return baseDir;
    }

    /**
     * Set a class that can exit the JVM; if not set then any class
     * can exit the JVM.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setExitClass( Class exitClass ) {
        checkExitClass();
        security.setExitClass( exitClass );
    }

    /**
     * Return the current Application, or null if none can be
     * determined.
     */
    public static ApplicationInstance getApplication() {
        return security.getApplication();
    }

    /**
     * Return a PropertiesFile object backed by the runtime's
     * properties file.
     */
    public static PropertiesFile getProperties() {
        File netxrc = new File( System.getProperty( "user.home" ), ".netxrc" );

        return new PropertiesFile( netxrc );
    }

    /**
     * Return whether debug statements for the JNLP client code
     * should be printed.
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Sets whether debug statements for the JNLP client code
     * should be printed to the standard output.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setDebug( boolean enabled ) {
        checkExitClass();
        debug = enabled;
    }

    /**
     * Sets the default update policy.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setDefaultUpdatePolicy( UpdatePolicy policy ) {
        checkExitClass();
        updatePolicy = policy;
    }

    /**
     * Returns the default update policy.
     */
    public static UpdatePolicy getDefaultUpdatePolicy() {
        return updatePolicy;
    }

    /**
     * Sets the default launch handler.
     */
    public static void setDefaultLaunchHandler( LaunchHandler handler ) {
        checkExitClass();
        JNLPRuntime.handler = handler;
    }

    /**
     * Returns the default launch handler.
     */
    public static LaunchHandler getDefaultLaunchHandler() {
        return handler;
    }

    /**
     * Sets the default download indicator.
     *
     * @throws IllegalStateException if caller is not the exit class
     */
    public static void setDefaultDownloadIndicator( DownloadIndicator indicator ) {
        checkExitClass();
        JNLPRuntime.indicator = indicator;
    }

    /**
     * Returns the default download indicator.
     */
    public static DownloadIndicator getDefaultDownloadIndicator() {
        return indicator;
    }

    /**
     * Returns the localized resource string identified by the
     * specified key.  If the message is empty, a null is
     * returned.
     */
    public static String getMessage( String key ) {
        try {
            String result = resources.getString( key );
            if( result.length() == 0 ) {
                return null;
            }
            else {
                return result;
            }
        }
        catch( Exception ex ) {
            if( !key.equals( "RNoResource" ) ) {
                return getMessage( "RNoResource", new Object[]{key} );
            }
            else {
                return "Missing resource: " + key;
            }
        }
    }

    /**
     * Returns the localized resource string using the specified
     * arguments.
     *
     * @param args the formatting arguments to the resource string
     */
    public static String getMessage( String key, Object args[] ) {
        return MessageFormat.format( getMessage( key ), args );
    }

    /**
     * Throws an exception if called when the runtime is
     * already initialized.
     */
    private static void checkInitialized() {
        if( initialized ) {
            throw new IllegalStateException( "JNLPRuntime already initialized." );
        }
    }

    /**
     * Throws an exception if called with security enabled but
     * a caller is not the exit class and the runtime has been
     * initialized.
     */
    private static void checkExitClass() {
        if( securityEnabled && initialized ) {
            if( !security.isExitClass() ) {
                throw new IllegalStateException( "Caller is not the exit class" );
            }
        }
    }

    /**
     * Check whether the VM is in headless mode.
     */
    private static void checkHeadless() {
        //if (GraphicsEnvironment.isHeadless()) // jdk1.4+ only
        //    headless = true;
        try {
            if( "true".equalsIgnoreCase( System.getProperty( "java.awt.headless" ) ) ) {
                headless = true;
            }
        }
        catch( SecurityException ex ) {
        }
    }

    /**
     * Load the resources.
     */
    private static void loadResources() {
        try {
            resources = ResourceBundle.getBundle( "netx.jnlp.resources.Messages" );
        }
        catch( Exception ex ) {
            throw new IllegalStateException( "Missing resource bundle in netx.jar:/netx/jnlp/resource/Messages.properties" );
        }
    }

    /**
     * Load the window icon.
     */
    private static void loadWindowIcon() {
        if( windowIcon != null ) {
            return;
        }

        try {
            ClassLoader cl = JNLPRuntime.class.getClassLoader();
            windowIcon = new javax.swing.ImageIcon( cl.getResource( "netx/jnlp/resources/netx-icon.png" ) ).getImage();
        }
        catch( Exception ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }
        }
    }

}


