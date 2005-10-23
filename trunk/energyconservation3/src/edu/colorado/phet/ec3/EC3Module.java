/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Module extends PiccoloModule {
    private EnergyConservationModel energyModel;
    private EC3Canvas energyCanvas;
    private EnergyLookAndFeel energyLookAndFeel = new EnergyLookAndFeel();
    private JFrame energyFrame;
    private int floorY = 600;
    private TimeSeriesPlaybackPanel timeSeriesPlaybackPanel;
    private EC3TimeSeriesModel energyTimeSeriesModel;
    private JFrame chartFrame;

    /**
     * @param name
     * @param clock
     */
    public EC3Module( String name, AbstractClock clock ) {
        super( name, clock );
        energyModel = new EnergyConservationModel( floorY );

        Floor floor = new Floor( getEnergyConservationModel(), energyModel.getZeroPointPotentialY() );
        energyModel.addFloor( floor );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EC3TimeSeriesModel( this );
        clock.addClockTickListener( energyTimeSeriesModel );

        EnergyPanel energyPanel = new EnergyPanel( this );
        setControlPanel( energyPanel );

        energyCanvas = new EC3Canvas( this );
        setPhetPCanvas( energyCanvas );

        energyFrame = new JFrame();
        energyFrame.setContentPane( new BarGraphCanvas( this ) );
        int frameWidth = 200;
        energyFrame.setSize( frameWidth, 600 );
        energyFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - frameWidth, 0 );

        chartFrame = new JFrame( "Charts" );
        chartFrame.setContentPane( new ChartCanvas( this ) );
        chartFrame.setSize( 400, 400 );
        chartFrame.setLocation( 0, 0 );

        init();


        timeSeriesPlaybackPanel = new TimeSeriesPlaybackPanel( energyTimeSeriesModel );


    }

    public void stepModel( double dt ) {
        energyModel.stepInTime( dt / 10.0 );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        energyFrame.setVisible( true );
        chartFrame.setVisible( true );
        app.getPhetFrame().getBasicPhetPanel().setAppControlPanel( timeSeriesPlaybackPanel );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyFrame.setVisible( false );
        chartFrame.setVisible( false );
        app.getPhetFrame().getBasicPhetPanel().setAppControlPanel( new JLabel( "This space for rent." ) );
    }

    public EnergyConservationModel getEnergyConservationModel() {
        return energyModel;
    }

    public EnergyLookAndFeel getEnergyLookAndFeel() {
        return energyLookAndFeel;
    }

    public EC3Canvas getEnergyConservationCanvas() {
        return energyCanvas;
    }

    public void reset() {
        energyModel.reset();
        energyCanvas.reset();
        energyTimeSeriesModel.reset();
        energyTimeSeriesModel.setLiveMode();
        init();
    }

    private void init() {
        Body body = new Body( Body.createDefaultBodyRect() );
        body.setPosition( 100, 0 );
        energyModel.addBody( body );

        for( int i = 0; i < energyModel.numBodies(); i++ ) {
            BodyGraphic bodyGraphic = new BodyGraphic( this, energyModel.bodyAt( i ) );
            energyCanvas.addBodyGraphic( bodyGraphic );
        }
        CubicSpline spline = new CubicSpline( EC3Canvas.NUM_CUBIC_SPLINE_SEGMENTS );
        spline.addControlPoint( 47, 170 );
        spline.addControlPoint( 336, 543 );
        spline.addControlPoint( 669, 152 );
        SplineSurface surface = new SplineSurface( spline );
//        AbstractSpline revspline = spline.createReverseSpline();
        SplineGraphic splineGraphic = new SplineGraphic( energyCanvas, surface );
        energyModel.addSplineSurface( surface );
        energyCanvas.addSplineGraphic( splineGraphic );
    }

    public Object getModelState() {
        return energyModel.copyState();
    }

    public void setState( EnergyConservationModel model ) {
        energyModel.setState( model );
        redrawAllGraphics();
    }

    private void redrawAllGraphics() {
        energyCanvas.redrawAllGraphics();
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return energyTimeSeriesModel;
    }
}
