// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Glass;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModule extends CLModule {

    private final DielectricModel model;
    private final DielectricCanvas canvas;

    public DielectricModule( CLGlobalProperties globalProperties ) {
        this( CLStrings.DIELECTRIC, globalProperties );
    }

    protected DielectricModule( String title, CLGlobalProperties globalProperties ) {
        super( title );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        DielectricMaterial[] materials = new DielectricMaterial[] { new CustomDielectricMaterial(), new Teflon(), new Paper(), new Glass() };
        model = new DielectricModel( getClock(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault(), materials );

        canvas = new DielectricCanvas( model, mvt, globalProperties, false /* eFieldDetectorSimplified */, true /* dielectricVisible */ );
        setSimulationPanel( canvas );

        setControlPanel( new DielectricControlPanel( this, model, canvas, globalProperties ) );

        reset();
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
