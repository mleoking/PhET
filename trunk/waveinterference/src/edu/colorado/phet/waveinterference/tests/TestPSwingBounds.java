/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 10:06:26 AM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class TestPSwingBounds extends Module {
    private PSwing pswing;

    public TestPSwingBounds() {
        super( "Test PSWing Bounds", new SwingClock( 30, 1 ) );
        PSwingCanvas panel = new PSwingCanvas();
        setSimulationPanel( panel );
        TestJComponent child = new TestJComponent();
        pswing = new PSwing( panel, child );
        panel.getLayer().addChild( pswing );
    }

    class TestJComponent extends VerticalLayoutPanel {
        public TestJComponent() {
//            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            final ModelSlider frequencySlider = new ModelSlider( "Frequency", "Hz", 0, 10, 5 );
            add( frequencySlider );
            frequencySlider.setTextFieldVisible( false );
            final ModelSlider amplitudeSlider = new ModelSlider( "Amplitude", "cm", 0, 2, 1 );
            amplitudeSlider.setTextFieldVisible( false );
            add( amplitudeSlider );
        }
    }

    public static void main( String[] args ) {
        TestPSwingBounds module = new TestPSwingBounds();
        System.out.println( "Made module..." );
        new ModuleApplication().startApplication( args, module );
        module.fix();
    }

    private void fix() {
//        pswing.computeBounds();
    }

}
