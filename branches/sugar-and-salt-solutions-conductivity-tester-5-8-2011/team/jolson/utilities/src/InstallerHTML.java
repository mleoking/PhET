import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 22, 2009
 * Time: 3:03:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class InstallerHTML {

    private static String DEFAULT_ENCODING = "utf-8";

    public static void writeString( File outputFile, String text, String encoding ) throws IOException {
        writeBytes( outputFile, text.getBytes( encoding ) );
    }

    public static void writeString( File outputFile, String text ) throws IOException {
        writeString( outputFile, text, DEFAULT_ENCODING );
    }

    public static void writeBytes( File outputFile, byte[] bytes ) throws IOException {
        //outputFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream( outputFile );
        try {
            outputStream.write( bytes );
        }
        finally {
            outputStream.close();
        }
    }

    public static String rawFile( File inFile ) throws IOException {
        // BAD BAD BAD! FileUtils depends on TranslationDiscrepancy which depends on most of buildtools, which depends on phetcommon
        // using this would require all of buildtools, phetcommon, and many external libraries to be included in each JAR
        // TODO: maybe in the future we can remove this, however I need a fix right now.
        //return FileUtils.loadFileAsString( inFile );


        // TODO: duplicating FileUtils.loadFileAsString, needs to be fixed
        InputStream inStream = new FileInputStream( inFile );

        ByteArrayOutputStream outStream;

        try {
            outStream = new ByteArrayOutputStream();

            int c;
            while ( ( c = inStream.read() ) >= 0 ) {
                outStream.write( c );
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }

        return new String( outStream.toByteArray(), "utf-8" );
    }




    public static void generateInstallerHTML( File regularHTML, File newHTML ) throws IOException {
        String str = rawFile( regularHTML );

        str = str.replaceAll("@@DISTRIBUTION_TAG@@", "test-installation");
        str = str.replaceAll("@@INSTALLATION_TIMESTAMP@@", "1134568000");
        str = str.replaceAll("@@INSTALLER_CREATION_TIMESTAMP@@", "1134567890");

        writeString( newHTML, str );
    }

    public static void main( String[] args ) {
        System.out.println( args[0] );

        try {
            File local = new File( "." );
            
            System.out.println( local.getCanonicalPath() );

            File trunk = new File( local, "../../../.." );


            System.out.println( trunk.getCanonicalPath() );

        
            String simName = args[0];

            String locale = "en";

            if( args.length > 1 ) {
                locale = args[1];
            }

            File inFile = new File( trunk, "simulations-flash/simulations/" + simName + "/deploy/" + simName + "_" + locale + ".html" );
            File outFile = new File( trunk, "simulations-flash/simulations/" + simName + "/deploy/" + simName + "_" + locale + "_installed.html" );

            System.out.println( inFile.getCanonicalPath() );
            System.out.println( outFile.getCanonicalPath() );

            generateInstallerHTML( inFile, outFile );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
