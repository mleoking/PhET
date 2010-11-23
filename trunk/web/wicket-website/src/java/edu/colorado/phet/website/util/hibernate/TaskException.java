package edu.colorado.phet.website.util.hibernate;

import org.apache.log4j.Level;

public class TaskException extends RuntimeException {
    public final Level level;

    public TaskException( String message ) {
        this( message, Level.WARN );
    }

    public TaskException( String message, Level level ) {
        super( message );
        this.level = level;
    }
}
