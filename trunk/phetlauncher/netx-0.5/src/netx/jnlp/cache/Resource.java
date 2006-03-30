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


package netx.jnlp.cache;

import netx.jnlp.Version;
import netx.jnlp.runtime.JNLPRuntime;
import netx.jnlp.util.WeakList;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Information about a single resource to download.
 * This class tracks the downloading of various resources of a
 * JNLP file to local files.  It can be used to download icons,
 * jnlp and extension files, jars, and jardiff files using the
 * version based protocol or any file using the basic download
 * protocol.<p>
 * <p/>
 * Resources can be put into download groups by specifying a part
 * name for the resource.  The resource tracker can also be
 * configured to prefetch resources, which are downloaded in the
 * order added to the media tracker.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class Resource {

    // todo: fix resources to handle different versions

    // todo: IIRC, any resource is checked for being up-to-date
    // only once, regardless of UpdatePolicy.  verify and fix.

    /**
     * status bits
     */
    public static final int UNINITIALIZED = 0;
    public static final int CONNECT = 1;
    public static final int CONNECTING = 2;
    public static final int CONNECTED = 4;
    public static final int DOWNLOAD = 8;
    public static final int DOWNLOADING = 16;
    public static final int DOWNLOADED = 32;
    public static final int ERROR = 64;
    public static final int STARTED = 128; // enqueued or being worked on

    /**
     * list of weak references of resources currently in use
     */
    private static WeakList resources = new WeakList();

    /**
     * weak list of trackers monitoring this resource
     */
    private WeakList trackers = new WeakList();

    /**
     * the remote location of the resource
     */
    URL location;

    /**
     * the local file downloaded to
     */
    File localFile;

    /**
     * the requested version
     */
    Version requestVersion;

    /**
     * the version downloaded from server
     */
    Version downloadVersion;

    /**
     * connection to resource
     */
    URLConnection connection;

    /**
     * amount in bytes transferred
     */
    long transferred = 0;

    /**
     * total size of the resource, or -1 if unknown
     */
    long size = -1;

    /**
     * the status of the resource
     */
    int status = UNINITIALIZED;


    /**
     * Create a resource.
     */
    private Resource( URL location, Version requestVersion ) {
        this.location = location;
        this.requestVersion = requestVersion;
    }

    /**
     * Return a shared Resource object representing the given
     * location and version.
     */
    public static Resource getResource( URL location, Version requestVersion ) {
        synchronized( resources ) {
            Resource resource = new Resource( location, requestVersion );

            int index = resources.indexOf( resource );
            if( index >= 0 ) { // return existing object
                Resource result = (Resource)resources.get( index );
                if( result != null ) {
                    return result;
                }
            }

            resources.add( resource );
            resources.trimToSize();

            return resource;
        }
    }

    /**
     * Returns the remote location of the resource.
     */
    public URL getLocation() {
        return location;
    }

    /**
     * Returns the tracker that first created or monitored the
     * resource, or null if no trackers are monitoring the resource.
     */
    ResourceTracker getTracker() {
        synchronized( trackers ) {
            List t = trackers.hardList();
            if( t.size() > 0 ) {
                return (ResourceTracker)t.get( 0 );
            }

            return null;
        }
    }

    /**
     * Returns true if any of the specified flags are set.
     */
    public boolean isSet( int flag ) {
        if( flag == UNINITIALIZED ) {
            return status == UNINITIALIZED;
        }
        else {
            return ( status & flag ) != 0;
        }
    }

    /**
     * Returns a human-readable status string.
     */
    private String getStatusString( int flag ) {
        StringBuffer result = new StringBuffer();

        if( flag == 0 ) {
            result.append( "<> " );
        }
        if( ( flag & CONNECT ) != 0 ) {
            result.append( "CONNECT " );
        }
        if( ( flag & CONNECTING ) != 0 ) {
            result.append( "CONNECTING " );
        }
        if( ( flag & CONNECTED ) != 0 ) {
            result.append( "CONNECTED " );
        }
        if( ( flag & DOWNLOAD ) != 0 ) {
            result.append( "DOWNLOAD " );
        }
        if( ( flag & DOWNLOADING ) != 0 ) {
            result.append( "DOWNLOADING " );
        }
        if( ( flag & DOWNLOADED ) != 0 ) {
            result.append( "DOWNLOADED " );
        }
        if( ( flag & ERROR ) != 0 ) {
            result.append( "ERROR " );
        }
        if( ( flag & STARTED ) != 0 ) {
            result.append( "STARTED " );
        }

        return result.deleteCharAt( result.length() - 1 ).toString();
    }

    /**
     * Changes the status by clearing the flags in the first
     * parameter and setting the flags in the second.  This method
     * is synchronized on this resource.
     */
    public void changeStatus( int clear, int add ) {
        int orig = 0;

        synchronized( this ) {
            orig = status;

            this.status &= ~clear;
            this.status |= add;
        }

        if( JNLPRuntime.isDebug() ) {
            if( status != orig ) {
                System.out.print( "Status: " + getStatusString( status ) );
                if( ( status & ~orig ) != 0 ) {
                    System.out.print( " +(" + getStatusString( status & ~orig ) + ")" );
                }
                if( ( ~status & orig ) != 0 ) {
                    System.out.print( " -(" + getStatusString( ~status & orig ) + ")" );
                }
                System.out.println( " @ " + location.getPath() );
            }
        }
    }

    /**
     * Removes the tracker to the list of trackers monitoring this
     * resource.
     */
    public void removeTracker( ResourceTracker tracker ) {
        synchronized( trackers ) {
            trackers.remove( tracker );
            trackers.trimToSize();
        }
    }

    /**
     * Adds the tracker to the list of trackers monitoring this
     * resource.
     */
    public void addTracker( ResourceTracker tracker ) {
        synchronized( trackers ) {
            List t = trackers.hardList(); // prevent GC between contains and add
            if( !t.contains( tracker ) ) {
                trackers.add( tracker );
            }

            trackers.trimToSize();
        }
    }

    /**
     * Instructs the trackers monitoring this resource to fire a
     * download event.
     */
    protected void fireDownloadEvent() {
        List send;

        synchronized( trackers ) {
            send = trackers.hardList();
        }

        for( int i = 0; i < send.size(); i++ ) {
            ResourceTracker rt = (ResourceTracker)send.get( i );
            rt.fireDownloadEvent( this );
        }
    }

    public boolean equals( Object other ) {
        if( other instanceof Resource ) {
            // this prevents the URL handler from looking up the IP
            // address and doing name resolution; much faster so less
            // time spent in synchronized addResource determining if
            // Resource is already in a tracker, and better for offline
            // mode on some OS.
            return CacheUtil.urlEquals( location, ( (Resource)other ).location );
        }
        return false;
    }

    public String toString() {
        return "location=" + location.toString() + " state=" + getStatusString( status );
    }

}


