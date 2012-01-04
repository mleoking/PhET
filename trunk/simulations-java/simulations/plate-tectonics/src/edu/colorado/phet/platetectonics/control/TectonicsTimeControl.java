// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;

public class TectonicsTimeControl extends PiccoloClockControlPanel {
    public TectonicsTimeControl( final IClock clock ) {
        super( clock );

        setRewindButtonVisible( false );
        setTimeDisplayVisible( true );
        setUnits( "Million Years" );
        setTimeFormat( new DecimalFormat( "0" ) );
        setTimeColumns( 4 );

        // TODO: LinearValueControl seems severely broken in Mac visual display
        LinearValueControl frameRateControl;
        // Frame Rate control
        {
            double min = 1;
            double max = 100;
            String label = "";
            String textFieldPattern = "";
            String units = "";
            frameRateControl = new LinearValueControl( min, max, label, textFieldPattern, units, new ILayoutStrategy() {
                @Override public void doLayout( AbstractValueControl valueControl ) {
                    valueControl.add( valueControl.getSlider() );
                }
            } );
            frameRateControl.setValue( 20 ); // TODO: improve here
            frameRateControl.setMinorTicksVisible( false );

            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( "slow" ) );
            labelTable.put( new Double( max ), new JLabel( "fast" ) );
            frameRateControl.setTickLabels( labelTable );

            // Change font on tick labels
            Dictionary d = frameRateControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent ) { ( (JComponent) o ).setFont( new PhetFont( 10 ) ); }
            }

            // Slider width
            frameRateControl.setSliderWidth( 125 );
        }
        addBetweenTimeDisplayAndButtons( frameRateControl );
        //addBetweenTimeDisplayAndButtons( new JLabel( "Speed control" ) );
    }

}
