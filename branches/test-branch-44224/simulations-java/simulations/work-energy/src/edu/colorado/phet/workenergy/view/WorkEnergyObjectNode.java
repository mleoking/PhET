package edu.colorado.phet.workenergy.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;

/**
 * @author Sam Reid
 */
public class WorkEnergyObjectNode extends PNode {
    private WorkEnergyObject workEnergyObject;
    private ModelViewTransform2D transform;

    public WorkEnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform ) {
        this.workEnergyObject = workEnergyObject;
        this.transform = transform;
        final PhetPPath path = new PhetPPath( new Ellipse2D.Double( 0, 0, 100, 100 ), Color.blue, new BasicStroke( 2 ), Color.black );
        addChild( path );

        final PText keReadoutText = new PText();
        addChild( keReadoutText );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( path );
//                System.out.println("delta = " + delta);
                if ( delta.width > 0 ) {
                    workEnergyObject.setAppliedForce( 1, 0 );
                }
                else if ( delta.width < 0 ) {
                    workEnergyObject.setAppliedForce( -1, 0 );
                }
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                workEnergyObject.setAppliedForce( 0, 0 );
            }
        } );

        final SimpleObserver updatePosition = new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( workEnergyObject.getPositionProperty().getValue() ) );
            }
        };
        updatePosition.update();
        workEnergyObject.getPositionProperty().addObserver( updatePosition );

        SimpleObserver updateKineticEnergy = new SimpleObserver() {
            public void update() {
                keReadoutText.setText( "KE = " + new DecimalFormat( "0.00" ).format( workEnergyObject.getKineticEnergyProperty().getValue() ) );
            }
        };
        updateKineticEnergy.update();
        workEnergyObject.getKineticEnergyProperty().addObserver( updateKineticEnergy );
    }
}
