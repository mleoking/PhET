// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;

/**
 * Identity map for sites.  This is its own class because it appears in the model and needs a @Data annotation for equals for regression testing.
 *
 * @author Sam Reid
 */
public @Data class IdentitySiteMap extends F<Site, Site> {
    @Override public Site f( final Site site ) {
        return site;
    }
}