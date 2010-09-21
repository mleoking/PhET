/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import edu.colorado.phet.glaciers.model.*;
import edu.colorado.phet.glaciers.view.ModelViewTransform;

/**
 * ToolNodeFactory uses the Factory design pattern to create tool nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolNodeFactory {

    /* not intended for instantiation */
    private ToolNodeFactory() {}
    
    /**
     * Creates a tool node for a specified tool.
     * 
     * @param tool
     * @param mvt
     * @return
     */
    public static AbstractToolNode createNode( AbstractTool tool, GlaciersModel model, ModelViewTransform mvt, TrashCanDelegate trashCan, boolean englishUnits ) {
        AbstractToolNode node = null;
        if ( tool instanceof Thermometer ) {
            node = new ThermometerNode( (Thermometer) tool, model.getGlacier(), mvt, trashCan );
        }
        else if ( tool instanceof GlacialBudgetMeter ) {
            node = new GlacialBudgetMeterNode( (GlacialBudgetMeter) tool,  model.getGlacier(), mvt, trashCan, englishUnits );
        }
        else if ( tool instanceof TracerFlag ) {
            node = new TracerFlagNode( (TracerFlag) tool, mvt, trashCan );
        }
        else if ( tool instanceof IceThicknessTool ) {
            node = new IceThicknessToolNode( (IceThicknessTool) tool, mvt, trashCan, englishUnits );
        }
        else if ( tool instanceof BoreholeDrill ) {
            node = new BoreholeDrillNode( (BoreholeDrill) tool, mvt, trashCan );
        }
        else if ( tool instanceof GPSReceiver ) {
            node = new GPSReceiverNode( (GPSReceiver) tool, mvt, trashCan, englishUnits );
        }
        else {
            throw new UnsupportedOperationException( "no node for tool type " + tool.getClass() );
        }
        
        return node;
    }
}
