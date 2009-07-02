package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import edu.colorado.phet.acidbasesolutions.control.RatioCheckBox.HydroniumHydroxideRatioCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * View controls for the Matching Game.
 * These are somewhat similar to view controls in other modules.
 * But they have enough unique requirements that it was preferable
 * to create an implementation that is specific to this module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameViewControlsNode extends PhetPNode {
    
    private final JPanel panel;
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
        
        dissociatedComponentsRatioCheckBox = new JCheckBox( HTMLUtils.toHTMLString( ABSStrings.CHECK_BOX_DISASSOCIATED_COMPONENTS_RATIO ) ) {
            // WORKAROUND for #1704, HTML text doesn't gray out when disabled
            public void setEnabled( boolean enabled ) {
                super.setEnabled( enabled );
                setForeground( enabled ? Color.BLACK : Color.GRAY );
            }
        };
        dissociatedComponentsRatioCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        });
        
        hyroniumHydroxideRatioCheckBox = new HydroniumHydroxideRatioCheckBox();
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
        panel = new JPanel();
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
     * Enabling/disabling this node enabled/disables all swing components.
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        panel.setEnabled( enabled );
        Component[] components = panel.getComponents();
        for ( int i = 0; i < components.length; i++ ) {
            components[i].setEnabled( enabled );
        }
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
    
    public void setGraphsSelected( boolean b ) {
        if ( b != isGraphsSelected() ) {
            graphsRadioButton.setSelected( b );
            notifyStateChanged();
        }
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
