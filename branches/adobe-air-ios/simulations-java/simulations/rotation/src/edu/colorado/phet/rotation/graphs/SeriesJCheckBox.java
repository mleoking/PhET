// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 19, 2007
 * Time: 2:37:55 PM
 */
public class SeriesJCheckBox extends JCheckBox {
    private ControlGraphSeries netForceSeries;

    public SeriesJCheckBox( final ControlGraphSeries netForceSeries ) {
        this.netForceSeries = netForceSeries;
        setText( netForceSeries.getTitle() );
        update();
        netForceSeries.addListener( new ControlGraphSeries.Adapter() {
            public void visibilityChanged() {
                update();
            }
        } );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                netForceSeries.setVisible( isSelected() );
            }
        } );
    }

    private void update() {
        setSelected( netForceSeries.isVisible() );
    }
}
