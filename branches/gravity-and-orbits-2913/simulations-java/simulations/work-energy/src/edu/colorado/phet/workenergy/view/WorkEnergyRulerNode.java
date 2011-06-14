// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class WorkEnergyRulerNode extends PNode {
    /**
     * Constructor.
     *
     * @param transform        - Model view transform.
     * @param rulerModelOrigin - Origin point for the ruler - the ruler will be positioned such that the 0 tick
     *                         mark on the right side is at this location in model space.
     */
    public WorkEnergyRulerNode( final Property<Boolean> visibleProperty, ModelViewTransform2D transform, Point2D rulerModelOrigin ) {
        visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visibleProperty.get() );
            }
        } );

        final RulerNode rulerNode = new RulerNode( Math.abs( transform.modelToViewDifferentialYDouble( 5 ) ),
                                                   50, new String[] { "0", "1", "2", "3", "4", "5" }, "m", 4, 18 );
        rulerNode.rotate( -Math.PI / 2 );
        rulerNode.setOffset( transform.modelToViewXDouble( rulerModelOrigin.getX() ) - rulerNode.getFullBoundsReference().width - WorkEnergyObjectNode.AMOUNT_LINE_EXTENDS_BEYOND_OBJECT,
                             transform.modelToViewYDouble( rulerModelOrigin.getY() ) + rulerNode.getInsetWidth() );
        addChild( rulerNode );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }
        } );
    }
}
