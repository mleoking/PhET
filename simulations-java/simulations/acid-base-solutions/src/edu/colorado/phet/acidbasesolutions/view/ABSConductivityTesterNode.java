// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.Components;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
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
            getPositiveProbeDragHandler().setStartEndFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                public void apply( SimSharingConstants.User.UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    sendProbeEvent( Components.conductivityTesterPositiveProbe, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                }
            } );
            getPositiveProbeDragHandler().setDragFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                boolean inSolution = tester.isPositiveProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( SimSharingConstants.User.UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    if ( inSolution != tester.isPositiveProbeInSolution() ) {
                        sendProbeEvent( Components.conductivityTesterPositiveProbe, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isPositiveProbeInSolution();
                }
            } );
        }

        // sim-sharing, negative probe
        {
            getNegativeProbeDragHandler().setStartEndFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                public void apply( SimSharingConstants.User.UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    sendProbeEvent( Components.conductivityTesterNegativeProbe, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                }
            } );
            getNegativeProbeDragHandler().setDragFunction( new SimSharingDragSequenceEventHandler.DragFunction() {
                boolean inSolution = tester.isNegativeProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( SimSharingConstants.User.UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                    if ( inSolution != tester.isNegativeProbeInSolution() ) {
                        sendProbeEvent( Components.conductivityTesterNegativeProbe, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isNegativeProbeInSolution();
                }
            } );
        }

        // sim-sharing, light bulb and battery (not interactive)
        {
            getLightBulbNode().addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendNotInteractiveEvent( Components.lightBulb, SimSharingConstants.User.UserActions.pressed );
                }
            } );
            getBatteryNode().addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendNotInteractiveEvent( Components.battery, SimSharingConstants.User.UserActions.pressed );
                }
            } );
        }
    }

    private static void sendProbeEvent( SimSharingConstants.User.UserComponent object, SimSharingConstants.User.UserAction action, boolean inSolution, boolean circuitCompleted ) {
        SimSharingManager.sendUserEvent( object, action,
                                         new Parameter( ABSSimSharing.ABSParameterKeys.isInSolution, inSolution ),
                                         new Parameter( ABSSimSharing.ABSParameterKeys.isCircuitCompleted, circuitCompleted ) );
    }
}
