// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.ConductivityTesterNode;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ConductivityTester;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Conductivity Tester Node specialized for Sugar and Salt Solutions.  This makes the light bulb draggable, and makes it possible to
 * drag into and out of the toolbox.
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsConductivityTesterNode extends ConductivityTesterNode {
    private final ConductivityTester conductivityTester;
    private final ModelViewTransform transform;

    public SugarAndSaltSolutionsConductivityTesterNode( final ConductivityTester conductivityTester, final ModelViewTransform transform, final PNode rootNode, Point2D location, ObservableProperty<Boolean> whiteBackground ) {
        super( UserComponents.conductivityTester, conductivityTester, transform, Color.lightGray, Color.lightGray, Color.lightGray, PhetColorScheme.RED_COLORBLIND, Color.green, Color.black, Color.black, false );
        this.conductivityTester = conductivityTester;
        this.transform = transform;

        //Set up the ConductivityTesterNode to work well against the selected background
        whiteBackground.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean whiteBackground ) {
                if ( whiteBackground ) {
                    setAgainstWhiteBackground();
                }
                else {
                    setAgainstDarkBackground();
                }
            }
        } );

        //Make it visible when the model shows the conductivity tester to be visible (i.e. enabled)
        conductivityTester.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        //Make it possible to drag the light bulb, which translates all parts of the conductivity tester (including probes), but constrain to the canvas (same as for the ConductivityTesterToolNode)
        getLightBulbNode().addInputEventListener( new CursorHandler() );
        getLightBulbNode().addInputEventListener( new CanvasBoundedDragHandler( getLightBulbNode() ) {
            @Override protected void dragNode( DragEvent event ) {
                dragAll( event.delta );
            }
        } );

        //Drag the entire component by the battery, but constrain to the canvas (same as for the ConductivityTesterToolNode)
        getBatteryNode().addInputEventListener( new CursorHandler() );
        getBatteryNode().addInputEventListener( new CanvasBoundedDragHandler( getBatteryNode() ) {
            @Override protected void dragNode( DragEvent event ) {
                dragAll( event.delta );
            }
        } );

        //initialize the probe locations so that relative locations will be equivalent to those when it is dragged out of the toolbox (same code used)

        //Update the location of the body and probes for the conductivity tester, called on initialization
        // (to make sure icon looks consistent) and when dragged out of the toolbox
        Point2D viewLocation = transform.modelToView( location );
        conductivityTester.setLocation( viewLocation.getX(), viewLocation.getY() );

        //Move the probes down to encourage the user to dip them in the water without dipping the light bulb/battery in the water too, which would short out the circuit
        double offsetY = 0.065;
        conductivityTester.setNegativeProbeLocation( location.getX() - 0.03, location.getY() - offsetY );
        conductivityTester.setPositiveProbeLocation( location.getX() + 0.07, location.getY() - offsetY );

        //When the location changes, tell the model the location and occupied regions of the bulb and battery
        //In order to test for a short circuit when computing the bulb brightness
        conductivityTester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {
            public void brightnessChanged() {
            }

            public void positiveProbeLocationChanged() {
            }

            public void negativeProbeLocationChanged() {
            }

            public void locationChanged() {
                Rectangle2D batteryBounds = getBatteryNode().getGlobalFullBounds();
                batteryBounds = rootNode.globalToLocal( batteryBounds );
                conductivityTester.setBatteryRegion( transform.viewToModel( batteryBounds ) );

                Rectangle2D bulbBounds = getLightBulbNode().getGlobalFullBounds();
                bulbBounds = rootNode.globalToLocal( bulbBounds );
                conductivityTester.setBulbRegion( transform.viewToModel( bulbBounds ) );
            }
        } );
    }

    //Used to create a thumbnail icon for use in the toolbox.
    public Image createImage() {

        //Generate a thumbnail of the conductivity tester node.  This is done by making it visible, calling toImage() and then making it invisible
        boolean visible = conductivityTester.visible.get();
        conductivityTester.visible.set( true );
        Image image = toImage();
        conductivityTester.visible.set( visible );//Restore default value
        return image;
    }

    public void dragAll( PDimension viewDelta ) {

        //Drag the conductivity tester in view coordinates
        conductivityTester.setLocation( conductivityTester.getLocationReference().getX() + viewDelta.getWidth(),
                                        conductivityTester.getLocationReference().getY() + viewDelta.getHeight() );

        //The probes drag in model coordinates
        Dimension2D modelDelta = transform.viewToModelDelta( viewDelta );
        conductivityTester.setNegativeProbeLocation( conductivityTester.getNegativeProbeLocationReference().getX() + modelDelta.getWidth(),
                                                     conductivityTester.getNegativeProbeLocationReference().getY() + modelDelta.getHeight() );
        conductivityTester.setPositiveProbeLocation( conductivityTester.getPositiveProbeLocationReference().getX() + modelDelta.getWidth(),
                                                     conductivityTester.getPositiveProbeLocationReference().getY() + modelDelta.getHeight() );

        //The thing you are dragging should always go in front.  Have to move the parent in front since it is the child in the canvas.submergedInWaterNode
        getParent().moveToFront();
    }

    //Only the bulb can be dropped back in the toolbox since it is the only part that translates the unit
    public PNode[] getDroppableComponents() {
        return new PNode[] { getLightBulbNode(), getBatteryNode() };
    }
}