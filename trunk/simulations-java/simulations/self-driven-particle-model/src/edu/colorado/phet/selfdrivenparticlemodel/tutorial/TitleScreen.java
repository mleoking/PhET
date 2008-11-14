/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.dialogs.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

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
    private SelfDrivenParticleModelApplication tutorialApplication;
    private PSwing startButton;
    private PImage titleImage;
    private static final String simName = "self-driven-particle-model";
    private PSwing aboutSwing;

    public TitleScreen( final SelfDrivenParticleModelApplication tutorialApplication ) {
        this.tutorialApplication = tutorialApplication;
        setBackground( Color.lightGray );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "self-driven-particle-model/images/title-page3.jpg" );
            if( SelfDrivenParticleModelApplication.isLowResolution() ) {
                image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, tutorialApplication.getTutorialFrame().getHeight() - 30 );
            }
            titleImage = new PImage( image );
            titleImage.setOffset( 100, 100 );
            addChild( titleImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        try {
            BufferedImage bufferedImage = ImageLoader.loadBufferedImage( "self-driven-particle-model/images/startbutton.jpg" );
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

            JButton aboutButton = new JButton( "About..." );

            aboutButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
//                    new PhetAboutDialog( tutorialApplication.getTutorialFrame(), new PhetAboutDialog.SimpleDialogConfig("Self-Driven Particle Model", "",PhetApplicationConfig.getVersion( simName ).formatForAboutDialog(),PhetApplicationConfig.getCredits( simName )) ).show();
                    new PhetAboutDialogAdapter( tutorialApplication.getTutorialFrame(), new PhetApplicationConfig( new String[0], "self-driven-particle-model") ).show();
                }
            } );
            aboutSwing = new PSwing( aboutButton );

            addChild( aboutSwing );
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

    static class PhetAboutDialogAdapter extends PhetAboutDialog {
        public PhetAboutDialogAdapter( Frame owner, ISimInfo config ) {
            super( owner, config );
        }
    }

    private void relayoutChildren() {
//        startButton.setOffset( getWidth() - startButton.getWidth() - 5, getHeight() - startButton.getHeight() - 5 );
        startButton.setOffset( getWidth() - startButton.getWidth() - 5, getHeight() - startButton.getHeight() - 100 );
        aboutSwing.setOffset( this.startButton.getOffset().getX(), this.startButton.getOffset().getY() + this.startButton.getFullBounds().getHeight() + 2 );
        titleImage.setOffset( ( getWidth() - titleImage.getWidth() ) / 2, ( getHeight() - titleImage.getHeight() ) / 2 );
    }

    private void addChild( PNode pNode ) {
        getLayer().addChild( pNode );
    }

}
