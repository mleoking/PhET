/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Handles a collection of download requests, provides notification of progress.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Downloader {
    
    //----------------------------------------------------------------------------
    // Data structures
    //----------------------------------------------------------------------------
    
    /*
     * Encapsulates the info about one download.
     */
    private static class DownloadSpec {
        
        private final String sourceURL;
        private final File destinationFile;
        private final int expectedContentLength; // actual content length may differ
        
        public DownloadSpec( String sourceURL, File destinationFile, int expectedContentLength ) {
            this.sourceURL = sourceURL;
            this.destinationFile = destinationFile;
            this.expectedContentLength = expectedContentLength;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList downloadSpecs; // array of DownloadSpec
    private final ArrayList listeners; // array of DownloadListener
    
    private int totalContentLength; // bytes
    private int downloadedContentLength; // bytes
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Downloader() {
        downloadSpecs = new ArrayList();
        listeners = new ArrayList();
        totalContentLength = 0;
        downloadedContentLength = 0;
    }

    /**
     * Constructor that adds one download request.
     * 
     * @param sourceURL
     * @param destinationFileName
     * @throws IOException
     */
    public Downloader( String sourceURL, String destinationFileName ) throws IOException {
        this( sourceURL, new File( destinationFileName) );
    }
    
    /**
     * Constructor that adds one download request.
     * 
     * @param sourceURL
     * @param destinationFileName
     * @throws IOException
     */
    public Downloader( String sourceURL, File destinationFile ) throws IOException {
        this();
        addDownload( sourceURL, destinationFile );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the total content length of all pending download requests.
     * <p>
     * This is the expected length at the time the requests were added.
     * The actual length may be different if the source content changed
     * between the time that the request was added and download() was called.
     * So this value may differ from getDownloadedContentLength.
     * <p>
     * If you call this after download() successfully completes, or after
     * calling clear(), you'll get zero.
     * 
     * @return bytes
     */
    public int getTotalContentLength() {
        return totalContentLength;
    }
    
    /**
     * Gets the number of bytes downloaded the last time that download() was called.
     * 
     * @return bytes
     */
    public int getDownloadedContentLength() {
        return downloadedContentLength;
    }
    
    //----------------------------------------------------------------------------
    // Downloads
    //----------------------------------------------------------------------------
    
    /**
     * Adds a download request. 
     * No downloads are performed until download is called.
     * 
     * @param sourceURL
     * @param destinationFileName
     * @throws IOException
     */
    public void add( String sourceURL, String destinationFileName ) throws IOException {
        addDownload( sourceURL, new File( destinationFileName ) );
    }

    public void addDownload( String sourceURL, File destinationFile ) throws IOException {
        
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
        DownloadSpec spec = new DownloadSpec( sourceURL, destinationFile, expectedContentLength );
        downloadSpecs.add( spec );
    }
    
    /**
     * Performs all download requests.
     * If this completes successfully, then all added requests are cleared.
     * Stops when the first error is encountered.
     * 
     * @throws IOException
     */
    public void download() throws IOException {
        downloadedContentLength = 0;
        Iterator i = downloadSpecs.iterator();
        while ( i.hasNext() ) {
            download( (DownloadSpec) i.next() );
        }
        clear();
    }
    
    /*
     * Performs one download request.
     */
    private void download( DownloadSpec spec ) throws IOException {
        
        try {
            // create the output file
            spec.destinationFile.getParentFile().mkdirs();
            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( spec.destinationFile ) );
            
            // create the input connection
            URLConnection urlConnection = new URL( spec.sourceURL ).openConnection();
            
            // adjust the content length if necessary
            final int contentLength = urlConnection.getContentLength();
            totalContentLength += ( contentLength - spec.expectedContentLength );
            
            // read from the input connection, write to the output file, notify of progress
            int subtotalBytesRead = 0;
            InputStream inputStream = urlConnection.getInputStream();
            byte[] data = new byte[2048];
            int bytesRead = 0;
            boolean done = false;
            while ( !done ) {
                bytesRead = inputStream.read( data );
                if ( bytesRead != -1 ) {
                    outputStream.write( data, 0, bytesRead );
                    subtotalBytesRead += bytesRead;
                    downloadedContentLength += bytesRead; // record bytes read
                    notifyProgress( spec.sourceURL, spec.destinationFile, subtotalBytesRead / (double)contentLength, downloadedContentLength / (double)totalContentLength );
                }
                else {
                    done = true;
                }
            }
            
            // clean up
            inputStream.close();
            outputStream.close();
            
            // notify that we're done with this download
            notifyCompleted( spec.sourceURL, spec.destinationFile );
        }
        catch( IOException e ) {
            notifyError( spec.sourceURL, spec.destinationFile, e.getMessage(), e );
            throw e;
        }
    }
    
    /**
     * Clears all download requests.
     */
    public void clear() {
        downloadSpecs.clear();
        totalContentLength = 0;
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in download progress.
     */
    public interface DownloadListener {
        
        /**
         * Indicates the progress made on satisfying a specific download request.
         * 
         * @param sourceURL
         * @param destinationFile
         * @param percentOfSource percent of sourceURL that has been downloaded so far
         * @param percentOfTotal percent of all download requests that have been downloaded so far
         */
        public void progress( String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal );
        
        /**
         * Indicates that a specific download request has been successfully completed.
         * 
         * @param sourceURL
         * @param destinationFile
         */
        public void completed( String sourceURL, File destinationFile );
        
        /**
         * Indicates that an error was encountered for a specific download request.
         * Downloading stops when this notification is sent.
         * 
         * @param sourceURL
         * @param destinationFile
         * @param message
         * @param e
         */
        public void error( String sourceURL, File destinationFile, String message, Exception e );
    }

    /*
     * Adapter, provides a no-op implementation.
     */
    public static class DownloadAdapter implements DownloadListener {
        public void progress( String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {}
        public void completed( String sourceURL, File destinationFile ) {}
        public void error( String sourceURL, File destinationFile, String message, Exception e ) {}
    }
    
    /*
     * Debug implementation, prints method calls and args to System.out.
     */
    public static class DebugDownloadListener implements DownloadListener {
        public void progress( String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {
            System.out.println( "DebugDownloadListener.process sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() + 
                    " percentOfSource=" + percentOfSource + " percentOfTotal=" + percentOfTotal );
        }
        public void completed( String sourceURL, File destinationFile ) {
            System.out.println( "DebugDownloadListener.completed sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() );
        }
        public void error( String sourceURL, File destinationFile, String message, Exception e ) {
            System.out.println( "DebugDownloadListener.error sourceURL=" + sourceURL + " destinationFile=" + destinationFile.getAbsolutePath() + 
                    " message=" + message + " exception=" + e );
        }
    }
    
    public void addDownloadListener( DownloadListener listener ) {
        listeners.add( listener );
    }
    
    public void removeDownloadListener( DownloadListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyProgress( String sourceURL, File destinationFile,  double percentOfSource, double percentOfTotal ) {
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadListener) i.next() ).progress( sourceURL, destinationFile, percentOfSource, percentOfTotal );
        }
    }
    
    private void notifyCompleted( String sourceURL, File destinationFile ) {
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadListener) i.next() ).completed( sourceURL, destinationFile );
        }
    }
    
    private void notifyError( String sourceURL, File destinationFile, String message, Exception e ) {
        Iterator i = listeners.iterator();
        while ( i.hasNext() ) {
            ( (DownloadListener) i.next() ).error( sourceURL, destinationFile, message, e );
        }
    }
    
    //----------------------------------------------------------------------------
    // Test
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        String tmpDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" );
        Downloader downloader = new Downloader();
        try {
            downloader.add( HTMLUtils.getSimJarURL( "glaciers", "glaciers", "&", "en" ), tmpDirName + "glaciers.jar" );
            downloader.add( HTMLUtils.getSimJarURL( "ph-scale", "ph-scale", "&", "en" ), tmpDirName + "ph-scale.jar" );
            downloader.addDownloadListener( new DebugDownloadListener() );
            System.out.println( "total content length = " + downloader.getTotalContentLength() );
            downloader.download();
            System.out.print( "downloaded content length = " + downloader.getDownloadedContentLength() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
