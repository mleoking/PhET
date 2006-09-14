package edu.colorado.phet.cck3.piccolo_cck;

import edu.colorado.phet.cck3.CCKControlPanel;
import edu.colorado.phet.cck3.CCKParameters;
import edu.colorado.phet.cck3.ICCKModule;
import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.ResistivityManager;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingClock;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 2:47:24 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKPiccoloModule extends Module implements ICCKModule {
    private String[] args;
    private CCKModel model;
    private CCKParameters cckParameters;

    public CCKPiccoloModule( String[] args ) {
        super( "CCK-Piccolo", new SwingClock( 30, 1 ) );
        cckParameters = new CCKParameters( this, args );
        this.args = args;
        setModel( new BaseModel() );
//        setSimulationPanel( new JLabel( "Simulation panel" ) );
        this.model = new CCKModel();
        setSimulationPanel( new CCKSimulationPanel( model ) );

        setControlPanel( new CCKControlPanel( this, this ) );
    }

    public Circuit getCircuit() {
        return model.getCircuit();
    }

    public void setLifelike( boolean b ) {
    }

    public boolean isLifelike() {
        return false;
    }

    public CircuitChangeListener getCircuitChangeListener() {
        return model.getCircuitChangeListener();
    }

    public CCKParameters getParameters() {
        return cckParameters;
    }

    public void setVoltmeterVisible( boolean visible ) {
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
    }

    public boolean isStopwatchVisible() {
        return false;
    }

    public void setStopwatchVisible( boolean selected ) {
    }

    public void addCurrentChart() {
    }

    public void addVoltageChart() {
    }

    public void setAllReadoutsVisible( boolean r ) {
    }

    public void setCircuit( Circuit circuit ) {
    }

    public boolean isInternalResistanceOn() {
        return false;
    }

    public void setZoom( double scale ) {
    }

    public void clear() {
    }

    public ResistivityManager getResistivityManager() {
        return model.getResistivityManager();
    }

    public boolean isElectronsVisible() {
        return false;
    }

    public void setElectronsVisible( boolean b ) {
    }

    public Rectangle2D getModelBounds() {
        return model.getModelBounds();
    }

    public void layoutElectrons( Branch[] branches ) {
        model.layoutElectrons( branches );
    }
}
