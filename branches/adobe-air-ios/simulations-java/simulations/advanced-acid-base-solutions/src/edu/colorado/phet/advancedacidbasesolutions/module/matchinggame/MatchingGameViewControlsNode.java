// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.module.matchinggame;

import java.awt.Color;
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

import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.advancedacidbasesolutions.control.RatioCheckBox.GeneralSoluteRatioCheckBox;
import edu.colorado.phet.advancedacidbasesolutions.control.RatioCheckBox.HydroniumHydroxideRatioCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
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
    private final JCheckBox soluteComponentsRatioCheckBox;
    private final JCheckBox hyroniumHydroxideRatioCheckBox;
    private final JCheckBox moleculeCountsCheckBox;
    private final ArrayList<ChangeListener> listeners;
    
    public MatchingGameViewControlsNode( Color background ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateCheckBoxEnable();
                notifyStateChanged();
            }
        };
        
        beakersRadioButton = new JRadioButton( AABSStrings.RADIO_BUTTON_BEAKERS );
        beakersRadioButton.addActionListener( actionListener );
        
        graphsRadioButton = new JRadioButton( AABSStrings.RADIO_BUTTON_GRAPHS );
        graphsRadioButton.addActionListener( actionListener );
        
        ButtonGroup group = new ButtonGroup();
        group.add( beakersRadioButton );
        group.add( graphsRadioButton );
        
        soluteComponentsRatioCheckBox = new GeneralSoluteRatioCheckBox() {
            // WORKAROUND for #1704, HTML text doesn't gray out when disabled
            public void setEnabled( boolean enabled ) {
                super.setEnabled( enabled );
                setForeground( enabled ? Color.BLACK : Color.GRAY );
            }
        };
        soluteComponentsRatioCheckBox.addActionListener( new ActionListener() {
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
        
        moleculeCountsCheckBox = new JCheckBox( AABSStrings.CHECK_BOX_MOLECULE_COUNTS );
        moleculeCountsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        });
        
        // panel with border
        panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), AABSStrings.TITLE_VIEW );
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
        layout.addComponent( soluteComponentsRatioCheckBox, row++, column );
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

    private void updateCheckBoxEnable() {
        boolean enabled = beakersRadioButton.isEnabled();
        soluteComponentsRatioCheckBox.setEnabled( beakersRadioButton.isSelected() && enabled );
        hyroniumHydroxideRatioCheckBox.setEnabled( beakersRadioButton.isSelected() && enabled );
        moleculeCountsCheckBox.setEnabled( beakersRadioButton.isSelected() && enabled );
    }
    
    /**
     * Enable and disable Swing controls.
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        beakersRadioButton.setEnabled( enabled );
        graphsRadioButton.setEnabled( enabled );
        updateCheckBoxEnable();
    }
    
    public void setBeakersSelected( boolean b ) {
        if ( b != isBeakersSelected() ) {
            beakersRadioButton.setSelected( b );
            updateCheckBoxEnable();
            notifyStateChanged();
        }
    }
    
    public boolean isBeakersSelected() {
        return beakersRadioButton.isSelected();
    }
    
    public void setGraphsSelected( boolean b ) {
        if ( b != isGraphsSelected() ) {
            graphsRadioButton.setSelected( b );
            updateCheckBoxEnable();
            notifyStateChanged();
        }
    }
    
    public boolean isGraphsSelected() {
        return graphsRadioButton.isSelected();
    }
    
    public void setSoluteComponentsRatioSelected( boolean b ) {
        if ( b != isSoluteComponentsRatioSelected() ) {
            soluteComponentsRatioCheckBox.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isSoluteComponentsRatioSelected() {
        return soluteComponentsRatioCheckBox.isSelected();
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
