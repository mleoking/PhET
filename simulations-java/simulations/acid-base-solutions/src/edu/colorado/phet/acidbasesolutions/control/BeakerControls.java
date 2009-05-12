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

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;


public class BeakerControls extends JPanel {
    
    private static final String RATIO_PATTERN = HTMLUtils.toHTMLString( ABSStrings.CHECK_BOX_RATIO );
    
    private final PNode moleculeCountsNode;
    private final PNode beakerLabelNode;
    
    private final JCheckBox _dissociatedComponentsRatioCheckBox;
    private final JCheckBox _hyroniumHydroxideRatioCheckBox;
    private final JCheckBox _moleculeCountsCheckBox;
    private final JCheckBox _beakerLabelCheckBox;
    
    public BeakerControls( final PNode moleculeCountsNode, final PNode beakerLabelNode ) {
        super();
        
        this.moleculeCountsNode = moleculeCountsNode;
        this.beakerLabelNode = beakerLabelNode;
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_BEAKER_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        _dissociatedComponentsRatioCheckBox = new JCheckBox();
        setDissociatedComponents( ABSSymbols.HA, ABSSymbols.A_MINUS );
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String text = MessageFormat.format( RATIO_PATTERN, args );
        _hyroniumHydroxideRatioCheckBox = new JCheckBox( text );
        
        _moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS, moleculeCountsNode.getVisible() );
        _moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moleculeCountsNode.setVisible( _moleculeCountsCheckBox.isSelected() );
            }
        });
        
        _beakerLabelCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_BEAKER_LABEL, beakerLabelNode.getVisible() );
        _beakerLabelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerLabelNode.setVisible( _beakerLabelCheckBox.isSelected() );
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
        String text = MessageFormat.format( RATIO_PATTERN, args );
        _dissociatedComponentsRatioCheckBox.setText( text );
    }
}
