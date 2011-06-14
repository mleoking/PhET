// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Configuration information for a circuit.
 * This is purely a data structure, whose purpose is to reduce the number of
 * parameters required in constructors and creation methods.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CircuitConfig {

    public final IClock clock;
    public final CLModelViewTransform3D mvt;
    public final Point3D batteryLocation;
    public final double capacitorXSpacing;
    public final double capacitorYSpacing;
    public final double plateWidth;
    public final double plateSeparation;
    public final DielectricMaterial dielectricMaterial;
    public final double dielectricOffset;
    public final double wireExtent;
    public final double wireThickness;

    public CircuitConfig( IClock clock,
                          CLModelViewTransform3D mvt,
                          Point3D batteryLocation,
                          double capacitorXSpacing,
                          double capacitorYSpacing,
                          double plateWidth,
                          double plateSeparation,
                          DielectricMaterial dielectricMaterial,
                          double dielectricOffset,
                          double wireThickness,
                          double wireExtent ) {
        this.clock = clock;
        this.mvt = mvt;
        this.batteryLocation = batteryLocation;
        this.capacitorXSpacing = capacitorXSpacing;
        this.capacitorYSpacing = capacitorYSpacing;
        this.plateWidth = plateWidth;
        this.plateSeparation = plateSeparation;
        this.dielectricMaterial = dielectricMaterial;
        this.dielectricOffset = dielectricOffset;
        this.wireThickness = wireThickness;
        this.wireExtent = wireExtent;
    }
}
