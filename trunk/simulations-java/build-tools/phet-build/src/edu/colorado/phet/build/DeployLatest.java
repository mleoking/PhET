package edu.colorado.phet.build;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.translate.ScpTo;
import edu.colorado.phet.build.util.FileUtils;

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
            for ( int i = 0; i < p.length; i++ ) {
                String[] f = p[i].getFlavorNames();
                for ( int j = 0; j < f.length; j++ ) {
                    final File flavorJAR = p[i].getDefaultDeployFlavorJar( f[j] );
                    ScpTo.uploadFile( flavorJAR, user, host, DIR + flavorJAR.getName(), password );
                }
            }
            deployAllSimList( p );
            deployLog( p );
        }
        catch( Exception e ) {
            throw new BuildException( e );
        }
    }

    private void deployLog( PhetProject[] p ) throws IOException, JSchException {
        String log = new Date() + "\n" + countFlavors( p ) + " sims (flavors) deployed";
        final File outputFile = new File( getBaseDir(), "/deploy/log.txt" );
        FileUtils.writeString( outputFile, log );
        ScpTo.uploadFile( outputFile, user, host, DIR + outputFile.getName(), password );
    }

    private int countFlavors( PhetProject[] p ) {
        int count = 0;
        for ( int i = 0; i < p.length; i++ ) {
            count += p[i].getFlavors().length;
        }
        return count;
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
