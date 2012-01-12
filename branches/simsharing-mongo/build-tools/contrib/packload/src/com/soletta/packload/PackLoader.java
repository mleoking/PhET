/** PackLoader builds small executable JAR files that use the Pack200 format
 * internally.  Source JARS are compressed with Pack200 into a single entry
 * in the primary JAR, which is then expanded at runtime and executed.  Pack200
 * expansion is reasonably fast so this is quite practical for most applications.
 * It is particularly useful for systems like Scala that have somewhat large 
 * runtimes, as the resulting executable JAR file can be relatively small.
 * Compressions usually average about 4 to 1.
 *
 * @Author Ross Judson
 *
 * This is in the public domain.  Use this code for any purpose!
 */
package com.soletta.packload;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.ZipEntry;

/**
 * PackLoader allows an application to run directly from a Pack200 format JAR.
 * It does this by decompressing the JAR to a temporary file then mapping the
 * temporary file back into memory so it is quickly available. During the
 * decompression process an index of classes and resources is constructed. NIO
 * is used to maintain the decompressed buffer.
 *
 * @author rjudson
 */
public class PackLoader extends ClassLoader implements Runnable {

    public static void main( String[] args ) {
        Properties props = new Properties();
        NumberFormat nf = NumberFormat.getInstance();

        try {
            if ( args.length > 3 && args[0].equals( "--packload" ) ) {
                int i = 1;
                String mainClass = args[i++];
                File dest = new File( args[i++] );
                File[] src = new File[args.length - i];
                final int mark = i;
                while ( i < args.length ) {
                    src[i - mark] = new File( args[i] );
                    i++;
                }
                props.setProperty( "mainclass", mainClass );

                System.out.println( "Creating loader..." );
                JarOutputStream output = new JarOutputStream( new FileOutputStream( dest ) );
                try {
                    ZipEntry ze;
                    ze = new ZipEntry( "META-INF/MANIFEST.MF" );
                    output.putNextEntry( ze );
                    output.write( "Manifest-Version: 1.0\n".getBytes() );
                    output.write( "Main-Class: com.soletta.packload.PackLoader\n".getBytes() );
                    output.closeEntry();

                    ze = new ZipEntry( "pack.properties" );
                    output.putNextEntry( ze );
                    props.store( output, "PackLoader" );
                    output.closeEntry();

                    WritableByteChannel oChannel = Channels.newChannel( output );
                    String[] loaderClasses = {
                            "PackLoader$1.class",
                            "PackLoader$Location.class",
                            "PackLoader$PackURLConnection$1.class",
                            "PackLoader$PackURLConnection.class",
                            "PackLoader$StreamClassLoader.class",
                            "PackLoader.class"
                    };
                    for ( String packClass : loaderClasses ) {
                        ze = new ZipEntry( "com/soletta/packload/" + packClass );
                        output.putNextEntry( ze );
                        InputStream cls = PackLoader.class.getResourceAsStream( packClass );
                        byte[] buffer = new byte[16000];
                        int bytes = cls.read( buffer );
                        cls.close();
                        oChannel.write( ByteBuffer.wrap( buffer, 0, bytes ) );
                        output.closeEntry();
                    }

                    ze = new ZipEntry( "pack.gz" );
                    output.putNextEntry( ze );
                    Packer packer = Pack200.newPacker();
                    // Initialize the state by setting the desired properties
                    Map<String, String> p = packer.properties();
                    // take more time choosing codings for better compression
                    p.put( Packer.EFFORT, "7" );  // default is "5"
                    // use largest-possible archive segments (>10% better compression).
                    p.put( Packer.SEGMENT_LIMIT, "-1" );
                    // reorder files for better compression.
                    p.put( Packer.KEEP_FILE_ORDER, Packer.FALSE );
                    // smear modification times to a single value.
                    p.put( Packer.MODIFICATION_TIME, Packer.LATEST );

                    long total = 0;
                    for ( File jar : src ) {
                        total += jar.length();
                        System.out.println( "Packing " + jar + " - " + nf.format( jar.length() ) );
                        JarFile jf = new JarFile( jar );
                        packer.pack( jf, output );
                    }
                    System.out.println( "Total bytes " + nf.format( total ) );

                }
                finally {
                    output.close();
                }
                System.out.println( "Packed Executable JAR - " + nf.format( dest.length() ) );
            }
            else {
                props.load( PackLoader.class.getResourceAsStream( "/pack.properties" ) );
                String mainClass = props.getProperty( "mainclass" );
                PackLoader pl = new PackLoader( PackLoader.class.getResourceAsStream( "/pack.gz" ),
                                                mainClass, args );
                pl.run();
            }
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private InputStream in;
    private File inFile;
    private String mainClass;
    private String[] args;
    ByteBuffer data;
    HashMap<String, Location> resources = new HashMap<String, Location>();
    private int packedResources;
    private int loaded;
    private URLStreamHandler packHandler = new URLStreamHandler() {
        @Override
        protected URLConnection openConnection( URL u ) throws IOException {
            return new PackURLConnection( u );
        }
    };

    public PackLoader( File inFile, String mainClass, String[] args ) {
        this.inFile = inFile;
        this.mainClass = mainClass;
        this.args = args;
    }

    public PackLoader( InputStream in, String mainClass, String[] args ) {
        this.in = in;
        this.mainClass = mainClass;
        this.args = args;
    }

    public void run() {
        Unpacker unpack = Pack200.newUnpacker();
        try {
            long begin = System.currentTimeMillis();
            StreamClassLoader streamClassLoader = new StreamClassLoader();
            if ( inFile != null ) { unpack.unpack( inFile, streamClassLoader ); }
            else { unpack.unpack( in, streamClassLoader ); }
            streamClassLoader.close();

            long unpackingTime = System.currentTimeMillis() - begin;

            Class main = findClass( mainClass );
            Method mainMethod = main.getMethod( "main", String[].class );
            mainMethod.invoke( null, new Object[] { args } );

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass( String name ) throws ClassNotFoundException {
        Class ret = null;
        Location l = resources.get( name.replace( '.', '/' ) + ".class" );
        if ( l != null ) {
            ByteBuffer slice = data.slice();
            slice.position( l.start );
            slice.limit( l.end );
            ret = defineClass( name, slice, null );
            resolveClass( ret );
            loaded++;
        }
        return ret == null ? super.findClass( name ) : ret;
    }

    @Override
    protected URL findResource( String name ) {
        try {
            if ( resources.containsKey( name ) ) { return new URL( "pack", "PackLoad", 0, name, packHandler ); }
        }
        catch ( MalformedURLException e ) {
        }
        return super.findResource( name );
    }

    class Location {
        int start, end;

        public Location( int start, int end ) {
            this.start = start;
            this.end = end;
        }
    }

    class PackURLConnection extends URLConnection {

        InputStream in;

        PackURLConnection( URL url ) {
            super( url );
        }

        @Override
        public void connect() throws IOException {
            final Location l = resources.get( url.getPath() );
            final ByteBuffer buffer = data.slice();
            buffer.position( l.start );
            buffer.limit( l.end );
            in = new InputStream() {
                @Override
                public int available() throws IOException {
                    return buffer.remaining();
                }

                @Override
                public synchronized void mark( int readlimit ) {
                    buffer.mark();
                }

                @Override
                public boolean markSupported() {
                    return true;
                }

                @Override
                public int read() throws IOException {
                    if ( buffer.remaining() > 0 ) { return buffer.get(); }
                    else { return -1; }
                }

                @Override
                public int read( byte[] b, int off, int len ) throws IOException {
                    len = Math.min( len, buffer.remaining() );
                    if ( len > 0 ) {
                        buffer.get( b, off, len );
                        return len;
                    }
                    else {
                        return -1;
                    }
                }

                @Override
                public synchronized void reset() throws IOException {
                    buffer.position( l.start );
                }

                @Override
                public long skip( long n ) throws IOException {
                    buffer.position( (int) ( buffer.position() + n ) );
                    return buffer.position();
                }
            };

        }

        @Override
        public InputStream getInputStream() throws IOException {
            if ( in == null ) { connect(); }
            return in;
        }

    }

    class StreamClassLoader extends JarOutputStream {

        private String current;
        private File unpacked = File.createTempFile( "unpack", "bin" );
        private FileChannel buffer;
        private RandomAccessFile bufferFile;

        private int start;

        public StreamClassLoader() throws IOException {
            super( new ByteArrayOutputStream() );
            unpacked.delete();
            bufferFile = new RandomAccessFile( unpacked, "rw" );
            buffer = bufferFile.getChannel();
            unpacked.deleteOnExit();
        }

        @Override
        public void close() throws IOException {
            long len = buffer.position();
            buffer.position( 0 );
            data = ByteBuffer.allocateDirect( (int) len );
            data.limit( (int) len );
            buffer.read( data );
            data.flip();
            buffer.close();
            bufferFile.close();
            unpacked.delete();
        }

        @Override
        public void closeEntry() throws IOException {
            if ( current != null && buffer.position() > start ) {
                Location l = new Location( start, (int) buffer.position() );
                packedResources++;
                resources.put( current, l );
            }
        }

        @Override
        public void putNextEntry( ZipEntry ze ) throws IOException {
            if ( ze.isDirectory() ) { current = null; }
            else {
                current = ze.getName();
                start = (int) buffer.position();
            }
        }

        @Override
        public void write( byte[] b ) throws IOException {
            buffer.write( ByteBuffer.wrap( b ) );
        }

        @Override
        public synchronized void write( byte[] b, int off, int len )
                throws IOException {
            buffer.write( ByteBuffer.wrap( b, off, len ) );
        }

        @Override
        public void write( int b ) throws IOException {
            buffer.write( ByteBuffer.wrap( new byte[] { (byte) b } ) );
        }
    }
}