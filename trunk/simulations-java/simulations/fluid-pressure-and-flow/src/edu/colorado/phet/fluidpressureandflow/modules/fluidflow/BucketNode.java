// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.RESOURCES;

/**
 * @author Sam Reid
 */
public class BucketNode extends PNode {
    public BucketNode( final FluidFlowModule module ) {
        final PImage image = new PImage( RESOURCES.getImage( "bucket.png" ) );
        addChild( image );
        final PSwing pswing = new PSwing( new JButton( "Pour" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getFluidFlowModel().pourFoodColoring();
                }
            } );
        }} );
        pswing.setOffset( image.getFullBounds().getWidth(), image.getFullBounds().getHeight() - pswing.getFullBounds().getHeight() );
        addChild( pswing );
    }
}
