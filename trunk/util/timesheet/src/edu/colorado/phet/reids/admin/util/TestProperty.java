package edu.colorado.phet.reids.admin.util;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

public class TestProperty {
    private String name = "sam";
    private ArrayList<SimpleObserver> nameChangeListeners = new ArrayList<SimpleObserver>();

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        if (this.name.equals( name )){
            for ( SimpleObserver nameChangeListener : nameChangeListeners ) {
                nameChangeListener.update();
            }
        }
    }

    public void addNameChangeListener( SimpleObserver nameChangeListener ) {
        nameChangeListeners.add( nameChangeListener );
    }

    public Property<String> getNameProperty() {
        return new Property<String>( name ) {
            public void setValue( String value ) {
                setName( value );
            }

            public String getValue() {
                return name;
            }

            public void reset() {
                setName( "sam" );
            }

            public void addObserver( SimpleObserver simpleObserver ) {
                addObserver( simpleObserver, true );
            }

            public void addObserver( SimpleObserver simpleObserver, boolean notifyOnAdd ) {
                nameChangeListeners.add( simpleObserver );
                if ( notifyOnAdd ) { simpleObserver.update(); }
            }
        };
    }
}
