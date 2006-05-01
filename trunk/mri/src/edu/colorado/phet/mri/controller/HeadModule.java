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

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.view.HeadGraphic;
import edu.colorado.phet.mri.view.SampleTargetGraphic;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.model.*;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = "The Head";

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    static double earOffsetX = 70 * MriConfig.SCALE_FOR_ORG;
    static double headOffsetY = 35 * MriConfig.SCALE_FOR_ORG;
    static Head head = new Head( new Ellipse2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + earOffsetX,
                                                       MriConfig.SAMPLE_CHAMBER_LOCATION.getY() - 25,
                                                       MriConfig.SAMPLE_CHAMBER_WIDTH - earOffsetX * 2,
                                                       MriConfig.SAMPLE_CHAMBER_HEIGHT + 100 * MriConfig.SCALE_FOR_ORG ) );
    private Detector detector;

    /**
     * Constructor
     */
    public HeadModule() {
        super( HeadModule.name, new SwingClock( delay, dt ), head );

        MriModel model = (MriModel)getModel();
        Electromagnet lowerMagnet = model.getLowerMagnet();

        // Add the horizontal gradient magnet
        GradientElectromagnet.Gradient gradient = new GradientElectromagnet.LinearGradient( 1, 0 );
        Point2D gradientMagnetLocation = new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                             MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + MriConfig.SAMPLE_CHAMBER_HEIGHT + 60 );
        GradientElectromagnet horizontalGradientMagnet = new GradientElectromagnet( gradientMagnetLocation,
                                                                                    lowerMagnet.getBounds().getWidth(),
                                                                                    lowerMagnet.getBounds().getHeight(),
                                                                                    getClock(),
                                                                                    gradient,
                                                                                    GradientElectromagnet.HORIZONTAL );
        model.addModelElement( horizontalGradientMagnet );

        // Add the vertical gradient magnet
        GradientElectromagnet.Gradient verticalGradient = new GradientElectromagnet.LinearGradient( 1, 0 );
        Point2D verticalGradientMagnetLocation = new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() - 40,
                                                                     MriConfig.SAMPLE_CHAMBER_LOCATION.getY()
                                                                     + MriConfig.SAMPLE_CHAMBER_HEIGHT / 2 );
        GradientElectromagnet verticalGradientMagnet = new GradientElectromagnet( verticalGradientMagnetLocation,
                                                                                  lowerMagnet.getBounds().getHeight(),
                                                                                  MriConfig.SAMPLE_CHAMBER_HEIGHT,
                                                                                  getClock(),
                                                                                  verticalGradient,
                                                                                  GradientElectromagnet.VERTICAL );
        model.addModelElement( verticalGradientMagnet );

        // Control panel
        HeadModuleControlPanel controlPanel = new HeadModuleControlPanel( this,
                                                                          horizontalGradientMagnet,
                                                                          verticalGradientMagnet );
        setControlPanel( controlPanel );

        // Make some dipoles
        head.createDipoles( (MriModel)getModel(), 40 );

        // Add the head graphic
        PNode headGraphic = new HeadGraphic();
        headGraphic.setOffset( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                               MriConfig.SAMPLE_CHAMBER_LOCATION.getY() - 35 );
        getGraphicsManager().addGraphic( headGraphic, getGraphicsManager().getHeadLayer() );

        // Add a detector
        Rectangle2D detectorBounds = new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH + 30,
                                                             MriConfig.SAMPLE_CHAMBER_LOCATION.getY() - 40,
                                                             150,
                                                             MriConfig.SAMPLE_CHAMBER_HEIGHT + 100 );
        detector = new Detector( detectorBounds, model );
        model.addModelElement( detector );

        // Set the initial view
        setEmRep( HeadModule.WAVE_VIEW );
    }

    public Head getHead() {
        return head;
    }

    public Detector getDetector() {
        return detector;
    }

}
