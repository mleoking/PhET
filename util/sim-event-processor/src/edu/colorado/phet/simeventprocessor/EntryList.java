// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;

/**
 * @author Sam Reid
 */
public class EntryList extends ArrayList<JavaEntry> {
    public void add( String object, String action, Parameter... parameters ) {
        add( new JavaEntry( object, action, parameters ) );
    }
}
