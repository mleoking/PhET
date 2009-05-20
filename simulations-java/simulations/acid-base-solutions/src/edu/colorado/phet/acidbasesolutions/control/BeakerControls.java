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
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


public class BeakerControls extends JPanel {
    
    private static final String RATIO_PATTERN = HTMLUtils.toHTMLString( ABSStrings.CHECK_BOX_RATIO );
    
    private final BeakerNode beakerNode;
    
    private final JCheckBox dissociatedComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final JCheckBox beakerLabelCheckBox;
    
    public BeakerControls( final BeakerNode beakerNode ) {
        super();
        
        this.beakerNode = beakerNode;
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_BEAKER_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        dissociatedComponentsRatioCheckBox = new JCheckBox();
        setDissociatedComponents( "?", "?" );
        dissociatedComponentsRatioCheckBox.setSelected( beakerNode.isDisassociatedRatioComponentsVisible() );
        dissociatedComponentsRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setDisassociatedRatioComponentsVisible( dissociatedComponentsRatioCheckBox.isSelected() );
            }
        });
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String text = MessageFormat.format( RATIO_PATTERN, args );
        hyroniumHydroxideRatioCheckBox = new JCheckBox( text );
        hyroniumHydroxideRatioCheckBox.setSelected( beakerNode.istHydroniumHydroxideRatioVisible() );
        hyroniumHydroxideRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setHydroniumHydroxideRatioVisible( hyroniumHydroxideRatioCheckBox.isSelected() );
            }
        });
        
        moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.setSelected( beakerNode.isMoleculeCountsVisible() );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setMoleculeCountsVisible( moleculeCountsCheckBox.isSelected() );
            }
        });
        
        beakerLabelCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_BEAKER_LABEL );
        beakerLabelCheckBox.setSelected( beakerNode.isBeakerLabelVisible() );
        beakerLabelCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beakerNode.setBeakerLabelVisible( beakerLabelCheckBox.isSelected() );
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
        beakerNode.setDisassociatedRatioComponentsVisible( b );
    }
    
    public boolean isDissociatedComponentsRatioSelected() {
        return dissociatedComponentsRatioCheckBox.isSelected();
    }
    
    public void setHydroniumHydroxideRatioSelected( boolean b ) {
        hyroniumHydroxideRatioCheckBox.setSelected( b );
        beakerNode.setHydroniumHydroxideRatioVisible( b );
    }
    
    public boolean isHydroniumHydroxideRatioSelected() {
        return hyroniumHydroxideRatioCheckBox.isSelected();
    }
    
    public void setMoleculeCountsSelected( boolean b ) {
        moleculeCountsCheckBox.setSelected( b );
        beakerNode.setMoleculeCountsVisible( b );
    }
    
    public boolean isMoleculeCountsSelected() {
        return moleculeCountsCheckBox.isSelected();
    }
    
    public void setBeakerLabelSelected( boolean b ) {
        beakerLabelCheckBox.setSelected( b );
        beakerNode.setBeakerLabelVisible( b );
    }
    
    public boolean isBeakerLabelSelected() {
        return beakerLabelCheckBox.isSelected();
    }
}
