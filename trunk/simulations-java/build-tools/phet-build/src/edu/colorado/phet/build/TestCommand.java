package edu.colorado.phet.build;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class TestCommand implements Command {
    private final PhetProject project;
    private final AntTaskRunner taskRunner;
    private final Task task;

    public TestCommand( PhetProject project, AntTaskRunner taskRunner, Task task ) {
        this.project = project;
        this.taskRunner = taskRunner;
        this.task = task;
    }

    public void execute() throws Exception {
        PhetBuildUtils.antEcho( taskRunner, "Testing " + project.getName() + ".", getClass() );

        JUnitTask junit = new JUnitTask();

        Assertions a = new Assertions();

        a.setProject( task.getProject() );
        a.setLocation( task.getLocation() );
        a.setEnableSystemAssertions( Boolean.TRUE );

        junit.setHaltonerror( true );
        junit.setShowOutput( true );
        junit.setFork( true );
        junit.addAssertions( a );

        File[] jars = project.getAllJarFiles();

        // Classpath includes all jars, compiled class files, and junit:
        Path classpath = junit.createClasspath();

        classpath.setProject( task.getProject() );
        classpath.setLocation( task.getLocation() );

        for ( int i = 0; i < jars.length; i++ ) {
            if ( !jars[i].exists() ) {
                throw new InternalError();
            }

            classpath.createPathElement().setLocation( jars[i] );
        }

        classpath.createPathElement().setPath( project.getClassesDirectory().getAbsolutePath() );

        FileSet fileSet = new FileSet();

        fileSet.setLocation( task.getLocation() );
        fileSet.setProject( task.getProject() );
        fileSet.setDir( project.getClassesDirectory() );
        fileSet.createInclude().setName( "**/Z*Tester.class" );
        fileSet.createExclude().setName( "**/ZAbstract*Tester.class" );

        BatchTest batchTest = junit.createBatchTest();

        batchTest.setTodir( project.getAntOutputDir() );
        batchTest.addFileSet( fileSet );

        taskRunner.runTask( junit );

        PhetBuildUtils.antEcho( taskRunner, "Finished testing " + project.getName() + ".", getClass() );
    }
}
