/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PiccoloModule;

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
    int floorY = 600;

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
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                energyModel.stepInTime( dt / 10.0 );
            }
        } );

        EnergyPanel energyPanel = new EnergyPanel( this );
        setControlPanel( energyPanel );

        energyCanvas = new EC3Canvas( this );
        setPhetPCanvas( energyCanvas );

        energyFrame = new JFrame();
        energyFrame.setContentPane( new BarGraphCanvas( this ) );
        int frameWidth = 200;
        energyFrame.setSize( frameWidth, 600 );
        energyFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - frameWidth, 0 );

        init();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        energyFrame.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        energyFrame.setVisible( false );
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
        AbstractSpline revspline = spline.createReverseSpline();
        SplineGraphic splineGraphic = new SplineGraphic( energyCanvas, spline, revspline );
        energyModel.addSpline( spline, revspline );

        energyCanvas.addSplineGraphic( splineGraphic );
    }
}
