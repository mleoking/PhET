/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 7, 2006
 * Time: 11:10:03 PM
 *
 */

public class GridLinesCheckBox extends VerticalLayoutPanel {
    private EnergySkateParkModule module;

    public GridLinesCheckBox( final EnergySkateParkModule module ) {
        this.module = module;
        final JCheckBox gridlines = new JCheckBox( EnergySkateParkStrings.getString( "show.grid" ), getRoot().isGridVisible() );
        gridlines.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getRoot().setGridVisible( gridlines.isSelected() );
            }
        } );
        add( gridlines );
    }

    public EnergySkateParkRootNode getRoot() {
        return module.getEnergyConservationCanvas().getRootNode();
    }
}
