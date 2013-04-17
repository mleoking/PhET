import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestZips {
    public static void main( String[] args ) {
        try {
            File file = new File( "baboon.zip" );
            ZipOutputStream zout = new ZipOutputStream( new FileOutputStream( file ) );
            PrintStream out = new PrintStream( zout );
            zout.putNextEntry( new ZipEntry( "a.txt" ) );
            out.println( "this is the A file" );
            zout.putNextEntry( new ZipEntry( "b.txt" ) );
            out.println( "this is the B file" );
            out.flush();
            zout.finish();
            out.close();
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
