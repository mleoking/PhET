// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkRootNode;

/**
 * User: Sam Reid
 * Date: Jun 7, 2006
 * Time: 11:10:03 PM
 */

public class GridLinesCheckBox extends VerticalLayoutPanel {
    private final AbstractEnergySkateParkModule module;
    public final JCheckBox checkBox;

    public GridLinesCheckBox( final AbstractEnergySkateParkModule module ) {
        this.module = module;
        checkBox = new JCheckBox( EnergySkateParkResources.getString( "controls.show-grid" ), getRoot().isGridVisible() );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getRoot().setGridVisible( checkBox.isSelected() );
            }
        } );
        add( checkBox );
        getRoot().addGridVisibilityChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                checkBox.setSelected( getRoot().isGridVisible() );
            }
        } );
    }

    private EnergySkateParkRootNode getRoot() {
        return module.getEnergySkateParkSimulationPanel().getRootNode();
    }
}
