package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

public class ResourceDeployClient {

    // TODO: refactor PhetServer so that this type of thing is not necessary
    public final String PROD_PATH = "/web/chroot/phet/usr/local/apache/htdocs/sims/resources/";

    private File resourceFile;
    private File propertiesFile;
    private String temporaryDirName;

    public ResourceDeployClient( File resourceFile, File propertiesFile ) {
        this.resourceFile = resourceFile;
        this.propertiesFile = propertiesFile;
        temporaryDirName = createTemporaryDirName( resourceFile );
    }

    // uploads the resource file and its corresponding properties file
    public void uploadResourceFile() throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        dirtyExecute( "mkdir -p -m 775 " + temporaryDirPath + "/resource" );

        ScpTo.uploadFile( resourceFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporaryDirPath + "/resource/" + resourceFile.getName(), authenticationInfo.getPassword() );
        ScpTo.uploadFile( propertiesFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporaryDirPath + "/resource/resource.properties", authenticationInfo.getPassword() );
    }

    // uploads an extra file for a particular sim
    public void uploadExtraFile( File extraFile, String sim ) throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        String temporarySimExtrasDir = temporaryDirPath + "/extras/" + sim;

        dirtyExecute( "mkdir -p -m 775 " + temporarySimExtrasDir );

        ScpTo.uploadFile( extraFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporarySimExtrasDir + "/" + extraFile.getName(), authenticationInfo.getPassword() );
    }

    // executes the resource deploy server on tigercat
    public void executeResourceDeployServer( File trunk ) throws IOException {
        String temporaryDirPath = getTemporaryDirPath();

        PhetServer server = PhetServer.PRODUCTION;

        BuildToolsProject buildToolsProject = new BuildToolsProject( new File( trunk, "build-tools" ) );
        String buildScriptDir = server.getServerDeployPath( buildToolsProject );

        String javaCmd = server.getJavaCommand();
        String jarCmd = server.getJarCommand();
        String jarName = buildToolsProject.getDefaultDeployJar().getName();
        String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();

        String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " +
                         ResourceDeployServer.class.getName() + " " + jarCmd + " " + pathToBuildLocalProperties +
                         " " + temporaryDirPath;

        dirtyExecute( command );
    }

    public String createTemporaryDirName( File resourceFile ) {
        String ret = resourceFile.getName();
        ret = ret.replaceAll( "[^a-zA-Z0-9]", "-" );
        ret = String.valueOf( ( new Date() ).getTime() ) + "_" + ret;
        return ret;
    }

    public String getTemporaryDirName() {
        return temporaryDirName;
    }

    public String getTemporaryDirPath() {
        return PROD_PATH + getTemporaryDirName();
    }

    public void dirtyExecute( String command ) {
        System.out.println( "# " + command );
        PhetServer server = PhetServer.PRODUCTION;
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            sshConnection.executeTask( new SshCommand( command ) );
        }
        catch( SshException e ) {
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                // TODO: check if authentication fails, don't try logging in again
                // on tigercat, 3 (9?) unsuccessful login attepts will lock you out
                System.out.println( "Authentication on '" + server.getHost() + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }
    }

}
