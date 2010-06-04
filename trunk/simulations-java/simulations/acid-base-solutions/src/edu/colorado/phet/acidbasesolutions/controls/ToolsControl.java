/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various "tools".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolsControl extends JPanel {

    private final JRadioButton pHPaperRadioButton, pHMeterRadioButton;
    private final JRadioButton conductivityTesterRadioButton;
    private final JRadioButton magnifyingGlassRadioButton, barGraphRadioButton;
    private final JCheckBox showWaterCheckBox;
    
    /**
     * Subclass that hides some of the tools.
     */
    public static class FewerToolsControlPanel extends ToolsControl {
        public FewerToolsControlPanel() {
            setPHPaperControlVisible( false );
            setCondutivityTesterControlVisible( false );
        }
    }
    
    public ToolsControl() {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TOOLS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //XXX update model
            }
        };
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, listener );
        pHMeterRadioButton = new ABSRadioButton( ABSStrings.PH_METER, group, listener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY_TESTER, group, listener );
        magnifyingGlassRadioButton = new ABSRadioButton( ABSStrings.MAGNIFYING_GLASS, group, listener );
        barGraphRadioButton = new ABSRadioButton( ABSStrings.BAR_GRAPH, group, listener );
        
        // "Show Water" check box
        showWaterCheckBox = new JCheckBox( ABSStrings.SHOW_WATER );
        showWaterCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //XXX update
            }
        });
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHPaperRadioButton, row++, column );
        layout.addComponent( pHMeterRadioButton, row++, column );
        layout.addComponent( conductivityTesterRadioButton, row++, column );
        layout.addComponent( magnifyingGlassRadioButton, row++, column );
        layout.addComponent( barGraphRadioButton, row++, column );
        layout.addComponent( showWaterCheckBox, row++, column );
        
        // default state
        pHPaperRadioButton.setSelected( true );
        showWaterCheckBox.setSelected( false );
    }
    
    protected void setPHPaperControlVisible( boolean visible ) {
        pHPaperRadioButton.setVisible( visible );
    }
    
    protected void setCondutivityTesterControlVisible( boolean visible ) {
        conductivityTesterRadioButton.setVisible( visible );
    }
}
