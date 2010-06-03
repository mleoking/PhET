package edu.colorado.phet.acidbasesolutions.controls;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class ToolsControl extends JPanel {

    private final JRadioButton pHPaperRadioButton;
    private final JRadioButton pHMeterRadioButton;
    private final JRadioButton conductivityTesterRadioButton;
    private final JRadioButton magnifyingGlassRadioButton, barGraphRadioButton;
    private final JCheckBox showWaterCheckBox;
    
    public static class FewerToolsControlPanel extends ToolsControl {
        public FewerToolsControlPanel() {
            setPHPaperControlVisible( false );
            setCondutivityTesterControlVisible( false );
        }
    }
    
    public ToolsControl() {
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TOOLS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        pHPaperRadioButton = new JRadioButton( ABSStrings.PH_PAPER );
        pHMeterRadioButton = new JRadioButton( ABSStrings.PH_METER );
        conductivityTesterRadioButton = new JRadioButton( ABSStrings.CONDUCTIVITY_TESTER );
        magnifyingGlassRadioButton = new JRadioButton( ABSStrings.MAGNIFYING_GLASS );
        barGraphRadioButton = new JRadioButton( ABSStrings.BAR_GRAPH );
        showWaterCheckBox = new JCheckBox( ABSStrings.SHOW_WATER );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( pHPaperRadioButton );
        buttonGroup.add( pHMeterRadioButton );
        buttonGroup.add( conductivityTesterRadioButton );
        buttonGroup.add( magnifyingGlassRadioButton );
        buttonGroup.add( barGraphRadioButton );
        
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
