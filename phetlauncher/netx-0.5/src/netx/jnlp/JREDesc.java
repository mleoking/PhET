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

/**
 * The J2SE element.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class JREDesc {

    /** the platform version or the product version if location is not null */
    private Version version;

    /** the location of a JRE product or null */
    private URL location;

    /** inital heap size */
    private long initialHeapSize;

    /** maximum head size */
    private long maximumHeapSize;

    /** list of ResourceDesc objects */
    private List resources;


    /**
     * Create a JRE descriptor.
     *
     * @param version the platform version or the product version
     * if location is not null
     * @param location the location of a JRE product or null
     * @param initialHeapSize inital heap size
     * @param maximumHeadSize maximum head size
     * @param resources list of ResourceDesc objects
     */
    public JREDesc(Version version, URL location, String initialHeapSize, String maximumHeapSize, List resources) {
        this.version = version;
        this.location = location;
        this.initialHeapSize = heapToLong(initialHeapSize);
        this.maximumHeapSize = heapToLong(maximumHeapSize);
        this.resources = resources;
    }

    /**
     * Returns the JRE version.  Use isPlatformVersion to
     * determine if this version corresponds to a platform or
     * product version.
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Returns true if the JRE version is a Java platform version
     * (java.specification.version property) or false if it is a
     * product version (java.version property).
     */
    public boolean isPlatformVersion() {
        return getLocation() == null;
    }

    /**
     * Returns the JRE version string.
     */
    public URL getLocation() {
        return location;
    }

    /**
     * Returns the maximum heap size in bytes.
     */
    public long getMaximumHeapSize() {
        return maximumHeapSize;
    }

    /**
     * Returns the initial heap size in bytes.
     */
    public long getInitialHeapSize() {
        return initialHeapSize;
    }

    /**
     * Returns the resources defined for this JRE.
     */
    public List getResourcesDesc() {
        return resources;
    }

    /**
     * Convert a heap size description string to a long value
     * indicating the heap min/max size.
     */
    static private long heapToLong(String heapSize) {
        // need to implement for completeness even though not used in netx
        return -1;
    }

}


