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
import java.awt.Dimension;
import java.net.*;
import java.util.*;

/**
 * The resources element.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class ResourcesDesc {

    /** the locales of these resources */
    private Locale locales[];

    /** the OS for these resources */
    private String os[];

    /** the arch for these resources */
    private String arch[];

    /** the JNLPFile this information is for */
    private JNLPFile jnlpFile;

    /** list of jars, packages, properties, and extensions */
    private List resources = new ArrayList(); // mixed list makes easier for lookup code


    /**
     * Create a representation of one information section of the
     * JNLP File.
     *
     * @param jnlpFile JNLP file the resources are for
     * @param locales the locales of these resources
     * @param os the os of these resources
     * @param arch the arch of these resources
     */
    public ResourcesDesc(JNLPFile jnlpFile, Locale locales[], String os[], String arch[]) {
        this.jnlpFile = jnlpFile;
        this.locales = locales;
        this.os = os;
        this.arch = arch;
    }

    /**
     * Returns the JVMs.
     */
    public JREDesc[] getJREs() {
        List resources = getResources(JREDesc.class);
        return (JREDesc[]) resources.toArray( new JREDesc[resources.size()] );
    }

    /**
     * Returns the main JAR for these resources.  There first JAR
     * is returned if no JARs are specified as the main JAR, and if
     * there are no JARs defined then null is returned.
     */
    public JARDesc getMainJAR() {
        JARDesc jars[] = getJARs();

        for (int i=0; i < jars.length; i++)
            if (jars[i].isMain())
                return jars[i];

        if (jars.length > 0)
            return jars[0];
        else 
            return null;
    }

    /**
     * Returns all of the JARs. 
     */
    public JARDesc[] getJARs() {
        List resources = getResources(JARDesc.class);
        return (JARDesc[]) resources.toArray( new JARDesc[resources.size()] );
    }

    /**
     * Returns the JARs with the specified part name. 
     *
     * @param partName the part name, null and "" equivalent
     */
    public JARDesc[] getJARs(String partName) {
        List resources = getResources(JARDesc.class);

        for (int i = resources.size(); i-- > 0;) {
            JARDesc jar = (JARDesc) resources.get(i);

            if (!(""+jar.getPart()).equals(""+partName))
                resources.remove(i);
        }

        return (JARDesc[]) resources.toArray( new JARDesc[resources.size()] );
    }

    /**
     * Returns the Extensions.
     */
    public ExtensionDesc[] getExtensions() {
        List resources = getResources(ExtensionDesc.class);
        return (ExtensionDesc[]) resources.toArray( new ExtensionDesc[resources.size()] );
    }

    /**
     * Returns the Packages.
     */
    public PackageDesc[] getPackages() {
        List resources = getResources(PackageDesc.class);
        return (PackageDesc[]) resources.toArray( new PackageDesc[resources.size()] );
    }

    /**
     * Returns the Packages that match the specified class name.
     *
     * @param className the fully qualified class name
     * @return the PackageDesc objects matching the class name
     */
    public PackageDesc[] getPackages(String className) {
        List resources = getResources(PackageDesc.class);

        for (int i = resources.size(); i-- > 0;) {
            PackageDesc pk = (PackageDesc) resources.get(i);

            if (!pk.matches(className))
                resources.remove(i);
        }

        return (PackageDesc[]) resources.toArray( new PackageDesc[resources.size()] );
    }

    /**
     * Returns the Properties as a list.
     */
    public PropertyDesc[] getProperties() {
        List resources = getResources(PropertyDesc.class);
        return (PropertyDesc[]) resources.toArray( new PropertyDesc[resources.size()] );
    }

    /**
     * Returns the properties as a map.
     */
    public Map getPropertiesMap() {
        Properties properties = new Properties();
        List resources = getResources(PropertyDesc.class);
        for (int i=0; i < resources.size(); i++) {
            PropertyDesc prop = (PropertyDesc) resources.get(i);
            properties.put( prop.getKey(), prop.getValue() );
        }

        return properties;
    }

    /**
     * Returns the os required by these resources, or null if no
     * locale was specified in the JNLP file.
     */
    public String[] getOS() {
        return os;
    }

    /**
     * Returns the architecture required by these resources, or null
     * if no locale was specified in the JNLP file.
     */
    public String[] getArch() {
        return arch;
    }

    /**
     * Returns the locale required by these resources, or null if no
     * locale was specified in the JNLP file.
     */
    public Locale[] getLocales() {
        return locales;
    }

    /**
     * Returns the JNLPFile the resources are for.
     */
    public JNLPFile getJNLPFile() {
        return jnlpFile;
    }

    /**
     * Returns all resources of the specified type.
     */
    public List getResources(Class type) {
        List result = new ArrayList();

        for (int i=0; i < resources.size(); i++)
            if ( type.isAssignableFrom(resources.get(i).getClass()) )
                result.add(resources.get(i));

        return result;
    }

    /**
     * Add a resource.
     */
    public void addResource(Object resource) {
        // if this is going to stay public it should probably take an
        // interface instead of an Object
        if (resource == null)
            throw new IllegalArgumentException("null resource");

        resources.add(resource);
    }

}

