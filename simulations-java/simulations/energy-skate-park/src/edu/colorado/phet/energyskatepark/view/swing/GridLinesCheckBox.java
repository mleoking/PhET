// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
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
        checkBox = new PropertyCheckBox( EnergySkateParkResources.getString( "controls.show-grid" ), module.gridVisible );
        add( checkBox );
    }

    private EnergySkateParkRootNode getRoot() {
        return module.getEnergySkateParkSimulationPanel().getRootNode();
    }
}
