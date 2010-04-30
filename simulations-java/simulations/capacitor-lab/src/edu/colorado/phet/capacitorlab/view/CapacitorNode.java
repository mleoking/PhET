/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class CapacitorNode extends PhetPNode {

    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    
    public CapacitorNode( Capacitor capacitor, ModelViewTransform mvt ) {
        
        this.capacitor = capacitor;
        this.mvt = mvt;
        
        topPlateNode = new PlateNode( capacitor.getTopPlate(), mvt );
        bottomPlateNode = new PlateNode( capacitor.getBottomPlate(), mvt );
        dielectricNode = new DielectricNode( capacitor.getDielectric(), mvt );
        
        // rendering order
        addChild( bottomPlateNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );
        
        // layout
        double x = 0;
        double y = 0;
        topPlateNode.setOffset( x, y );
        y = topPlateNode.getFullBoundsReference().getMaxY();
        dielectricNode.setOffset( x, y );
        y = dielectricNode.getFullBoundsReference().getMaxY();
        bottomPlateNode.setOffset( x, y );
    }
}
