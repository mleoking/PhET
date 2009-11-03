/* Copyright 2008, University of Colorado */
package edu.colorado.phet.common.phetcommon.util.logging;

public interface ILogger {
    
    public void setEnabled( boolean enabled );
    
    public boolean isEnabled();

    public void log( String message );

    public void warning( String message );

    public void error( String message );
    
    public void test();
}
