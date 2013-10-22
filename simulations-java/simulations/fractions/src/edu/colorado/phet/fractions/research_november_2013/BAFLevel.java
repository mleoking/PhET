// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.util.List;

/**
 * Created by Sam on 10/22/13.
 */
public class BAFLevel {
    public final String name;
    public final List<String> targets;
    private String type;

    public BAFLevel( String name, String type, List<String> targets ) {
        this.name = name;
        this.type = type;
        this.targets = targets;
    }
}
