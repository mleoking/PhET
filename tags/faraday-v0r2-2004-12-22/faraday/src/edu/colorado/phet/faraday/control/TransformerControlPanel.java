/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.module.TransformerModule;

/**
 * TransformerControlPanel
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TransformerControlPanel extends ControlPanel {
    
    private TransformerModule _module;
    
    /**
     * Sole constructor.
     * 
     * @param module the module that this control panel is associated with.
     */
    public TransformerControlPanel( TransformerModule module ) {
        
        super( module );
        
        _module = module;
        
        JPanel panel = new JPanel();
        {
            Font defaultFont = panel.getFont();
            Font titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );
            
            // WORKAROUND: Filler to set consistent panel width
            JPanel fillerPanel = new JPanel();
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
            
            //  Layout so that control groups fill horizontal space.
            BorderLayout layout = new BorderLayout();
            layout.setVgap( 20 ); // vertical space between control groups
            panel.setLayout( layout );
            panel.add( fillerPanel, BorderLayout.NORTH );
            
            // Wire up event handling
            EventListener listener = new EventListener();
        }
        super.setControlPane( panel );
    }
    
    private class EventListener implements ActionListener, ChangeListener {
        
        public EventListener() {}
        
        public void actionPerformed( ActionEvent e ) {
            throw new IllegalArgumentException( "unexpected event: " + e );
        }
        
        public void stateChanged( ChangeEvent e ) {
            throw new IllegalArgumentException( "unexpected event: " + e );
        }
    }
    
}