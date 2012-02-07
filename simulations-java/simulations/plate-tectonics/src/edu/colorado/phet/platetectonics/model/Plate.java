// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.platetectonics.model.regions.Region;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.*;

public class Plate {
    private Region crust;
    private Region mantle;

    public final ObservableList<Region> regions = new ObservableList<Region>();

    public void addCrust( Region crust ) {
        assert this.crust == null;

        this.crust = crust;
        regions.add( crust );
    }

    public void addMantle( Region mantle ) {
        assert this.mantle == null;

        this.mantle = mantle;
        regions.add( mantle );
    }

    public List<SamplePoint> getSamples() {
        return unique( flatten( map( regions, new Function1<Region, Collection<? extends SamplePoint>>() {
            public Collection<? extends SamplePoint> apply( Region region ) {
                return region.getSamples();
            }
        } ) ) );
    }

    public Region getCrust() {
        return crust;
    }

    public Region getMantle() {
        return mantle;
    }
}
