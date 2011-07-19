//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestJME extends SimpleApplication {
    public static void main( String[] args ) {
        TestJME app = new TestJME();
        app.start();
    }

    protected Geometry player;

    @Override
    public void simpleInitApp() {

        Box b = new Box( Vector3f.ZERO, 1, 1, 1 );
        player = new Geometry( "blue cube", b );
        Material mat = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
        mat.setColor( "Color", ColorRGBA.Blue );
        player.setMaterial( mat );
        rootNode.attachChild( player );

        inputManager.clearMappings();
        inputManager.addMapping( "Rotate", new MouseAxisTrigger( MouseInput.AXIS_X, false ) );

        inputManager.addListener( new AnalogListener() {
            public void onAnalog( String name, float value, float tpf ) {
                System.out.println( name + ", " + value );
            }
        } );
    }

    /* This is the update loop */
    @Override
    public void simpleUpdate( float tpf ) {
        // make the player rotate
        player.rotate( 0, 2 * tpf, 0 );
    }
}