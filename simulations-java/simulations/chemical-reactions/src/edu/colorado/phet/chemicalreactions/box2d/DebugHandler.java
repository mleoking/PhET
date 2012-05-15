// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.box2d;

import java.awt.Graphics;

import javax.swing.JFrame;

import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.framework.DebugDrawJ2D;
import org.jbox2d.testbed.framework.TestPanel;
import org.jbox2d.testbed.framework.TestbedSettings;

/**
 * Extracted jbox2d debug handling from WaterModel in SASS
 */
public class DebugHandler {
    //Panel that allows us to see jbox2d model and computations
    protected TestPanel testPanel;

    private final World world;

    public DebugHandler( World world ) {
        this.world = world;

        initDebugDraw();
    }

    public void step() {
        //Turn off the animation thread in the test panel, we are doing the animation ourselves
        testPanel.stop();

        //Make sure the debug draw paints on the screen
        testPanel.render();
        world.drawDebugData();
        testPanel.paintImmediately( 0, 0, testPanel.getWidth(), testPanel.getHeight() );
    }

    //Set up jbox2D debug draw so we can see the model and computations
    private void initDebugDraw() {

        //We had to change code in TestPanel to make dbImage public, using jbox2D animation thread was glitchy and flickering
        //So instead we control the rendering ourselves
        testPanel = new TestPanel( new TestbedSettings() ) {
            @Override protected void paintComponent( Graphics g ) {
                super.paintComponent( g );    //To change body of overridden methods use File | Settings | File Templates.
                g.drawImage( dbImage, 0, 0, null );
            }
        };

        //Create a frame to show the debug draw in
        JFrame frame = new JFrame();
        frame.setContentPane( testPanel );
        frame.pack();
        frame.setVisible( true );

        world.setDebugDraw( new DebugDrawJ2D( testPanel ) {{

            //Show the shapes in the debugger
            setFlags( e_shapeBit );

            //Move the camera over so that the shapes will show up at a good size and location
            setCamera( -10, 10, 20 );
        }} );
    }
}
