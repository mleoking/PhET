package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

public class SpriteTest extends JFrame {
    private SpriteCanvas spriteCanvas;

    public SpriteTest() throws HeadlessException {
        super( "SpriteTest" );

        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        spriteCanvas = new SpriteCanvas();
        panel.add( spriteCanvas );
        setContentPane( panel );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        ( new Thread() {
            @Override
            public void run() {
                try {
                    while ( true ) {
                        Thread.sleep( 1000 / 25 );
                        for ( Sprite sprite : spriteCanvas.getLandscape().getSprites() ) {
                            if ( sprite instanceof ActiveSprite ) {
                                ( (ActiveSprite) sprite ).perturb();
                            }
                        }
                        Collections.sort( spriteCanvas.getLandscape().getChildrenReference(), new Comparator() {
                            public int compare( Object a, Object b ) {
                                Sprite sa = (Sprite) a;
                                Sprite sb = (Sprite) b;

                                if ( sa.getPosition().getZ() == sb.getPosition().getZ() ) {
                                    return 0;
                                }

                                if ( sa.getPosition().getZ() > sb.getPosition().getZ() ) {
                                    return -1;
                                }
                                else {
                                    return 1;
                                }
                            }
                        } );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } ).start();
    }

    public static void main( String[] args ) {
        SpriteTest demo = new SpriteTest();
        demo.pack();
        demo.setSize( 400, 300 );
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );
    }
}
