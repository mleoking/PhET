// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.ParameterKeys;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.ConductivityTesterNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler.DragFunction;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Specialization of the conductivity node for Acid-Base Solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConductivityTesterNode extends ConductivityTesterNode {

    public ABSConductivityTesterNode( final ConductivityTester tester, boolean dev ) {
        super( edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.conductivityTester, tester, ModelViewTransform.createIdentity(), Color.BLACK, Color.RED, Color.BLACK, dev );

        //Wire up so the conductivity tester is only shown when selected
        tester.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( tester.isVisible() );
            }
        } );

        // sim-sharing, light bulb and battery (not interactive)
        getLightBulbNode().addInputEventListener( new NonInteractiveEventHandler( UserComponents.lightBulb ) );
        getBatteryNode().addInputEventListener( new NonInteractiveEventHandler( UserComponents.battery ) );

        // sim-sharing, positive probe
        {
            DragFunction startEndDragFunction = new DragFunction() {
                public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameters, PInputEvent event ) {
                    sendProbeEvent( userComponent, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                }
            };
            getPositiveProbeDragHandler().setStartDragFunction( startEndDragFunction );
            getPositiveProbeDragHandler().setEndDragFunction( startEndDragFunction );
            getPositiveProbeDragHandler().setDragFunction( new SimSharingDragHandler.DragFunction() {
                boolean inSolution = tester.isPositiveProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameters, PInputEvent event ) {
                    if ( inSolution != tester.isPositiveProbeInSolution() ) {
                        sendProbeEvent( userComponent, action, tester.isPositiveProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isPositiveProbeInSolution();
                }
            } );
        }

        // sim-sharing, negative probe
        {
            DragFunction startEndDragFunction = new DragFunction() {
                public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameters, PInputEvent event ) {
                    sendProbeEvent( userComponent, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                }
            };
            getNegativeProbeDragHandler().setStartDragFunction( startEndDragFunction );
            getNegativeProbeDragHandler().setEndDragFunction( startEndDragFunction );
            getNegativeProbeDragHandler().setDragFunction( new SimSharingDragHandler.DragFunction() {
                boolean inSolution = tester.isNegativeProbeInSolution();

                // Send event when probe transitions between in/out of solution.
                public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameters, PInputEvent event ) {
                    if ( inSolution != tester.isNegativeProbeInSolution() ) {
                        sendProbeEvent( userComponent, action, tester.isNegativeProbeInSolution(), tester.isCircuitCompleted() );
                    }
                    inSolution = tester.isNegativeProbeInSolution();
                }
            } );
        }
    }

    private static void sendProbeEvent( IUserComponent object, IUserAction action, boolean inSolution, boolean circuitCompleted ) {
        SimSharingManager.sendUserMessage( object, UserComponentTypes.sprite, action,
                                           ParameterSet.parameterSet( ParameterKeys.isInSolution, inSolution ).
                                                   with( ParameterKeys.isCircuitCompleted, circuitCompleted ) );
    }
}
