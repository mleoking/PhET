package edu.colorado.phet.circuitconstructionkit;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.circuitconstructionkit.controls.CCKControlPanel;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.ResistivityManager;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CircuitNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.MeasurementToolSet;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.VoltmeterModel;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 2:47:24 AM
 */

public class CCKModule extends PiccoloModule {
    public static Color BACKGROUND_COLOR = new Color( 100, 160, 255 );
    private String[] args;
    private CCKModel model;
    private CCKParameters cckParameters;
    private CCKSimulationPanel cckSimulationPanel;
    private MeasurementToolSet measurementToolSet;

    public CCKModule( String[] args ) {
        super( "CCK-Piccolo", new SwingClock( 30, 1 ) );

        cckParameters = new CCKParameters( this, args );
        this.args = args;
        setModel( new BaseModel() );

        this.model = new CCKModel();
        this.measurementToolSet = new MeasurementToolSet( model );
        cckSimulationPanel = new CCKSimulationPanel( model, this, getClock() );
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
        Bulb.setHeightScale( 0.5 * 0.75 );
    }

    public Circuit getCircuit() {
        return model.getCircuit();
    }

    public void setLifelike( boolean b ) {
        cckSimulationPanel.setLifelike( b );
    }

    public boolean isLifelike() {
        return cckSimulationPanel.isLifelike();
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

    public void addGrabBag() {
        cckSimulationPanel.addGrabBag();
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
        cckSimulationPanel.setVirtualAmmeterVisible( selected );
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        cckSimulationPanel.setSeriesAmmeterVisible( selected );
    }

    public boolean isStopwatchVisible() {
        return false;
    }

    public void setStopwatchVisible( boolean selected ) {
        cckSimulationPanel.setStopwatchVisible( selected );
    }


    public void addCurrentChart() {
        cckSimulationPanel.addCurrentChart();
    }

    public void addVoltageChart() {
        cckSimulationPanel.addVoltageChart();
    }

    public void setAllReadoutsVisible( boolean r ) {
        getCircuitNode().setAllReadoutsVisible( r );
    }

    public void setCircuit( Circuit circuit ) {
        model.setCircuit( circuit );
    }

    public void setZoom( double zoom ) {
        cckSimulationPanel.setZoom( zoom );
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
        model.selectAll();
    }

    public void addTestCircuit() {
    }

    public void deleteSelectedBranches() {
        model.deleteSelectedBranches();
    }

    public void desolderSelection() {
        model.desolderSelectedJunctions();
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
        return getCircuitNode().isReadoutVisible( branch );
    }

    public void setReadoutVisible( Branch branch, boolean selected ) {
        branch.setEditing( selected );//todo: not sure if this is flexible enough.
    }

    public boolean isReadoutGraphicsVisible() {
        return getCircuitNode().isReadoutGraphicVisible();
    }

    public void applicationStarted() {
        cckSimulationPanel.applicationStarted();
    }

    public void setHelpEnabled( boolean enabled ) {
        super.setHelpEnabled( enabled );
        cckSimulationPanel.setHelpEnabled( enabled );
    }

    public CCKParameters getCckParameters() {
        return cckParameters;
    }

    public CCKSimulationPanel getCckSimulationPanel() {
        return cckSimulationPanel;
    }
}
