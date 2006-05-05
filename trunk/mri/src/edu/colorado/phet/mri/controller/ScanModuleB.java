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
import edu.colorado.phet.mri.view.computedimage.ComputedImageWindow;
import edu.colorado.phet.common.util.PhetUtilities;

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

        double dwellTime = 100;
        double stepSize = 50;
        SampleScannerB sampleScanner = new SampleScannerB( model, getHead(), getDetector(), getClock(), dwellTime, stepSize );
        SampleTargetGraphic sampleTargetGraphic = new SampleTargetGraphic( sampleScanner.getSampleTarget() );
        getGraphicsManager().addGraphic( sampleTargetGraphic );

        getDetector().setDetectingPeriod( Double.MAX_VALUE );

        ComputedImageWindow ciw = new ComputedImageWindow( getHead().getBounds(),
                                                           sampleScanner,
                                                           getDetector() );
        ciw.setVisible( true );
        sampleScanner.start();

    }
}
