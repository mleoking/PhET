package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.umd.cs.piccolox.pswing.PSwing;


public class ReactionChoiceNode extends PhetPNode {
    
    private final ArrayList<ChangeListener> listeners;
    private final JRadioButton waterRadioButton, ammoniaRadioButton, methaneRadioButton;
    
    public ReactionChoiceNode() {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        
        waterRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_WATER );
        waterRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireStateChanged();
            }
        });
        ammoniaRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_AMMONIA );
        ammoniaRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireStateChanged();
            }
        });
        methaneRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_METHANE );
        methaneRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireStateChanged();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add( waterRadioButton );
        group.add( ammoniaRadioButton );
        group.add( methaneRadioButton );
        waterRadioButton.setSelected( true );
        
        JPanel panel = new JPanel();
        panel.setBackground( new Color( 0, 0, 0, 0 ) ); // transparent
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( waterRadioButton, row++, column );
        layout.addComponent( ammoniaRadioButton, row++, column );
        layout.addComponent( methaneRadioButton, row++, column );
        
        addChild( new PSwing( panel ) );
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( e );
        }
    }

}
