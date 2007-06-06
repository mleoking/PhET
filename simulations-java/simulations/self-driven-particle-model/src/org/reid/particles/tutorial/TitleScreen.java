/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 22, 2005
 * Time: 11:27:36 PM
 * Copyright (c) Aug 22, 2005 by Sam Reid
 */

public class TitleScreen extends PSwingCanvas {
    private TutorialApplication tutorialApplication;
    private PSwing startButton;
    private PImage titleImage;

    public TitleScreen( final TutorialApplication tutorialApplication ) {
        this.tutorialApplication = tutorialApplication;
        setBackground( Color.lightGray );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/title-page3.jpg" );
            if (TutorialApplication.isLowResolution() ){
                image= BufferedImageUtils.rescaleYMaintainAspectRatio(image, tutorialApplication.getTutorialFrame().getHeight()-30 );
            }
            titleImage = new PImage( image );
            titleImage.setOffset( 100, 100 );
            addChild( titleImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        try {
            BufferedImage bufferedImage = ImageLoader.loadBufferedImage( "images/startbutton.jpg" );
            ImageIcon ii = new ImageIcon( bufferedImage );
            JButton startButton = new JButton( ii );
            startButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    tutorialApplication.startTutorial();
                }
            } );
            this.startButton = new PSwing( this, startButton );
            addChild( this.startButton );
            this.startButton.setOffset( getWidth() - this.startButton.getWidth() - 5, getHeight() - this.startButton.getHeight() - 5 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

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
        relayoutChildren();
    }

    private void relayoutChildren() {
//        startButton.setOffset( getWidth() - startButton.getWidth() - 5, getHeight() - startButton.getHeight() - 5 );
        startButton.setOffset( getWidth() - startButton.getWidth() - 5, getHeight() - startButton.getHeight() - 100 );
        titleImage.setOffset( ( getWidth() - titleImage.getWidth() ) / 2, ( getHeight() - titleImage.getHeight() ) / 2 );
    }

    private void addChild( PNode pNode ) {
        getLayer().addChild( pNode );
    }

}
