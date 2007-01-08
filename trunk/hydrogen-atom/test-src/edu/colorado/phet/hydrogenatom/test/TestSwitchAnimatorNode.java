/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.SwitchAnimatorNode;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.piccolo.PhetPCanvas;


public class TestSwitchAnimatorNode extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 300, 800 );
    
    public static void main( String[] args ) {
        SimStrings.init( args, HAConstants.LOCALIZATION_BUNDLE_BASENAME );
        JFrame frame = new TestSwitchAnimatorNode( "MasterSwitch test harness");
        frame.show();
    }
    
    public TestSwitchAnimatorNode( String title ) {
        super( title );
        
        PhetPCanvas canvas = new PhetPCanvas();
        
        ModeSwitch modeSwitch = new ModeSwitch();
        AtomicModelSelector atomicModelSelector = new AtomicModelSelector();
        
        SwitchAnimatorNode masterSwitch = new SwitchAnimatorNode( modeSwitch, atomicModelSelector );
        masterSwitch.setOffset( 10, 10 );
        canvas.getLayer().addChild( masterSwitch );
        
        getContentPane().add( canvas );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

}
