package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.membranediffusion.MembraneDiffusionResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

/**
 * Control panel for the Photon Absorption tab of this application.
 * 
 * @author John Blanco
 */
public class PhotonAbsorptionControlPanel extends ControlPanel {
	
	public PhotonAbsorptionControlPanel (PiccoloModule module){

    	// Set the control panel's minimum width.
        int minimumWidth = MembraneDiffusionResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Put some space at the top of the panel.
        addControlFullWidth(createVerticalSpacingPanel(20));
        
        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        VerticalLayoutPanel greenhouseGasPanel = new VerticalLayoutPanel();
        greenhouseGasPanel.setBorder(createTitledBorder("Greenhouse Gas"));
        addControlFullWidth(greenhouseGasPanel);
        
        // Add buttons for selecting greenhouse gas.
        JRadioButton h2oButton = new JRadioButton("H2O");
        greenhouseGasPanel.add(h2oButton);
        JRadioButton co2Button = new JRadioButton("CO2");
        greenhouseGasPanel.add(co2Button);
        JRadioButton ch4Button = new JRadioButton("CH4");
        greenhouseGasPanel.add(ch4Button);
        JRadioButton n2oButton = new JRadioButton("N2O");
        greenhouseGasPanel.add(n2oButton);
        
        // Create and add a panel that will contain the buttons for selecting
        // the non-greenhouse gasses.
        VerticalLayoutPanel otherGas = new VerticalLayoutPanel();
        otherGas.setBorder(createTitledBorder("Other Gas"));
        addControlFullWidth(otherGas);
        
        // Add buttons for other gas selection.
        JRadioButton n2Button = new JRadioButton("N2");
        otherGas.add(n2Button);
        JRadioButton o2Button = new JRadioButton("O2");
        otherGas.add(o2Button);

        // Create and add a panel that will contain the buttons for selecting
        // the atmosphere.
        VerticalLayoutPanel atmosphere = new VerticalLayoutPanel();
        atmosphere.setBorder(createTitledBorder("Atmosphere"));
        addControlFullWidth(atmosphere);
        
        // Add buttons for atmosphere selection.
        JRadioButton earthAtmosphereButton = new JRadioButton("Earth");
        atmosphere.add(earthAtmosphereButton);
        JRadioButton venusAtmosphereButton = new JRadioButton("Venus");
        atmosphere.add(venusAtmosphereButton);

        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(60));
        addResetAllButton( module );
	}

	private TitledBorder createTitledBorder(String title) {
		BevelBorder otherGasBaseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( otherGasBaseBorder,
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
		return titledBorder;
	}
	
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
}
