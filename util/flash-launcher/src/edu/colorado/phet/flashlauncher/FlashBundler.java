//package edu.colorado.phet.flashlauncher;
//
//import java.io.File;
//import java.io.IOException;
//
//import edu.colorado.phet.build.FileUtils;
//
///**
// * Created by: Sam
// * May 29, 2008 at 8:39:40 AM
// */
//public class FlashBundler {
//    private File trunk;
//
//    public FlashBundler( File trunk ) {
//        this.trunk = trunk;
//    }
//
//    public static void main( String[] args ) throws IOException {
//        String trunk = "C:\\reid\\phet\\svn\\trunk\\";
//        new FlashBundler( new File( trunk ) ).bundle( "curve-fit" );
//    }
//
//    private void bundle( String sim ) throws IOException {
//        File dir = new File( trunk, "simulations-flash/simulations/" + sim );
//        FileUtils.zip( new File[]{new File( dir, "src/" + sim + ".swf" )}, new File( dir, "src/" + sim + ".jar" ) );
//    }
//}
