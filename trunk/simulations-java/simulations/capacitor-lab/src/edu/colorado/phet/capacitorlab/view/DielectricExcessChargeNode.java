/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PText;


public class DielectricExcessChargeNode extends PhetPNode {
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PText debugNode;

    public DielectricExcessChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        debugNode = new PText( "excess charge" );
        debugNode.setFont( new PhetFont( 16 ) );
        if ( dev ) {
            addChild( debugNode );
        }
        
        circuit.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void plateSizeChanged() {
                update();
            }

            @Override
            public void plateSeparationChanged() {
                update();
            }
        } );
        
        update();
    }
    
    private void update() {
        // center the debug node on the front face of the dielectric
        double x = 0;
        double y = circuit.getCapacitor().getPlateSeparation() / 2;
        double z = -circuit.getCapacitor().getPlateSideLength() / 2;
        Point2D pView = mvt.modelToView( x, y, z );
        debugNode.setOffset( pView.getX() - ( debugNode.getFullBoundsReference().getWidth() / 2 ), pView.getY() - ( debugNode.getFullBoundsReference().getHeight() / 2 ) );
    }
    
    private int getNumberOfCharges( double excessDielectricCharge ) {
        return 5;
    }
}
