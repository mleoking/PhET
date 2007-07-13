package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Author: Sam Reid
 * Jul 13, 2007, 10:13:47 AM
 */
public class SeriesSelectionPanel extends JPanel {
    public SeriesSelectionPanel( final ControlGraph controlGraph ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        for( int i = 0; i < controlGraph.getSeriesCount(); i++ ) {
            final ControlGraph.ControlGraphSeries series = controlGraph.getControlGraphSeries( i );
            final JCheckBox jCheckBox = new JCheckBox( series.getTitle(), series.isVisible() );
            jCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    controlGraph.setSeriesVisible( series, jCheckBox.isSelected() );
                }
            } );
            add( jCheckBox );
        }
    }
}
