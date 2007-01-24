/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.ConfigurableElementProperties;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.quantum.model.ElectronSource;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * DischargeLampEnergyMonitorPanel2
 * <p/>
 * Wrapper for EnergyLevelMonitorPanel that adds a spinner to set the number of energy levels.
 * <p/>
 * todo; Since there are no instances of DischargeLampEnergyLevelMonitorPanel anywhere other than
 * the one created in this class, this one should subsume the other one.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampEnergyMonitorPanel2 extends JPanel {

    private DischargeLampEnergyLevelMonitorPanel elmp;
    private DischargeLampModel model;
    private JSpinner numLevelsSpinner;
    private JPanel spinnerPanel;
    // Determines if electrons are shown on the panel
    private boolean showElectrons;


    /**
     * @param model
     * @param atomicStates
     * @param panelWidth
     * @param panelHeight
     */
    public DischargeLampEnergyMonitorPanel2(final DischargeLampModel model,
                                            AtomicState[] atomicStates,
                                            int panelWidth, int panelHeight,
                                            final ConfigurableElementProperties configurableElement) {
        super(new GridBagLayout());

        //--------------------------------------------------------------------------------------------------------------
        // Add listeners to the model
        //--------------------------------------------------------------------------------------------------------------

        this.model = model;
        model.addChangeListener(new SpinnerManager());

        // Listen to the plates to for when electrons are emitted
        model.getLeftHandPlate().addElectronProductionListener(new ElectronSource.ElectronProductionListener() {
            public void electronProduced(ElectronSource.ElectronProductionEvent event) {
                if (showElectrons) {
                    addElectron(event.getElectron());
                }
            }
        });
        model.getRightHandPlate().addElectronProductionListener(new ElectronSource.ElectronProductionListener() {
            public void electronProduced(ElectronSource.ElectronProductionEvent event) {
                if (showElectrons) {
                    addElectron(event.getElectron());
                }
            }
        });

        elmp = new DischargeLampEnergyLevelMonitorPanel(model, atomicStates, panelWidth, panelHeight);
        elmp.setBorder(new EtchedBorder());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 10, 0, 10), 0, 0);
        this.add(elmp, gbc);

        // Add the spinner that controls the number of energy levels
        numLevelsSpinner = new JSpinner(new SpinnerNumberModel(DischargeLampsConfig.NUM_ENERGY_LEVELS, 2,
                DischargeLampsConfig.MAX_NUM_ENERGY_LEVELS,
                1));

        // Add a listener that will create the number of atomic states specified by the spinner, and apply them
        // to all the existing atoms
        numLevelsSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int numLevels = ((Integer) numLevelsSpinner.getValue()).intValue();
                configurableElement.setNumEnergyLevels(numLevels);
                model.setElementProperties(configurableElement);
            }
        });

        spinnerPanel = new JPanel(new GridBagLayout());
        JLabel spinnerLabel = new JLabel("# of levels");
        GridBagConstraints gbc2 = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(4, 2, 4, 2), 0, 0);
        gbc2.anchor = GridBagConstraints.EAST;
        spinnerPanel.add(spinnerLabel, gbc2);
        gbc2.gridx = 1;
        gbc2.anchor = GridBagConstraints.WEST;
        spinnerPanel.add(numLevelsSpinner, gbc2);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(spinnerPanel, gbc);
        setSpinnerVisibility(model);
    }

    public void addElectron(Electron electron) {
        elmp.addElectron(electron);
    }

    public void reset() {
        elmp.setEnergyLevels(model.getAtomicStates());
        elmp.setEnergyLevelsMovable(model.getElementProperties().isLevelsMovable());
    }

    public void addAtom(Atom atom) {
        elmp.addAtom(atom);
    }

    public void setEnergyLevels(AtomicState[] atomicStates) {
        elmp.setEnergyLevels(atomicStates);
        elmp.setEnergyLevelsMovable(model.getElementProperties().isLevelsMovable());
    }

    public void setSquigglesEnabled(boolean selected) {
        elmp.setSquigglesEnabled(selected);
    }

    public DischargeLampEnergyLevelMonitorPanel getElmp() {
        return elmp;
    }

    private void setSpinnerVisibility(DischargeLampModel model) {
        if (model.getElementProperties() instanceof ConfigurableElementProperties) {
            spinnerPanel.setVisible(true);
        } else {
            spinnerPanel.setVisible(false);
        }
    }

    public boolean isShowElectrons() {
        return showElectrons;
    }

    public void setShowElectrons(boolean showElectrons) {
        this.showElectrons = showElectrons;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class SpinnerManager extends DischargeLampModel.ChangeListenerAdapter {
        public void energyLevelsChanged(DischargeLampModel.ChangeEvent event) {
            setSpinnerVisibility(event.getDischargeLampModel());
        }
    }
}
