/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:23:59 PM
 */

public class DGReaderControlPanel extends VerticalLayoutPanel {
    private DGModule dgModule;

    public DGReaderControlPanel( final DGModule dgModule ) {
        this.dgModule = dgModule;
        setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "intensity.reader.testing.only" ) ) );
        JRadioButton edge = new JRadioButton( QWIResources.getString( "edge" ), dgModule.getPlotPanel().isIntensityReaderEdge() );
        JRadioButton circ = new JRadioButton( QWIResources.getString( "radial" ), dgModule.getPlotPanel().isIntensityReaderRadial() );
        add( edge );
        add( circ );
        edge.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.getPlotPanel().setEdgeIntensityReader();
            }
        } );
        circ.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.getPlotPanel().setRadialIntensityReader();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( edge );
        buttonGroup.add( circ );
    }
}
