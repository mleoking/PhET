package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class WorkEnergyObjectNode extends PNode {
    private WorkEnergyObject workEnergyObject;
    private ModelViewTransform2D transform;
    boolean debug = false;

    public WorkEnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform ) {
        this.workEnergyObject = workEnergyObject;
        this.transform = transform;

        final PImage child = new PImage( workEnergyObject.getImage() ) {{
            double h = Math.abs( transform.modelToViewDifferentialYDouble( workEnergyObject.getHeight() ) );
            double w = Math.abs( transform.modelToViewDifferentialXDouble( workEnergyObject.getWidth() ) );
            final double scale = h / getFullBounds().getHeight();
            translate( -w / 2, -h *
                               0.8 );//so the bottom of the 3d crate looks like it is touching the ground
            scale( scale );
        }};

        addChild( child );

        if (debug){//for debugging
            double ellipseHeight = Math.abs( transform.modelToViewDifferentialYDouble( 0.5 ) );
            final PhetPPath centerDebugger = new PhetPPath( new Ellipse2D.Double( -ellipseHeight / 2, -ellipseHeight / 2, ellipseHeight, ellipseHeight ), new Color( 0, 0, 255, 128 ), new BasicStroke( 2 ), Color.black );
            addChild( centerDebugger );
        }

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
}
