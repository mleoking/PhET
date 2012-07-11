// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Legend for the various different energy chunks, i.e. thermal, electrical,
 * etc.
 *
 * @author John Blanco
 */
public class EnergyChunkLegend extends PNode {
    private static final Font LEGEND_ENTRY_FONT = new PhetFont( 14 );

    public EnergyChunkLegend() {
        PNode contents = new VBox(
                new PhetPText( "Forms of Energy", new PhetFont( 14, true ) ),
                new HBox( new PImage( EnergyFormsAndChangesResources.Images.E_MECH_OUTLINE ), new PhetPText( "Mechanical", LEGEND_ENTRY_FONT ) ),
                new HBox( new PImage( EnergyFormsAndChangesResources.Images.E_ELECTRIC_OUTLINE ), new PhetPText( "Electrical", LEGEND_ENTRY_FONT ) ),
                new HBox( new PImage( EnergyFormsAndChangesResources.Images.E_THERM_OUTLINE ), new PhetPText( "Thermal", LEGEND_ENTRY_FONT ) )
        );
        addChild( new ControlPanelNode( contents, new Color( 250, 250, 250 ) ) );
    }
}
