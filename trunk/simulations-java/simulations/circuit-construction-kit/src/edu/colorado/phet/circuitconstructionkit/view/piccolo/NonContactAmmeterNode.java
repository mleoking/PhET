// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.NoiseGenerator;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:56:28 PM
 */
public class NonContactAmmeterNode extends PhetPNode {
    private TargetReadoutToolNode targetReadoutToolNode;
    private Component panel;
    private CCKModule module;
    private Circuit circuit;
    private Branch previousBranch = null; // Used to detect changes in connection state.
    DelayedRunner dragRunner = new DelayedRunner();
    DelayedRunner valueRunner = new DelayedRunner();
    private boolean constructor = true;
    private boolean noiseDirty = true;
    private double noise = 0.0;

    public NonContactAmmeterNode( Circuit circuit, Component panel, CCKModule module ) {
        this( new TargetReadoutToolNode(), panel, circuit, module );
    }

    public NonContactAmmeterNode( TargetReadoutToolNode targetReadoutTool, final Component panel, Circuit circuit, final CCKModule module ) {
        this.targetReadoutToolNode = targetReadoutTool;
        this.panel = panel;
        this.module = module;
        this.circuit = circuit;
        targetReadoutToolNode.scale( 1.0 / 60.0 * 0.75 );
        targetReadoutToolNode.setOffset( 1, 1 );
        addChild( targetReadoutTool );

        addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.nonContactAmmeter, UserComponentTypes.sprite, UserActions.startDrag,
                                                   ParameterSet.parameterSet( ParameterKeys.x, getXOffset() ).
                                                           with( ParameterKeys.y, getYOffset() )
                );
            }

            public void mouseDragged( PInputEvent event ) {
                final PDimension pt = event.getDeltaRelativeTo( NonContactAmmeterNode.this );
                dragRunner.set( new Runnable() {
                    public void run() {
                        SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.nonContactAmmeter, UserComponentTypes.sprite, UserActions.drag,
                                                           ParameterSet.parameterSet( ParameterKeys.x, getXOffset() + pt.width ).
                                                                   with( ParameterKeys.y, getYOffset() + pt.height )
                        );
                    }
                } );

                translate( pt.width, pt.height );
                update();
            }

            @Override public void mouseReleased( PInputEvent event ) {
                super.mouseReleased( event );
                dragRunner.terminate();
                SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.nonContactAmmeter, UserComponentTypes.sprite, UserActions.endDrag,
                                                   ParameterSet.parameterSet( ParameterKeys.x, getXOffset() ).
                                                           with( ParameterKeys.y, getYOffset() )
                );
            }
        } );
        addInputEventListener( new CursorHandler() );
        module.getCCKModel().getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                update();
            }
        } );
        circuit.addCircuitListener( new CircuitListenerAdapter() {

            public void junctionRemoved( Junction junction ) {
                update();
            }

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void junctionsMoved() {
                update();
            }

            public void branchesMoved( Branch[] branches ) {
                update();
            }

            public void junctionAdded( Junction junction ) {
                update();
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                update();
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                update();
            }

            public void branchAdded( Branch branch ) {
                update();
            }

        } );
        update();
        constructor = false;

        CCKModule.fluctuateRandomly( new Runnable() {
            @Override public void run() {
                noiseDirty = true;
                update();
            }
        } );
    }

    public void update() {
        Point2D target = new Point2D.Double();
        targetReadoutToolNode.localToGlobal( target );
        globalToLocal( target );
        localToParent( target );
        String[] previousText = targetReadoutToolNode.getText();

        //check for intersect with circuit.
        Branch branch = circuit.getBranch( target );
        if ( branch != null && !branch.isFixed() ) {
            double current = branch.getCurrent();
            if ( CCKModule.randomFluctuations && noiseDirty ) {
                double readout = NoiseGenerator.getReadout( current );
                noise = readout - current;
                noiseDirty = false;
            }
            if ( CCKModule.randomFluctuations ) {
                current = current + noise;
            }
            DecimalFormat df = new DecimalFormat( "0.00" );
            String amps = df.format( Math.abs( current ) );
            targetReadoutToolNode.setText( amps + " " + CCKResources.getString( "VirtualAmmeter.Amps" ) );
        }
        else {
            resetText();
        }

        // Send a sim sharing message if the connection state has changed.
        if ( previousBranch != branch && !constructor && getVisible() ) {
            IModelAction modelAction = previousBranch == null ? CCKSimSharing.ModelActions.connectionFormed : CCKSimSharing.ModelActions.connectionBroken;
            SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.nonContactAmmeterModel,
                                                ModelComponentTypes.modelElement,
                                                modelAction, ParameterSet.parameterSet( CCKSimSharing.ParameterKeys.component, branch == null ? "none" : branch.getUserComponentID().toString() )
            );
            previousBranch = branch;
        }

        // Send a sim sharing message if the readout has changed.
        String[] currentText = targetReadoutToolNode.getText();
        boolean change = ( previousText.length != currentText.length && !constructor ) ||
                         ( previousText.length == 1 && currentText.length == 1 && !previousText[0].equals( currentText[0] ) );
        if ( change && getVisible() ) {

            final String currentlyDisplayedText = ( branch == null || currentText.length == 0 ) ? "undefined" : currentText[0];
            valueRunner.set( new Runnable() {
                public void run() {
                    SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.nonContactAmmeterModel,
                                                        ModelComponentTypes.modelElement,
                                                        CCKSimSharing.ModelActions.measuredCurrentChanged,
                                                        new ParameterSet( new Parameter( new ParameterKey( "current" ), currentlyDisplayedText ) ) );
                }
            } );

        }
    }

    private void resetText() {
        String[] text = new String[]{
                CCKResources.getString( "VirtualAmmeter.HelpString1" ),
                CCKResources.getString( "VirtualAmmeter.HelpString2" )
        };
        targetReadoutToolNode.setText( text );
    }

}
