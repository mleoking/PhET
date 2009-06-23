package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class ComparingViewControlsNode extends PhetPNode {
    
    private final JRadioButton beakersRadioButton, graphsRadioButton, equationsRadioButton;
    private final ArrayList<ChangeListener> listeners;
    
    public ComparingViewControlsNode( Color background ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        };
        
        beakersRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_BEAKERS );
        beakersRadioButton.addActionListener( actionListener );
        
        graphsRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_GRAPHS );
        graphsRadioButton.addActionListener( actionListener );
        
        equationsRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATIONS );
        equationsRadioButton.addActionListener( actionListener );
        
        ButtonGroup group = new ButtonGroup();
        group.add( beakersRadioButton );
        group.add( graphsRadioButton );
        group.add( equationsRadioButton );
        
        // panel with border
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_VIEW );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        panel.setBorder( border );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( beakersRadioButton, row++, column );
        layout.addComponent( graphsRadioButton, row++, column );
        layout.addComponent( equationsRadioButton, row++, column );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        // default state
        beakersRadioButton.setSelected( true );
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
    
    public void setEquationsSelected( boolean b ) {
        if ( b != isEquationsSelected() ) {
            equationsRadioButton.setSelected( b );
            notifyStateChanged();
        }
    }
    
    public boolean isEquationsSelected() {
        return equationsRadioButton.isSelected();
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
