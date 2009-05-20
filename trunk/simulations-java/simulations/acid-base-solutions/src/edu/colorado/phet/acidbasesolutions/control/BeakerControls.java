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
    
    private final JCheckBox dissociatedComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final JCheckBox beakerLabelCheckBox;
    
    public BeakerControls( final PNode moleculeCountsNode, final PNode beakerLabelNode ) {
        super();
        
        this.moleculeCountsNode = moleculeCountsNode;
        this.beakerLabelNode = beakerLabelNode;
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_BEAKER_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        dissociatedComponentsRatioCheckBox = new JCheckBox();
        setDissociatedComponents( "?", "?" );
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String text = MessageFormat.format( RATIO_PATTERN, args );
        hyroniumHydroxideRatioCheckBox = new JCheckBox( text );
        
        moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.setSelected( moleculeCountsNode.getVisible() );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moleculeCountsNode.setVisible( moleculeCountsCheckBox.isSelected() );
            }
        });
        
        beakerLabelCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_BEAKER_LABEL );
        beakerLabelCheckBox.setSelected( beakerLabelNode.getVisible() );
        beakerLabelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerLabelNode.setVisible( beakerLabelCheckBox.isSelected() );
            }
        });

        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( dissociatedComponentsRatioCheckBox, row++, column );
        layout.addComponent( hyroniumHydroxideRatioCheckBox, row++, column );
        layout.addComponent( moleculeCountsCheckBox, row++, column );
        layout.addComponent( beakerLabelCheckBox, row++, column );
    }
    
    public void setDissociatedComponents( String component1, String component2 ) {
        Object[] args = { component1, component2 };
        String text = MessageFormat.format( RATIO_PATTERN, args );
        dissociatedComponentsRatioCheckBox.setText( text );
    }
    
    public void setDissociatedComponentsRatioSelected( boolean b ) {
        dissociatedComponentsRatioCheckBox.setSelected( b );
        //XXX node.setVisible( b );
    }
    
    public boolean isDissociatedComponentsRatioSelected() {
        return dissociatedComponentsRatioCheckBox.isSelected();
    }
    
    public void setHydroniumHydroxideRatioSelected( boolean b ) {
        hyroniumHydroxideRatioCheckBox.setSelected( b );
        //XXX node.setVisible( b );
    }
    
    public boolean isHydroniumHydroxideRatioSelected() {
        return hyroniumHydroxideRatioCheckBox.isSelected();
    }
    
    public void setMoleculeCountsSelected( boolean b ) {
        moleculeCountsCheckBox.setSelected( b );
        moleculeCountsNode.setVisible( b );
    }
    
    public boolean isMoleculeCountsSelected() {
        return moleculeCountsCheckBox.isSelected();
    }
    
    public void setBeakerLabelSelected( boolean b ) {
        beakerLabelCheckBox.setSelected( b );
        beakerLabelNode.setVisible( b );
    }
    
    public boolean isBeakerLabelSelected() {
        return beakerLabelCheckBox.isSelected();
    }
}
