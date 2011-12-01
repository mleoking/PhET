// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.swing.PropertyCheckBoxNode;
import edu.colorado.phet.energyskatepark.view.swing.PropertyTogglingImageNode;

/**
 * Misc controls for visibility of things in the view (charts, grid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends ControlPanelNode {

    public ViewControlPanel( final EnergySkateParkBasicsModule module, final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final JDialog barChartDialog ) {
        super( new VBox( 10, VBox.LEFT_ALIGNED,
                         new HBox(
                                 //Checkbox to show/hide bar chart
                                 new PropertyCheckBoxNode( EnergySkateParkResources.getString( "plots.bar-graph" ), module.barChartVisible ),
                                 new PropertyTogglingImageNode( EnergySkateParkResources.getImage( "icons/bar_icon.png" ), module.barChartVisible )
                         ),

                         new HBox(
                                 //Checkbox to show/hide the pie chart
                                 new PropertyCheckBoxNode( EnergySkateParkResources.getString( "pieChart" ), module.pieChartVisible ),
                                 new PropertyTogglingImageNode( EnergySkateParkResources.getImage( "icons/pie_icon.png" ), module.pieChartVisible )
                         ),

                         new HBox(
                                 //Checkbox to show/hide the grid lines
                                 new PropertyCheckBoxNode( EnergySkateParkResources.getString( "controls.show-grid" ), module.gridVisible ),
                                 new PropertyTogglingImageNode( EnergySkateParkResources.getImage( "icons/grid_icon.png" ), module.gridVisible )
                         )

        ), EnergySkateParkLookAndFeel.backgroundColor );

        //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, EnergySkateParkBasicsModule.INSET );
            }
        } );
    }
}
