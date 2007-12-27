package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.umd.cs.piccolo.PNode;


public class ToolNodeFactory {

    /* not intended for instantiation */
    private ToolNodeFactory() {}
    
    public static PNode createNode( AbstractTool tool ) {
        PNode node = null;
        if ( tool instanceof Thermometer ) {
            node = new ThermometerNode( (Thermometer)tool );
        }
        else if ( tool instanceof GlacialBudgetMeter ) {
            node = new GlacialBudgetMeterNode( (GlacialBudgetMeter)tool );
        }
        else if ( tool instanceof TracerFlag ) {
            node = new TracerFlagNode( (TracerFlag)tool );
        }
        else if ( tool instanceof IceThicknessTool ) {
            node = new IceThicknessToolNode( (IceThicknessTool)tool );
        }
        else if ( tool instanceof BoreholeDrill ) {
            node = new BoreholeDrillNode( (BoreholeDrill)tool );
        }
        else {
            throw new UnsupportedOperationException( "no node for tool type " + tool.getClass() );
        }
        return node;
    }
}
