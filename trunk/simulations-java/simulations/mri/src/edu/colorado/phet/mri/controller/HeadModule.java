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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.BFieldIndicatorB;
import edu.colorado.phet.mri.view.DipoleGraphic;
import edu.colorado.phet.mri.view.HeadGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * HeadModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = MriResources.getString( "Module.MriTitle" );

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    static double earOffsetX = 70 * MriConfig.SCALE_FOR_ORG;
    static double headOffsetY = 35 * MriConfig.SCALE_FOR_ORG;
    static Head head = new Head( new Ellipse2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + earOffsetX,
                                                       MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                       MriConfig.SAMPLE_CHAMBER_WIDTH - earOffsetX * 2,
                                                       MriConfig.SAMPLE_CHAMBER_HEIGHT + 0 * MriConfig.SCALE_FOR_ORG ),
                                 new Ellipse2D[]{
                                         new Ellipse2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                               MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + 120,
                                                               earOffsetX,
                                                               50 ),
                                         new Ellipse2D.Double( MriConfig.SAMPLE_CHAMBER_BOUNDS.getMaxX() - earOffsetX,
                                                               MriConfig.SAMPLE_CHAMBER_LOCATION.getY() + 120,
                                                               earOffsetX,
                                                               50 ),
                                 } );
    private Detector detector;
    private GradientElectromagnet horizontalGradientMagnet;
    private GradientElectromagnet verticalGradientMagnet;
    private PNode headGraphic;

    /**
     * Constructor
     */
    public HeadModule() {
        this( HeadModule.name );
    }

    public HeadModule( String name ) {
        super( name, new SwingClock( delay, dt ), head );
        setLogoPanelVisible( false );
    }

    public boolean auxiliarySquiggleVisible() {
        return false;
    }

    protected void init() {
        super.init();

        MriModel model = (MriModel)getModel();
        Electromagnet lowerMagnet = model.getLowerMagnet();

        // Add the horizontal gradient magnet
        GradientElectromagnet.LinearGradient gradient = new GradientElectromagnet.LinearGradient( 1, 0 );
        Point2D gradientMagnetLocation = new Point2D.Double( lowerMagnet.getPosition().getX(),
                                                             lowerMagnet.getPosition().getY() - lowerMagnet.getBounds().getHeight() );
        horizontalGradientMagnet = new LinearGradientMagnet( gradientMagnetLocation,
                                                             lowerMagnet.getBounds().getWidth(),
                                                             lowerMagnet.getBounds().getHeight(),
                                                             getClock(),
                                                             gradient,
                                                             GradientElectromagnet.HORIZONTAL );
        model.addModelElement( horizontalGradientMagnet );

        // Add the vertical gradient magnet
        GradientElectromagnet.LinearGradient verticalGradient = new GradientElectromagnet.LinearGradient( 1, 0 );
        Point2D verticalGradientMagnetLocation = new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() - 40,
                                                                     MriConfig.SAMPLE_CHAMBER_LOCATION.getY()
                                                                     + MriConfig.SAMPLE_CHAMBER_HEIGHT / 2 );
        verticalGradientMagnet = new LinearGradientMagnet( verticalGradientMagnetLocation,
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
        head.createDipoles( (MriModel)getModel(), 40.0 );

        // Add the head graphic
        headGraphic = new HeadGraphic( head );
        headGraphic.setOffset( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                               MriConfig.SAMPLE_CHAMBER_LOCATION.getY() );
        getGraphicsManager().addGraphic( headGraphic, getGraphicsManager().getHeadLayer() );

        // Add a detector
        Rectangle2D detectorBounds = new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH + 80,
                                                             MriConfig.SAMPLE_CHAMBER_LOCATION.getY() - 40,
                                                             25,
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

    protected GradientElectromagnet getHorizontalGradientMagnet() {
        return horizontalGradientMagnet;
    }

    protected GradientElectromagnet getVerticalGradientMagnet() {
        return verticalGradientMagnet;
    }

    public PNode getHeadGraphic() {
        return headGraphic;
    }

    public void setDipolesVisible( boolean visible ) {
        getGraphicsManager().setAllOfTypeVisible( DipoleGraphic.class, visible );
    }

    public void setFieldArrowsVisible( boolean visible ) {
        getGraphicsManager().setAllOfTypeVisible( BFieldIndicatorB.class, visible );
    }
}
