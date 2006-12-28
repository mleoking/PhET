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

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.mri.model.SampleScannerB;
import edu.colorado.phet.mri.view.SampleTargetGraphic;
import edu.colorado.phet.mri.view.computedimage.ComputedImageWindow;

/**
 * ScanModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ScanModuleB extends HeadModule {
    private ComputedImageWindow computedImageWindow;
    private SampleScannerB sampleScanner;

    public ScanModuleB() {
        super( "Scanner II" );
    }

    protected void init() {
        super.init();

        System.out.println( "ScanModuleB.init" );

        MriModel model = (MriModel)getModel();
        RadiowaveSource radioSource = model.getRadiowaveSource();
        radioSource.setFrequency( 42E6 );
        Electromagnet magnet = model.getLowerMagnet();
        magnet.setFieldStrength( 33 / MriConfig.CURRENT_TO_FIELD_FACTOR );
//        magnet.setCurrent( 33 );
        magnet = model.getUpperMagnet();
        magnet.setFieldStrength( 33 / MriConfig.CURRENT_TO_FIELD_FACTOR );
//        magnet.setCurrent( 33 );

        double dwellTime = 1000;
        double stepSize = 20;
        sampleScanner = new SampleScannerB( model, getHead(),
                                            getDetector(),
                                            getClock(),
                                            dwellTime,
                                            stepSize,
                                            getHorizontalGradientMagnet(),
                                            getVerticalGradientMagnet() );
        SampleTargetGraphic sampleTargetGraphic = new SampleTargetGraphic( sampleScanner.getSampleTarget() );
        getGraphicsManager().addGraphic( sampleTargetGraphic );

        getDetector().setDetectingPeriod( Double.MAX_VALUE );

        computedImageWindow = new ComputedImageWindow( getHead().getBounds(),
                                                       sampleScanner,
                                                       getDetector() );
        computedImageWindow.setVisible( false );
    }

    public void activate() {
        super.activate();
        computedImageWindow.setVisible( true );
        sampleScanner.start();
    }

    public void deactivate() {
        super.deactivate();
        computedImageWindow.setVisible( false );
    }
}
