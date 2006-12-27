
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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.security.*;
import java.lang.reflect.*;
import javax.jnlp.*;
import netx.jnlp.cache.*;
import netx.jnlp.*;

/**
 * Classloader that takes it's resources from a JNLP file.  If the
 * JNLP file defines extensions, separate classloaders for these
 * will be created automatically.  Classes are loaded with the
 * security context when the classloader was created.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
public class JNLPClassLoader extends URLClassLoader {

    // todo: initializePermissions should get the permissions from
    // extension classes too so that main file classes can load
    // resources in an extension.

    /** map from JNLPFile url to shared classloader */
    private static Map urlToLoader = new HashMap(); // never garbage collected!

    /** number of times a classloader with native code is created */
    private static int nativeCounter = 0;

    /** the directory for native code */
    private File nativeDir = null; // if set, some native code exists

    /** security context */
    private AccessControlContext acc = AccessController.getContext();

    /** the permissions for the cached jar files */
    private List resourcePermissions;

    /** the app */
    private ApplicationInstance app = null; // here for faster lookup in security manager

    /** list of this, local and global loaders this loader uses */
    private JNLPClassLoader loaders[] = null; // ..[0]==this

    /** whether to strictly adhere to the spec or not */
    private boolean strict = true;

    /** loads the resources */
    private ResourceTracker tracker = new ResourceTracker(true); // prefetch

    /** the update policy for resources */
    private UpdatePolicy updatePolicy;

    /** the JNLP file */
    private JNLPFile file;

    /** the resources section */
    private ResourcesDesc resources;

    /** the security section */
    private SecurityDesc security;

    /** all jars not yet part of classloader or active */
    private List available = new ArrayList();


    /**
     * Create a new JNLPClassLoader from the specified file.
     *
     * @param file the JNLP file
     */
    protected JNLPClassLoader(JNLPFile file, UpdatePolicy policy) {
        super(new URL[0], JNLPClassLoader.class.getClassLoader());

        if (JNLPRuntime.isDebug())
            System.out.println("New classloader: "+file.getFileLocation());

        this.file = file;
        this.updatePolicy = policy;
        this.resources = file.getResources();
        this.security = file.getSecurity();

        // initialize extensions
        initializeExtensions();

        // initialize permissions
        initializePermissions();

        // populate the resource tracker and load the inital jars
        initializeResources();
    }

    /**
     * Returns a JNLP classloader for the specified JNLP file.
     *
     * @param file the file to load classes for
     * @param policy the update policy to use when downloading resources
     */
    public static JNLPClassLoader getInstance(JNLPFile file, UpdatePolicy policy) {
        JNLPClassLoader loader = null;
        URL location = file.getFileLocation();

        if (location != null)
            loader = (JNLPClassLoader) urlToLoader.get(location);

        if (loader == null)
            loader = new JNLPClassLoader(file, policy);

        if (file.getInformation().isSharingAllowed())
            urlToLoader.put(location, loader);

        return loader;
    }

    /**
     * Returns a JNLP classloader for the JNLP file at the specified
     * location. 
     *
     * @param location the file's location
     * @param policy the update policy to use when downloading resources
     */
    public static JNLPClassLoader getInstance(URL location, UpdatePolicy policy) throws IOException, ParseException {
        JNLPClassLoader loader = (JNLPClassLoader) urlToLoader.get(location);

        if (loader == null)
            loader = getInstance(new JNLPFile(location, false, policy), policy);

        return loader;
    }

    /**
     * Load the extensions specified in the JNLP file.
     */
    void initializeExtensions() {
        ExtensionDesc ext[] = resources.getExtensions();
        List loaderList = new ArrayList();

        loaderList.add(this);

        for (int i=0; i < ext.length; i++) {
            try {
                JNLPClassLoader loader = getInstance(ext[i].getLocation(), updatePolicy);
                loaderList.add(loader);
            }
            catch (Exception ex) {
                if (JNLPRuntime.isDebug())
                    ex.printStackTrace();
                // should throw an exception here?
            }
        }

        loaders = (JNLPClassLoader[]) loaderList.toArray(new JNLPClassLoader[ loaderList.size()]);
    }

    /**
     * Make permission objects for the classpath.
     */
    void initializePermissions() {
        resourcePermissions = new ArrayList();

        JARDesc jars[] = resources.getJARs();
        for (int i=0; i < jars.length; i++) {
            Permission p = CacheUtil.getReadPermission(jars[i].getLocation(),
                                                       jars[i].getVersion());

            if (p != null)
                resourcePermissions.add(p);
        }
    }

    /**
     * Load all of the JARs used in this JNLP file into the
     * ResourceTracker for downloading.
     */
    void initializeResources() {
        JARDesc jars[] = resources.getJARs();
        List initialJars = new ArrayList();

        for (int i=0; i < jars.length; i++) {
            available.add(jars[i]);

            if (jars[i].isEager())
                initialJars.add(jars[i]); // regardless of part

            tracker.addResource(jars[i].getLocation(), 
                                jars[i].getVersion(), 
                                JNLPRuntime.getDefaultUpdatePolicy()
                               );
        }

        if (strict)
            fillInPartJars(initialJars); // add in each initial part's lazy jars

        activateJars(initialJars);
    }

    /**
     * Add applet's codebase URL.  This allows compatibility with
     * applets that load resources from their codebase instead of
     * through JARs, but can slow down resource loading.  Resources
     * loaded from the codebase are not cached.
     */
    public void enableCodeBase() {
        addURL( file.getCodeBase() ); // nothing happens if called more that once?
    }

    /**
     * Sets the JNLP app this group is for; can only be called once.
     */
    public void setApplication(ApplicationInstance app) {
        if (this.app != null) {
            if (JNLPRuntime.isDebug()) {
                Exception ex = new IllegalStateException("Application can only be set once");
                ex.printStackTrace();
            }
            return;
        }

        this.app = app;
    }

    /**
     * Returns the JNLP app for this classloader
     */
    public ApplicationInstance getApplication() {
        return app;
    }

    /**
     * Returns the JNLP file the classloader was created from.
     */
    public JNLPFile getJNLPFile() {
        return file;
    }

    /**
     * Returns the permissions for the CodeSource.
     */
    protected PermissionCollection getPermissions(CodeSource cs) {
        Permissions result = new Permissions();

        // should check for extensions or boot, automatically give all
        // access w/o security dialog once we actually check certificates.

        // copy security permissions from SecurityDesc element
        Enumeration e = security.getPermissions().elements();
        while (e.hasMoreElements())
            result.add((Permission) e.nextElement());

        // add in permission to read the cached JAR files
        for (int i=0; i < resourcePermissions.size(); i++)
            result.add((Permission) resourcePermissions.get(i));

        return result;
    }

    /**
     * Adds to the specified list of JARS any other JARs that need
     * to be loaded at the same time as the JARs specified (ie, are
     * in the same part).
     */
    protected void fillInPartJars(List jars) {
        for (int i=0; i < jars.size(); i++) {
            String part = ((JARDesc) jars.get(i)).getPart();

            for (int a=0; a < available.size(); a++) {
                JARDesc jar = (JARDesc) available.get(a);

                if (part != null && part.equals(jar.getPart()))
                    if (!jars.contains(jar))
                        jars.add(jar);
            }
        }
    }

    /**
     * Ensures that the list of jars have all been transferred, and
     * makes them available to the classloader.  If a jar contains
     * native code, the libraries will be extracted and placed in
     * the path.
     *
     * @param jars the list of jars to load
     */
    protected void activateJars(final List jars) {
        PrivilegedAction activate = new PrivilegedAction() {
            public Object run() {
                // transfer the Jars
                waitForJars(jars);

                for (int i=0; i < jars.size(); i++) {
                    JARDesc jar = (JARDesc) jars.get(i);

                    available.remove(jar);

                    // add jar
                    File localFile = tracker.getCacheFile(jar.getLocation());
                    try {
                        URL location = jar.getLocation(); // non-cacheable, use source location
                        if (localFile != null)
                            location = localFile.toURL(); // cached file

                        addURL(location);

                        if (JNLPRuntime.isDebug())
                            System.err.println("Activate jar: "+location);
                    }
                    catch (Exception ex) {
                        if (JNLPRuntime.isDebug())
                            ex.printStackTrace();
                    }

                    if (jar.isNative())
                        activateNative(jar);
                }

                return null;
            }
        };

        AccessController.doPrivileged(activate, acc);
    }

    /**
     * Enable the native code contained in a JAR by copying the
     * native files into the filesystem.  Called in the security
     * context of the classloader.
     */
    protected void activateNative(JARDesc jar) {
        if (JNLPRuntime.isDebug())
            System.out.println("Activate native: "+jar.getLocation());

        File localFile = tracker.getCacheFile(jar.getLocation());
        if (localFile == null)
            return;

        if (nativeDir == null)
            nativeDir = getNativeDir();

        try {
            JarFile jarFile = new JarFile(localFile, false);
            Enumeration entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry e = (JarEntry) entries.nextElement();

                if (e.isDirectory() || e.getName().indexOf('/') != -1)
                    continue;

                File outFile = new File(nativeDir, e.getName());

                CacheUtil.streamCopy(jarFile.getInputStream(e),
                                     new FileOutputStream(outFile));
            }
        }
        catch (IOException ex) {
            if (JNLPRuntime.isDebug())
                ex.printStackTrace();
        }
    }

    /**
     * Return the base directory to store native code files in.
     * This method does not need to return the same directory across
     * calls.
     */
    protected File getNativeDir() {
        nativeDir = new File(System.getProperty("java.io.tmpdir") 
                             + File.separator + "netx-native-" 
                             + (new Random().nextInt() & 0xFFFF));

        if (!nativeDir.mkdirs()) 
            return null;
        else
            return nativeDir;
    }

    /**
     * Return the absolute path to the native library.
     */
    protected String findLibrary(String lib) {
        if (nativeDir == null)
            return null;

        String syslib = System.mapLibraryName(lib);

        File target = new File(nativeDir, syslib);
        if (target.exists())
            return target.toString();
        else {
            String result = super.findLibrary(lib);
            if (result != null)
                return result;

            return findLibraryExt(lib);
        }
    }

    /**
     * Try to find the library path from another peer classloader.
     */
    protected String findLibraryExt(String lib) {
        for (int i=0; i < loaders.length; i++) {
            String result = null;

            if (loaders[i] != this)
                result = loaders[i].findLibrary(lib);

            if (result != null)
                return result;
        }

        return null;
    }

    /**
     * Wait for a group of JARs, and send download events if there
     * is a download listener or display a progress window otherwise.
     *
     * @param jars the jars
     */
    private void waitForJars(List jars) {
        URL urls[] = new URL[jars.size()];

        for (int i=0; i < jars.size(); i++) {
            JARDesc jar = (JARDesc) jars.get(i);

            urls[i] = jar.getLocation();
        }

        CacheUtil.waitForResources(app, tracker, urls, file.getTitle());
    }

    /**
     * Find the loaded class in this loader or any of its extension loaders.
     */
    protected Class findLoadedClassAll(String name) {
        for (int i=0; i < loaders.length; i++) {
            Class result = null;

            if (loaders[i] == this)
                result = super.findLoadedClass(name);
            else
                result = loaders[i].findLoadedClassAll(name);

            if (result != null)
                return result;
        }

        return null;
    }

    /**
     * Find a JAR in the shared 'extension' classloaders, this
     * classloader, or one of the classloaders for the JNLP file's
     * extensions.
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        Class result = findLoadedClassAll(name);

        // try parent classloader
        if (result == null) {
            try {
                ClassLoader parent = getParent();
                if (parent == null)
                    parent = ClassLoader.getSystemClassLoader();

                return parent.loadClass(name);
            }
            catch (ClassNotFoundException ex) { }
        }

        // filter out 'bad' package names like java, javax
        // validPackage(name);

        // search this and the extension loaders
        if (result == null)
            result = loadClassExt(name);

        return result;
    }

    /**
     * Find the class in this loader or any of its extension loaders.
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        for (int i=0; i < loaders.length; i++) {
            try {
                if (loaders[i] == this)
                    return super.findClass(name);
                else
                    return loaders[i].findClass(name);
            }
            catch(ClassNotFoundException ex) { }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * Search for the class by incrementally adding resources to the
     * classloader and its extension classloaders until the resource
     * is found.
     */
    private Class loadClassExt(String name) throws ClassNotFoundException {
        // make recursive
        addAvailable();

        // find it
        try {
            return findClass(name);
        }
        catch(ClassNotFoundException ex) {
        }

        // add resources until found
        while (true) {
            JNLPClassLoader addedTo = addNextResource();

            if (addedTo == null)
                throw new ClassNotFoundException(name);

            try {
                return addedTo.findClass(name);
            }
            catch(ClassNotFoundException ex) {
            }
        }
    }

    /**
     * Finds the resource in this, the parent, or the extension
     * class loaders.
     */
    public URL getResource(String name) {
        URL result = super.getResource(name);

        for (int i=1; i < loaders.length; i++)
            if (result == null)
                result = loaders[i].getResource(name);

        return result;
    }

    /**
     * Finds the resource in this, the parent, or the extension
     * class loaders.
     */
    public Enumeration findResources(String name) throws IOException {
        Vector resources = new Vector();

        for (int i=0; i < loaders.length; i++) {
            Enumeration e;

            if (loaders[i] == this)
                e = super.findResources(name);
            else 
                e = loaders[i].findResources(name);

            while (e.hasMoreElements())
                resources.add(e.nextElement());
        }

        return resources.elements();
    }

    /**
     * Adds whatever resources have already been downloaded in the
     * background.
     */
    protected void addAvailable() {
        // go through available, check tracker for it and all of its
        // part brothers being available immediately, add them.

        for (int i=1; i < loaders.length; i++) {
            loaders[i].addAvailable();
        }
    }

    /**
     * Adds the next unused resource to the classloader.  That
     * resource and all those in the same part will be downloaded
     * and added to the classloader before returning.  If there are
     * no more resources to add, the method returns immediately.
     *
     * @return the classloader that resources were added to, or null
     */
    protected JNLPClassLoader addNextResource() {
        if (available.size() == 0) {
            for (int i=1; i < loaders.length; i++) {
                JNLPClassLoader result = loaders[i].addNextResource();

                if (result != null)
                    return result;
            }
            return null;
        }

        // add jar
        List jars = new ArrayList();
        jars.add(available.get(0));

        fillInPartJars(jars);
        activateJars(jars);

        return this;
    }

    // this part compatibility with previous classloader
    /**
     * @deprecated
     */
    public String getExtensionName() {
        String result = file.getInformation().getTitle();

        if (result == null)
            result = file.getInformation().getDescription();
        if (result == null && file.getFileLocation() != null)
            result = file.getFileLocation().toString();
        if (result == null && file.getCodeBase() != null)
            result = file.getCodeBase().toString();

        return result;
    }

    /**
     * @deprecated
     */
    public String getExtensionHREF() {
        return file.getFileLocation().toString();
    }

}


