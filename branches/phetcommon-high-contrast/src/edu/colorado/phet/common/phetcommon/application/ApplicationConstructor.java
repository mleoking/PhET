package edu.colorado.phet.common.phetcommon.application;

/**
     * We need one of these to start the simulation.
 */
public interface ApplicationConstructor {
    PhetApplication getApplication( PhetApplicationConfig config );
}
