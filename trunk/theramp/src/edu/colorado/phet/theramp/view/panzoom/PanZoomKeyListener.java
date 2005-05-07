/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.panzoom;

import edu.colorado.phet.common.view.ApparatusPanel2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:17:59 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class PanZoomKeyListener implements KeyListener {
    private ApparatusPanel2 apparatusPanel2;

    private CompositeAnimationStep compositeAnimationStep;
    private Timer animationTimer;
    private ActionListener animator;
    private Dimension defaultSize;

    public PanZoomKeyListener( ApparatusPanel2 apparatusPanel2, Dimension defaultSize ) {
        this.apparatusPanel2 = apparatusPanel2;
        this.defaultSize = defaultSize;

        animator = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean done = compositeAnimationStep.step();
                if( done ) {
                    animationTimer.stop();
                }
            }
        };
        animationTimer = new Timer( 20, animator );
        animationTimer.setInitialDelay( 0 );
        animationTimer.setRepeats( true );
        compositeAnimationStep = new CompositeAnimationStep( apparatusPanel2, -1 );
    }

    public void keyPressed( KeyEvent e ) {
        int scrollSpeed = 30;
        if( e.getKeyCode() == KeyEvent.VK_UP ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Moving( e.getKeyCode(), apparatusPanel2, 0, scrollSpeed ) );
            animationTimer.start();
        }
        if( e.getKeyCode() == KeyEvent.VK_DOWN ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Moving( e.getKeyCode(), apparatusPanel2, 0, -scrollSpeed ) );
            animationTimer.start();
        }
        if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Moving( e.getKeyCode(), apparatusPanel2, scrollSpeed, 0 ) );
            animationTimer.start();
        }
        if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Moving( e.getKeyCode(), apparatusPanel2, -scrollSpeed, 0 ) );
            animationTimer.start();
        }
        if( e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Zooming( e.getKeyCode(), apparatusPanel2, 1.1 ) );
            animationTimer.start();
        }
        if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
            compositeAnimationStep.addAnimationStep( new AnimationStep.Zooming( e.getKeyCode(), apparatusPanel2, 0.9 ) );
            animationTimer.start();
        }
    }

    public void keyReleased( KeyEvent e ) {
        compositeAnimationStep.release( e.getKeyCode() );
        if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            apparatusPanel2.setReferenceSize( getDefaultSize() );//my special little rendering size.
            apparatusPanel2.getTransformManager().setViewPortOrigin( 0, 0 );
        }
        else if( e.getKeyCode() == KeyEvent.VK_1 ) {
            setReferenceSize( 1000, 1000 );
        }
        else if( e.getKeyCode() == KeyEvent.VK_2 ) {
            setReferenceSize( 2000, 2000 );
        }
        else if( e.getKeyCode() == KeyEvent.VK_3 ) {
            setReferenceSize( 3000, 3000 );
        }
        else if( e.getKeyCode() == KeyEvent.VK_0 ) {
            setReferenceSize();
        }
    }

    private Dimension getDefaultSize() {
        return defaultSize;
    }

    private void setReferenceSize() {
        apparatusPanel2.setReferenceSize();
    }

    private void setReferenceSize( int width, int height ) {
        apparatusPanel2.setReferenceSize( width, height );
    }

    public void keyTyped( KeyEvent e ) {
    }

}
