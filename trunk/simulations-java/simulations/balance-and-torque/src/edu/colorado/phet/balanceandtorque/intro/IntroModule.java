// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BrickStack;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Main module for this tab.
 *
 * @author John Blanco
 */
public class IntroModule extends Module {

    BalancingActModel model;

    public IntroModule() {
        this( new BalancingActModel() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    private IntroModule( BalancingActModel model ) {
        // TODO: i18n
        super( "Intro", model.getClock() );
        this.model = model;
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
        // TODO: Temp for prototyping - Add some initial objects to the model.
        model.addMass( new BrickStack( 1, new Point2D.Double( 2.5, 0 ) ) );
        model.addMass( new BrickStack( 1, new Point2D.Double( 2.8, 0 ) ) );
        model.addMass( new BrickStack( 2, new Point2D.Double( 3.1, 0 ) ) );
    }
}
