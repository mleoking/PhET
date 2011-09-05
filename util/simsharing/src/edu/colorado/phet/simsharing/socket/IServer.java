// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import testjavasockets.Client;

import java.io.IOException;

/**
 * @author Sam Reid
 */
public interface IServer {
    Object ask( Object question ) throws IOException, ClassNotFoundException;

    void tell( Object statement ) throws IOException;

    public static class Impl {
        //Actors.remote().actorFor( "server", MessageServer.HOST_IP_ADDRESS, MessageServer.PORT );
        public static IServer getServer() throws ClassNotFoundException, IOException {
            return new Client( Server.HOST_IP_ADDRESS, Server.PORT );
        }
    }
}
