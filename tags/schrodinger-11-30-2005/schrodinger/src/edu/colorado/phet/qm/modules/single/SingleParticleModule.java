/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.qm.ModelDebugger;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.SwoopText;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.event.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends SchrodingerModule {
    private SwoopText swoopText;

    public SingleParticleModule( SchrodingerApplication clock ) {
        super( "Single Particles", clock );
//        setDiscreteModel( new DiscreteModel( 100, 100 ) );
        setDiscreteModel( new DiscreteModel() );
//        setDiscreteModel( new DiscreteModel( 40,40) );
        final SingleParticlePanel schrodingerPanel = new SingleParticlePanel( this );
        setSchrodingerPanel( schrodingerPanel );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
        getModel().addModelElement( new ModelDebugger( getClass() ) );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setBrightnessSliderVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setTypeControlVisible( false );
//        getModel().addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                System.out.println( "Stepped @ "+System.currentTimeMillis());
//            }
//        } );

        getSchrodingerPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                showSwoopText( schrodingerPanel, 2000 );
            }
        } );
        getSchrodingerPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( containsSwoopText() ) {
                    removeSwoopText();
                }
            }
        } );
        getSchrodingerPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_S ) {
                    System.out.println( "SingleParticleModule.keyPressed" );
                    if( containsSwoopText() ) {
                        removeSwoopText();
                    }
                    swoopText = null;
                    showSwoopText( schrodingerPanel, 0 );
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        finishInit();
    }

    private boolean containsSwoopText() {
        return getSchrodingerPanel().getScreenNode().getChildrenReference().contains( swoopText );
    }

    private void removeSwoopText() {
        getSchrodingerPanel().removeScreenChild( swoopText );
    }

    private void showSwoopText( final SingleParticlePanel schrodingerPanel, int initialDelay ) {
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( swoopText == null ) {
                    PNode fireJC = schrodingerPanel.getAbstractGun().getFireButtonGraphic();

                    swoopText = new SwoopText( "Push the button.", fireJC.getGlobalFullBounds().getMaxX() + 20, fireJC.getGlobalFullBounds().getY() );
                    getSchrodingerPanel().addScreenChild( swoopText );
                    swoopText.animateAll();
                }
            }
        } );
        timer.setInitialDelay( initialDelay );
        timer.setRepeats( false );
        timer.start();
    }
}
