// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.*;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1.IntroductionUnit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SelfDrivenParticleModelApplication {
    TutorialFrame tutorialFrame;
    TitleScreen titleScreen;
    ArrayList units = new ArrayList();
    private Unit currentUnit;
    private int currentUnitIndex = 0;
    private TutorialKeyHandler keyHandler;

    public SelfDrivenParticleModelApplication() {
        tutorialFrame = new TutorialFrame( this );
        keyHandler = new TutorialKeyHandler();
        if( isLowResolution() ) {
            tutorialFrame.setSize( 800, 680 );
        }
        else {
            tutorialFrame.setSize( 800, 825 );
        }
        titleScreen = new TitleScreen( this );
        tutorialFrame.setContentPane( titleScreen );
        centerWindowOnScreen( tutorialFrame );

        Unit unitZero = new IntroductionUnit( this );
        units.add( unitZero );

        Unit unitOne = new EmergenceUnit( this );
        units.add( unitOne );

        Unit doneUnit = new FinalUnit( this );
        units.add( doneUnit );

        titleScreen.addKeyListener( keyHandler );
    }

    public KeyListener getKeyHandler() {
        return keyHandler;
    }

    public boolean isFirstUnit() {
        return currentUnitIndex == 0;
    }

    public class TutorialKeyHandler implements KeyListener {

        public void keyPressed( KeyEvent e ) {

            System.out.println( "e = " + e );
            if( e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_NUMPAD2 ) {
                System.out.println( "e = 2" + e );
                setUnit( 1 );
            }
            if( e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_NUMPAD1 ) {
                System.out.println( "e = 1" + e );
                setUnit( 0 );
            }
            if( e.getKeyCode() == KeyEvent.VK_3 || e.getKeyCode() == KeyEvent.VK_NUMPAD3 ) {
                System.out.println( "e = 3" + e );
                setUnit( 2 );
            }
            if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                moveRight();
            }
            else if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                moveLeft();
            }
        }

        public void keyReleased( KeyEvent e ) {
        }

        public void keyTyped( KeyEvent e ) {
        }
    }


    private void moveLeft() {
        if( currentUnit != null ) {
            currentUnit.moveLeft();
        }
    }

    private void moveRight() {
        if( currentUnit != null ) {
            currentUnit.moveRight();
        }
    }

    public static void centerWindowOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        window.setLocation( (int)( screenSize.getWidth() / 2 - window.getWidth() / 2 ), 0 );
    }

    public static boolean isLowResolution() {
        return Toolkit.getDefaultToolkit().getScreenSize().height <= 768;
    }

    public static void main( String[] args ) {
        SelfDrivenParticleModelApplication tutorialApplication = new SelfDrivenParticleModelApplication();
        tutorialApplication.start();
    }

    private void start() {
        tutorialFrame.show();
        titleScreen.requestFocus();
    }

    public void startTutorial() {
        //each unit has a title page, and conclusion page and some data
        setUnit( 0 );
    }

    private void setUnit( int i ) {
        this.currentUnitIndex = i;
        if( currentUnit != null ) {
            currentUnit.teardown( this );
        }
        unitAt( i ).start( this );
        currentUnit = unitAt( i );
    }

    private Unit unitAt( int i ) {
        return (Unit)units.get( i );
    }

    public void setContentPane( JComponent jc ) {
        tutorialFrame.setContentPane( jc );
        fixit( tutorialFrame.getContentPane() );
        fixit( tutorialFrame );
    }

    private void fixit( Component jc ) {
        jc.invalidate();
        jc.validate();
        jc.doLayout();
        jc.repaint();
    }

    public TutorialFrame getTutorialFrame() {
        return tutorialFrame;
    }

    public void nextUnit() {
        setUnit( currentUnitIndex + 1 );
    }

    public void previousUnit() {
        if( currentUnitIndex == 0 ) {
            return;
        }
        else {
            setUnit( currentUnitIndex - 1 );
        }
    }
}
