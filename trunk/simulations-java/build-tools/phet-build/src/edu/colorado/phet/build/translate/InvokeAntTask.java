package edu.colorado.phet.build.translate;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Echo;

/**
 * Created by: Sam
 * Dec 11, 2007 at 3:08:00 PM
 */
public class InvokeAntTask {
    public static void main( String[] args ) {
        Project project = new Project();
        project.init();
        ProjectHelper ph = ProjectHelper.getProjectHelper();
        project.addReference( "ant.projectHelper", ph );

        Echo task = new Echo();
        task.setOwningTarget( new Target() );
        task.setMessage( "hello ant" );
        task.setProject( project );
        task.init();
        task.execute();

        System.out.println( "Task execute finished" );
    }
}
