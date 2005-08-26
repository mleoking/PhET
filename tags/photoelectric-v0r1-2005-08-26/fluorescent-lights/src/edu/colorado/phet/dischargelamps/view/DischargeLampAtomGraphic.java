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

import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.AtomGraphic;

import java.awt.*;

/**
 * DischargeLampAtomGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampAtomGraphic extends AtomGraphic implements Atom.ChangeListener {

    // Time for which the atom will show the color associated with an energy state change
    private long colorTime = 100;
    private Atom atom;
//    private EnergyRepColorStrategy energyRepColorStrategy = new VisibleColorStrategy();
    private EnergyRepColorStrategy energyRepColorStrategy = new GrayScaleStrategy();

    // A number to be displayed in the middle of the atom
    PhetTextGraphic numberGraphic;

    /**
     *
     * @param component
     * @param atom
     */
    public DischargeLampAtomGraphic( Component component, Atom atom ) {
        super( component, atom );
        this.atom = atom;
        getEnergyGraphic().setStroke( new BasicStroke( 0.5f ));
        getEnergyGraphic().setBorderColor( Color.black );
        numberGraphic = new PhetTextGraphic( component, DischargeLampsConfig.defaultControlFont, "", Color.white );
        numberGraphic.setJustification( PhetTextGraphic.CENTER );
        setNumberGraphicText();
        addGraphic( numberGraphic, 1000 );
        getEnergyGraphic().setColor( energyRepColorStrategy.getColor( atom ) );
    }

    /**
     * Sets the text to be written on the atom to be the index of the atom's state, or
     * "G" if it's the ground state.
     */
    private void setNumberGraphicText() {
        // Add a number to the middle of the grpahic
        int stateIdx = atom.getCurrStateNumber();
        String numStr = stateIdx == 0 ? "G" : Integer.toString( stateIdx );
        numberGraphic.setText( numStr );
    }

    //----------------------------------------------------------------
    // Atom.ChangeListener implementation
    //----------------------------------------------------------------

    /**
     * Sets the color for the representation of the atom's energy level when the atom's state
     * changes
     * @param event
     */
    public void stateChanged( Atom.ChangeEvent event ) {
        super.stateChanged( event );

        // TODO: go through this a clean it up
        double dE = event.getPrevState().getEnergyLevel() - event.getCurrState().getEnergyLevel();
        Color energyRepColor = null;
        if( dE > 0 ) {
            double wavelength = PhysicsUtil.energyToWavelength( event.getPrevState().getEnergyLevel() );
            if( wavelength == Photon.GRAY ) {
                energyRepColor = Color.darkGray;
            }
            else {
                energyRepColor = energyRepColorStrategy.getColor( atom );
                if( energyRepColor.equals( VisibleColor.INVISIBLE ) ) {
                    energyRepColor = Color.darkGray;
                }
            }
            new ColorChanger().start();
        }
        else {
            energyRepColor = energyRepColorStrategy.getColor( atom );
//            energyRepColor = Color.darkGray;
        }
        getEnergyGraphic().setColor( energyRepColor );

        setNumberGraphicText();
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Thread that changes the color of the atom energy rep back to gray when it times out
     */
    private class ColorChanger extends Thread {
        public void run() {
            try {
                Thread.sleep( colorTime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            getEnergyGraphic().setColor( energyRepColorStrategy.getColor( atom ) );
            setBoundsDirty();
            repaint();
        }
    }

    /**
     * Picks a Color to represent the energy level of an atom
     */
    private interface EnergyRepColorStrategy {
        Color getColor( Atom atom );
    }

    /**
     * Picks an RGB color that renders the color corresponding to the energy level of the atom
     */
    private class VisibleColorStrategy implements EnergyRepColorStrategy {

        public Color getColor( Atom atom ) {
            double wavelength = atom.getCurrState().getWavelength();
            return VisibleColor.wavelengthToColor( wavelength );
        }
    }

    /**
     * Picks a shade of gray for the energy rep color.
     */
    private class GrayScaleStrategy implements EnergyRepColorStrategy {
        private Color[] grayScale = new Color[240];
//        private Color[] grayScale = new Color[220];

        GrayScaleStrategy() {
            for( int i = 0; i < grayScale.length; i++ ) {
                grayScale[i] = new Color( i, i, i );
            }
        }

        public Color getColor( Atom atom ) {
            int idx = (int)( grayScale.length * ( ( atom.getCurrState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) /
                                                  ( atom.getHighestEnergyState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) ) );
//            int idx = (int)( grayScale.length * ( ( atom.getCurrState().getEnergyLevel() - Photon.wavelengthToEnergy( Photon.MAX_VISIBLE_WAVELENGTH ) ) /
//                                     ( Photon.wavelengthToEnergy( Photon.MIN_VISIBLE_WAVELENGTH ) - Photon.wavelengthToEnergy( ( Photon.MAX_VISIBLE_WAVELENGTH ) ) ) ) );
            idx = Math.min( Math.max( 0, idx ), grayScale.length - 1 );
            return grayScale[idx];
        }
    }
}
