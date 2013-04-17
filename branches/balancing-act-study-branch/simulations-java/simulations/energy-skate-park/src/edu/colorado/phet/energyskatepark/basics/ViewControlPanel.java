// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.swing.PropertyCheckBoxNode;
import edu.colorado.phet.energyskatepark.view.swing.PropertyTogglingImageNode;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.*;

/**
 * Misc controls for visibility of things in the view (charts, grid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author John Blanco
 * @author Sam Reid
 */
public class ViewControlPanel extends ControlPanelNode {
    private static final BufferedImage PIE_ICON = EnergySkateParkResources.getImage( "icons/pie_icon.png" );

    public ViewControlPanel( final EnergySkateParkBasicsModule module ) {
        super( new ContentPane( module ), EnergySkateParkLookAndFeel.backgroundColor );
    }

    public static class ContentPane extends HBox {
        public ContentPane( EnergySkateParkBasicsModule module ) {
            super( 10,
                   new VBox(
                           VBox.LEFT_ALIGNED,
                           //Checkbox to show/hide bar chart
                           new PropertyCheckBoxNode( barGraphCheckBox, EnergySkateParkResources.getString( "plots.bar-graph" ), module.barChartVisible ),
                           new PropertyCheckBoxNode( pieChartCheckBox, EnergySkateParkResources.getString( "pieChart" ), module.pieChartVisible ),
                           new PropertyCheckBoxNode( gridCheckBox, EnergySkateParkResources.getString( "controls.show-grid" ), module.gridVisible ),
                           new PropertyCheckBoxNode( speedCheckBox, EnergySkateParkResources.getString( "properties.speed" ), module.speedVisible )
                   ),

                   new VBox(
                           //Checkbox to show/hide the pie chart
                           new PropertyTogglingImageNode( barGraphCheckBoxIcon, EnergySkateParkResources.getImage( "icons/bar_icon.png" ), module.barChartVisible ),
                           new PropertyTogglingImageNode( pieChartCheckBoxIcon, PIE_ICON, module.pieChartVisible ),
                           new PropertyTogglingImageNode( gridCheckBoxIcon, EnergySkateParkResources.getImage( "icons/grid_icon.png" ), module.gridVisible ),
                           new PropertyTogglingImageNode( speedCheckBoxIcon, new ESPSpeedometerNode( new Property<Option<Double>>( new Some<Double>( 0.0 ) ) ).toImage( PIE_ICON.getWidth(), PIE_ICON.getWidth(), new Color( 0, 0, 0, 0 ) ), module.speedVisible )
                   )
            );
        }
    }
}