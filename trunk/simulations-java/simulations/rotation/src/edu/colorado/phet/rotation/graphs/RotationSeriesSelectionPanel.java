package edu.colorado.phet.rotation.graphs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.rotation.RotationStrings;

/**
 * Author: Sam Reid
 * Jul 13, 2007, 10:44:00 PM
 */
public class RotationSeriesSelectionPanel extends JPanel {
    public RotationSeriesSelectionPanel( RotationGraph graph ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        for ( int i = 0; i < graph.getSeriesPairCount(); i++ ) {
            final RotationGraph.SeriesPair seriesPair = graph.getSeriesPair( i );

//            final ControlGraphSeries series = graph.getControlGraphSeries( i );
            final JCheckBox jCheckBox = new JCheckBox( RotationStrings.getString( "controls.show" ) + " " + seriesPair.getName(), seriesPair.isVisible() );
            jCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    seriesPair.setVisible( jCheckBox.isSelected() );
                }
            } );
            add( jCheckBox );
        }

    }
}
