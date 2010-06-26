package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.MyAntTaskRunner;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.WebsiteBuildCommand;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;

import com.jcraft.jsch.JSchException;

/**
 * Project meant to compile the Wicket website
 */
public class WebsiteProject extends JavaProject {
    public WebsiteProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../.." );
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
        //return BuildToolsPaths.BUILD_TOOLS_PROD_SERVER_DEPLOY_PATH;
    }

    public boolean isTestable() {
        return true;
    }

    @Override
    public boolean isShrink() {
        // until many things are done
        return false;
    }

    @Override
    public boolean getSignJar() {
        return false;
    }

    @Override
    public boolean build() throws Exception {
        new WebsiteBuildCommand( this, new MyAntTaskRunner(), isShrink(), this.getDefaultDeployJar() ).execute();
        File[] f = getDeployDir().listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.getName().toLowerCase().endsWith( ".war" );
            }
        } );
        return f.length == 1;//success if there is exactly one jar
    }

    @Override
    public void runSim( Locale locale, String simulationName ) {

    }

    @Override
    public File getJarFile() {
        File file = new File( getAntOutputDir(), "jars/ROOT.war" );
        file.getParentFile().mkdirs();
        return file;
    }

    public File getBuildWarFile() {
        return new File( getDeployDir(), "ROOT.war" );
    }

    public boolean deploy( String host, String protocol, AuthenticationInfo userInfo, AuthenticationInfo managerInfo, boolean dev ) {
        String webHost = host.equals( "figaro.colorado.edu" ) ? "phet.colorado.edu" : host; //Have to use public web url for curling, or it will fail 
        boolean success = false;
        try {
            System.out.println( "Starting website deployment" );

            System.out.println( "Uploading WAR" );
            ScpTo.uploadFile( getBuildWarFile(), userInfo.getUsername(), host, "/tmp/", userInfo.getPassword() );

            System.out.println( "Finished uploading, executing undeploy and deploy on Tomcat" );
            success = SshUtils.executeCommands( new String[]{
                    host.equals( "phetsims.colorado.edu" ) || host.equals( "figaro.colorado.edu" ) ? "cp /tmp/ROOT.war /data/web/htdocs/phetsims/website-backup/website-code/ROOT-`date +%s`-`date | sed -e 's/ /_/g'`.war" : "",
                    "curl -k -u " + managerInfo.getUsername() + ":" + managerInfo.getPassword() + " '" + protocol + "://" + webHost + "/manager/undeploy?path=/'",
                    "curl -k -u " + managerInfo.getUsername() + ":" + managerInfo.getPassword() + " '" + protocol + "://" + webHost + "/manager/deploy?war=/tmp/ROOT.war&path=/'"
            }, host, userInfo );

            System.out.println( "Finished running Tomcat commands. Should have stated 'OK - Deployed application at context path'" );

        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public String getClasspath() {
        try {
            return super.getClasspath() + " : " + new File( getProjectDir(), "contrib/javaee.jar" ).getCanonicalPath();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return super.getClasspath();
        }
    }
}