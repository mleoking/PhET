package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;

/**
 * User: Sam Reid
 * Date: Jun 30, 2004
 * Time: 1:09:58 PM
 */
public class CCKKeyListener implements KeyListener {
    CCKModule cck;

    public CCKKeyListener( CCKModule cck ) {
        this.cck = cck;
    }

    public void keyPressed( KeyEvent e ) {
        if ( e.getKeyCode() == KeyEvent.VK_C ) {
            cck.resetDynamics();
        }
    }

    public void keyReleased( KeyEvent e ) {
        if ( e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE ) {
            cck.deleteSelectedBranches();
            cck.desolderSelection();
        }
        else if ( e.getKeyCode() == KeyEvent.VK_A && e.isControlDown() ) {
            cck.selectAll();
        }
        else if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            boolean orig = cck.isLifelike();
            for ( int i = 0; i < 50; i++ ) {
                cck.setLifelike( !orig );
                cck.setLifelike( orig );
                System.out.println( "Lifelike count = " + i );
            }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
            Frame[] win = JFrame.getFrames();
            for ( int i = 0; i < win.length; i++ ) {
                Frame frame = win[i];
                frame.repaint();
            }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_T ) {
            cck.addTestCircuit();
        }
        else if ( e.getKeyCode() == KeyEvent.VK_R ) {
            cck.clear();
            cck.addTestCircuit();
            for ( int i = 0; i < 500; i++ ) {
                cck.layoutElectrons( cck.getCircuit().getBranches() );
                System.out.println( "Relayout = " + i );
            }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_SEMICOLON ) {
            for ( int i = 0; i < 50; i++ ) {
                cck.addTestCircuit();
                cck.clear();
                System.out.println( "Num Test Circuits = " + i );
            }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_Z ) {
            cck.clear();
            cck.addTestCircuit();
            for ( int i = 0; i < 5000; i++ ) {
                cck.layoutElectrons( cck.getCircuit().getBranches() );
                System.out.println( "Relayout = " + i );
            }
        }
//        else if ( e.getKeyCode() == KeyEvent.VK_K ) {
//            ModifiedNodalAnalysis_Orig na = new ModifiedNodalAnalysis_Orig();
//            na.apply( cck.getCircuit() );
//        }
    }

    public void keyTyped( KeyEvent e ) {
    }


}
