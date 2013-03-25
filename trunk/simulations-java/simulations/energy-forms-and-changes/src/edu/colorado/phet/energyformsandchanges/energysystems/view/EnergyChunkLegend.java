// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Legend for the various different energy chunks, i.e. thermal, electrical,
 * etc.
 *
 * @author John Blanco
 */
public class EnergyChunkLegend extends PNode {
    private static final Font LEGEND_ENTRY_FONT = new PhetFont( 14 );
    private static final ModelViewTransform ENERGY_CHUNK_MVT = ModelViewTransform.createIdentity();

    public EnergyChunkLegend() {
        PNode contents = new SwingLayoutNode( new GridBagLayout() ) {{
            GridBagConstraints constraints = new GridBagConstraints();
            // Add the title.
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.FORMS_OF_ENERGY, new PhetFont( 14, true ) ), constraints );

            // Add the legend entries.
            constraints.gridy++;
            constraints.gridwidth = 1;
            constraints.gridx = 0;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets.top = 10;
            constraints.insets.left = 0;
            addChild( EnergyChunkNode.createEnergyChunkNode( EnergyType.MECHANICAL ), constraints );
            constraints.gridx++;
            constraints.insets.left = 5;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.MECHANICAL, LEGEND_ENTRY_FONT ), constraints );

            constraints.gridy++;
            constraints.gridx = 0;
            constraints.insets.top = 5;
            constraints.insets.left = 0;
            addChild( EnergyChunkNode.createEnergyChunkNode( EnergyType.ELECTRICAL ), constraints );
            constraints.gridx++;
            constraints.insets.left = 5;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.ELECTRICAL, LEGEND_ENTRY_FONT ), constraints );

            constraints.gridy++;
            constraints.gridx = 0;
            constraints.insets.left = 0;
            addChild( EnergyChunkNode.createEnergyChunkNode( EnergyType.THERMAL ), constraints );
            constraints.gridx++;
            constraints.insets.left = 5;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.THERMAL, LEGEND_ENTRY_FONT ), constraints );

            constraints.gridy++;
            constraints.gridx = 0;
            constraints.insets.left = 0;
            addChild( EnergyChunkNode.createEnergyChunkNode( EnergyType.LIGHT ), constraints );
            constraints.gridx++;
            constraints.insets.left = 5;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.LIGHT, LEGEND_ENTRY_FONT ), constraints );

            constraints.gridy++;
            constraints.gridx = 0;
            constraints.insets.left = 0;
            addChild( EnergyChunkNode.createEnergyChunkNode( EnergyType.CHEMICAL ), constraints );
            constraints.gridx++;
            constraints.insets.left = 5;
            addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.CHEMICAL, LEGEND_ENTRY_FONT ), constraints );
        }};

        // Create the control panel.
        addChild( new ControlPanelNode( contents, new Color( 250, 250, 250 ) ) );
    }
}
