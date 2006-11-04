/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

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
    private boolean expanded = false;

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

    protected void showAdvanced() {
        if( !isExpanded() ) {
            expanded = true;
            remove( advanced );
            add( hideButton );
            add( controls );
            validateAll();
            setBorder( BorderFactory.createRaisedBevelBorder() );
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.advancedPanelShown( this );
            }

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
        Window parent = SwingUtilities.getWindowAncestor( this );
//        if( parent instanceof JFrame ) {
//            JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( this );
        parent.invalidate();
        parent.validate();
        parent.repaint();
//        }
    }

    protected void hideAdvanced() {
        if( isExpanded() ) {
            expanded = false;
            remove( hideButton );
            remove( controls );
            add( advanced );
            validateAll();
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.advancedPanelHidden( this );
            }
            setBorder( null );
        }
    }

    private boolean isExpanded() {
        return expanded;
    }

    ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public interface Listener {
        void advancedPanelHidden( AdvancedPanel advancedPanel );

        void advancedPanelShown( AdvancedPanel advancedPanel );
    }

    public VerticalLayoutPanel getControls() {
        return controls;
    }
}