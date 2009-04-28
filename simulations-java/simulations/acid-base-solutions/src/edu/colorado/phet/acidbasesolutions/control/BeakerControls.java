package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;


public class BeakerControls extends JPanel {
    
    //TODO localize
    private static final String TITLE = "View";
    private static final String DISSOCIATED_COMPONENTS_RATIO_PATTERN = "<html>{0}/{1} ratio</html>";
    private static final String HYDRONIUM_HYDROXIDE_RATIO_PATTERN = "<html>{0}/{1} ratio<html>";
    private static final String MOLECULE_COUNTS = "Molecule Counts";
    private static final String BEAKER_LABEL = "Label";
    
    private final PNode _moleculeCountsNode;
    private final PNode _beakerLabelNode;
    
    private final JCheckBox _dissociatedComponentsRatioCheckBox;
    private final JCheckBox _hyroniumHydroxideRatioCheckBox;
    private final JCheckBox _moleculeCountsCheckBox;
    private final JCheckBox _beakerLabelCheckBox;
    
    public BeakerControls( PNode moleculeCountsNode, PNode beakerLabelNode ) {
        super();
        
        _moleculeCountsNode = moleculeCountsNode;
        _beakerLabelNode = beakerLabelNode;
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 2 ), TITLE );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        _dissociatedComponentsRatioCheckBox = new JCheckBox();
        setDissociatedComponents( ABSSymbols.HA, ABSSymbols.A_MINUS );
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String text = MessageFormat.format( HYDRONIUM_HYDROXIDE_RATIO_PATTERN, args );
        _hyroniumHydroxideRatioCheckBox = new JCheckBox( text );
        
        _moleculeCountsCheckBox = new JCheckBox( MOLECULE_COUNTS, _moleculeCountsNode.getVisible() );
        _moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _moleculeCountsNode.setVisible( _moleculeCountsCheckBox.isSelected() );
            }
        });
        
        _beakerLabelCheckBox = new JCheckBox( BEAKER_LABEL, _beakerLabelNode.getVisible() );
        _beakerLabelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _beakerLabelNode.setVisible( _beakerLabelCheckBox.isSelected() );
            }
        });

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
