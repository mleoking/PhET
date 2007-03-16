package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:56:28 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class VirtualAmmeterNode extends PhetPNode {
    private TargetReadoutToolNode targetReadoutToolNode;
    private Component panel;
    private ICCKModule module;
    private Circuit circuit;

    public VirtualAmmeterNode( Circuit circuit, Component panel, ICCKModule module ) {
        this( new TargetReadoutToolNode(), panel, circuit, module );
    }

    public VirtualAmmeterNode( TargetReadoutToolNode targetReadoutTool, final Component panel, Circuit circuit, final ICCKModule module ) {
        this.targetReadoutToolNode = targetReadoutTool;
        this.panel = panel;
        this.module = module;
        this.circuit = circuit;
        targetReadoutToolNode.scale( 1.0 / 60.0 * 0.75 );
        targetReadoutToolNode.setOffset( 1, 1 );
        addChild( targetReadoutTool );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension pt = event.getDeltaRelativeTo( VirtualAmmeterNode.this );
                translate( pt.width, pt.height );
                update();
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
    }

    public void update() {
        Point2D target = new Point2D.Double();
        targetReadoutToolNode.localToGlobal( target );
        globalToLocal( target );
        localToParent( target );
        //check for intersect with circuit.
        Branch branch = circuit.getBranch( target );
        if( branch != null ) {
            double current = branch.getCurrent();
            DecimalFormat df = new DecimalFormat( "0.00" );
            String amps = df.format( Math.abs( current ) );
            targetReadoutToolNode.setText( amps + " " + SimStrings.get( "VirtualAmmeter.Amps" ) );
        }
        else {
            resetText();
        }
    }

    private void resetText() {
        String[] text = new String[]{
                SimStrings.get( "VirtualAmmeter.HelpString1" ),
                SimStrings.get( "VirtualAmmeter.HelpString2" )
        };
        targetReadoutToolNode.setText( text );
    }

}
