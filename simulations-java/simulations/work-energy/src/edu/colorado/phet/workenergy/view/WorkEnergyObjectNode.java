// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class WorkEnergyObjectNode extends PNode {
    public static final int AMOUNT_LINE_EXTENDS_BEYOND_OBJECT = 30;
    private final WorkEnergyObject workEnergyObject;
    private final ModelViewTransform2D transform;
    protected PImage imageNode;
//    boolean debug = false;

    public WorkEnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform, final Property<Boolean> originLineVisible ) {
        this.workEnergyObject = workEnergyObject;
        this.transform = transform;

        imageNode = new PImage( workEnergyObject.getImage() ) {{
            double h = Math.abs( transform.modelToViewDifferentialYDouble( workEnergyObject.getHeight() ) );
            final double scale = h / getFullBounds().getHeight();
            translate( -getObjectWidthView() / 2, -h *
                                                  0.8 );//so the bottom of the 3d crate looks like it is touching the ground
            scale( scale );
        }};

        addChild( imageNode );

        final PhetPPath originLine = new PhetPPath( new Line2D.Double( -getObjectWidthView() / 2 - AMOUNT_LINE_EXTENDS_BEYOND_OBJECT, 0, getObjectWidthView() / 2 + AMOUNT_LINE_EXTENDS_BEYOND_OBJECT, 0 ),
                                                    new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 4, 4 }, 0 ), Color.black ) {{
            originLineVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( originLineVisible.get() );
                }
            } );
        }};
        addChild( originLine );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                workEnergyObject.setUserControlled( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                workEnergyObject.setUserControlled( true );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                workEnergyObject.setUserControlled( false );
            }
        } );

        final SimpleObserver updatePosition = new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( workEnergyObject.getPositionProperty().get() ) );
            }
        };
        updatePosition.update();
        workEnergyObject.getPositionProperty().addObserver( updatePosition );
    }

    private double getObjectWidthView() {
        return Math.abs( transform.modelToViewDifferentialXDouble( workEnergyObject.getWidth() ) );
    }
}
