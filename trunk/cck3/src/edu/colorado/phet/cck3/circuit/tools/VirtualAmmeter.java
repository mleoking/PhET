/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.tools;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.common.TargetReadoutTool;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:56:28 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class VirtualAmmeter extends DefaultInteractiveGraphic {
    private TargetReadoutTool trt;
    private Component panel;
    private CircuitGraphic circuitGraphic;
    private CCK3Module module;
    private Circuit circuit;

    public VirtualAmmeter( CircuitGraphic circuitGraphic, Component panel, CCK3Module module ) {
        this( new TargetReadoutTool( panel ), panel, circuitGraphic, module );
    }

    public VirtualAmmeter( TargetReadoutTool targetReadoutTool, final Component panel, final CircuitGraphic circuitGraphic, final CCK3Module module ) {
        super( targetReadoutTool );
        this.trt = targetReadoutTool;
        this.panel = panel;
        this.circuitGraphic = circuitGraphic;
        this.module = module;
        this.circuit = circuitGraphic.getCircuit();
        trt.setLocation( 100, 100 );

        addCursorHandBehavior();
        addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                if( module.getTransform().getViewBounds().contains( trt.getPoint().x + (int)dx, trt.getPoint().y + (int)dy ) )
                {
                    trt.translate( (int)dx, (int)dy );
                    recompute();
                }

            }
        } );
        resetText();
        setVisible( false );
    }

    public void recompute() {
        Point target = trt.getPoint();
        //check for intersect with circuit.
        Graphic[] g = circuitGraphic.getBranchGraphics();
        for( int i = g.length - 1; i >= 0; i-- ) {
            Graphic graphic = g[i];
            if( graphic instanceof InteractiveBranchGraphic ) {
                InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                Branch branch = ibg.getBranch();
                BranchGraphic branchGraphic = ibg.getBranchGraphic();
                Shape shape = branchGraphic.getCoreShape();//getShape();
                if( shape.contains( target ) ) {
                    double current = branch.getCurrent();
                    DecimalFormat df = circuitGraphic.getModule().getDecimalFormat();
                    String amps = df.format( Math.abs( current ) );
                    trt.setText( amps + " " + SimStrings.get( "VirtualAmmeter.Amps" ) );
                    return;
                }
            }
        }
        resetText();
    }

    private void resetText() {
        String[] text = new String[]{
                SimStrings.get( "VirtualAmmeter.HelpString1" ),
                SimStrings.get( "VirtualAmmeter.HelpString2" )
        };
        trt.setText( text );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        panel.repaint();
    }

}


