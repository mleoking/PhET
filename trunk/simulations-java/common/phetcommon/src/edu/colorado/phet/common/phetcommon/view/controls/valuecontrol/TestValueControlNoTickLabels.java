package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

import javax.swing.*;

/**
 * Demonstration of failure of setting both Minor & Major tick labels both invisible on a LinearValueControl.
 * Workaround is to use getSlider().setPaintTicks(false)
 *
 * @author Sam Reid
 */
public class TestValueControlNoTickLabels extends JFrame {

    public TestValueControlNoTickLabels() {
        final LinearValueControl protonsControl = new LinearValueControl( 0, 10, "label", "0.00", "units" );
        //these lines fail
        protonsControl.setMinorTickLabelsVisible( false );
        protonsControl.setMajorTickLabelsVisible( false );

        //but these lines work
//        protonsControl.getSlider().setPaintTicks( false );
//        protonsControl.getSlider().setPaintLabels( false );

        //and slider works properly
        JSlider slider = new JSlider( 0, 100 );
        slider.setPaintTicks( false );

        JPanel contentPane = new VerticalLayoutPanel();
        contentPane.add( protonsControl );
        contentPane.add( slider );

        setContentPane( contentPane );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        new TestValueControlNoTickLabels().show();
    }
}
