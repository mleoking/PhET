// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.equalitylab.model;

import fj.F;
import lombok.EqualsAndHashCode;

import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Site;

/**
 * Mapping from left sites to right sites for circular slices.
 * Factored into a separate class for testing serialization.
 * Lombok is used to generate the equality test for regression testing.
 *
 * @author Sam Reid
 */
@EqualsAndHashCode(callSuper = false) class SiteMap extends F<Site, Site> {
    @Override public Site f( final Site s ) {
        return s.eq( 0, 0 ) ? new Site( 1, 2 ) :
               s.eq( 0, 1 ) ? new Site( 0, 2 ) :
               s.eq( 0, 2 ) ? new Site( 1, 1 ) :
               s.eq( 1, 0 ) ? new Site( 0, 1 ) :
               s.eq( 1, 1 ) ? new Site( 1, 0 ) :
               s.eq( 1, 2 ) ? new Site( 0, 0 ) :
               null;
    }
}