package edu.colorado.phet.buildtools.util;

/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetWebsite;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class ScpTo {
    public static void main( String[] arg ) throws JSchException, IOException {
        if ( arg.length != 3 ) {
            System.err.println( "usage: java ScpTo file1 user@remotehost:file2 password" );
            System.exit( -1 );
        }

        String lfile = arg[0];
        String user = arg[1].substring( 0, arg[1].indexOf( '@' ) );
        arg[1] = arg[1].substring( arg[1].indexOf( '@' ) + 1 );
        String host = arg[1].substring( 0, arg[1].indexOf( ':' ) );
        String rfile = arg[1].substring( arg[1].indexOf( ':' ) + 1 );
        String password = arg[2];

        uploadFile( new File( lfile ), user, host, rfile, password );
    }

    // List of JSch sessions that are currently active, essentially a cache so
    // that sessions don't have to be reestablished for every transfer.
    private static ArrayList<Session> sessionList = new ArrayList<Session>();

    public static void uploadFile( PhetWebsite website, BuildLocalProperties buildLocalProperties, File localFile, String remoteFilePath ) throws JSchException, IOException {
        AuthenticationInfo credentials = website.getServerAuthenticationInfo( buildLocalProperties );
        uploadFile(
                localFile,
                credentials.getUsername(),
                website.getServerHost(),
                remoteFilePath,
                credentials.getPassword()
        );
    }

    public static void uploadFile( File localFile, String user, String host, String remoteFilePath, String password ) throws JSchException, IOException {
        FileInputStream fis = null;
        try {
            // Obtain a previously cached JSch session or, if no matching one exists, create a new one.
            Session session = null;
            for ( Session s : sessionList ) {
                if ( s.getHost().equals( host ) && ( s.getUserName().equals( user ) ) ) {
                    session = s;
                }
            }
            if ( session == null ) {
                // No cached session found, create one.
                JSch jsch = new JSch();
                session = jsch.getSession( user, host, 22 );

                // username and password will be given via UserInfo interface.
                UserInfo ui = new MyUserInfo( password );
                session.setUserInfo( ui );
                session.setConfig( "PreferredAuthentications", "publickey,keyboard-interactive,password" ); // Workaround for a JSch issue, see https://issues.apache.org/bugzilla/show_bug.cgi?id=53437.
                session.connect();
                sessionList.add( session );
            }

            // exec 'scp -t remoteFilePath' remotely
            String command = "scp -p -t " + remoteFilePath;
            Channel channel = session.openChannel( "exec" );
            ( (ChannelExec) channel ).setCommand( command );

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if ( checkAck( in ) != 0 ) {
                new Exception( "ack failed, exiting" ).printStackTrace();
                System.exit( 0 );
            }

            /*
             * NOTE: The "C0664" specifies the UNIX file permissions; see chmod.
             * This code originally used "C0644", but that causes permissions problems
             * when different developers attempted to deploy the same sim.
             */
            // send "C0664 fileSize filename", where filename should not include '/'
            long fileSize = ( localFile ).length();
            command = "C0664 " + fileSize + " ";
            if ( localFile.getAbsolutePath().lastIndexOf( '/' ) > 0 ) {
                command += localFile.getAbsolutePath().substring( localFile.getAbsolutePath().lastIndexOf( '/' ) + 1 );
            }
            else if ( localFile.getAbsolutePath().lastIndexOf( '\\' ) > 0 ) {
                command += localFile.getAbsolutePath().substring( localFile.getAbsolutePath().lastIndexOf( '\\' ) + 1 );
            }
            command += "\n";
            out.write( command.getBytes() );
            out.flush();
            if ( checkAck( in ) != 0 ) {
                new Exception( "ack failed, exiting" ).printStackTrace();
                System.exit( 0 );
            }

            // send a content of localFile

            fis = new FileInputStream( localFile );
            byte[] buf = new byte[1024];
            while ( true ) {
                int len = fis.read( buf, 0, buf.length );
                if ( len <= 0 ) {
                    break;
                }
                out.write( buf, 0, len ); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write( buf, 0, 1 );
            out.flush();
            if ( checkAck( in ) != 0 ) {
                System.out.println( "ack failed... continuing" );
            }
            out.close();

            channel.disconnect();

            System.out.println( "Finished scp (from, to):" + localFile + " " + remoteFilePath );
        }
        finally {
            if ( fis != null ) {
                fis.close();
            }
        }
    }

    static int checkAck( InputStream in ) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if ( b == 0 ) {
            return b;
        }
        if ( b == -1 ) {
            return b;
        }

        if ( b == 1 || b == 2 ) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append( (char) c );
            }
            while ( c != '\n' );
            if ( b == 1 ) { // error
                System.out.print( sb.toString() );
            }
            if ( b == 2 ) { // fatal error
                System.out.print( sb.toString() );
            }
        }
        return b;
    }

    /**
     * Close any JSch sessions that were created to enable file uploads.
     */
    public static void closeAllSessions() {
        for ( Session session : new ArrayList<Session>( sessionList ) ) {
            System.out.println( "Closing JSch session with host " + session.getHost() );
            session.disconnect();
            sessionList.remove( session );
        }
    }

    public static class MyUserInfo implements UserInfo {

        String passwd;

        public MyUserInfo( String passwd ) {
            this.passwd = passwd;
        }

        public String getPassword() {
            return passwd;
        }

        public boolean promptPassword( String s ) {
            return true;
        }

        public boolean promptYesNo( String str ) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase( String message ) {
            return true;
        }

        public void showMessage( String message ) {
            System.out.println( "message = " + message );
        }

    }
}
