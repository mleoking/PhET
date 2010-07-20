package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.circuitconstructionkit.controls.CCKControlPanel;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.ResistivityManager;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.*;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 2:47:24 AM
 */

public class CCKModule extends PiccoloModule {
    public static Color BACKGROUND_COLOR = new Color(100, 160, 255);
    private CCKModel model;
    private CCKParameters cckParameters;
    private CCKSimulationPanel cckSimulationPanel;
    private MeasurementToolSet measurementToolSet;
    private static int delay = 30;//ms
    public static double dt = delay / 1000.0;//simulation units = seconds
    private CCKViewState viewState=new CCKViewState();

    public CCKModule(String[] args, boolean ac, boolean virtualLab) {
        super("CCK-Piccolo", new SwingClock(delay, dt));

        cckParameters = new CCKParameters(this, args, ac, virtualLab);
        setModel(new BaseModel());

        this.model = new CCKModel();
        this.measurementToolSet = new MeasurementToolSet(model);
        cckSimulationPanel = new CCKSimulationPanel(model, this, getClock());
        setSimulationPanel(cckSimulationPanel);
        setControlPanel(new CCKControlPanel(this, this));

        addModelElement(new ModelElement() {
            public void stepInTime(double dt) {
                model.stepInTime(dt);
            }
        });
        setLogoPanel(null);
    }

    public void activate() {
        super.activate();
        getSimulationPanel().requestFocus();
        Bulb.setHeightScale(0.5 * 0.75);
    }

    public Circuit getCircuit() {
        return model.getCircuit();
    }

    public void setLifelike(boolean b) {
        getCCKViewState().getLifelikeProperty().setValue(b);
    }

    public boolean isLifelike() {
        return getCCKViewState().getLifelikeProperty().getValue();
    }

    public CircuitChangeListener getCircuitChangeListener() {
        return model.getCircuitChangeListener();
    }

    public CCKParameters getParameters() {
        return cckParameters;
    }

    public void setVoltmeterVisible(boolean visible) {
        getVoltmeterModel().setVisible(visible);
    }

    public VoltmeterModel getVoltmeterModel() {
        return measurementToolSet.getVoltmeterModel();
    }

    public void addGrabBag() {
        cckSimulationPanel.addGrabBag();
    }

    public void setVirtualAmmeterVisible(boolean selected) {
        cckSimulationPanel.setVirtualAmmeterVisible(selected);
    }

    public void setSeriesAmmeterVisible(boolean selected) {
        cckSimulationPanel.setSeriesAmmeterVisible(selected);
    }

    public boolean isStopwatchVisible() {
        return false;
    }

    public void setStopwatchVisible(boolean selected) {
        cckSimulationPanel.setStopwatchVisible(selected);
    }


    public void addCurrentChart() {
        cckSimulationPanel.addCurrentChart();
    }

    public void addVoltageChart() {
        cckSimulationPanel.addVoltageChart();
    }

    public void setReadoutsVisible(boolean r) {
        viewState.setReadoutsVisible(r);
    }

    public void setCircuit(Circuit circuit) {
        model.setCircuit(circuit);
    }

    public void setZoom(double zoom) {
        cckSimulationPanel.setZoom(zoom);
    }

    public void resetAll() {
        model.resetAll();
        viewState.resetAll();
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

    public void setElectronsVisible(boolean b) {
        getCircuitNode().setElectronsVisible(b);
    }

    public Rectangle2D getModelBounds() {
        return model.getModelBounds();
    }

    public void layoutElectrons(Branch[] branches) {
        model.layoutElectrons(branches);
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
        return cckSimulationPanel.getCCKBackground();
    }

    public void setMyBackground(Color color) {
        cckSimulationPanel.setCCKBackground(color);
    }

    public void setToolboxBackgroundColor(Color color) {
        cckSimulationPanel.setToolboxBackgroundColor(color);
    }

    public Color getToolboxBackgroundColor() {
        return cckSimulationPanel.getToolboxBackgroundColor();
    }

    public CCKModel getCCKModel() {
        return model;
    }

    public boolean isReadoutVisible(Branch branch) {
        return getCircuitNode().isReadoutVisible(branch);
    }

    public void setReadoutVisible(Branch branch, boolean selected) {
        branch.setEditing(selected);//todo: not sure if this is flexible enough.
    }

    public boolean isReadoutGraphicsVisible() {
        return getCircuitNode().isReadoutGraphicVisible();
    }

    public void applicationStarted() {
        cckSimulationPanel.applicationStarted();
    }

    public void setHelpEnabled(boolean enabled) {
        super.setHelpEnabled(enabled);
        cckSimulationPanel.setHelpEnabled(enabled);
    }

    public CCKParameters getCckParameters() {
        return cckParameters;
    }

    public CCKSimulationPanel getCckSimulationPanel() {
        return cckSimulationPanel;
    }

    public void addBranchNodeFactoryListener(BranchNodeFactory.Listener listener) {
        getCircuitNode().addBranchNodeFactoryListener(listener);
    }

    public CCKViewState getCCKViewState() {
        return viewState;
    }
}
