package edu.colorado.phet.build;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.translate.ScpTo;

import com.jcraft.jsch.JSchException;

/**
 * Created by: Sam
 * Jan 28, 2008 at 12:55:00 PM
 */
public class DeployLatest extends PhetAllSimTask {
    private String user;
    private String password;
    private String host;
    private String DIR = "/htdocs/physics/phet/dev/latest-build/";

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    public void execute() throws BuildException {
        super.execute();
        try {
            PhetProject[] p = PhetProject.getAllProjects( getBaseDir() );
            deployAllSimList( p );
//            p = new PhetProject[]{p[0]};//for testing
            for ( int i = 0; i < p.length; i++ ) {
                String[] f = p[i].getFlavorNames();
                for ( int j = 0; j < f.length; j++ ) {
                    final File flavorJAR = p[i].getDefaultDeployFlavorJar( f[i] );
                    ScpTo.uploadFile( flavorJAR, user, host, DIR + flavorJAR.getName(), password );
                }
            }
        }
        catch( Exception e ) {
            throw new BuildException( e );
        }
    }

    private void deployAllSimList( PhetProject[] p ) throws IOException, JSchException {
        String s = "";
        for ( int i = 0; i < p.length; i++ ) {
            PhetProject phetProject = p[i];
            String[] f = phetProject.getFlavorNames();
            for ( int j = 0; j < f.length; j++ ) {
                s += f[j] + "\n";
            }
        }
        final File outputFile = new File( getBaseDir(), "/deploy/sims.txt" );
        FileUtils.writeString( outputFile, s );
        ScpTo.uploadFile( outputFile, user, host, DIR + outputFile.getName(), password );
    }
}
