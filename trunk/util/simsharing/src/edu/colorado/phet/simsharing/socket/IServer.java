// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

/**
 * @author Sam Reid
 */
public interface IServer {
    Object ask( Object question );

    void tell( Object statement );

    public static class Impl {
        //Actors.remote().actorFor( "server", Server.HOST_IP_ADDRESS, Server.PORT );
        public static IServer getServer() {
            return new IServer() {
                public Object ask( Object question ) {
                    return null;
                }

                public void tell( Object statement ) {
                }
            };
        }
    }
}
