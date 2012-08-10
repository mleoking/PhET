// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model;

import fj.F;
import lombok.EqualsAndHashCode;

import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Site;

/**
 * Identity map for sites.  This is its own class because it appears in the model and needs a @Data annotation for equals for regression testing.
 *
 * @author Sam Reid
 */
@EqualsAndHashCode(callSuper = false) class IdentitySiteMap extends F<Site, Site> {
    @Override public Site f( final Site site ) {
        return site;
    }
}