package edu.colorado.phet.build.java;

import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.build.util.ProcessOutputReader;
import edu.colorado.phet.build.PhetProject;

public class SVNStatusChecker {
    public SVNStatusChecker() {
    }

    public boolean isUpToDate( PhetProject project ) {

//        	<!-- fails if the working copy is not synchronized with the SVN repository -->
//	<target name="_fail-if-working-copy-is-not-synchronized" depends="_init">
//
//		<!-- if sim.name is set, check only its dependencies. otherwise check the entire working copy -->
//		<if>
//			<isset property="sim.name"/>
//			<then>
//				<phet-list-depends project="${sim.name}" property="phet.dependslist" commandlineformat="true" />
//			</then>
//		    <else>
//		    	<property name="phet.dependslist" value="."/>
//			</else>
//		</if>
//
//		<!-- check the working copy's status -->
//		<exec executable="svn" outputproperty="svn.output" failonerror="true">
//			<arg line="status ${phet.dependslist}" />
//		</exec>
//
//		<!-- fail if svn returned anything -->
//		<if>
//			<not>
//				<equals arg1="${svn.output}" arg2="" />
//			</not>
//			<then>
//				<fail message="Your working copy is out of sync with the SVN repository: ${svn.output}" />
//			</then>
//		</if>
//	</target>
        ArrayList args = new ArrayList();
        args.add( "svn" );
        args.add( "status" );
        PhetProject[] projects = project.getAllDependencies();
        for ( int i = 0; i < projects.length; i++ ) {
            args.add( projects[i].getProjectDir().getAbsolutePath() );
        }
        try {
            String[] cmd = (String[]) args.toArray( new String[0] );
            System.out.println( "execing: " + toString( cmd ) );
            Process p = Runtime.getRuntime().exec( cmd );
            ProcessOutputReader pop = new ProcessOutputReader( p.getInputStream() );
            pop.start();
            ProcessOutputReader poe = new ProcessOutputReader( p.getErrorStream() );
            poe.start();
            try {
                p.waitFor();
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            String out = pop.getOutput();
            String err = poe.getOutput();
            if ( out.length() == 0 && err.length() == 0 ) {
                return true;
            }
            else {
                System.out.println( "out=" + out + ", err=" + err );
                System.out.println( "Out of sync with SVN" );
                return false;
            }

        }
        catch( IOException e ) {
            e.printStackTrace();
            return false;
        }

    }

    private String toString( String[] cmd ) {
        String s = "";
        for ( int i = 0; i < cmd.length; i++ ) {
            String s1 = cmd[i];
            s = s + " " + s1;
        }
        return s;
    }
}
