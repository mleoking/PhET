package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKControlPanel;
import edu.colorado.phet.cck.CCKParameters;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitChangeListener;
import edu.colorado.phet.cck.model.ResistivityManager;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;

import java.awt.*;
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
    private CCKSimulationPanel cckSimulationPanel;
    private MeasurementToolSet measurementToolSet;

    public CCKPiccoloModule( String[] args ) {
        super( "CCK-Piccolo", new SwingClock( 30, 1 ) );


        cckParameters = new CCKParameters( this, args );
        this.args = args;
        setModel( new BaseModel() );

        this.model = new CCKModel();
        this.measurementToolSet = new MeasurementToolSet( model );
        cckSimulationPanel = new CCKSimulationPanel( model, this );
        setSimulationPanel( cckSimulationPanel );
        setControlPanel( new CCKControlPanel( this, this ) );

        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                model.stepInTime( dt );
            }
        } );
        setLogoPanel( null );
    }

    public void activate() {
        super.activate();
        getSimulationPanel().requestFocus();
        Bulb.setHeightScale( 0.5 );
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
        getVoltmeterModel().setVisible( visible );
    }

    public VoltmeterModel getVoltmeterModel() {
        return measurementToolSet.getVoltmeterModel();
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
        cckSimulationPanel.setVirtualAmmeterVisible( selected );
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
    }

    public boolean isStopwatchVisible() {
        return false;
    }

    public void setStopwatchVisible( boolean selected ) {
        cckSimulationPanel.setStopwatchVisible( selected );
    }

    public void addCurrentChart() {
    }

    public void addVoltageChart() {
    }

    public void setAllReadoutsVisible( boolean r ) {
        getCircuitNode().setAllReadoutsVisible( r );
    }

    public void setCircuit( Circuit circuit ) {
        model.setCircuit( circuit );
    }

    public boolean isInternalResistanceOn() {
        return false;
    }

    public void setZoom( double scale ) {
    }

    public void clear() {
        model.clear();
    }

    public ResistivityManager getResistivityManager() {
        return model.getResistivityManager();
    }

    public boolean isElectronsVisible() {
        return getCircuitNode().isElectronsVisible();
    }

    private CircuitNode getCircuitNode() {
        return cckSimulationPanel.getCircuitNode();
    }

    public void setElectronsVisible( boolean b ) {
        getCircuitNode().setElectronsVisible( b );
    }

    public Rectangle2D getModelBounds() {
        return model.getModelBounds();
    }

    public void layoutElectrons( Branch[] branches ) {
        model.layoutElectrons( branches );
    }

    public void resetDynamics() {
        model.resetDynamics();
    }

    public void selectAll() {
    }

    public void addTestCircuit() {
    }

    public void deleteSelection() {
    }

    public void desolderSelection() {
    }

    public Color getMyBackground() {
        return getSimulationPanel().getBackground();
    }

    public void setMyBackground( Color color ) {
        getSimulationPanel().setBackground( color );
    }

    public void setToolboxBackgroundColor( Color color ) {
        cckSimulationPanel.setToolboxBackgroundColor( color );
    }

    public Color getToolboxBackgroundColor() {
        return cckSimulationPanel.getToolboxBackgroundColor();
    }

    public CCKModel getCCKModel() {
        return model;
    }

    public boolean isReadoutVisible( Branch branch ) {
        return true;
    }

    public void setReadoutVisible( Branch branch, boolean selected ) {
    }

    public boolean isReadoutGraphicsVisible() {
        return true;
    }

}
