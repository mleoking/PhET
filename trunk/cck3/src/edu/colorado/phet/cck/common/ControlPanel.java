/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.view.help.HelpPanel;

import javax.swing.*;
import java.awt.*;

/**
 * The panel that contains the controls for the simulation, normally on the right side of a PhetFrame.
 * By default, the panel has the PhET logo at the top. This can be over-ridden with setTitleVisible(false);
 * A panel with a button for showing/hiding help can be displayed with setHelpPanelEnabled().
 * <p/>
 * This class no longer extends JComponent, since it is too easy to abuse the interface.
 * To get the associated swing component, use getComponent().
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

    /**
     * Constructs a ControlPanel for the specified module.
     *
     * @param module
     */
    public ControlPanel( Module module ) {
        controlPane = new ContentPane();
        scrollPolicy = new DefaultScrollPolicy( controlPane );
        controlPane.setFillNone();
        scrollPane = new JScrollPane( controlPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scrollPane.setBackground( Color.blue );
        helpPanel = new HelpPanel( module );
        setHelpPanelEnabled( true );
        titlePanel = new TitlePanel();
        addControlFullWidth( titlePanel );
    }

    public HelpPanel getHelpPanel() {
        return helpPanel;
    }

    /**
     * Returns the component for embedding in a swing panel.
     *
     * @return the component for embedding in a swing panel.
     */
    public JComponent getComponent() {
        return scrollPane;
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
        helpPanel.setVisible( enabled );
    }

    /**
     * Shows or hides the PhET logo at the top of the control panel
     *
     * @param isVisible
     */
    public void setTitleVisible( boolean isVisible ) {
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
     * Sets a new scroll policy for the JComponent in this ControlPanel.
     *
     * @param scrollPolicy
     */
    public void setScrollPolicy( Scrollable scrollPolicy ) {
        this.scrollPolicy = scrollPolicy;
    }

    /**
     * Adds a component to the control area, to fill the width.
     *
     * @param component
     */
    public void addControlFullWidth( JComponent component ) {
        controlPane.addFullWidth( component );
    }

    private int getVisibleScrollBarWidth() {
        return scrollBarVisible() ? scrollBarWidth() : 0;
    }

    private int scrollBarWidth() {
        return scrollPane.getVerticalScrollBar().getPreferredSize().width;
    }

    private boolean scrollBarVisible() {
        return scrollPane.getVerticalScrollBar().isVisible();
    }

    /**
     * This class represents the logo at the top of the ControlPanel.
     */
    private static class TitlePanel extends JPanel {
        private ImageIcon imageIcon;
        private JLabel titleLabel;

        public TitlePanel() {
            imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" ) );
            titleLabel = ( new JLabel( imageIcon ) );
            titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
            add( titleLabel );
        }
    }

    /**
     * The DefaultScrollPolicy uses the preferred size of the component, adding on the width of the visible scroll bars
     * in the ControlPanel.
     */
    public class DefaultScrollPolicy implements Scrollable {
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
            return new Dimension( component.getPreferredSize().width + getVisibleScrollBarWidth(), component.getPreferredSize().height );
        }

        public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return 10;
        }

        public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
            return 10;
        }
    }

    /**
     * This is where the controls go in the ControlPanel.  It delegates its Scrollable interface to
     */
    private class ContentPane extends VerticalLayoutPanel implements Scrollable {

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