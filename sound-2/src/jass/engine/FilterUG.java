package jass.engine;


/**
 * FilterUG abstract class. Like InOUt but only allows one source.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public abstract class FilterUG extends InOut {

    public FilterUG( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Add source to Sink. Override to allow one input only
     *
     * @param s Source to add.
     * @return object representing Source in Sink (may be null).
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        if( getSources().length > 0 ) {
            throw new SinkIsFullException();
        }
        else {
            return super.addSource( s );
        }
    }
}
