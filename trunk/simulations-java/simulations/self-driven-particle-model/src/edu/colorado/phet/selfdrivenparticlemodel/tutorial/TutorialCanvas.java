// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.util.JSAudioPlayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TutorialCanvas extends PSwingCanvas {

    public static BufferedImage backgroundImage;
    private PNode backgroundNode;

    static {
        try {
            backgroundImage = ImageLoader.loadBufferedImage( "self-driven-particle-model/images/background.jpg" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public TutorialCanvas() {
        setPanEventHandler( null );
//        setZoomEventHandler( null );
        backgroundNode = new PImage( backgroundImage );
        addChild( backgroundNode );
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayoutChildren();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutChildren();
            }

        } );
    }

    protected void relayoutChildren() {
        backgroundNode.setOffset( ( getWidth() - backgroundNode.getWidth() ) / 2, ( getHeight() - backgroundNode.getHeight() ) / 2 );
    }

    public void start( SelfDrivenParticleModelApplication tutorialApplication ) {
        tutorialApplication.setContentPane( this );
    }

    public void teardown( SelfDrivenParticleModelApplication tutorialApplication ) {
//        tutorialApplication.setContentPane( null );
    }

    public void addChild( PNode pNode ) {
        if( !getLayer().getChildrenReference().contains( pNode ) ) {
            getLayer().addChild( pNode );
        }
//        System.out.println( "Page.addchild, layer=" + getLayer().getChildrenReference().size() + ", " + getLayer().getChildrenReference() );
    }

    public void playHarp() {
        JSAudioPlayer.playNoBlock( BasicTutorialCanvas.class.getClassLoader().getResource( "self-driven-particle-model/audio/upstroke.wav" ) );
    }

    public void removeChild( PNode child ) {
        if( getLayer().getChildrenReference().contains( child ) ) {
            getLayer().removeChild( child );
//        System.out.println( "Page.removechild, layer=" + getLayer().getChildrenReference().size() + ", " + getLayer().getChildrenReference() );
        }
    }


    public void moveRight() {
    }

    public void moveLeft() {
    }
}
