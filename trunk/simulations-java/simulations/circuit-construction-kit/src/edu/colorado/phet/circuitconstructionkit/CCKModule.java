// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.controls.CCKControlPanel;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.ResistivityManager;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.BranchNodeFactory;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CircuitNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.MeasurementToolSet;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.VoltmeterModel;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 2:47:24 AM
 */

public class CCKModule extends PiccoloModule {
    public static Color BACKGROUND_COLOR = new Color( 100, 160, 255 );
    public static boolean createUnpickableCircuit = false;
    public static boolean randomFluctuations = false;
    public static final Random random = new Random();
    public final boolean blackBox;
    private CCKModel model;
    private CCKParameters cckParameters;
    private CCKSimulationPanel cckSimulationPanel;
    private MeasurementToolSet measurementToolSet;
    private static int delay = 30;//ms
    public static double dt = delay / 1000.0;//simulation units = seconds
    private CCKViewState viewState = new CCKViewState();

    //Listeners that are notified when the module is reset
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    public CCKModule( String[] args, boolean ac, boolean virtualLab ) {
        super( "CCK-Piccolo", new SwingClock( delay, dt ) );

        //Show the black box, see https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3602
        this.blackBox = Arrays.asList( args ).contains( "stanford-black-box" );
        boolean blackBoxWithElectrons = Arrays.asList( args ).contains( "stanford-black-box-with-electrons" );

        randomFluctuations = Arrays.asList( args ).contains( "randomFluctuations" );

        cckParameters = new CCKParameters( this, args, ac, virtualLab, blackBox, blackBoxWithElectrons );
        setModel( new BaseModel() );

        this.model = new CCKModel();
        this.measurementToolSet = new MeasurementToolSet( model );

        cckSimulationPanel = new CCKSimulationPanel( model, this, getClock(), blackBox );
        setSimulationPanel( cckSimulationPanel );
        setControlPanel( new CCKControlPanel( this, this ) );

        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                model.stepInTime( dt );
            }
        } );
        setLogoPanel( null );

        if ( CircuitConstructionKitDCApplication.isStanfordStudyA() ) {
            setElectronsVisible( false );
        }

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
        getCCKViewState().getLifelikeProperty().set( b );
    }

    public boolean isLifelike() {
        return getCCKViewState().getLifelikeProperty().get();
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

    public void setReadoutsVisible( boolean r ) {
        viewState.setReadoutsVisible( r );
    }

    public void setCircuit( Circuit circuit ) {
        model.setCircuit( circuit );

        //Make existing elements unpickable for black box, see https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3602
        if ( this.blackBox ) {
            cckSimulationPanel.makeCircuitUnpickable();
            for ( int i = 0; i < circuit.getJunctions().length; i++ ) {
                circuit.getJunctions()[i].setFixed( true );
            }
        }
    }

    public void setZoom( double zoom ) {
        cckSimulationPanel.setZoom( zoom );
    }

    public void resetAll() {
        model.resetAll();
        viewState.resetAll();

        //Notify listeners that the module has been reset
        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }
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
        return cckSimulationPanel.getCCKBackground();
    }

    public void setMyBackground( Color color ) {
        cckSimulationPanel.setCCKBackground( color );
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

    public void addBranchNodeFactoryListener( BranchNodeFactory.Listener listener ) {
        getCircuitNode().addBranchNodeFactoryListener( listener );
    }

    public CCKViewState getCCKViewState() {
        return viewState;
    }

    //Adds a listener that will be notified when this module is reset
    public void addResetListener( VoidFunction0 voidFunction0 ) {
        resetListeners.add( voidFunction0 );
    }

    // Methods to support random fluctuations, see #3682
    public static double getTimeToNextRandomFluctuation() {
        double maxDelay = 3000;
        final double minDelay = 200;
        final double range = ( maxDelay - minDelay );
        return Math.random() * range + minDelay;
    }

    public static void fluctuateRandomly( final Runnable runnable ) {
        if ( CCKModule.randomFluctuations ) {
            final double[] timeToNextRandom = {CCKModule.getTimeToNextRandomFluctuation()};
            final double[] lastRandomUpdateTime = {System.currentTimeMillis()};
            new Timer( 100, new ActionListener() {
                @Override public void actionPerformed( ActionEvent e ) {
                    if ( System.currentTimeMillis() - lastRandomUpdateTime[0] > timeToNextRandom[0] ) {
                        runnable.run();
                        timeToNextRandom[0] = CCKModule.getTimeToNextRandomFluctuation();
                        lastRandomUpdateTime[0] = System.currentTimeMillis();
                    }
                }
            } ).start();
        }
    }
}