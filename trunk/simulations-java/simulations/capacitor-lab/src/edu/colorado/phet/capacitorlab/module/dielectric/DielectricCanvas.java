/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.BullseyeNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {
    
    private final CLModel model;
    private final ModelViewTransform mvt;
    
    private final CapacitorNode capacitorNode;
    private final BatteryNode batteryNode;
    private final BullseyeNode originNode;
    
    public DielectricCanvas( CLModel model, boolean dev ) {
        
        this.model = model;
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.MVT_OFFSET );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        
        capacitorNode = new CapacitorNode( model.getCapacitor(), mvt );
        addChild( capacitorNode );
        
        originNode = new BullseyeNode();
        if ( dev ) {
            addChild( originNode );
        }
    }
    
    public void reset() {
        //XXX
    }
    
    @Override
    public void updateLayout() {
        super.updateLayout();
        
        Point2D pModel = new Point2D.Double();
        Point2D pView = new Point2D.Double();
        
        // capacitor
        pModel.setLocation( model.getBattery().getLocationReference().getX(), model.getBattery().getLocationReference().getY() );
        mvt.modelToView( pModel, pView );
        batteryNode.setOffset( pView.getX(), pView.getY() );
        
        // battery
        pModel.setLocation( model.getCapacitor().getLocationReference().getX(), model.getCapacitor().getLocationReference().getY() );
        mvt.modelToView( pModel, pView );
        capacitorNode.setOffset( pView.getX(), pView.getY() );
        
        // origin marker
        pModel.setLocation( 0, 0 );
        mvt.modelToView( pModel, pView );
        originNode.setOffset( pView.getX(), pView.getY() );
    }
}
