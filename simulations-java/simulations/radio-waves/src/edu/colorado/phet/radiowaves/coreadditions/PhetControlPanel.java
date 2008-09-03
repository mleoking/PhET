/**
 * Class: PhetControlPanel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Jul 15, 2004
 */
package edu.colorado.phet.radiowaves.coreadditions;

import edu.colorado.phet.common_1200.application.Module;
import edu.colorado.phet.common_1200.view.help.HelpPanel;
import edu.colorado.phet.common_1200.view.util.FractionSpring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.net.URL;

/**
 * This class is a JPanel with the PhET logo at the top, a HelpPanel at the
 * bottom, and a client-supplied JPanel in between. For this panel to lay out
 * properly, the client-supplied panel must provide a meaningful Dimension when
 * getPreferredSize() is called on it.
 */
public class PhetControlPanel extends JPanel {
    private Module module;
    private JPanel controlPane;
    private SpringLayout layout;
    private JLabel titleLabel;
    private HelpPanel helpPanel;
    private ImageIcon imageIcon;
    private int padX = 5;
    private int padY = 5;

    /**
     * @param module
     * @param controlPane A panel with application-specific controls
     */
    public PhetControlPanel( Module module, JPanel controlPane ) {
        this.module = module;
        this.controlPane = controlPane;
        layout = new SpringLayout();
        this.setLayout( layout );
        URL resource = getClass().getClassLoader().getResource( "radio-waves/images/Phet-Flatirons-logo-3-small.gif" );
        imageIcon = new ImageIcon( resource );
        titleLabel = ( new JLabel( imageIcon ) );
        helpPanel = new HelpPanel( module );

        this.add( titleLabel );
        this.add( controlPane );
        this.add( helpPanel );

        adjustLayout();
        controlPane.addContainerListener( new ContainerAdapter() {
            public void componentAdded( ContainerEvent e ) {
                adjustLayout();
            }

            public void componentRemoved( ContainerEvent e ) {
                adjustLayout();
            }
        } );

        controlPane.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                adjustLayout();
            }

            public void componentShown( ComponentEvent e ) {
                adjustLayout();
            }
        } );
    }

    protected void adjustLayout() {
        Dimension controlPaneSize = controlPane.getPreferredSize();
        int controlPaneWidth = (int)Math.round( controlPaneSize.getWidth() );
        int controlPaneHeight = (int)Math.round( controlPaneSize.getHeight() );
        int width = (int)Math.max( imageIcon.getIconWidth() + padX * 2,
                                   controlPaneWidth /*+ padX * 2 */ );
        int height = (int)( padY * 4 + imageIcon.getIconHeight()
                            + helpPanel.getPreferredSize().getHeight()
                            + controlPaneHeight );

        this.setMinimumSize( new Dimension( width, height ) );
        this.setPreferredSize( new Dimension( width, height ) );

        // Create springs to the center of the panel
        Spring containerSouthEdge = layout.getConstraint( SpringLayout.SOUTH, this );
        Spring containerEastEdge = layout.getConstraint( SpringLayout.EAST, this );
        Spring yCenterS = FractionSpring.half( containerSouthEdge );
        Spring xCenterS = FractionSpring.half( containerEastEdge );

        // Place the logo icon
        Spring middleOfIconS = Spring.constant( imageIcon.getIconWidth() / 2 );
        Spring leftOfIconS = Spring.sum( xCenterS, Spring.minus( middleOfIconS ) );
        layout.putConstraint( SpringLayout.NORTH, titleLabel, padY,
                              SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, titleLabel, leftOfIconS,
                              SpringLayout.WEST, this );

        // Place the panel with the controls
        Spring controlsHalfHeightS = Spring.constant( controlPaneHeight / 2 );
        Spring bottomOfIconS = Spring.constant( imageIcon.getIconHeight() + padY * 2 );
        Spring controlsTopS = Spring.constant( padY * 2 );
        // Enable the following line if you want the controls to float in the middle of
        // the control panel
//        Spring controlsTopS = Spring.sum( yCenterS, Spring.minus( controlsHalfHeightS ) );
        Spring controlsTopSS = Spring.max( bottomOfIconS, controlsTopS );
        layout.putConstraint( SpringLayout.NORTH, controlPane, controlsTopSS,
                              SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, controlPane,
                              (int)( ( this.getPreferredSize().getWidth() - controlPaneWidth ) / 2 ),
                              SpringLayout.WEST, this );

        // Place the help panel
        Spring middleOfHelpPanelS = Spring.constant( (int)helpPanel.getPreferredSize().getWidth() / 2 );
        Spring leftOfHelpPanelS = Spring.sum( xCenterS, Spring.minus( middleOfHelpPanelS ) );
        layout.putConstraint( SpringLayout.WEST, helpPanel, leftOfHelpPanelS,
                              SpringLayout.WEST, this );
        Spring bottomOfControlsS = Spring.sum( controlsTopSS, Spring.constant( controlPaneHeight ) );
        Spring minOffsetY = Spring.sum( containerSouthEdge, Spring.minus( Spring.constant( (int)helpPanel.getPreferredSize().getHeight() ) ) );
        Spring s = Spring.max( Spring.sum( bottomOfControlsS, Spring.constant( padY ) ),
                               minOffsetY );
        layout.putConstraint( SpringLayout.NORTH, helpPanel, s,
                              SpringLayout.NORTH, this );
        this.invalidate();
        this.repaint();
    }
}
