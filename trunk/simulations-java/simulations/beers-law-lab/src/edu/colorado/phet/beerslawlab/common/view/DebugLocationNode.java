// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.common.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Debug node.
 * Put this directly on the canvas.
 * Then drag it around to display model and view location values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugLocationNode extends PComposite {

    private static final String MODEL_PATTERN = "model = ({0},{1}) cm";
    private static final String VIEW_PATTERN = "view = ({0},{1})";
    private static final NumberFormat MODEL_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final NumberFormat VIEW_FORMAT = new DefaultDecimalFormat( "0" );

    public DebugLocationNode( final ModelViewTransform mvt ) {

        final PText modelNode = new PText( MODEL_PATTERN );
        final PText viewNode = new PText( VIEW_PATTERN );
        DebugOriginNode originNode = new DebugOriginNode();

        addChild( modelNode );
        addChild( viewNode );
        addChild( originNode );

        // put values above left
        viewNode.setOffset( originNode.getFullBoundsReference().getMinX() - modelNode.getFullBoundsReference().getWidth() - 5,
                            originNode.getFullBoundsReference().getMinY() - viewNode.getFullBoundsReference().getHeight() );
        modelNode.setOffset( viewNode.getXOffset(),
                              viewNode.getFullBoundsReference().getMinY() - modelNode.getFullBoundsReference().getHeight() );


        // make it draggable
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() );

        // update location values when bounds change
        addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                modelNode.setText( MessageFormat.format( MODEL_PATTERN, MODEL_FORMAT.format( mvt.viewToModelDeltaX( getXOffset() ) ), MODEL_FORMAT.format( mvt.viewToModelDeltaY( getYOffset() ) ) ) );
                viewNode.setText( MessageFormat.format( VIEW_PATTERN, VIEW_FORMAT.format( getXOffset() ), VIEW_FORMAT.format( getYOffset() ) ) );
            }
        } );
    }
}
