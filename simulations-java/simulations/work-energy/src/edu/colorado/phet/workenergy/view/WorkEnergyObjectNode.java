package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.Property;
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
    private WorkEnergyObject workEnergyObject;
    private ModelViewTransform2D transform;
//    boolean debug = false;

    public WorkEnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform, final Property<Boolean> originLineVisible ) {
        this.workEnergyObject = workEnergyObject;
        this.transform = transform;

        final PImage child = new PImage( workEnergyObject.getImage() ) {{
            double h = Math.abs( transform.modelToViewDifferentialYDouble( workEnergyObject.getHeight() ) );
            final double scale = h / getFullBounds().getHeight();
            translate( -getObjectWidthView() / 2, -h *
                                                  0.8 );//so the bottom of the 3d crate looks like it is touching the ground
            scale( scale );
        }};

        addChild( child );

        double ellipseHeight = Math.abs( transform.modelToViewDifferentialYDouble( 0.5 ) );
        final PhetPPath centerDebugger = new PhetPPath( new Ellipse2D.Double( -ellipseHeight / 2, -ellipseHeight / 2, ellipseHeight, ellipseHeight ), new Color( 0, 0, 255, 128 ), new BasicStroke( 2 ), Color.black );
        addChild( centerDebugger );

        final int amountLineExtendsBeyondObject = 30;
        final PhetPPath originLine = new PhetPPath( new Line2D.Double( -getObjectWidthView() / 2 - amountLineExtendsBeyondObject, 0, getObjectWidthView() / 2 + amountLineExtendsBeyondObject, 0 ),
                                                    new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 4, 4 }, 0 ), Color.black ) {{
            originLineVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( originLineVisible.getValue() );
                }
            } );
        }};
        addChild( originLine );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                workEnergyObject.setUserControlled( true );
            }

            public void mouseDragged( PInputEvent event ) {
                workEnergyObject.setUserControlled( true );
            }

            public void mouseReleased( PInputEvent event ) {
                workEnergyObject.setUserControlled( false );
            }
        } );

        final SimpleObserver updatePosition = new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( workEnergyObject.getPositionProperty().getValue() ) );
            }
        };
        updatePosition.update();
        workEnergyObject.getPositionProperty().addObserver( updatePosition );
    }

    private double getObjectWidthView() {
        return Math.abs( transform.modelToViewDifferentialXDouble( workEnergyObject.getWidth() ) );
    }
}
