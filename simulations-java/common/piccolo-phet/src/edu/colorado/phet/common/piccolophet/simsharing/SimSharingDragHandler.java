// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.slider.simsharing.SimSharingHSliderNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.numberDragEvents;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * To use it, specify the user component in the constructor as well as any args for computing drag information.
 * <p/>
 * Other implementation did not follow the same standard pattern of sim sharing subclasses in which information is required in constructor and the class sending the messages itself.
 * <p/>
 * Unlike other simsharing subclasses, this one allows client code to supply additional information via overriding the get***Parameters methods.
 * TODO: add more summary information and hooks for the sim client to send additional param info.
 *
 * @author Sam Reid
 */
public class SimSharingDragHandler extends PDragSequenceEventHandler {

    protected final IUserComponent userComponent;
    private ArrayList<Point2D> dragPoints = new ArrayList<Point2D>();

    public SimSharingDragHandler( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    @Override protected void startDrag( final PInputEvent event ) {
        dragPoints.clear();
        SimSharingManager.sendUserMessage( userComponent, UserActions.startDrag, getStartDragParameters().add( getXParameter( event ) ).add( getYParameter( event ) ) );
        super.startDrag( event );
    }

    //Override to supply any additional parameters to send on start drag message
    public ParameterSet getStartDragParameters() {
        return getParametersForAllEvents();
    }

    //Return parameters that are used for startDrag/drag/endDrag
    public ParameterSet getParametersForAllEvents() {
        return new ParameterSet();
    }

    //Finish the drag and report on simsharing for this drag event.
    @Override protected void endDrag( PInputEvent event ) {
        ArrayList<Double> xValues = extract( dragPoints, new Function1<Point2D, Double>() {
            public Double apply( Point2D point2D ) {
                return point2D.getX();
            }
        } );
        ArrayList<Double> yValues = extract( dragPoints, new Function1<Point2D, Double>() {
            public Double apply( Point2D point2D ) {
                return point2D.getY();
            }
        } );
        double minX = SimSharingHSliderNode.min( xValues );
        double maxX = SimSharingHSliderNode.max( xValues );
        double averageX = SimSharingHSliderNode.average( xValues );
        double minY = SimSharingHSliderNode.min( yValues );
        double maxY = SimSharingHSliderNode.max( yValues );
        double averageY = SimSharingHSliderNode.average( yValues );
        SimSharingManager.sendUserMessage( userComponent, UserActions.endDrag, getEndDragParameters().
                add( getXParameter( event ) ).
                add( getYParameter( event ) ).
                param( numberDragEvents, dragPoints.size() ).
                param( ParameterKeys.minX, minX ).
                param( ParameterKeys.maxX, maxX ).
                param( ParameterKeys.averageX, averageX ).
                param( ParameterKeys.minY, minY ).
                param( ParameterKeys.maxY, maxY ).
                param( ParameterKeys.averageY, averageY ) );
        dragPoints.clear();
        super.endDrag( event );
    }

    private ArrayList<Double> extract( ArrayList<Point2D> all, Function1<Point2D, Double> extractor ) {
        ArrayList<Double> list = new ArrayList<Double>();
        for ( Point2D point2D : all ) {
            list.add( extractor.apply( point2D ) );
        }
        return list;
    }

    public ParameterSet getEndDragParameters() {
        return getParametersForAllEvents();
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionX, event.getCanvasPosition().getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionY, event.getCanvasPosition().getY() );
    }
}