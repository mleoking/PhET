/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;

import javax.swing.*;
import java.awt.*;

/**
 * The panel that sits on the right side of the frame and contains the controls for the simulation.
 * By default, the panel has the PhET logo at the top. This can be over-ridden with removeTitle().
 * A panel with a button for showing/hiding help cna be displayed with setHelpPanelEnabled().
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanel {
    private JScrollPane scrollPane;
    private ContentPane controlPane;
    private TitlePanel titlePanel;
    private HelpPanel helpPanel;
    private Scrollable scrollPolicy;

    public ControlPanel( Module module ) {
        controlPane = new ContentPane();
        scrollPolicy = new DefaultScrollPolicy( controlPane );
        controlPane.setFillNone();
        scrollPane = new JScrollPane( controlPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) {
            public Dimension getPreferredSize() {
//                return new Dimension( );
                return super.getPreferredSize();
            }
        };
        System.out.println( "scrollPane.getVerticalScrollBar().getPreferredSize() = " + scrollPane.getVerticalScrollBar().getPreferredSize() );
        System.out.println( "scrollPane.getVerticalScrollBar().getSize()= " + scrollPane.getVerticalScrollBar().getSize() );
        helpPanel = new HelpPanel( module );
        setHelpPanelEnabled( module.hasHelp() );
        titlePanel = new TitlePanel();
        addControlFullWidth( titlePanel );
    }

    public int getScrollBarWidth() {
        return scrollBarVisible() ? scrollBarWidth() : 0;
    }

    private int scrollBarWidth() {
        return scrollPane.getVerticalScrollBar().getPreferredSize().width;
    }

    private boolean scrollBarVisible() {
        return scrollPane.getVerticalScrollBar().isVisible();
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    public void setTitleVisible( boolean visible ) {
        titlePanel.setVisible( visible );
    }

    /**
     * Makes the help button visible/invisible
     *
     * @param isEnabled
     */
    public void setHelpPanelEnabled( boolean isEnabled ) {
        helpPanel.setVisible( isEnabled );
    }

    /**
     * Sets the state of the Help button in the control panel.
     * This is used to keep the menubar's Help menu item
     * in sync with the control panel's Help button.
     *
     * @param enabled
     */
    public void setHelpEnabled( boolean enabled ) {
        helpPanel.setHelpEnabled( enabled );
    }

    /**
     * Shows or hides the PhET logo at the top of the control panel
     *
     * @param isVisible
     */
    public void setLogoVisible( boolean isVisible ) {
        titlePanel.setVisible( isVisible );
    }

    /**
     * Adds a component to the control area.
     *
     * @param component
     * @return the component argument
     */
    public Component addControl( Component component ) {
        return controlPane.add( component );
    }

    /**
     * Removes a component from the control area
     *
     * @param comp
     */
    public void removeControl( Component comp ) {
        controlPane.remove( comp );
    }

    /**
     * Adds a component to the control area, to fill the width.
     *
     * @param component
     */
    public void addControlFullWidth( JComponent component ) {
        controlPane.addFullWidth( component );
    }

    private static class TitlePanel extends JPanel {
        private ImageIcon imageIcon;
        private JLabel titleLabel;
//        private JPanel titlePanel;

        public TitlePanel() {
            // The panel with the logo
            imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" ) );
            titleLabel = ( new JLabel( imageIcon ) );
            titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
            add( titleLabel );

        }
    }


    class DefaultScrollPolicy implements Scrollable {
        JComponent component;

        public DefaultScrollPolicy( JComponent component ) {
            this.component = component;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension( component.getPreferredSize().width + getScrollBarWidth(), component.getPreferredSize().height );
        }

        public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return 10;
        }

        public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return 10;
        }
    }

    class ContentPane extends VerticalLayoutPanel implements Scrollable {

        public boolean getScrollableTracksViewportHeight() {
            return scrollPolicy.getScrollableTracksViewportHeight();
        }

        public boolean getScrollableTracksViewportWidth() {
            return scrollPolicy.getScrollableTracksViewportWidth();
        }

        public Dimension getPreferredScrollableViewportSize() {
            return scrollPolicy.getPreferredScrollableViewportSize();
        }

        public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return scrollPolicy.getScrollableBlockIncrement( visibleRect, orientation, direction );
        }

        public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return scrollPolicy.getScrollableUnitIncrement( visibleRect, orientation, direction );
        }
    }
}