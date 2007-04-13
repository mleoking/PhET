/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.colorvision.phetcommon.view;

import edu.colorado.phet.colorvision.phetcommon.application.Module;
import edu.colorado.phet.colorvision.phetcommon.view.help.HelpPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ControlPanel
 *
 * @author ?
 * @version $Revision$
 */
public class PhetControlPanel extends JPanel {
    private JPanel controlPane = new JPanel( new GridBagLayout() );
    private JLabel titleLabel;
    private HelpPanel helpPanel;
    private ImageIcon imageIcon;
    private ArrayList controls = new ArrayList();
    private HashMap panelEntries = new HashMap();
    private Insets defaultInsets = new Insets( 0, 0, 0, 0 );


    /**
     * @param module
     */
    public PhetControlPanel( Module module ) {
        this.setLayout( new BorderLayout() );

        // Logo at top of panel
        URL resource = getClass().getClassLoader().getResource( "color-vision/images/Phet-Flatirons-logo-3-small.gif" );
        imageIcon = new ImageIcon( resource );
        titleLabel = ( new JLabel( imageIcon ) );
        JPanel p = new JPanel();
        p.add( titleLabel );
        this.add( p, BorderLayout.NORTH );

        // Panel for help button
        helpPanel = new HelpPanel( module );
        JPanel hp = new JPanel();
        hp.add( helpPanel );
        this.add( hp, BorderLayout.SOUTH );
        setHelpPanelEnabled( module.hasHelp() );

        // Panel for simulation-specific controls
        setControlPane( new JPanel() );
        JPanel centerPane = new JPanel();
        centerPane.add( controlPane );
        setControlPane( controlPane );
    }

    public PhetControlPanel( Module module, JPanel panel ) {
        this( module );
        this.add( panel );
    }

    public void setHelpPanelEnabled( boolean isEnabled ) {
        helpPanel.setVisible( isEnabled );
    }

    /**
     * This method does nothing anymore. It was used to adjust things when this class used a SpringLayout.
     * It is kept only for backward compatibility.
     * Commented, vestigial code used to center the controls in the panel using a SpringLayout. I'm hanging om to this so
     * I'll have an example of SpringLayout use.
     *
     * @deprecated
     */
    public void adjustLayout() {

        // Does nothing
        return;

//        Dimension controlPaneSize = controlPane.getPreferredSize();
//        int controlPaneWidth = (int)Math.round( controlPaneSize.getWidth() );
//        int controlPaneHeight = (int)Math.round( controlPaneSize.getHeight() );
//        int width = (int)Math.max( imageIcon.getIconWidth() + padX * 2,
//                                   controlPaneWidth /*+ padX * 2 */ );
//        int height = (int)( padY * 4 + imageIcon.getIconHeight()
//                            + helpPanel.getPreferredSize().getHeight()
//                            + controlPaneHeight );
//
//        this.setMinimumSize( new Dimension( width, height ) );
//        this.setPreferredSize( new Dimension( width, height ) );
//
//        // Create springs to the center of the panel
//        Spring containerSouthEdge = layout.getConstraint( SpringLayout.SOUTH, this );
//        Spring containerEastEdge = layout.getConstraint( SpringLayout.EAST, this );
//        Spring yCenterS = FractionSpring.half( containerSouthEdge );
//        Spring xCenterS = FractionSpring.half( containerEastEdge );
//
//        // Place the logo icon
//        Spring middleOfIconS = Spring.constant( imageIcon.getIconWidth() / 2 );
//        Spring leftOfIconS = Spring.sum( xCenterS, Spring.minus( middleOfIconS ) );
//        layout.putConstraint( SpringLayout.NORTH, titleLabel, padY,
//                              SpringLayout.NORTH, this );
//        layout.putConstraint( SpringLayout.WEST, titleLabel, leftOfIconS,
//                              SpringLayout.WEST, this );
//
//        // Place the panel with the controls
//        Spring controlsHalfHeightS = Spring.constant( controlPaneHeight / 2 );
//        Spring bottomOfIconS = Spring.constant( imageIcon.getIconHeight() + padY * 2 );
//        Spring controlsTopS = Spring.constant( padY * 2 );
//        // Enable the following line if you want the controls to float in the middle of
//        // the control panel
//        //        Spring controlsTopS = Spring.sum( yCenterS, Spring.minus( controlsHalfHeightS ) );
//        Spring controlsTopSS = Spring.max( bottomOfIconS, controlsTopS );
//        layout.putConstraint( SpringLayout.NORTH, controlPane, controlsTopSS,
//                              SpringLayout.NORTH, this );
//        layout.putConstraint( SpringLayout.WEST, controlPane,
//                              (int)( ( this.getPreferredSize().getWidth() - controlPaneWidth ) / 2 ),
//                              SpringLayout.WEST, this );
//
//        // Place the help panel
//        Spring middleOfHelpPanelS = Spring.constant( (int)helpPanel.getPreferredSize().getWidth() / 2 );
//        Spring leftOfHelpPanelS = Spring.sum( xCenterS, Spring.minus( middleOfHelpPanelS ) );
//        layout.putConstraint( SpringLayout.WEST, helpPanel, leftOfHelpPanelS,
//                              SpringLayout.WEST, this );
//        Spring bottomOfControlsS = Spring.sum( controlsTopSS, Spring.constant( controlPaneHeight ) );
//        Spring minOffsetY = Spring.sum( containerSouthEdge, Spring.minus( Spring.constant( (int)helpPanel.getPreferredSize().getHeight() ) ) );
//        Spring s = Spring.max( Spring.sum( bottomOfControlsS, Spring.constant( padY ) ),
//                               minOffsetY );
//        layout.putConstraint( SpringLayout.NORTH, helpPanel, s,
//                              SpringLayout.NORTH, this );
//        this.invalidate();
//        this.repaint();
    }

    /**
     * Sets the central pane in the control panel. Provided wiht public access primarilly for
     * backward compatibility to older simulations.
     *
     * @param controlPane
     */
    public void setControlPane( JPanel controlPane ) {
        JPanel centerPane = new JPanel();
        centerPane.add( controlPane );
        JScrollPane scrollPane = new JScrollPane( centerPane );
        scrollPane.setBorder( null );
        this.controlPane = controlPane;
        this.add( scrollPane, BorderLayout.CENTER );
    }

    public JPanel getControlPane() {
        return controlPane;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Add/remove methods
    //

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
        GridBagConstraints gbc = new GridBagConstraints( 0, controls.indexOf( comp ),
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0 );
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
        this.panelEntries.put( comp, constraints );
        controlPane.add( comp, constraints );
        revalidate();
        repaint();
        return comp;
    }

    /**
     * Adds a component to the control panel with specified insets.
     *
     * @param comp
     * @param insets
     * @return
     */
    public Component add( Component comp, Insets insets ) {
        GridBagConstraints gbc = new GridBagConstraints( 0, controls.indexOf( comp ),
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0 );
        return add( comp, gbc );
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
}
