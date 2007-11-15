/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersResources;


public class MiscControlPanel extends JPanel {

    private JButton _equilibriumButton;
    private JButton _resetAllButton;
    private JButton _helpButton;
    
    public MiscControlPanel() {
        super();
        
        _equilibriumButton = new JButton( GlaciersResources.getString( "button.equilibrium" ) );
        _resetAllButton = new JButton( GlaciersResources.getCommonString( "ControlPanel.button.resetAll" ) );
        _helpButton = new JButton( GlaciersResources.getCommonString( "Common.HelpPanel.ShowHelp" ) );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int column = 0;
        layout.addComponent( _equilibriumButton, 0, column++ );
        layout.addComponent( _resetAllButton, 0, column++ );
        layout.addComponent( _helpButton, 0, column++ );
    }
}
