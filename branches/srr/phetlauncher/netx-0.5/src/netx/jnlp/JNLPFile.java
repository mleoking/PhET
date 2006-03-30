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


package netx.jnlp;

import java.io.*;
import java.net.*;
import java.util.*;
import netx.jnlp.cache.*;
import netx.jnlp.runtime.*;

/**
 * Provides methods to access the information in a Java Network
 * Launching Protocol (JNLP) file.  The Java Network Launching
 * Protocol specifies in an XML file the information needed to
 * load, cache, and run Java code over the network and in a secure
 * environment.<p>
 *
 * This class represents the overall information about a JNLP file
 * from the jnlp element.  Other information is accessed through
 * objects that represent the elements of a JNLP file
 * (information, resources, application-desc, etc).  References to
 * these objects are obtained by calling the getInformation,
 * getResources, getSecurity, etc methods.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
public class JNLPFile {

    // todo: save the update policy, then if file was not updated
    // then do not check resources for being updated.
    //
    // todo: make getLaunchInfo return a superclass that all the
    // launch types implement (can get codebase from it).
    //
    // todo: currently does not filter resources by jvm version.
    //
    
    private static String R(String key) { return JNLPRuntime.getMessage(key); }

    /** the location this JNLP file was created from */
    private URL sourceLocation = null;

    /** the network location of this JNLP file */
    private URL fileLocation;

    /** the URL used to resolve relative URLs in the file */
    private URL codeBase;

    /** file version */
    private Version fileVersion;

    /** spec version */
    private Version specVersion;

    /** information */
    private List info;

    /** resources */
    private List resources;

    /** additional resources not in JNLP file (from command line) */
    private ResourcesDesc sharedResources = new ResourcesDesc(this, null, null, null);

    /** the application description */
    private Object launchType;

    /** the security descriptor */
    private SecurityDesc security;

    /** the default OS */
    private Locale defaultLocale = null;

    /** the default arch */
    private String defaultOS = null;

    /** the default jvm */
    private String defaultArch = null;

    { // initialize defaults if security allows
        try {
            defaultLocale = Locale.getDefault();
            defaultOS = System.getProperty("os.name");
            defaultArch = System.getProperty("os.arch");
        }
        catch (SecurityException ex) {
            // null values will still work, and app can set defaults later
        }
    }


    /**
     * Create a JNLPFile from a URL.
     *
     * @param location the location of the JNLP file
     * @throws IOException if an IO exception occurred
     * @throws ParseException if the JNLP file was invalid
     */
    public JNLPFile(URL location) throws IOException, ParseException {
        this(location, false); // not strict
    }

    /**
     * Create a JNLPFile from a URL checking for updates using the
     * default policy.
     *
     * @param location the location of the JNLP file
     * @param strict whether to enforce the spec when 
     * @throws IOException if an IO exception occurred
     * @throws ParseException if the JNLP file was invalid
     */
    public JNLPFile(URL location, boolean strict) throws IOException, ParseException {
        this(location, strict, JNLPRuntime.getDefaultUpdatePolicy());
    }

    /**
     * Create a JNLPFile from a URL checking for updates using the
     * specified policy.
     *
     * @param location the location of the JNLP file
     * @param strict whether to enforce the spec when 
     * @param policy the update policy
     * @throws IOException if an IO exception occurred
     * @throws ParseException if the JNLP file was invalid
     */
    public JNLPFile(URL location, boolean strict, UpdatePolicy policy) throws IOException, ParseException {
        Node root = Parser.getRootNode(openURL(location, policy));
        parse(root, strict, location);

        this.fileLocation = location;
    }

    /**
     * Create a JNLPFile from an input stream.
     *
     * @throws IOException if an IO exception occurred
     * @throws ParseException if the JNLP file was invalid
     */
    public JNLPFile(InputStream input, boolean strict) throws ParseException {
        parse(Parser.getRootNode(input), strict, null);
    }

    /**
     * Create a JNLPFile from a character stream.
     *
     * @param input the stream
     * @param strict whether to enforce the spec when 
     * @throws IOException if an IO exception occurred
     * @throws ParseException if the JNLP file was invalid
     */
    private JNLPFile(Reader input, boolean strict) throws ParseException {
        // todo: now that we are using NanoXML we can use a Reader
        //parse(Parser.getRootNode(input), strict, null);
    }


    /**
     * Open the jnlp file URL from the cache if there, otherwise
     * download to the cache.  Called from constructor.
     */
    private static InputStream openURL(URL location, UpdatePolicy policy) throws IOException {
        if (location == null || policy == null)
            throw new IllegalArgumentException(R("NullParameter"));

        try {
            ResourceTracker tracker = new ResourceTracker(false); // no prefetch
            tracker.addResource(location, null/*version*/, policy);

            return tracker.getInputStream(location);
        }
        catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Returns the JNLP specification versions supported.
     */
    public static Version getSupportedVersions() {
        return Parser.getSupportedVersions();
    }

    /**
     * Returns the JNLP file's title.  This method returns the same
     * value as InformationDesc.getTitle().
     */
    public String getTitle() {
        return getInformation().getTitle();
    }

    /**
     * Returns the JNLP file's network location as specified in the
     * JNLP file.
     */
    public URL getSourceLocation() {
        return sourceLocation;
    }

    /**
     * Returns the location of the file parsed to create the JNLP
     * file, or null if it was not created from a URL.
     */
    public URL getFileLocation() {
        return fileLocation;
    }

    /**
     * Returns the JNLP file's version.
     */
    public Version getFileVersion() {
        return fileVersion;
    }

    /**
     * Returns the specification version required by the file.
     */
    public Version getSpecVersion() {
        return specVersion;
    }

    /**
     * Returns the codebase URL for the JNLP file.
     */
    public URL getCodeBase() {
        return codeBase;
    }

    /**
     * Returns the information section of the JNLP file as viewed
     * through the default locale.
     */
    public InformationDesc getInformation() {
        return getInformation(defaultLocale);
    }

    /**
     * Returns the information section of the JNLP file as viewed
     * through the specified locale.
     */
    public InformationDesc getInformation(final Locale locale) {
        return new InformationDesc(this, new Locale[] {locale}) {
            protected List getItems(Object key) {
                List result = new ArrayList();

                for (int i=0; i < info.size(); i++) {
                    InformationDesc infoDesc = (InformationDesc) info.get(i);

                    if (localMatches(locale, infoDesc.getLocales()))
                        if (localMatches(locale, infoDesc.getLocales()))
                            result.addAll(infoDesc.getItems(key) );
                }

                return result;
            }
        };
    }

    /**
     * Returns the security section of the JNLP file.
     */
    public SecurityDesc getSecurity() {
        return security;
    }

    /**
     * Returns the resources section of the JNLP file as viewed
     * through the default locale and the os.name and os.arch
     * properties.
     */
    public ResourcesDesc getResources() {
        return getResources(defaultLocale, defaultOS, defaultArch);
    }

    /**
     * Returns the information section of the JNLP file for the
     * specified locale, os, and arch.
     */
    public ResourcesDesc getResources(final Locale locale, final String os, final String arch) {
        return new ResourcesDesc(this, new Locale[] {locale}, new String[] {os}, new String[] {arch}) {
            public List getResources(Class launchType) {
                List result = new ArrayList();

                for (int i=0; i < resources.size(); i++) {
                    ResourcesDesc rescDesc = (ResourcesDesc) resources.get(i);

                    if (localMatches(locale, rescDesc.getLocales())
                        && stringMatches(os, rescDesc.getOS())
                        && stringMatches(arch, rescDesc.getArch()))
                        result.addAll(rescDesc.getResources(launchType) );
                }

                result.addAll(sharedResources.getResources(launchType));

                return result;
            }

            public void addResource(Object resource) {
                // todo: honor the current locale, os, arch values
                sharedResources.addResource(resource);
            }
        };
    }

    /**
     * Returns an object of one of the following types: AppletDesc,
     * ApplicationDesc, InstallerDesc, and ComponentDesc.
     */
    public Object getLaunchInfo() {
        return launchType;
    }

    /**
     * Returns the launch information for an applet.
     *
     * @throws UnsupportedOperationException if there is no applet information
     */
    public AppletDesc getApplet() {
        if (!isApplet())
            throw new UnsupportedOperationException(R("JNotApplet"));

        return (AppletDesc) launchType;
    }

    /**
     * Returns the launch information for an application.
     *
     * @throws UnsupportedOperationException if there is no application information
     */
    public ApplicationDesc getApplication() {
        if (!isApplication())
            throw new UnsupportedOperationException(R("JNotApplication"));

        return (ApplicationDesc) launchType;
    }

    /**
     * Returns the launch information for a component.
     *
     * @throws UnsupportedOperationException if there is no component information
     */
    public ComponentDesc getComponent() {
        if (!isComponent())
            throw new UnsupportedOperationException(R("JNotComponent"));

        return (ComponentDesc) launchType;
    }

    /**
     * Returns the launch information for an installer.
     *
     * @throws UnsupportedOperationException if there is no installer information
     */
    public InstallerDesc getInstaller() {
        if (!isInstaller())
            throw new UnsupportedOperationException(R("NotInstaller"));

        return (InstallerDesc) launchType;
    }

    /** 
     * Returns whether the lauch descriptor describes an Applet.
     */
    public boolean isApplet() {
        return launchType instanceof AppletDesc;
    }

    /** 
     * Returns whether the lauch descriptor describes an Application.
     */
    public boolean isApplication() {
        return launchType instanceof ApplicationDesc;
    }

    /** 
     * Returns whether the lauch descriptor describes a Component.
     */
    public boolean isComponent() {
        return launchType instanceof ComponentDesc;
    }

    /** 
     * Returns whether the lauch descriptor describes an Installer.
     */
    public boolean isInstaller() {
        return launchType instanceof InstallerDesc;
    }

    /**
     * Sets the default view of the JNLP file returned by
     * getInformation, getResources, etc.  If unset, the defaults
     * are the properties os.name, os.arch, and the locale returned
     * by Locale.getDefault().
     */
    public void setDefaults(String os, String arch, Locale locale) {
        defaultOS = os;
        defaultArch = arch;
        defaultLocale = locale;
    }


    /**
     * Returns whether a locale is matched by one of more other
     * locales.  Only the non-empty language, country, and variant
     * codes are compared; for example, a requested locale of
     * Locale("","","") would always return true.
     *
     * @param requested the local
     * @param available the available locales
     * @return true if requested matches any of available, or if
     * available is empty or null.
     */
    private boolean localMatches(Locale requested, Locale available[]) {
        if (available == null || available.length == 0)
            return true;

        for (int i=0; i < available.length; i++) {
            String language = requested.getLanguage(); // "" but never null
            String country = requested.getCountry();
            String variant = requested.getVariant();

            if (!"".equals(language) && !language.equalsIgnoreCase(available[i].getLanguage()))
                continue;
            if (!"".equals(country) && !country.equalsIgnoreCase(available[i].getCountry()))
                continue;
            if (!"".equals(variant) && !variant.equalsIgnoreCase(available[i].getVariant()))
                continue;

            return true;
        }

        return false;
    }

    /**
     * Returns whether the string is a prefix for any of the strings
     * in the specified array.
     *
     * @param prefixStr the prefix string
     * @param available the strings to test
     * @return true if prefixStr is a prefix of any strings in
     * available, or if available is empty or null.
     */
    private boolean stringMatches(String prefixStr, String available[]) {
        if (available == null || available.length == 0)
            return true;

        for (int i=0; i < available.length; i++)
            if (available[i] != null && available[i].startsWith(prefixStr))
                return true;

        return false;
    }

    /**
     * Initialize the JNLPFile fields. Private because it's called
     * from the constructor.
     *
     * @param root the root node
     * @param strict whether to enforce the spec when 
     * @param location the file location or null
     */
    private void parse(Node root, boolean strict, URL location) throws ParseException {
        try {
            //if (location != null)
            //  location = new URL(location, "."); // remove filename

            Parser parser = new Parser(this, location, root, strict, true); // true == allow extensions

            // JNLP tag information
            specVersion = parser.getSpecVersion();
            fileVersion = parser.getFileVersion();
            codeBase = parser.getCodeBase();
            sourceLocation = parser.getFileLocation();

            info = parser.getInfo(root);
            resources = parser.getResources(root, false); // false == not a j2se resources section
            launchType = parser.getLauncher(root);
            security = parser.getSecurity(root);
        }
        catch (ParseException ex) {
            throw ex;
        }
        catch (Exception ex) {
            if (JNLPRuntime.isDebug())
                ex.printStackTrace();

            throw new RuntimeException(ex.toString());
        }
    }

}
