/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.tools;

import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.common.TargetReadoutTool;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.util.GraphicsUtil;

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
    private Circuit circuit;

    public VirtualAmmeter( CircuitGraphic circuitGraphic, Component panel ) {
        this( new TargetReadoutTool(), panel, circuitGraphic );
    }

    public VirtualAmmeter( TargetReadoutTool targetReadoutTool, final Component panel, final CircuitGraphic circuitGraphic ) {
        super( targetReadoutTool );
        this.trt = targetReadoutTool;
        this.panel = panel;
        this.circuitGraphic = circuitGraphic;
        this.circuit = circuitGraphic.getCircuit();
        trt.setLocation( 100, 100 );

        addCursorHandBehavior();
        addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Rectangle before = trt.getBounds();
                trt.translate( (int)dx, (int)dy );
                Rectangle after = trt.getBounds();
                GraphicsUtil.fastRepaint( panel, before, after );
                recompute();
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
                Shape shape = branchGraphic.getShape();
                if( shape.contains( target ) ) {
                    double current = branch.getCurrent();
                    DecimalFormat df = new DecimalFormat( "#0.00#" );
                    trt.clear();
                    String amps = df.format( Math.abs( current ) );
                    trt.addText( amps + " Amps" );
                    GraphicsUtil.fastRepaint( panel, trt.getBounds() );
                    return;
                }
            }
        }
        resetText();
    }

    private void resetText() {
        trt.clear();
        trt.addText( "Move over a wire" );
        trt.addText( "to read current." );
        if( trt.getBounds() != null ) {
            GraphicsUtil.fastRepaint( panel, trt.getBounds() );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        panel.repaint();
    }

}


