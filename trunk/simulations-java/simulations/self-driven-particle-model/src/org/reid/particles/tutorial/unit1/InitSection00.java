/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.PButton;
import org.reid.particles.tutorial.Page;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 2:07:37 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class InitSection00 extends Page {
    private boolean showedNextButtonForAudioTest = false;
    private PButton testAudioGraphic;

    public InitSection00( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        String initText = "This tutorial is interactive--to move through the tutorial you must complete tasks.  " +
                          "When you demonstrate " +
                          "your knowledge, a song will play and a \"Next\" button will appear.  Start by pressing the " +
                          "\'Test Audio\' button.  (You may press it repeatedly to adjust your speakers' volume.)" +
                          "";
        super.setText( initText );

        PButton testAudioGraphic = new PButton( basicPage, "Test Audio" );
        testAudioGraphic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                basicPage.playHarp();
                if( !showedNextButtonForAudioTest ) {
                    showedNextButtonForAudioTest = true;
                    basicPage.showNextButton();
                }
            }
        } );

        this.testAudioGraphic = testAudioGraphic;
    }

    public void init() {
        super.init();
        getBasePage().topLeft( testAudioGraphic );
        getBasePage().addChild( this.testAudioGraphic );
    }

    public void teardown() {
        getBasePage().removeChild( testAudioGraphic );
    }

}
