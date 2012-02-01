// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import edu.colorado.phet.platetectonics.model.SamplePoint;

/**
 * A quadrilateral patch of cross-section
 */
public class CrossSectionPatch {
    public final SamplePoint a;
    public final SamplePoint b;
    public final SamplePoint c;

    public CrossSectionPatch( SamplePoint a, SamplePoint b, SamplePoint c ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
