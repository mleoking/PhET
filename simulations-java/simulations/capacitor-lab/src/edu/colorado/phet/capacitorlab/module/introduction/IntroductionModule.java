// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.capacitorlab.module.CLModule;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;

/**
 * The "Introduction" module.
 * </p>
 * This module is identical to the "Dielectric" module, and reuses its model and canvas.
 * </p>
 * The differences are:
 * <ul>
 * <li>the dielectric is air and is not visible
 * <li>there are no dielectric controls
 * <li>the E-Field Detector is simplified (fewer controls, fewer vectors visible)
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends CLModule {

    private final DielectricModel model;
    private final DielectricCanvas canvas;

    public IntroductionModule( CLGlobalProperties globalProperties ) {
        super( CLStrings.INTRODUCTION );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        //TODO would it be OK to have the dielectric fully inserted?
        double dielectricOffset = CLConstants.PLATE_WIDTH_RANGE.getMax() + 1; // dielectric fully outside
        model = new DielectricModel( getClock(), mvt, dielectricOffset, new DielectricMaterial[] { new Air() } );

        canvas = new DielectricCanvas( model, mvt, globalProperties, true /* eFieldDetectorSimplified */, false /* dielectricVisible */ );
        setSimulationPanel( canvas );

        setControlPanel( new IntroductionControlPanel( this, model, canvas, globalProperties ) );

        reset();
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
