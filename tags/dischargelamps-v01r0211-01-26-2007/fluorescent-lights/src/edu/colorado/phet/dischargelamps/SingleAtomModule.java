/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.control.ElectronProductionControl;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.view.CollisionEnergyIndicator;
import edu.colorado.phet.dischargelamps.view.DischargeLampEnergyMonitorPanel2;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.quantum.model.ElectronSource;
import edu.colorado.phet.quantum.model.Tube;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * SingleAtomModule
 * <p/>
 * Provides a lamp with a single atom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SingleAtomModule extends DischargeLampModule {
    private DischargeLampAtom atom;
    private double maxCurrent = 3;
    private CollisionEnergyIndicator collisionEnergyIndicatorGraphic;
    private JPanel logoPanel;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * Constructor
     *
     * @param clock
     */
    protected SingleAtomModule( String name, IClock clock ) {
        super( name, clock );
        addAtom( getTube() );

        // Set model parameters
        setElectronProductionMode( ElectronProductionControl.SINGLE_SHOT );
        getDischargeLampModel().setMaxCurrent( getMaxCurrent() );

        // Make the area from which the cathode emits electrons very small, so they will always
        // come out of the middle of the plate
        getDischargeLampModel().getLeftHandPlate().setEmittingLength( 1 );
        getDischargeLampModel().getRightHandPlate().setEmittingLength( 1 );

        setSquigglesEnabled( true );
    }

    protected double getMaxCurrent() {
        return 25;
    }


    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     */
    private void addAtom( Tube tube ) {
        Rectangle2D tubeBounds = tube.getBounds();

        atom = new DischargeLampAtom( (LaserModel)getModel(), getDischargeLampModel().getElementProperties() );
        atom.setPosition( tubeBounds.getX() + tubeBounds.getWidth() / 2,
                          tubeBounds.getY() + tubeBounds.getHeight() / 2 );
        AtomGraphic atomGraphic = addAtom( atom );
        // The graphic may have been put behind the circuit graphic (it is randomly put in front of or behind
        // the circuit in DischargeLampModule.addAtom(). We need to make sure it is above the circuit graphic
        // so that we can get at it with the mouse
        getApparatusPanel().removeGraphic( atomGraphic );
        getApparatusPanel().addGraphic( atomGraphic, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        // Make the atom movable with the mouse within the bounds of the tube
        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
                                                         tubeBounds.getMinY() + atom.getRadius(),
                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
                                                         tubeBounds.getHeight() - atom.getRadius() * 2 );
        atomGraphic.setIsMouseable( true, atomBounds );
        atomGraphic.setCursorHand();

        getEneregyLevelsMonitorPanel().reset();
    }

    /**
     * @return
     */
    public Atom getAtom() {
        return atom;
    }
}
