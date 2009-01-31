package edu.colorado.phet.build;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 29, 2009
 * Time: 11:22:23 AM
 *
 *         //phetProjects.addAll(Arrays.asList( PhetFlashProject.getFlashProjects(baseDir ) ));
 */
public class PhetFlashProject extends PhetProject{
    public PhetFlashProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public PhetFlashProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }
    public static PhetProject[]getFlashProjects(File baseDir){
        File flashSimDir=new File(baseDir.getParentFile(),"team/jolson/simulations");
        File[]files=flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory()&&!pathname.getName().startsWith( "." );
            }
        } );
        ArrayList projects=new ArrayList( );
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            try {
                projects.add(new PhetFlashProject( file));
            }
            catch( IOException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return (PhetFlashProject[]) projects.toArray( new PhetFlashProject[0] );
    }
}
