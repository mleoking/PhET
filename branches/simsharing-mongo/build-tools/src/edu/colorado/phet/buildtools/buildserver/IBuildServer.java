package edu.colorado.phet.buildtools.buildserver;

import java.io.File;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.util.SshUtils;

/**
 * Notes:
 * 1. We will need to install Java, Flash CS4 and Flex on all Build Servers.
 * 2. We will need to SVN checkout on all Build Servers
 *
 * @author Sam Reid
 */
public interface IBuildServer {
    /**
     * Builds a jar that can be launched for testing
     *
     * @param buildCommand the command for doing build/deploy work
     */
    void runBuildCommand( String buildCommand );

    /**
     * This build server runs on the local client machine.
     */
    public static class LocalBuildServer implements IBuildServer {
        private File trunk = new File( "C:\\workingcopy\\phet-svn\\trunk" );

        public void runBuildCommand( String buildCommand ) {
            BuildServer.main( buildCommand );
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class RemoteBuildServer implements IBuildServer {
        private String host;
        private String username;
        private String password;

        public void runBuildCommand( String buildCommand ) {
            //TODO: when do build tools get updated on IBuildServers?
            //TODO: what about building SWF on client machine, since cannot be done on Linux?
            SshUtils.executeCommands( new String[] { "java -jar " + BuildServer.class.getName() + " " + buildCommand }, host, new AuthenticationInfo( username, password ) );
        }
    }
}