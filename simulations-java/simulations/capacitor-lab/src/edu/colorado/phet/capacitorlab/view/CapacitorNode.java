/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class CapacitorNode extends PhetPNode {

    private final Capacitor capacitor;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    
    public CapacitorNode( Capacitor capacitor ) {
        this.capacitor = capacitor;
        
        topPlateNode = new PlateNode( capacitor.getTopPlate() );
        bottomPlateNode = new PlateNode( capacitor.getBottomPlate() );
        dielectricNode = new DielectricNode( capacitor.getDielectric() );
        
        // rendering order
        addChild( bottomPlateNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );
        
        // layout
        double x = 0;
        double y = 0;
        topPlateNode.setOffset( x, y );
        y = topPlateNode.getYOffset() + capacitor.getTopPlate().getHeight();
        dielectricNode.setOffset( x, y );
        y = dielectricNode.getYOffset() + capacitor.getDielectric().getHeight();
        bottomPlateNode.setOffset( x, y );
    }
}
