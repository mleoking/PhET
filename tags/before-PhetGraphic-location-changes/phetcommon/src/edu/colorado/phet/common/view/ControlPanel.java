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
 *
 * @author ?
 * @version $Revision$
 */
public class ControlPanel extends JPanel {
    private Module module;
    private JPanel controlPane = new JPanel(new GridBagLayout());
    private SpringLayout layout;
    private JLabel titleLabel;
    private HelpPanel helpPanel;
    private ImageIcon imageIcon;
    private int padX = 5;
    private int padY = 5;
    private ArrayList controls = new ArrayList();
    private HashMap panelEntries = new HashMap();
    private Insets defaultInsets = new Insets(10, 5, 0, 5);


    /**
     * @param module
     */
    public ControlPanel(Module module) {
        this.module = module;
        URL resource = getClass().getClassLoader().getResource("images/Phet-Flatirons-logo-3-small.gif");
        imageIcon = new ImageIcon(resource);

        this.setLayout(new BorderLayout());
        titleLabel = (new JLabel(imageIcon));
        helpPanel = new HelpPanel(module);

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(helpPanel, BorderLayout.SOUTH);
        setHelpPanelEnabled( module.hasHelp() );
        JPanel centerPane = new JPanel();
        centerPane.add(controlPane);
        this.add(centerPane, BorderLayout.CENTER);
    }


    public void setHelpPanelEnabled( boolean isEnabled ) {
        helpPanel.setVisible( isEnabled );        
    }

    /**
     * Vestigial code used to center the controls in the panel using a SpringLayout. I'm hanging om to this so
     * I'll have an example of SpringLayout use
     */
    private void adjustLayout() {
        Dimension controlPaneSize = controlPane.getPreferredSize();
        int controlPaneWidth = (int) Math.round(controlPaneSize.getWidth());
        int controlPaneHeight = (int) Math.round(controlPaneSize.getHeight());
        int width = (int) Math.max(imageIcon.getIconWidth() + padX * 2,
                controlPaneWidth /*+ padX * 2 */);
        int height = (int) (padY * 4 + imageIcon.getIconHeight()
                + helpPanel.getPreferredSize().getHeight()
                + controlPaneHeight);

        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));

        // Create springs to the center of the panel
        Spring containerSouthEdge = layout.getConstraint(SpringLayout.SOUTH, this);
        Spring containerEastEdge = layout.getConstraint(SpringLayout.EAST, this);
        Spring yCenterS = FractionSpring.half(containerSouthEdge);
        Spring xCenterS = FractionSpring.half(containerEastEdge);

        // Place the logo icon
        Spring middleOfIconS = Spring.constant(imageIcon.getIconWidth() / 2);
        Spring leftOfIconS = Spring.sum(xCenterS, Spring.minus(middleOfIconS));
        layout.putConstraint(SpringLayout.NORTH, titleLabel, padY,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, titleLabel, leftOfIconS,
                SpringLayout.WEST, this);

        // Place the panel with the controls
        Spring controlsHalfHeightS = Spring.constant(controlPaneHeight / 2);
        Spring bottomOfIconS = Spring.constant(imageIcon.getIconHeight() + padY * 2);
        Spring controlsTopS = Spring.constant(padY * 2);
        // Enable the following line if you want the controls to float in the middle of
        // the control panel
        //        Spring controlsTopS = Spring.sum( yCenterS, Spring.minus( controlsHalfHeightS ) );
        Spring controlsTopSS = Spring.max(bottomOfIconS, controlsTopS);
        layout.putConstraint(SpringLayout.NORTH, controlPane, controlsTopSS,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, controlPane,
                (int) ((this.getPreferredSize().getWidth() - controlPaneWidth) / 2),
                SpringLayout.WEST, this);

        // Place the help panel
        Spring middleOfHelpPanelS = Spring.constant((int) helpPanel.getPreferredSize().getWidth() / 2);
        Spring leftOfHelpPanelS = Spring.sum(xCenterS, Spring.minus(middleOfHelpPanelS));
        layout.putConstraint(SpringLayout.WEST, helpPanel, leftOfHelpPanelS,
                SpringLayout.WEST, this);
        Spring bottomOfControlsS = Spring.sum(controlsTopSS, Spring.constant(controlPaneHeight));
        Spring minOffsetY = Spring.sum(containerSouthEdge, Spring.minus(Spring.constant((int) helpPanel.getPreferredSize().getHeight())));
        Spring s = Spring.max(Spring.sum(bottomOfControlsS, Spring.constant(padY)),
                minOffsetY);
        layout.putConstraint(SpringLayout.NORTH, helpPanel, s,
                SpringLayout.NORTH, this);
        this.invalidate();
        this.repaint();
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
    public Component add(Component comp) {
        return add(comp, defaultInsets);
    }

    /**
     * Adds a component to the control panel. The component is expanded horizontally to be as wide as the
     * widest component in the panel.
     *
     * @param comp
     * @return
     */
    public Component addFullWidth(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints(0, controls.indexOf(comp),
                1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0);
        return add(comp, gbc);
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
    public Component add(Component comp, GridBagConstraints constraints) {
        controls.add(comp);
        constraints.gridy = controls.indexOf(comp);
        this.panelEntries.put(comp, constraints);
        controlPane.add(comp, constraints);
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
    public Component add(Component comp, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints(0, controls.indexOf(comp),
                1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0);
        return add(comp, gbc);
    }

    /**
     * Removes a compoonent from the control panel. All controls that were below the component will be
     * moved up in the panel.
     *
     * @param comp
     */
    public void remove(Component comp) {
        // remove the component from the pane
        controlPane.remove(comp);

        // Adjust the positions of the remaining controls
        int idx = controls.indexOf(comp);
        controls.remove(comp);
        for (int i = 0; i < controls.size(); i++) {
            Component compToMove = (Component) controls.get(i);
            if (i >= idx) {
                GridBagConstraints constraints = (GridBagConstraints) panelEntries.get(compToMove);
                constraints.gridy--;
                controlPane.remove(compToMove);
                controlPane.add(compToMove, constraints);
            }
        }

        // Redraw the panel
        revalidate();
        repaint();
    }
}
