package edu.colorado.phet.common.phetcommon.model;

/**
 * This can be used to represent a Boolean value in a MVC style pattern.  It remembers its default value and can be reset.
 *
 * @author Sam Reid
 */
public class MutableBoolean extends Observable<Boolean> {
    public MutableBoolean( Boolean value ) {
        super( value );
    }
}
