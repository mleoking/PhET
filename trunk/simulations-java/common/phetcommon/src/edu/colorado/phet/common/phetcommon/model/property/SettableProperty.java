// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * In addition to being Observable, this SettableProperty<T> is also settable, that is, it adds the method setValue().
 * It does not declare the data storage type, and is thus compatible with combined properties such as And and Or.
 *
 * @author Sam Reid
 */
public abstract class SettableProperty<T> extends ObservableProperty<T> {
    private final Option<String> description;

    public SettableProperty( T oldValue ) {
        this( null, oldValue );
    }

    /**
     * Create a SettableProperty with the specified description
     *
     * @param description for use in sim data collection/processing  (can be null)
     * @param oldValue
     */
    public SettableProperty( Option<String> description, T oldValue ) {
        super( oldValue );
        this.description = description;
    }

    public Option<String> getDescription() {
        return description;
    }

    public abstract void set( T value );

    //Gets the description if there is one, otherwise the constant string "?"
    public String getDescriptionOrElseQuestion() {
        return description.getOrElse( "?" );
    }
}
