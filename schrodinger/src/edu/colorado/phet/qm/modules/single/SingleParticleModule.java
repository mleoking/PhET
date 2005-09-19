/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.qm.ModelDebugger;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends SchrodingerModule {
    public SingleParticleModule( SchrodingerApplication clock ) {
        super( "Single Particles", clock );
//        setDiscreteModel( new DiscreteModel( 100, 100 ) );
        setDiscreteModel( new DiscreteModel() );
//        setDiscreteModel( new DiscreteModel( 40,40) );
        setSchrodingerPanel( new SingleParticlePanel( this ) );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
        getModel().addModelElement( new ModelDebugger( getClass() ) );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setBrightnessSliderVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setTypeControlVisible( false );
//        getModel().addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                System.out.println( "Stepped @ "+System.currentTimeMillis());
//            }
//        } );
    }
}
