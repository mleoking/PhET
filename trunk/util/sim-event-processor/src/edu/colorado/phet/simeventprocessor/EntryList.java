// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;

/**
 * @author Sam Reid
 */
public class EntryList extends ArrayList<Entry> {
    public void add( String object, String action, Parameter... parameters ) {
        add( new Entry( object, action, parameters ) );
    }
}
