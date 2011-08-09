// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;

public class HUDNode extends Geometry {
    public HUDNode( AssetManager assetManager ) {
        super( "HUD", new Quad( 512, 64, true ) );
        setMaterial( new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            setTexture( "ColorMap", new Texture2D() {{
                setImage( new PaintableImage( 512, 64, true ) {
                    JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );

                    {
                        panel.add( new JLabel( "This is a test 图片" ) {{
                            setForeground( Color.BLACK );
                            setFont( new PhetFont( 20 ) );
                        }} );
                        panel.add( new JLabel( "Test" ) );
                        panel.add( new JButton( "Test" ) );
                        panel.setPreferredSize( new Dimension( 256, 64 ) );
                        panel.setSize( panel.getPreferredSize() );
                        layoutComponent( panel );
                        panel.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)

                        System.out.println( panel.getBounds() );
                        System.out.println( panel.isDisplayable() );

                        refreshImage();
                    }

                    @Override public void paint( Graphics2D g ) {
                        g.setBackground( new Color( 0f, 0f, 0f, 0f ) );
                        g.clearRect( 0, 0, getWidth(), getHeight() );
                        if ( panel != null ) {
                            panel.paint( g );
                        }
                    }
                } );
            }} );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }} );
//            setQueueBucket( Bucket.Transparent );

        setLocalTranslation( new Vector3f( 10, 10, 0 ) );
    }

    private static void layoutComponent( Component component ) {
        synchronized ( component.getTreeLock() ) {
            component.doLayout();

            if ( component instanceof Container ) {
                for ( Component child : ( (Container) component ).getComponents() ) {
                    layoutComponent( child );
                }
            }
        }
    }
}
