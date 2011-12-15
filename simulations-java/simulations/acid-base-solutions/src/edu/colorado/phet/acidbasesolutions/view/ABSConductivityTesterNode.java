// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.Objects;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.Parameters;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.ConductivityTesterNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Specialization of the conductivity node for Acid-Base Solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConductivityTesterNode extends ConductivityTesterNode {

    public ABSConductivityTesterNode( final ConductivityTester tester, ModelViewTransform transform, Color wireColor, Color positiveProbeFillColor, Color negativeProbeFillColor, boolean dev ) {
        super( tester, ModelViewTransform.createIdentity(), Color.BLACK, Color.RED, Color.BLACK, dev );

        //Wire up so the conductivity tester is only shown when selected
        tester.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( tester.isVisible() );
            }
        } );

        // sim-sharing, positive probe
        {
            getPositiveProbeDragHandler().setStartEndDragFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    sendProbeEvent( Objects.CONDUCTIVITY_TESTER_POSITIVE_PROBE, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                }
            } );
            getPositiveProbeDragHandler().setDraggingFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                boolean inSolution = tester.isPositiveProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    if ( inSolution != tester.isPositiveProbeInSolution() ) {
                        sendProbeEvent( Objects.CONDUCTIVITY_TESTER_POSITIVE_PROBE, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isPositiveProbeInSolution();
                }
            } );
        }

        // sim-sharing, negative probe
        {
            getNegativeProbeDragHandler().setStartEndDragFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    sendProbeEvent( Objects.CONDUCTIVITY_TESTER_NEGATIVE_PROBE, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                }
            } );
            getNegativeProbeDragHandler().setDraggingFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                boolean inSolution = tester.isNegativeProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    if ( inSolution != tester.isNegativeProbeInSolution() ) {
                        sendProbeEvent( Objects.CONDUCTIVITY_TESTER_NEGATIVE_PROBE, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isNegativeProbeInSolution();
                }
            } );
        }

        // sim-sharing, light bulb and battery (not interactive)
        {
            getLightBulbNode().addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendNotInteractiveEvent( Objects.LIGHT_BULB, Actions.PRESSED );
                }
            } );
            getBatteryNode().addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendNotInteractiveEvent( Objects.BATTERY, Actions.PRESSED );
                }
            } );
        }
    }

    private static void sendProbeEvent( String object, String action, boolean inSolution, boolean circuitCompleted ) {
        SimSharingManager.sendEvent( object, action,
                                     new Parameter( Parameters.IS_IN_SOLUTION, inSolution ),
                                     new Parameter( Parameters.IS_CIRCUIT_COMPLETED, circuitCompleted ) );
    }
}
