/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Cursor;

import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandler;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {
    
    public static enum DielectricChargeView { NONE, ALL, EXCESS };
    
    private final DielectricTotalChargeNode totalChargeNode;
    private final DielectricExcessChargeNode excessChargeNode;
    
    private DielectricChargeView dielectricChargeView;

    public DielectricNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev, DoubleRange valueRange ) {
        super( mvt, circuit.getCapacitor().getDielectricMaterial().getColor() );
        
        // dielectric is directly draggable
        addInputEventListener( new CursorHandler( Cursor.E_RESIZE_CURSOR ) );
        addInputEventListener( new DielectricOffsetDragHandler( this, circuit.getCapacitor(), mvt, valueRange ) );
        
        totalChargeNode = new DielectricTotalChargeNode( circuit, mvt, dev );
        addChild( totalChargeNode );
        
        excessChargeNode = new DielectricExcessChargeNode( circuit, mvt, dev );
        addChild( excessChargeNode );
    }
    
    public void setDielectricChargeView( DielectricChargeView dielectricChargeView ) {
        this.dielectricChargeView = dielectricChargeView;
        totalChargeNode.setVisible( dielectricChargeView == DielectricChargeView.ALL );
        excessChargeNode.setVisible( dielectricChargeView == DielectricChargeView.EXCESS );
    }
    
    public DielectricChargeView getDielectricChargeView() {
        return dielectricChargeView;
    }
}
