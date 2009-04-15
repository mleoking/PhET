package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


public class BeakerControls extends JPanel {
    
    //TODO localize
    private static final String TITLE = "View";
    private static final String DISSOCIATED_COMPONENTS_RATIO_PATTERN = "<html>{0}/{1} ratio</html>";
    private static final String HYDRONIUM_HYDROXIDE_RATIO_PATTERN = "<html>{0}/{1} ratio<html>";
    private static final String MOLECULE_COUNTS = "Molecule Counts";
    private static final String BEAKER_LABEL = "Label";
    
    private final JCheckBox _dissociatedComponentsRatioCheckBox;
    private final JCheckBox _hyroniumHydroxideRatioCheckBox;
    private final JCheckBox _moleculeCountsCheckBox;
    private final JCheckBox _beakerLabelCheckBox;
    
    public BeakerControls() {
        super();
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 2 ), TITLE );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        _dissociatedComponentsRatioCheckBox = new JCheckBox();
        setDissociatedComponents( "?", "?" );
        
        Object[] args = { ABSSymbols.H3O, ABSSymbols.OH };
        String text = MessageFormat.format( HYDRONIUM_HYDROXIDE_RATIO_PATTERN, args );
        _hyroniumHydroxideRatioCheckBox = new JCheckBox( text );
        
        _moleculeCountsCheckBox = new JCheckBox( MOLECULE_COUNTS );
        
        _beakerLabelCheckBox = new JCheckBox( BEAKER_LABEL );

        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _dissociatedComponentsRatioCheckBox, row++, column );
        layout.addComponent( _hyroniumHydroxideRatioCheckBox, row++, column );
        layout.addComponent( _moleculeCountsCheckBox, row++, column );
        layout.addComponent( _beakerLabelCheckBox, row++, column );
    }
    
    public void setDissociatedComponents( String component1, String component2 ) {
        Object[] args = { component1, component2 };
        String text = MessageFormat.format( DISSOCIATED_COMPONENTS_RATIO_PATTERN, args );
        _dissociatedComponentsRatioCheckBox.setText( text );
    }
}
