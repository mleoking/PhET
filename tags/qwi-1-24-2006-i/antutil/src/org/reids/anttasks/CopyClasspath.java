/*Copyright, Sam Reid, 2003.*/
package org.reids.anttasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 3:11:24 PM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */
public class CopyClasspath extends Task {
    private File dir;
    static final FileUtils utils = FileUtils.newFileUtils();
    ArrayList ignore = new ArrayList();

    public CopyClasspath() {
    }

    public void setDebug( boolean debug ) {
        if( debug ) {
            jf.setContentPane( jta );
            jf.setVisible( true );
            jf.pack();
        }
        else {
            jf.setVisible( false );
        }
    }

    private ArrayList parsePathList( String cp ) {
        StringTokenizer st = new StringTokenizer( cp, System.getProperty( "path.separator" ) );
        ArrayList all = new ArrayList();
        while( st.hasMoreTokens() ) {
            File token = utils.normalize( st.nextToken() );
            all.add( token );
        }
        return all;
    }

    JFrame jf = new JFrame( "CopyClasspath output" );
    JTextArea jta = new JTextArea( 40, 40 );

    /**
     * Gets all items in the classpath and copies them to a common location.
     */
    public void execute() throws BuildException {

        super.execute();
        String cp = System.getProperty( "java.class.path" );
        ArrayList all = parsePathList( cp );
        Copy c = new Copy();

        c.setProject( getProject() );
//        c.setRuntimeConfigurableWrapper(getRuntimeConfigurableWrapper());
        c.setOwningTarget( getOwningTarget() );

        boolean anythingToCopy = false;
        for( int i = 0; i < all.size(); i++ ) {
            File path = (File)all.get( i );
            boolean copy = true;
            if( ignore != null && ignore.contains( path ) ) {
                copy = false;
            }
            if( copy ) {
                anythingToCopy = true;
                FileSet fs = getFileSet( path );
                traceln( "Adding fileset: for path=" + path );
                c.addFileset( fs );
            }
            else {
                traceln( "Ignoring-->" + path );
            }
        }
        if( anythingToCopy ) {
//        File target=new File("C:/idea-ant-temp/");
            File target = dir;
            if( target == null ) {
                throw new BuildException( "No target directory specified." );
            }
            target.mkdirs();
//            traceln("Created Target.");
            c.setTodir( target );
//            traceln("Starting copy");
            c.execute();
//            traceln("Executed copy");
        }
        else {
//            traceln("Nothing to copy.");
        }
    }

    private void traceln( String s ) {
        jta.append( s + "\n" );
    }

    // The setter for the "message" attribute
    public void setDirectory( File dir ) {
        this.dir = dir;
    }

    public void setIgnore( String pathList ) {
        this.ignore = parsePathList( pathList );
//        traceln("Ignore=" + ignore);
    }

    private FileSet getFileSet( File path ) {
        if( path.isDirectory() ) {
            FileSet fs = new FileSet();
            fs.setDir( path );
            return fs;
        }
        else {
            FileSet fs = new FileSet();
            fs.setIncludesfile( path );
            return fs;
        }
    }

}
