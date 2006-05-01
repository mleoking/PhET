/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.SampleTargetGraphic;

/**
 * ScanModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ScanModuleB extends HeadModule {

    public ScanModuleB() {
        super( "Scanner II");
        MriModel model = (MriModel)getModel();
        RadiowaveSource radioSource = model.getRadiowaveSource();
        radioSource.setFrequency( 42E6 );
        Electromagnet magnet = model.getLowerMagnet();
        magnet.setCurrent( 33 );
        magnet = model.getUpperMagnet();
        magnet.setCurrent( 33 );


//        SampleTarget sampleTarget = new SampleTarget();
//        sampleTarget.setLocation( 300, 300 );
//        SampleTargetGraphic sampleTargetGraphic = new SampleTargetGraphic( sampleTarget );
//        getGraphicsManager().addGraphic( sampleTargetGraphic );
//
//        sampleTarget.addChangeListener( new SampleTargetModelConfigurator( (MriModel)getModel() ) );

        SampleScanner sampleScanner = new SampleScanner( getHead() );
        model.addModelElement( sampleScanner);
        SampleTargetGraphic sampleTargetGraphic = new SampleTargetGraphic( sampleScanner.getSampleTarget() );
        getGraphicsManager().addGraphic( sampleTargetGraphic );
    }
}
