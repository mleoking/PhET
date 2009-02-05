package edu.colorado.phet.build;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class MyAntTaskRunner implements AntTaskRunner {
    private Project p;

    public MyAntTaskRunner() {
        p = new Project();
        p.init();
    }

    public void runTask( Task childTask ) {
        childTask.setProject( getProject() );
        childTask.init();
        childTask.execute();
    }

    public Project getProject() {
        return p;
    }
}
