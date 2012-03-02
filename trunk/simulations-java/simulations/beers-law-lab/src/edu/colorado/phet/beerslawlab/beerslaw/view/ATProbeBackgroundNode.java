// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.ATDetector;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.ParameterKeys;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The background portion of the AT detector probe, which passes behind the cuvette.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ATProbeBackgroundNode extends PhetPNode {

    //TODO duplicated in ATDetectorNode
    private static final double PROBE_CENTER_Y_OFFSET = 55; // specific to image file

    public ATProbeBackgroundNode( final ATDetector detector, final ModelViewTransform mvt ) {

        PImage imageNode = new PImage( Images.AT_DETECTOR_PROBE_LEFT );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth(), -PROBE_CENTER_Y_OFFSET );

        // probe location
        detector.probe.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( mvt.modelToView( location.toPoint2D() ) );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MovableDragHandler( UserComponents.detectorProbe, detector.probe, this, mvt ) {
            @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                return super.getParametersForAllEvents( event ).add( ParameterKeys.inBeam, detector.probeInBeam() );
            }
        } );
    }
}
