// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fluidpressureandflow;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class ThreePatchImageNode extends PNode {
    public ThreePatchImageNode() {
        addChild( new PImage( FluidPressureAndFlowResources.RESOURCES.getImage( "pressure_meter_left.png" ) ) );
        addChild( new PImage( FluidPressureAndFlowResources.RESOURCES.getImage( "pressure_meter_center.png" ) ) );
        addChild( new PImage( FluidPressureAndFlowResources.RESOURCES.getImage( "pressure_meter_right.png" ) ) );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    setContentPane( new PCanvas() {{
                        getLayer().addChild( new ThreePatchImageNode() );
                    }} );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}
