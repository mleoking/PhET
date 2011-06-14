// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A list whose items may or may not be serializable; the list as a whole is
 * serializable and any serializable items will be serialized.
 */
public class OptionalItemSerializableList extends ListDecorator implements Externalizable {

    public OptionalItemSerializableList() {
        underlying = new ArrayList();
    }

    public OptionalItemSerializableList( List initial ) {
        underlying = new ArrayList( initial );
    }

    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        objectOutput.writeInt( countSerializables() );

        for( int i = 0; i < size(); i++ ) {
            Object o = get( i );

            if( o instanceof Serializable ) {
                objectOutput.writeObject( o );
            }
        }
    }

    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        int count = objectInput.readInt();

        underlying = new ArrayList( count );

        for( int i = 0; i < count; i++ ) {
            add( objectInput.readObject() );
        }
    }

    private int countSerializables() {
        int count = 0;

        for( int i = 0; i < size(); i++ ) {
            Object o = get( i );

            if( o instanceof Serializable ) {
                ++count;
            }
        }

        return count;
    }
}
