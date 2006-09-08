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
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.help.HelpPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.net.URL;
import java.util.Arrays;

/**
 * ControlPanel
 * <p/>
 * The panel that sits on the right side of the frame and contains the controls for the simulation.
 * <p/>
 * By default, the panel has the PhET logo at the top. This can be over-ridden with removeTitle().
 * <p/>
 * A panel with a button for showing/hiding help cna be displayed with setHelpPanelEnabled().
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanel extends JPanel {
    private static final int CONTROLS_OFF = 0;
    private static final int CONTROLS_ON = 1;
    private static final int CONTROLS_SCROLL = 2;

    private ControlPanel.ContentPanel controlPane = new ContentPanel();
    private ImageIcon imageIcon;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private HelpPanel helpPanel;
    private JPanel northPanel;
    private int paddingDY = 5;

    public ContentPanel getControlPane() {
        return controlPane;
    }

    public ControlPanel( Module module ) {
        setLayout( new ControlPanel.Layout() );
        // The panel with the logo
        URL resource = getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" );
        imageIcon = new ImageIcon( resource );
        titleLabel = ( new JLabel( imageIcon ) );
        northPanel = new JPanel();
        northPanel.add( titleLabel );
        addToPanel( northPanel );

        // The panel where the simulation-specific controls go
        scrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scrollPane.setBorder( new EtchedBorder( new Color( 220, 200, 255 ), Color.gray ) );

        // The panel for the help button
        helpPanel = new HelpPanel( module );
        addToPanel( helpPanel );
        setHelpPanelEnabled( module.hasHelp() );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayoutControlPanel();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutControlPanel();
            }
        } );

        addContainerListener( new ContainerAdapter() {
            public void componentAdded( ContainerEvent e ) {
                relayoutControlPanel();
            }

            public void componentRemoved( ContainerEvent e ) {
                relayoutControlPanel();
            }
        } );
        relayoutControlPanel();
    }

    /**
     * Called when a control is added to the control panel. Forces the ScrollPaneManager
     * to recompute its prefered and minimum sizes.
     *
     * @deprecated
     */
    public void resizeControlPane() {
        ControlPanel.this.revalidate();
    }

    /**
     * Helper function so clients can safely use add(Component) to add to the control section.
     *
     * @param component
     */
    private void addToPanel( Component component ) {
        super.add( component );
    }

    /**
     * Adds a component to the control area.
     *
     * @param comp
     * @return
     * @deprecated Use addControl instead
     */
    public Component add( Component comp ) {
        return addControl( comp );
    }

    /**
     * Removes the logo from the control panel
     */
    public void removeTitle() {
        northPanel.remove( titleLabel );
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
     * Adds a component to the control area.
     *
     * @param component
     * @return
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
     * @return
     */
    public Component addFullWidth( Component component ) {
        return controlPane.addFullWidth( component );
    }

    /**
     * Adds a component to the control area, to fill the width.
     *
     * @param component
     */
    public void addControlFullWidth( JComponent component ) {
        controlPane.addFullWidth( component );
    }

    private void relayoutControlPanel() {
        revalidate();
        repaint();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Layout manager for the panel
     */
    private class Layout implements LayoutManager {

        public void removeLayoutComponent( Component comp ) {
        }

        /**
         * Determines whether a vertical scroll bar is needed, and sizes the panel appropriately
         *
         * @param parent
         */
        public void layoutContainer( Container parent ) {
            northPanel.reshape( getPositionToCenter( northPanel ), 0, northPanel.getPreferredSize().width, northPanel.getPreferredSize().height );
            helpPanel.reshape( getPositionToCenter( helpPanel ), getHeight() - helpPanel.getPreferredSize().height, helpPanel.getPreferredSize().width, helpPanel.getPreferredSize().height );
            int controlTop = getLogoBottom() + paddingDY;
            int controlBottom = getHelpTop() - paddingDY;
            int remainingHeight = controlBottom - controlTop;

            remove( scrollPane );
            remove( controlPane );
            scrollPane.setViewportView( null );

            if( remainingHeight <= 0 ) {
                //no room for controls, sorry.
            }
            else if( controlPane.getPreferredSize().height > remainingHeight ) {
                //not enough room for all controls, so use vertical scrolling.
                controlPane.setLocation( 0, 0 );
                scrollPane.setViewportView( controlPane );
                addToPanel( scrollPane );

                scrollPane.setLocation( 0, controlTop );
                scrollPane.setSize( new Dimension( controlPane.getPreferredSize().width + getScrollBarWidth(), remainingHeight ) );

                scrollPane.revalidate();
            }
            else {
                //controls will fit without scrollpane.
                addToPanel( controlPane );
                controlPane.reshape( 0, controlTop, controlPane.getPreferredSize().width, controlPane.getPreferredSize().height );
//                reshapeAll(controlPane);
            }
            if( isMacOSX() ) {
                fixAll( controlPane );
            }
//            controlPane.repaint();
        }

        private boolean isMacOSX() {
            String lcOSName = System.getProperty( "os.name" ).toLowerCase();
            boolean MAC_OS_X = lcOSName.startsWith( "mac os x" );
//            System.out.println( "MAC_OS_X = " + MAC_OS_X );
            return MAC_OS_X;
        }

        /**
         * On a Mac, setBounds and repaint seem to be necessary to ensure visibility of all descendants.
         *
         * @param component
         */
        private void fixAll( Component component ) {
            Dimension d = component.getPreferredSize();

            component.setBounds( component.getX(), component.getY(), d.width, d.height );
            component.repaint();
            if( component instanceof Container ) {
                Container c = (Container)component;
                for( int i = 0; i < c.getComponentCount(); i++ ) {
                    fixAll( c.getComponent( i ) );
                }
            }

        }

        /**
         * Determine what x will center the specified component in this.
         *
         * @param component
         * @return
         */
        private int getPositionToCenter( Component component ) {
            int availWidth = getWidth() - component.getWidth();
            if( availWidth <= 0 ) {
                return 0;
            }
            else {
                return availWidth / 2;
            }
        }

        /**
         * Find the top of the south (help) panel.
         *
         * @return
         */
        private int getHelpTop() {
            if( helpPanel.isVisible() && containsComponent( helpPanel ) ) {
                return helpPanel.getY();
            }
            else {
                return getHeight();
            }
        }

        /**
         * Find the bottom of the north (logo) panel.
         *
         * @return
         */
        private int getLogoBottom() {
            if( northPanel.isVisible() && containsComponent( northPanel ) ) {
                return northPanel.getY() + northPanel.getHeight();
            }
            else {
                return 0;
            }
        }

        /**
         * Determine whether this container contains the specified component.
         *
         * @param component
         * @return
         */
        private boolean containsComponent( Component component ) {
            return Arrays.asList( getComponents() ).contains( component );
        }

        public void addLayoutComponent( String name, Component comp ) {
        }

        public Dimension minimumLayoutSize( Container parent ) {
            return preferredLayoutSize( parent );
        }

        public Dimension preferredLayoutSize( Container parent ) {
            int minWidth = getMinWidth();
            return new Dimension( minWidth, parent.getHeight() );
        }

        /**
         * Computes the preferred width of the control panel.
         * If we anticipate scrollbars, we widen to accomodate.
         *
         * @return
         */
        private int getMinWidth() {
            int width = 0;
            if( northPanel.isVisible() ) {
                width = Math.max( width, northPanel.getPreferredSize().width );
//                width = Math.max( width, northPanel.getWidth() );
            }
            if( controlPane.isVisible() ) {
                width = Math.max( width, controlPane.getPreferredSize().width );
//                width = Math.max( width, controlPane.getWidth() );
            }
            if( helpPanel.isVisible() ) {
                width = Math.max( width, helpPanel.getPreferredSize().width );
//                width = Math.max( width, helpPanel.getWidth() );
            }
            if( getLayoutRequiresScrollPane() ) {
                width += getScrollBarWidth();
            }
            return width;
        }

        private int getScrollBarWidth() {
            return scrollPane.getVerticalScrollBar().getPreferredSize().width;
        }

        /**
         * Determine whether we anticipate using vertical scrollbars.
         *
         * @return
         */
        private boolean getLayoutRequiresScrollPane() {
            int controlTop = getLogoBottom() + paddingDY;
            int controlBottom = getHelpTop() - paddingDY;
            int remainingHeight = controlBottom - controlTop;
            if( remainingHeight < controlPane.getPreferredSize().height ) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Class of panel for holding simulation-specific controls
     */
    private class ContentPanel extends VerticalLayoutPanel {
        public ContentPanel() {
            setFill( GridBagConstraints.NONE );
        }

    }

}
