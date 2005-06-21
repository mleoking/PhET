/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Has an expand and collapse button.
 */

public class AdvancedPanel extends VerticalLayoutPanel {

    private JButton advanced;
    private JButton hideButton;
    private VerticalLayoutPanel controls;

    /**
     * Create an AdvancedPanel with text Advanced >> and Hide <<.
     */
    public AdvancedPanel() {
        this( "Advanced >>", "Hide <<" );
    }

    /**
     * Create an AdvancedPanel with specified expansion and collapse button labels.
     *
     * @param show
     * @param hide
     */
    public AdvancedPanel( String show, String hide ) {
        controls = new VerticalLayoutPanel();
        controls.setFillNone();

        advanced = new JButton( show );
        advanced.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showAdvanced();
            }
        } );

        hideButton = new JButton( hide );
        hideButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                hideAdvanced();
            }
        } );
        setFill( GridBagConstraints.NONE );
        add( advanced );
    }

    /**
     * Add another control to the expanded Advanced Panel.
     *
     * @param control
     */
    public void addControl( JComponent control ) {
        controls.add( control );
    }

    /**
     * Add another control to the expanded Advanced Panel, to span the width.
     *
     * @param control
     */
    public void addControlFullWidth( JComponent control ) {
        controls.addFullWidth( control );
    }

    private void showAdvanced() {
        remove( advanced );
        add( hideButton );
        add( controls );
        validateAll();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.advancedPanelShown( this );
        }
    }

    private void validateAll() {
        invalidate();
        controls.invalidate();
        if( getParent() != null ) {
            getParent().invalidate();
            if( getParent().getParent() != null ) {
                getParent().getParent().invalidate();
                getParent().getParent().validate();
            }
        }
        JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( this );
        parent.invalidate();
        parent.validate();
        parent.repaint();
    }

    private void hideAdvanced() {
        remove( hideButton );
        remove( controls );
        add( advanced );
        validateAll();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.advancedPanelHidden( this );
        }
    }

    ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public interface Listener {
        void advancedPanelHidden( AdvancedPanel advancedPanel );

        void advancedPanelShown( AdvancedPanel advancedPanel );
    }
}
