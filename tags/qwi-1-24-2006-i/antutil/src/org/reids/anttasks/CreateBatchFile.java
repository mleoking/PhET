/*Copyright, Sam Reid, 2003.*/
package org.reids.anttasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 3:11:24 PM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */

/*Usage:
<createbatchfile mainJar=${mainjarname} classname=${mainclassname}/>
*/

public class CreateBatchFile extends Task {
    String mainJar;
    private String mainClassName;

    File libDir;
    String javaCommand = "java";
    private String pathSeparator = System.getProperty( "path.separator" );

    static final FileUtils utils = FileUtils.newFileUtils();
    File outputFile = null;//if null, output is only to System.out

    public String getPathSeparator() {
        return pathSeparator;
    }

    public void setPathSeparator( String pathSeparator ) {
        this.pathSeparator = pathSeparator;
    }

    public String getJavaCommand() {
        return javaCommand;
    }

    public void setJavaCommand( String javaCommand ) {
        this.javaCommand = javaCommand;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName( String mainClassName ) {
        this.mainClassName = mainClassName;
    }

    public void setMainJar( String mainJar ) {
        this.mainJar = mainJar;
    }

    public void setOutputFile( File outputFile ) {
        this.outputFile = outputFile;
    }

    public void setLibDir( File libDir ) {
        this.libDir = libDir;
    }

    public String buildClasspath() {
        File[] files = this.libDir.listFiles();
        if( files == null ) {
            files = new File[0];
        }
        String cp = "";
        for( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            cp += libDir.getName() + "/" + file.getName() + pathSeparator;
        }
        cp += mainJar;
        return cp;
    }

    /**
     * Gets all items in the classpath and copies them to a common location.
     */
    public void execute() throws BuildException {
        super.execute();

        String classpath = buildClasspath();
        String output = "";
        output += javaCommand + " -classpath " + classpath + " " + mainClassName;

        Echo echo = new Echo();
        echo.setProject( getProject() );
//        echo.setRuntimeConfigurableWrapper(getRuntimeConfigurableWrapper());
        echo.setOwningTarget( getOwningTarget() );
        echo.setMessage( output );
        if( outputFile != null ) {
            echo.setFile( outputFile );
        }
        echo.execute();
    }

}
