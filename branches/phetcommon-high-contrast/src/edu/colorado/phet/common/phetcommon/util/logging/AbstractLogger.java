package edu.colorado.phet.common.phetcommon.util.logging;


public abstract class AbstractLogger implements ILogger {
    
    private boolean enabled;
    
    protected AbstractLogger( boolean enabled ) {
        this.enabled = enabled;
    }
    
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void warning( String message ) {
        log( "WARNING: " + message );
    }

    public void error( String message ) {
        log( "ERROR: " + message );
    }
    
    public void test() {
        log( "test message" );
        warning( "test warning" );
        error( "test error" );
    }
}
