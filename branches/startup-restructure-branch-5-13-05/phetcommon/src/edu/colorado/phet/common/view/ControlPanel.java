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
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.FractionSpring;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
    private JPanel controlPane = new JPanel( new GridBagLayout() );
    private SpringLayout layout;
    private JLabel titleLabel;
    private HelpPanel helpPanel;
    private ImageIcon imageIcon;
    private int padX = 5;
    private int padY = 5;
    private ArrayList controls = new ArrayList();
    private HashMap panelEntries = new HashMap();
    private Insets defaultInsets = new Insets( 0, 0, 0, 0 );
    private JPanel logoPanel;
    private JScrollPane scrollPane;

//    GridBagConstraints controlsInternalGbc = new GridBagConstraints( 0, 0,
    GridBagConstraints controlsInternalGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                     1, 1, 1, 0,
                                                                     GridBagConstraints.NORTH, GridBagConstraints.NONE,
                                                                     new Insets( 0, 0, 0, 0 ), 0, 0 );
//                                                         GridBagConstraints.NORTH, GridBagConstraints.NONE, insets, 0, 0 );
//                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0 );


    /**
     * @param module
     */
    public ControlPanel( Module module ) {
        this.setLayout( new GridBagLayout() );
        GridBagConstraints logoGbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                             GridBagConstraints.NORTH,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
//        GridBagConstraints controlsGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
        GridBagConstraints controlsGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 1,
                                                                 GridBagConstraints.NORTH,
                                                                 GridBagConstraints.BOTH,
//                                                         GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints helpGbc = new GridBagConstraints( 0, 2, 1, 1, 0, 0,
                                                             GridBagConstraints.SOUTH,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

        // The panel with the logo
        URL resource = getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" );
        imageIcon = new ImageIcon( resource );
        titleLabel = ( new JLabel( imageIcon ) );
        logoPanel = new JPanel();
        logoPanel.add( titleLabel );
        super.add( logoPanel, logoGbc );

        // The panel where the simulation-specific controls go
        scrollPane = new JScrollPane( controlPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setBorder( null );
//        JPanel jp = new JPanel( new FlowLayout( FlowLayout.RIGHT) );
//        jp.add( scrollPane );
//        super.add( controlPane, controlsGbc );
//        super.add( jp, controlsGbc );
        super.add( scrollPane, controlsGbc );

        // The panel for the help button
        helpPanel = new HelpPanel( module );
        super.add( helpPanel, helpGbc );
        setHelpPanelEnabled( module.hasHelp() );
    }

    /**
     * Removes the logo from the control panel
     */
    public void removeTitle() {
        logoPanel.remove( titleLabel );
    }

    /**
     * Makes the help button visible/invisible
     *
     * @param isEnabled
     */
    public void setHelpPanelEnabled( boolean isEnabled ) {
        helpPanel.setVisible( isEnabled );
    }


    //----------------------------------------------------------------
    // Methods for clients to add/remove controls from the panel
    //----------------------------------------------------------------

    /**
     * Adds a component to the control panel using the default positioning. The control will be
     * centered in the panel with default insets.
     *
     * @param comp
     * @return
     */
    public Component add( Component comp ) {
        return add( comp, defaultInsets );
    }

    /**
     * Adds a component to the control panel. The component is expanded horizontally to be as wide as the
     * widest component in the panel.
     *
     * @param comp
     * @return
     */
    public Component addFullWidth( Component comp ) {
//        GridBagConstraints gbc = new GridBagConstraints( 0, 0,
//                                                         1, 1, 0, 0,
//                                                         GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0 );
        GridBagConstraints gbc = (GridBagConstraints)controlsInternalGbc.clone();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return add( comp, gbc );
    }

    /**
     * Adds a component to the control panel with specified insets.
     *
     * @param comp
     * @param insets
     * @return
     */
    public Component add( Component comp, Insets insets ) {
        GridBagConstraints gbc = (GridBagConstraints)controlsInternalGbc.clone();
        gbc.insets = insets;
        return add( comp, gbc );
    }

    /**
     * Adds a componenet to tne control panel using specified GridBagConstraints. Note that the gridy attribute
     * of the constraints will be set by the ControlPanel so that the control is placed below whatever controls
     * are already in the panel.
     *
     * @param comp
     * @param constraints
     * @return
     */
    public Component add( Component comp, GridBagConstraints constraints ) {
        controls.add( comp );
        constraints.gridy = controls.indexOf( comp );
        GridBagConstraints gbc = (GridBagConstraints)controlsInternalGbc.clone();
        this.panelEntries.put( comp, gbc );
        controlPane.add( comp, gbc );
        revalidate();
        repaint();
        return comp;
    }

    /**
     * Removes a compoonent from the control panel. All controls that were below the component will be
     * moved up in the panel.
     *
     * @param comp
     */
    public void remove( Component comp ) {
        // remove the component from the pane
        controlPane.remove( comp );

        // Adjust the positions of the remaining controls
        int idx = controls.indexOf( comp );
        controls.remove( comp );
        for( int i = 0; i < controls.size(); i++ ) {
            Component compToMove = (Component)controls.get( i );
            if( i >= idx ) {
                GridBagConstraints constraints = (GridBagConstraints)panelEntries.get( compToMove );
                constraints.gridy--;
                controlPane.remove( compToMove );
                controlPane.add( compToMove, constraints );
            }
        }

        // Redraw the panel
        revalidate();
        repaint();
    }


    /**
     * Vestigial code used to center the controls in the panel using a SpringLayout. I'm hanging om to this so
     * I'll have an example of SpringLayout use
     */
    private void adjustLayout() {
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
