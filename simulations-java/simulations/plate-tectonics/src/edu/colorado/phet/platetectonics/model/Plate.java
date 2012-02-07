// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.platetectonics.model.regions.Region;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.flatten;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;

public class Plate {
    private Region crust;
    private Region mantle;

    public List<Region> getRegions() {
        return new ArrayList<Region>() {{
            add( crust );
            add( mantle );
        }};
    }

    public List<SamplePoint> getSamples() {
        return flatten( map( getRegions(), new Function1<Region, Collection<? extends SamplePoint>>() {
            public Collection<? extends SamplePoint> apply( Region region ) {
                return region.getSamples();
            }
        } ) );
    }
}
