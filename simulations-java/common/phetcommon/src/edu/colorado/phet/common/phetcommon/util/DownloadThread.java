/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Handles a batch of download requests.
 * The downloads are performed in a separate thread.
 * Notification of progress is provided via a listener interface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DownloadThread extends Thread {

    //----------------------------------------------------------------------------
    // Data structures
    //----------------------------------------------------------------------------
    
    /*
     * Encapsulates the info about one download request.
     */
    private static class DownloadRequest {
        
        private final String requestName; // name visible to the user
        private final String sourceURL; // download from this URL
        private final File destinationFile; // download to this File
        private final int expectedContentLength; // actual content length may differ
        
        public DownloadRequest( String requestName, String sourceURL, File destinationFile, int expectedContentLength ) {
            this.requestName = requestName;
            this.sourceURL = sourceURL;
            this.destinationFile = destinationFile;
            this.expectedContentLength = expectedContentLength;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList requests; // array of DownloadRequest
    private final ArrayList listeners; // array of DownloadListener
    
    private int totalContentLength; // bytes
    private int downloadedContentLength; // bytes
    private boolean canceled;
    private boolean succeeded;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DownloadThread() {
        requests = new ArrayList();
        listeners = new ArrayList();
        totalContentLength = 0;
        downloadedContentLength = 0;
        canceled = false;
        succeeded = false;
    }

    /**
     * Constructor that adds one download request.
     * 
     * @param requestName
     * @param sourceURL
     * @param destinationFileName
     * @throws IOException
     */
    public DownloadThread( String requestName, String sourceURL, String destinationFileName ) throws IOException {
        this( requestName, sourceURL, new File( destinationFileName) );
    }
    
    /**
     * Constructor that adds one download request.
     * 
     * @param requestName
     * @param sourceURL
     * @param destinationFile
     * @throws IOException
     */
    public DownloadThread( String requestName, String sourceURL, File destinationFile ) throws IOException {
        this();
        addRequest( requestName, sourceURL, destinationFile );
    }
   
    //----------------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------------
    
    /**
     * Performs a batch download.
     * Do not call this method directly, call start().
     */
    public void run() {
        try {
            download();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the total content length of requests in the batch.
     * <p>
     * This is the expected length at the time the requests were added.
     * This values is adjusted as the batch is processed.
     * The actual length may be different if the source content changed
     * between the time that the request was added and download() was called.
     * So this value may differ from getDownloadedContentLength.
     * 
     * @return bytes
     */
    public int getTotalContentLength() {
        return totalContentLength;
    }
    
    /**
     * Gets the number of bytes actually downloaded so far.
     * 
     * @return bytes
     */
    public int getDownloadedContentLength() {
        return downloadedContentLength;
    }
    
    /**
     * Adds a download request. 
     * No downloads are performed until start is called.
     * 
     * @param requestName
     * @param sourceURL
     * @param destinationFileName
     * @throws IOException
     * @throws IllegalStateException
     */
    public void addRequest( String requestName, String sourceURL, String destinationFileName ) throws IOException {
        addRequest( requestName, sourceURL, new File( destinationFileName ) );
    }

    public void addRequest( String requestName, String sourceURL, File destinationFile ) throws IOException {
        
        if ( isAlive() ) {
            throw new IllegalStateException( "cannot add downloads while thread is alive" );
        }
        
        /*
         * Get the expected content length. The advantages of doing this when the 
         * download request is added are (1) the client can inquire about the total
         * content length before starting the download, and (2) we have a total that 
         * lets us provide progress information.
         * 
         * This requires us to open the URL connection twice (once here, once when 
         * we do the download).  We could keep the URL connection open, but we don't
         * know how long it will be before download() is called, and we don't know 
         * how many download requests the client will be adding.  We don't want to
         * have a large number of connections open for a long period of time.
         * So this seems like a nice tradeoff.
         */
        URLConnection urlConnection = new URL( sourceURL ).openConnection();
        final int expectedContentLength = urlConnection.getContentLength();
        totalContentLength += expectedContentLength;
        
        // create the download spec
        DownloadRequest request = new DownloadRequest( requestName, sourceURL, destinationFile, expectedContentLength );
        requests.add( request );
        
        notifyRequestAdded( request );
    }
    
    
    /**
     * Cancels a batch download if one is in progress.
     * If no download is in progress, this does nothing.
     * It is the client's responsibility to remove any files 
     * that many have been downloaded prior to canceling.
     */
    public void cancel() {
        canceled = true;
    }
    
    /**
     * Did the entire batch of download requests succeed?
     * @return
     */
    public boolean getSucceeded() {
        return succeeded;
    }
    
    /**
     * Clears the batch.
     */
    public void clear() {
        if ( isAlive() ) {
            throw new IllegalStateException( "cannot clear the batch while thread is alive" );
        }
        requests.clear();
        totalContentLength = 0;
    }
    
    //----------------------------------------------------------------------------
    // Downloading
    //----------------------------------------------------------------------------
    
    /*
     * Performs all download requests.
     * Stops when canceled, or when an error is encountered.
     * 
     * @throws IOException
     */
    private void download() throws IOException {
        
        downloadedContentLength = 0;
        canceled = false;
        
        // perform the downloads
        ArrayList requestsCopy = new ArrayList( requests ); // use a copy to avoid ConcurrentModificationException
        Iterator i = requestsCopy.iterator();
        while ( i.hasNext() && !canceled) {
            try {
                download( (DownloadRequest) i.next() );
            }
            catch ( IOException e ) {
                notifyFailed();
                throw e;
            }
        }
        
        if ( canceled ) {
            notifyCanceled();
        }
        else {
            succeeded = true;
            notifySucceeded();
        }
    }
    
    /*
     * Performs one download request.
     */
    private void download( DownloadRequest request ) throws IOException {
        
        try {
            // create the output file
            request.destinationFile.getParentFile().mkdirs();
            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( request.destinationFile ) );
            
            // create the input connection
            URLConnection urlConnection = new URL( request.sourceURL ).openConnection();
            
            // adjust the content length if necessary
            final int contentLength = urlConnection.getContentLength();
            totalContentLength += ( contentLength - request.expectedContentLength );
            
            // read from the input connection, write to the output file, notify of progress
            int subtotalBytesRead = 0;
            InputStream inputStream = urlConnection.getInputStream();
            byte[] data = new byte[2048];
            int bytesRead = 0;
            boolean done = false;
            while ( !done && !canceled ) {
                bytesRead = inputStream.read( data );
                if ( bytesRead != -1 ) {
                    outputStream.write( data, 0, bytesRead );
                    subtotalBytesRead += bytesRead;
                    downloadedContentLength += bytesRead; // record bytes read
                    final double percentOfSource = subtotalBytesRead / (double)contentLength;
                    final double percentOfTotal = downloadedContentLength / (double)totalContentLength;
                    notifyProgress( request, percentOfSource, percentOfTotal );
                    yield(); // allows other threads to execute
                }
                else {
                    done = true;
                }
            }
            
            // clean up
            inputStream.close();
            outputStream.close();
            
            // notify that we're done with this download
            if ( !canceled ) {
                notifyCompleted( request );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
            notifyError( request, e.getMessage(), e );
            throw e;
        }
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public void addListener( DownloadThreadListener listener ) {
        listeners.add( listener );
    }
    
    public void removeListener( DownloadThreadListener listener ) {
        listeners.remove( listener );
    }
    
    public void removeAllListeners() {
        listeners.clear();
    }
    
    /**
     * Interface implemented by all listeners who are interested in download progress.
     */
    public interface DownloadThreadListener {
        
        /**
         * Indicates that the batch download succeeded.
         */
        public void succeeded();
        
        /**
         * Indicates that the batch download failed.
         */
        public void failed();
        
        /**
         * Indicates that the batch download was canceled.
         */
        public void canceled();
        
        /**
         * Indicates that a download request has been added to the batch.
         * 
         * @param sourceURL
         * @param destinationFile
         */
        public void requestAdded( String requestName, String sourceURL, File destinationFile );
        
        /**
         * Indicates the progress made on one request in the batch.
         * 
         * @param sourceURL
         * @param destinationFile
         * @param percentOfSource percent of sourceURL that has been downloaded so far
         * @param percentOfTotal percent of all download requests that have been downloaded so far
         */
        public void progress( String requestName, String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal );
        
        /**
         * Indicates that one request in the batch has been successfully completed.
         * 
         * @param sourceURL
         * @param destinationFile
         */
        public void completed( String requestName, String sourceURL, File destinationFile );
        
        /**
         * Indicates that an error was encountered for some request in the batch.
         * Downloading stops when this notification is sent.
         * 
         * @param sourceURL
         * @param destinationFile
         * @param message
         * @param e
         */
        public void error( String requestName, String sourceURL, File destinationFile, String message, Exception e );
    }

    /*
     * Adapter, provides a no-op implementation.
     */
    public static class DownloadThreadAdapter implements DownloadThreadListener {
        public void succeeded() {}
        public void failed() {}
        public void canceled() {}
        public void requestAdded( String requestName, String sourceURL, File destinationFile ) {}
        public void progress( String requestName, String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {}
        public void completed( String requestName, String sourceURL, File destinationFile ) {}
        public void error( String requestName, String sourceURL, File destinationFile, String message, Exception e ) {}
    }
    
    /*
     * Debug implementation, prints method calls and args to System.out.
     */
    public static class DebugDownloadThreadListener implements DownloadThreadListener {
        public void succeeded() {
            System.out.println( "DebugDownloadThreadListener.succeeded" );
        }
        public void failed() {
            System.out.println( "DebugDownloadThreadListener.failed" );
        }
        public void canceled() {
            System.out.println( "DebugDownloadThreadListener.canceled" );
        }
        public void requestAdded( String requestName, String sourceURL, File destinationFile ) {
            System.out.println( "DebugDownloadThreadListener.requestAdded" +
                     " requestName=" + requestName + " sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() );
        }
        public void progress( String requestName, String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {
            System.out.println( "DebugDownloadThreadListener.process" + 
                    " requestName=" + requestName + " sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() + 
                    " percentOfSource=" + percentOfSource + " percentOfTotal=" + percentOfTotal );
        }
        public void completed( String requestName, String sourceURL, File destinationFile ) {
            System.out.println( "DebugDownloadThreadListener.completed" + 
                    " requestName=" + requestName + " sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() );
        }
        public void error( String requestName, String sourceURL, File destinationFile, String message, Exception e ) {
            System.out.println( "DebugDownloadThreadListener.error" +
                    " requestName=" + requestName + " sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() + 
                    " message=" + message + " exception=" + e );
        }
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySucceeded() {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).succeeded();
        }
    }
    
    private void notifyFailed() {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).failed();
        }
    }
    
    private void notifyCanceled() {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).canceled();
        }
    }
    
    private void notifyRequestAdded( DownloadRequest request ) {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).requestAdded( request.requestName, request.sourceURL, request.destinationFile );
        }
    }
    
    private void notifyProgress( DownloadRequest request,  double percentOfSource, double percentOfTotal ) {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).progress( request.requestName, request.sourceURL, request.destinationFile, percentOfSource, percentOfTotal );
        }
    }
    
    private void notifyCompleted( DownloadRequest request ) {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).completed( request.requestName, request.sourceURL, request.destinationFile );
        }
    }
    
    private void notifyError( DownloadRequest request, String message, Exception e ) {
        ArrayList listeners = getListeners();
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadThreadListener) i.next() ).error( request.requestName, request.sourceURL, request.destinationFile, message, e );
        }
    }
    
    private ArrayList getListeners() {
        return new ArrayList( listeners ); // return a copy to avoid ConcurrentModificationException
    }
    
    //----------------------------------------------------------------------------
    // Test
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) throws IOException, InterruptedException {
        
        // create download thread
        DownloadThread downloadThread = new DownloadThread();
        
        // add a listener
        downloadThread.addListener( new DebugDownloadThreadListener() );
        
        // add download requests
        String tmpDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" );
        downloadThread.addRequest( "downloading glaciers.jar", HTMLUtils.getSimJarURL( "glaciers", "glaciers", new Locale( "en" ), "&" ), tmpDirName + "glaciers.jar" );
        downloadThread.addRequest( "downloading ph-scale.jar", HTMLUtils.getSimJarURL( "ph-scale", "ph-scale", new Locale( "en" ), "&" ), tmpDirName + "ph-scale.jar" );

        // do the download
        System.out.println( "total content length = " + downloadThread.getTotalContentLength() );
        downloadThread.start();
        downloadThread.join();
        System.out.print( "downloaded content length = " + downloadThread.getDownloadedContentLength() );
    }
}
