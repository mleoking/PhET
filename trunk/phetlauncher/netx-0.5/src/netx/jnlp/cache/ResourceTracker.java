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
import netx.jnlp.event.DownloadEvent;
import netx.jnlp.event.DownloadListener;
import netx.jnlp.runtime.JNLPRuntime;
import netx.jnlp.util.WeakList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This class tracks the downloading of various resources of a
 * JNLP file to local files in the cache.  It can be used to
 * download icons, jnlp and extension files, jars, and jardiff
 * files using the version based protocol or any file using the
 * basic download protocol (jardiff and version not implemented
 * yet).<p>
 * <p/>
 * The resource tracker can be configured to prefetch resources,
 * which are downloaded in the order added to the media
 * tracker.<p>
 * <p/>
 * Multiple threads are used to download and cache resources that
 * are actively being waited for (blocking a caller) or those that
 * have been started downloading by calling the startDownload
 * method.  Resources that are prefetched are downloaded one at a
 * time and only if no other trackers have requested downloads.
 * This allows the tracker to start downloading many items without
 * using many system resources, but still quickly download items
 * as needed.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class ResourceTracker {

    // todo: use event listener arrays instead of lists

    // todo: see if there is a way to set the socket options just
    // for use by the tracker so checks for updates don't hang for
    // a long time.

    // todo: ability to restart/retry a hung download

    // todo: move resource downloading/processing code into Resource
    // class, threading stays in ResourceTracker

    // todo: get status method? and some way to convey error status
    // to the caller.

    // todo: might make a tracker be able to download more than one
    // version of a resource, but probably not very useful.

    // defines
    //    ResourceTracker.Downloader (download threads)

    // separately locks on (in order of aquire order, ie, sync on prefetch never syncs on lock):
    //   lock, prefetch, this.resources, each resource, listeners

    /**
     * notified on initialization or download of a resource
     */
    private static Object lock = new Integer( 0 ); // used to lock static structures

    // shortcuts
    private static final int UNINITIALIZED = Resource.UNINITIALIZED;
    private static final int CONNECT = Resource.CONNECT;
    private static final int CONNECTING = Resource.CONNECTING;
    private static final int CONNECTED = Resource.CONNECTED;
    private static final int DOWNLOAD = Resource.DOWNLOAD;
    private static final int DOWNLOADING = Resource.DOWNLOADING;
    private static final int DOWNLOADED = Resource.DOWNLOADED;
    private static final int ERROR = Resource.ERROR;
    private static final int STARTED = Resource.STARTED;

    /**
     * max threads
     */
    private static final int maxThreads = 5;

    /**
     * running threads
     */
    private static int threads = 0;

    /**
     * weak list of resource trackers with resources to prefetch
     */
    private static WeakList prefetchTrackers = new WeakList();

    /**
     * resources requested to be downloaded
     */
    private static ArrayList queue = new ArrayList();

    /**
     * resource trackers threads are working for (used for load balancing across multi-tracker downloads)
     */
    private static ArrayList active = new ArrayList(); // 

    /**
     * the resources known about by this resource tracker
     */
    private List resources = new ArrayList();

    /**
     * download listeners for this tracker
     */
    private List listeners = new ArrayList();

    /**
     * whether to download parts before requested
     */
    private boolean prefetch;


    /**
     * Creates a resource tracker that does not prefetch resources.
     */
    public ResourceTracker() {
        this( false );
    }

    /**
     * Creates a resource tracker.
     *
     * @param prefetch whether to download resources before requested.
     */
    public ResourceTracker( boolean prefetch ) {
        this.prefetch = prefetch;

        if( prefetch ) {
            synchronized( prefetchTrackers ) {
                prefetchTrackers.add( this );
                prefetchTrackers.trimToSize();
            }
        }
    }

    /**
     * Add a resource identified by the specified location and
     * version.  The tracker only downloads one version of a given
     * resource per instance (ie cannot download both versions 1 and
     * 2 of a resource in the same tracker).
     *
     * @param location     the location of the resource
     * @param version      the resource version
     * @param updatePolicy whether to check for updates if already in cache
     */
    public void addResource( URL location, Version version, UpdatePolicy updatePolicy ) {
        if( location == null ) {
            throw new IllegalArgumentException( "location==null" );
        }

        Resource resource = Resource.getResource( location, version );
        boolean downloaded = false;

        synchronized( resources ) {
            if( resources.contains( resource ) ) {
                return;
            }
            resource.addTracker( this );
            resources.add( resource );
        }

        // checkCache may take a while (loads properties file).  this
        // should really be synchronized on resources, but the worst
        // case should be that the resource will be updated once even
        // if unnecessary.
        downloaded = checkCache( resource, updatePolicy );

        synchronized( lock ) {
            if( !downloaded ) {
                if( prefetch && threads == 0 ) // existing threads do pre-fetch when queue empty
                {
                    startThread();
                }
            }
        }
    }

    /**
     * Removes a resource from the tracker.  This method is useful
     * to allow memory to be reclaimed, but calling this method is
     * not required as resources are reclaimed when the tracker is
     * collected.
     *
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public void removeResource( URL location ) {
        synchronized( resources ) {
            Resource resource = getResource( location );

            if( resource != null ) {
                resources.remove( resource );
                resource.removeTracker( this );
            }

            // should remove from queue? probably doesn't matter
        }
    }

    /**
     * Check the cache for a resource, and initialize the resource
     * as already downloaded if found. <p>
     *
     * @param updatePolicy whether to check for updates if already in cache
     * @return whether the resource are already downloaded
     */
    private boolean checkCache( Resource resource, UpdatePolicy updatePolicy ) {
        if( !CacheUtil.isCacheable( resource.location, resource.downloadVersion ) ) {
            // pretend that they are already downloaded; essentially
            // they will just 'pass through' the tracker as if they were
            // never added (for example, not affecting the total download size).
            synchronized( resource ) {
                resource.changeStatus( 0, DOWNLOADED | CONNECTED | STARTED );
            }
            fireDownloadEvent( resource );
            return true;
        }

        if( updatePolicy != UpdatePolicy.ALWAYS ) { // save loading entry props file
            CacheEntry entry = new CacheEntry( resource.location, resource.downloadVersion );

            if( entry.isCached() && !updatePolicy.shouldUpdate( entry ) ) {
                if( JNLPRuntime.isDebug() ) {
                    System.out.println( "not updating: " + resource.location );
                }

                synchronized( resource ) {
                    resource.localFile = CacheUtil.getCacheFile( resource.location, resource.downloadVersion );
                    resource.size = resource.localFile.length();
                    resource.transferred = resource.localFile.length();
                    resource.changeStatus( 0, DOWNLOADED | CONNECTED | STARTED );
                }
                fireDownloadEvent( resource );
                return true;
            }
        }

        // may or may not be cached, but check update when connection
        // is open to possibly save network communication time if it
        // has to be downloaded, and allow this call to return quickly
        return false;
    }

    /**
     * Adds the listener to the list of objects interested in
     * receivind DownloadEvents.<p>
     *
     * @param location the resource to add a callback for
     * @param runnable the runnable to call when resource is completed
     */
    public void addDownloadListener( DownloadListener listener ) {
        synchronized( listeners ) {
            if( !listeners.contains( listener ) ) {
                listeners.add( listener );
            }
        }
    }

    /**
     * Removes a download listener.
     */
    public void removeDownloadListener( DownloadListener listener ) {
        synchronized( listeners ) {
            listeners.remove( listener );
        }
    }

    /**
     * Fires the download event corresponding to the resource's
     * state.  This method is typicall called by the Resource itself
     * on each tracker that is monitoring the resource.  Do not call
     * this method with any locks because the listeners may call
     * back to this ResourceTracker.
     */
    protected void fireDownloadEvent( Resource resource ) {
        DownloadListener l[] = null;
        synchronized( listeners ) {
            l = (DownloadListener[])listeners.toArray( new DownloadListener[0] );
        }

        int status;
        synchronized( resource ) {
            status = resource.status;
        }

        DownloadEvent event = new DownloadEvent( this, resource );
        for( int i = 0; i < l.length; i++ ) {
            if( 0 != ( ( ERROR | DOWNLOADED ) & status ) ) {
                l[i].downloadCompleted( event );
            }
            else if( 0 != ( DOWNLOADING & status ) ) {
                l[i].downloadStarted( event );
            }
            else if( 0 != ( CONNECTING & status ) ) {
                l[i].updateStarted( event );
            }
        }
    }

    /**
     * Returns a URL pointing to the cached location of the
     * resource, or the resource itself if it is a non-cacheable
     * resource.<p>
     * <p/>
     * If the resource has not downloaded yet, the method will block
     * until it has been transferred to the cache.<p>
     *
     * @param location the resource location
     * @return the resource, or null if it could not be downloaded
     * @throws IllegalArgumentException if the resource is not being tracked
     * @see CacheUtil#isCacheable
     */
    public URL getCacheURL( URL location ) {
        try {
            File f = getCacheFile( location );
            if( f != null ) {
                return f.toURL();
            }
        }
        catch( MalformedURLException ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }
        }

        return location;
    }

    /**
     * Returns a file containing the downloaded resource.  If the
     * resource is non-cacheable then null is returned unless the
     * resource is a local file (the original file is returned).<p>
     * <p/>
     * If the resource has not downloaded yet, the method will block
     * until it has been transferred to the cache.<p>
     *
     * @param location the resource location
     * @return a local file containing the resource, or null
     * @throws IllegalArgumentException if the resource is not being tracked
     * @see CacheUtil#isCacheable
     */
    public File getCacheFile( URL location ) {
        try {
            Resource resource = getResource( location );
            if( !resource.isSet( DOWNLOADED | ERROR ) ) {
                waitForResource( location, 0 );
            }

            if( resource.isSet( ERROR ) ) {
                return null;
            }

            if( resource.localFile != null ) {
                return resource.localFile;
            }

            if( location.getProtocol().equalsIgnoreCase( "file" ) ) {
                File file = new File( location.getFile() );
                if( file.exists() ) {
                    return file;
                }
            }

            return null;
        }
        catch( InterruptedException ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }

            return null; // need an error exception to throw
        }
    }

    /**
     * Returns an input stream that reads the contents of the
     * resource.  For non-cacheable resources, an InputStream that
     * reads from the source location is returned.  Otherwise the
     * InputStream reads the cached resource.<p>
     * <p/>
     * This method will block while the resource is downloaded to
     * the cache.
     *
     * @throws IOException              if there was an error opening the stream
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public InputStream getInputStream( URL location ) throws IOException {
        try {
            Resource resource = getResource( location );
            if( !resource.isSet( DOWNLOADED | ERROR ) ) {
                waitForResource( location, 0 );
            }

            if( resource.localFile != null ) {
                return new FileInputStream( resource.localFile );
            }

            return resource.location.openStream();
        }
        catch( InterruptedException ex ) {
            throw new IOException( "wait was interrupted" );
        }
    }

    /**
     * Wait for a group of resources to be downloaded and made
     * available locally.
     *
     * @param urls    the resources to wait for
     * @param timeout the time in ms to wait before returning, 0 for no timeout
     * @return whether the resources downloaded before the timeout
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public boolean waitForResources( URL urls[], long timeout ) throws InterruptedException {
        Resource resources[] = new Resource[ urls.length ];

        synchronized( resources ) {
            // keep the lock so getResource doesn't have to aquire it each time
            for( int i = 0; i < urls.length; i++ ) {
                resources[i] = getResource( urls[i] );
            }
        }

        if( resources.length > 0 ) {
            return wait( resources, timeout );
        }

        return true;
    }

    /**
     * Wait for a particular resource to be downloaded and made
     * available.
     *
     * @param location the resource to wait for
     * @param timeout  the timeout, or 0 to wait until completed
     * @return whether the resource downloaded before the timeout
     * @throws InterruptedException     if another thread interrupted the wait
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public boolean waitForResource( URL location, long timeout ) throws InterruptedException {
        return wait( new Resource[]{getResource( location )}, timeout );
    }

    /**
     * Returns the number of bytes downloaded for a resource.
     *
     * @param location the resource location
     * @return the number of bytes transferred
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public long getAmountRead( URL location ) {
        // not atomic b/c transferred is a long, but so what (each
        // byte atomic? so probably won't affect anything...)
        return getResource( location ).transferred;
    }

    /**
     * Returns whether a resource is available for use (ie, can be
     * accessed with the getCacheFile method).
     *
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public boolean checkResource( URL location ) {
        return getResource( location ).isSet( DOWNLOADED | ERROR ); // isSet atomic
    }

    /**
     * Starts loading the resource if it is not already being
     * downloaded or already cached.  Resources started downloading
     * using this method may download faster than those prefetched
     * by the tracker because the tracker will only prefetch one
     * resource at a time to conserve system resources.
     *
     * @return true if the resource is already downloaded (or an error occurred)
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public boolean startResource( URL location ) {
        Resource resource = getResource( location );

        return startResource( resource );
    }

    /**
     * Sets the resource status to connect and download, and
     * enqueues the resource if not already started.
     *
     * @return true if the resource is already downloaded (or an error occurred)
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    private boolean startResource( Resource resource ) {
        boolean enqueue = false;

        synchronized( resource ) {
            if( resource.isSet( ERROR ) ) {
                return true;
            }

            enqueue = !resource.isSet( STARTED );

            if( !resource.isSet( CONNECTED | CONNECTING ) ) {
                resource.changeStatus( 0, CONNECT | STARTED );
            }
            if( !resource.isSet( DOWNLOADED | DOWNLOADING ) ) {
                resource.changeStatus( 0, DOWNLOAD | STARTED );
            }

            if( !resource.isSet( DOWNLOAD | CONNECT ) ) {
                enqueue = false;
            }
        }

        if( enqueue ) {
            queueResource( resource );
        }

        return !enqueue;
    }

    /**
     * Returns the number of total size in bytes of a resource, or
     * -1 it the size is not known.
     *
     * @param location the resource location
     * @return the number of bytes, or -1
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    public long getTotalSize( URL location ) {
        return getResource( location ).size; // atomic
    }

    /**
     * Start a new download thread if there are not too many threads
     * already running.<p>
     * <p/>
     * Calls to this method should be synchronized on lock.
     */
    protected void startThread() {
        if( threads < maxThreads ) {
            threads++;

            Thread thread = new Thread( new Downloader() );
            thread.start();
        }
    }

    /**
     * A thread is ending, called by the thread itself.<p>
     * <p/>
     * Calls to this method should be synchronized.
     */
    private void endThread() {
        threads--;

        if( threads < 0 ) {
            // this should never happen but try to recover
            threads = 0;

            if( queue.size() > 0 ) // if any on queue make sure a thread is running
            {
                startThread(); // look into whether this could create a loop
            }

            throw new RuntimeException( "tracker threads < 0" );
        }

        if( threads == 0 ) {
            synchronized( prefetchTrackers ) {
                queue.trimToSize(); // these only accessed by threads so no sync needed
                active.clear(); // no threads so no trackers actively downloading
                active.trimToSize();
                prefetchTrackers.trimToSize();
            }
        }
    }

    /**
     * Add a resource to the queue and start a thread to download or
     * initialize it.
     */
    private void queueResource( Resource resource ) {
        synchronized( lock ) {
            if( !resource.isSet( CONNECT | DOWNLOAD ) ) {
                throw new IllegalArgumentException( "Invalid resource state (resource: " + resource + ")" );
            }

            queue.add( resource );
            startThread();
        }
    }

    /**
     * Process the resource by either downloading it or initializing
     * it.
     */
    private void processResource( Resource resource ) {
        boolean doConnect = false;
        boolean doDownload = false;

        synchronized( resource ) {
            if( resource.isSet( CONNECTING ) ) {
                doConnect = true;
            }
        }
        if( doConnect ) {
            initializeResource( resource );
        }

        synchronized( resource ) {
            // return to queue if we just initalized but it still needs
            // to download (not cached locally / out of date)
            if( resource.isSet( DOWNLOAD ) ) // would be DOWNLOADING if connected before this method
            {
                queueResource( resource );
            }

            if( resource.isSet( DOWNLOADING ) ) {
                doDownload = true;
            }
        }
        if( doDownload ) {
            downloadResource( resource );
        }
    }

    /**
     * Downloads an resource to a file.
     */
    private void downloadResource( Resource resource ) {
        resource.fireDownloadEvent(); // fire DOWNLOADING

        try {
            // create out second in case in does not exist
            InputStream in = new BufferedInputStream( resource.connection.getInputStream() );
            OutputStream out = CacheUtil.getOutputStream( resource.location, resource.downloadVersion );
            byte buf[] = new byte[1024];
            int rlen;

            while( -1 != ( rlen = in.read( buf ) ) ) {
                resource.transferred += rlen;
                out.write( buf, 0, rlen );
            }

            in.close();
            out.close();

            resource.changeStatus( DOWNLOADING, DOWNLOADED );
            synchronized( lock ) {
                lock.notifyAll(); // wake up wait's to check for completion
            }
            resource.fireDownloadEvent(); // fire DOWNLOADED
        }
        catch( Exception ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }

            resource.changeStatus( 0, ERROR );
            synchronized( lock ) {
                lock.notifyAll(); // wake up wait's to check for completion
            }
            resource.fireDownloadEvent(); // fire ERROR
        }
    }

    /**
     * Open a URL connection and get the content length and other
     * fields.
     */
    private void initializeResource( Resource resource ) {
        resource.fireDownloadEvent(); // fire CONNECTING

        try {
            File localFile = CacheUtil.getCacheFile( resource.location, resource.downloadVersion );

            // connect
            URLConnection connection = resource.location.openConnection(); // this won't change so should be okay unsynchronized
            int size = connection.getContentLength();
            boolean current = CacheUtil.isCurrent( resource.location, resource.requestVersion, connection );

            synchronized( resource ) {
                resource.localFile = localFile;
                resource.connection = connection;
                resource.size = size;
                resource.changeStatus( CONNECT | CONNECTING, CONNECTED );

                // check if up-to-date; if so set as downloaded
                if( current ) {
                    resource.changeStatus( DOWNLOAD | DOWNLOADING, DOWNLOADED );
                }
            }

            // update cache entry
            CacheEntry entry = new CacheEntry( resource.location, resource.requestVersion );
            if( !current ) {
                entry.initialize( connection );
            }

            entry.setLastUpdated( System.currentTimeMillis() );
            entry.store();

            synchronized( lock ) {
                lock.notifyAll(); // wake up wait's to check for completion
            }
            resource.fireDownloadEvent(); // fire CONNECTED
        }
        catch( Exception ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }

            resource.changeStatus( 0, ERROR );
            synchronized( lock ) {
                lock.notifyAll(); // wake up wait's to check for completion
            }
            resource.fireDownloadEvent(); // fire ERROR
        }
    }

    /**
     * Pick the next resource to download or initialize.  If there
     * are no more resources requested then one is taken from a
     * resource tracker with prefetch enabled.<p>
     * <p/>
     * The resource state is advanced before it is returned
     * (CONNECT-&gt;CONNECTING).<p>
     * <p/>
     * Calls to this method should be synchronized on lock.<p>
     *
     * @return the resource to initialize or download, or null
     */
    private static Resource selectNextResource() {
        Resource result;

        // pick from queue
        result = selectByFlag( queue, CONNECT, ERROR ); // connect but not error
        if( result == null ) {
            result = selectByFlag( queue, DOWNLOAD, ERROR | CONNECT | CONNECTING );
        }

        // remove from queue if found
        if( result != null ) {
            queue.remove( result );
        }

        // prefetch if nothing found so far and this is the last thread
        if( result == null && threads == 1 ) {
            result = getPrefetch();
        }

        if( result == null ) {
            return null;
        }

        synchronized( result ) {
            if( result.isSet( CONNECT ) ) {
                result.changeStatus( CONNECT, CONNECTING );
            }
            else if( result.isSet( DOWNLOAD ) ) {
                // only download if *not* connecting, when done connecting
                // select next will pick up the download part.  This makes
                // all requested connects happen before any downloads, so
                // the size is known as early as possible.
                result.changeStatus( DOWNLOAD, DOWNLOADING );
            }
        }

        return result;
    }

    /**
     * Returns the next resource to be prefetched before
     * requested.<p>
     * <p/>
     * Calls to this method should be synchronized on lock.<p>
     */
    private static Resource getPrefetch() {
        Resource result = null;
        Resource alternate = null;

        // first find one to initialize 
        synchronized( prefetchTrackers ) {
            for( int i = 0; i < prefetchTrackers.size() && result == null; i++ ) {
                ResourceTracker tracker = (ResourceTracker)prefetchTrackers.get( i );
                if( tracker == null ) {
                    continue;
                }

                synchronized( tracker.resources ) {
                    result = selectByFlag( tracker.resources, UNINITIALIZED, ERROR );

                    if( result == null && alternate == null ) {
                        alternate = selectByFlag( tracker.resources, CONNECTED, ERROR | DOWNLOADED | DOWNLOADING | DOWNLOAD );
                    }
                }
            }
        }

        // if none to initialize, switch to download
        if( result == null ) {
            result = alternate;
        }

        if( result == null ) {
            return null;
        }

        synchronized( result ) {
            ResourceTracker tracker = result.getTracker();
            if( tracker == null ) {
                return null; // GC of tracker happened between above code and here
            }

            // prevents startResource from putting it on queue since
            // we're going to return it.
            result.changeStatus( 0, STARTED );

            tracker.startResource( result );
        }

        return result;
    }

    /**
     * Selects a resource from the source list that has the
     * specified flag set.<p>
     * <p/>
     * Calls to this method should be synchronized on lock and
     * source list.<p>
     */
    private static Resource selectByFlag( List source, int flag, int notflag ) {
        Resource result = null;
        int score = Integer.MAX_VALUE;

        for( int i = 0; i < source.size(); i++ ) {
            Resource resource = (Resource)source.get( i );
            boolean selectable = false;

            synchronized( resource ) {
                if( resource.isSet( flag ) && !resource.isSet( notflag ) ) {
                    selectable = true;
                }
            }

            if( selectable ) {
                int activeCount = 0;

                for( int j = 0; j < active.size(); j++ ) {
                    if( (ResourceTracker)active.get( j ) == resource.getTracker() ) {
                        activeCount++;
                    }
                }

                // try to spread out the downloads so that a slow host
                // won't monopolize the downloads
                if( activeCount < score ) {
                    result = resource;
                    score = activeCount;
                }
            }
        }

        return result;
    }

    /**
     * Return the resource matching the specified URL.
     *
     * @throws IllegalArgumentException if the resource is not being tracked
     */
    private Resource getResource( URL location ) {
        synchronized( resources ) {
            for( int i = 0; i < resources.size(); i++ ) {
                Resource resource = (Resource)resources.get( i );

                if( CacheUtil.urlEquals( resource.location, location ) ) {
                    return resource;
                }
            }
        }

        throw new IllegalArgumentException( "Location does not specify a resource being tracked." );
    }

    /**
     * Wait for some resources.
     *
     * @param resources the resources to wait for
     * @param timeout   the timeout, or 0 to wait until completed
     * @throws InterruptedException if another thread interrupted the wait
     * @returns true if the resources were downloaded or had errors,
     * false if the timeout was reached
     */
    private boolean wait( Resource resources[], long timeout ) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        // start them downloading / connecting in background
        for( int i = 0; i < resources.length; i++ ) {
            startResource( resources[i] );
        }

        // wait for completion
        while( true ) {
            boolean finished = true;

            synchronized( lock ) {
                // check for completion 
                for( int i = 0; i < resources.length; i++ ) {
                    synchronized( resources[i] ) {
                        if( !resources[i].isSet( DOWNLOADED | ERROR ) ) {
                            finished = false;
                            break;
                        }
                    }
                }
                if( finished ) {
                    return true;
                }

                // wait
                long waitTime = 0;

                if( timeout > 0 ) {
                    waitTime = timeout - ( System.currentTimeMillis() - startTime );
                    if( waitTime <= 0 ) {
                        return false;
                    }
                }

                lock.wait( waitTime );
            }
        }
    }

    // inner classes

    /**
     * This class downloads and initializes the queued resources.
     */
    class Downloader implements Runnable {
        Resource resource = null;

        public void run() {
            while( true ) {
                synchronized( lock ) {
                    // remove from active list, used for load balancing
                    if( resource != null ) {
                        active.remove( resource.getTracker() );
                    }

                    resource = selectNextResource();

                    if( resource == null ) {
                        endThread();
                        break;
                    }

                    // add to active list, used for load balancing
                    active.add( resource.getTracker() );
                }

                try {
                    processResource( resource );
                }
                catch( Exception ex ) {
                    if( JNLPRuntime.isDebug() ) {
                        ex.printStackTrace();
                    }
                }
            }
            // should have a finally in case some exception is thrown by
            // selectNextResource();
        }
    }

    ;

}


