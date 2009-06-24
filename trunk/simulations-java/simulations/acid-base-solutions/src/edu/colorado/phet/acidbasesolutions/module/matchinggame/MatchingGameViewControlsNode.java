package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class MatchingGameViewControlsNode extends PhetPNode {
    
    private final JRadioButton beakersRadioButton, graphsRadioButton;
    private final JCheckBox dissociatedComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final ArrayList<ChangeListener> listeners;
    
    public MatchingGameViewControlsNode( Color background ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dissociatedComponentsRatioCheckBox.setEnabled( beakersRadioButton.isSelected() );
                hyroniumHydroxideRatioCheckBox.setEnabled( beakersRadioButton.isSelected() );
                moleculeCountsCheckBox.setEnabled( beakersRadioButton.isSelected() );
                notifyStateChanged();
            }
        };
        
        beakersRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_BEAKERS );
        beakersRadioButton.addActionListener( actionListener );
        
        graphsRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_GRAPHS );
        graphsRadioButton.addActionListener( actionListener );
        
        ButtonGroup group = new ButtonGroup();
        group.add( beakersRadioButton );
        group.add( graphsRadioButton );
        
        dissociatedComponentsRatioCheckBox = new JCheckBox( HTMLUtils.toHTMLString( ABSStrings.CHECK_BOX_DISASSOCIATED_COMPONENTS_RATIO ) );
        dissociatedComponentsRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        });
        
        Object[] args = { ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS };
        String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.CHECK_BOX_RATIO, args ) );
        hyroniumHydroxideRatioCheckBox = new JCheckBox( html );
        hyroniumHydroxideRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        });
        
        moleculeCountsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        });
        
        // panel with border
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_VIEW );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        panel.setBorder( border );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setMinimumWidth( 0, 20 ); // indentation of beaker-related check boxes
        int row = 0;
        int column = 0;
        layout.addComponent( beakersRadioButton, row++, column, 2, 1 );
        column = 1;
        layout.addComponent( dissociatedComponentsRatioCheckBox, row++, column );
        layout.addComponent( hyroniumHydroxideRatioCheckBox, row++, column );
        layout.addComponent( moleculeCountsCheckBox, row++, column );
        column = 0;
        layout.addComponent( graphsRadioButton, row++, column, 2, 1 );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        // default state
        setBeakersSelected( true );
        setHydroniumHydroxideRatioSelected( true );
    }
    
    /**
     * All controls except "H3O/OH ratio" are disabled when user is
     * deciding whether the solution is an acid or base.
     */
    public void setModeAcidBaseQuestion() {
        beakersRadioButton.setEnabled( true );
        dissociatedComponentsRatioCheckBox.setEnabled( false );
        hyroniumHydroxideRatioCheckBox.setEnabled( true );
        moleculeCountsCheckBox.setEnabled( false );
        graphsRadioButton.setEnabled( false );
        setBeakersSelected( true );
        setHydroniumHydroxideRatioSelected( true );
    }
    
    /**
     * All controls are enabled when the user is trying to match the solution.
     */
    public void setModeMatchSolutionQuestion() {
        beakersRadioButton.setEnabled( true );
        dissociatedComponentsRatioCheckBox.setEnabled( beakersRadioButton.isSelected() );
        hyroniumHydroxideRatioCheckBox.setEnabled( beakersRadioButton.isSelected() );
        moleculeCountsCheckBox.setEnabled( beakersRadioButton.isSelected() );
        graphsRadioButton.setEnabled( true );
    }
    
    public void setBeakersSelected( boolean b ) {
        if ( b != isBeakersSelected() ) {
            beakersRadioButton.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isBeakersSelected() {
        return beakersRadioButton.isSelected();
    }
    
    public boolean isGraphsSelected() {
        return graphsRadioButton.isSelected();
    }
    
    public void setDissociatedComponentsRatioSelected( boolean b ) {
        if ( b != isDissociatedComponentsRatioSelected() ) {
            dissociatedComponentsRatioCheckBox.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isDissociatedComponentsRatioSelected() {
        return dissociatedComponentsRatioCheckBox.isSelected();
    }
    
    public void setHydroniumHydroxideRatioSelected( boolean b ) {
        if ( b != isHydroniumHydroxideRatioSelected() ) {
            hyroniumHydroxideRatioCheckBox.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isHydroniumHydroxideRatioSelected() {
        return hyroniumHydroxideRatioCheckBox.isSelected();
    }
    
    public void setMoleculeCountsSelected( boolean b ) {
        if ( b != isMoleculeCountsSelected() ) {
            moleculeCountsCheckBox.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isMoleculeCountsSelected() {
        return moleculeCountsCheckBox.isSelected();
    }
    
    public interface MatchingGameViewChangeListener {
        public void modeChanged();
        public void disassociatedComponentsRatioChanged( boolean selected );
        public void hydroniumHydroxideRatioChanged( boolean selected );
        public void moleculeCountsChanged( boolean selected );
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator<ChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().stateChanged( event );
        }
    }
}
