/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.quantum.model.Atom;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * AnnotatedAtomGraphic
 * <p/>
 * An AtomGraphic annotated with a number, or the character "G", that indicates its
 * energy state.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AnnotatedAtomGraphic extends AtomGraphic implements Atom.ChangeListener {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private BufferedImage[] characters;

    static {
        try {
            characters = new BufferedImage[]{
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "G.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "1.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "2.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "3.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "4.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "5.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "6.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "7.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "8.png" ),
                ImageLoader.loadBufferedImage( LaserConfig.IMAGE_DIRECTORY + "9.png" )
            };
        }
        catch( Exception e ) {
            System.out.println( "e = " + e );
        }
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private PhetImageGraphic[] characterGraphics = new PhetImageGraphic[10];
    // Time for which the atom will show the color associated with an energy state change
    private long colorTime = 100;
    private Atom atom;

    // A number to be displayed in the middle of the atom
    PhetGraphic numberGraphic;
//    private Font font;


    /**
     * @param component
     * @param atom
     */
    public AnnotatedAtomGraphic( Component component, Atom atom ) {
        super( component, atom );
        this.atom = atom;

        // Initialize image graphics for energy level indicators
        for( int i = 0; i < 10; i++ ) {
            characterGraphics[i] = new PhetImageGraphic( component, characters[i] );
            characterGraphics[i].setRegistrationPoint( characters[i].getWidth() / 2, characters[i].getHeight() / 2 );
        }

        getEnergyGraphic().setStroke( new BasicStroke( 0.5f ) );
        getEnergyGraphic().setBorderColor( Color.black );
//        font = new Font( LaserConfig.DEFAULT_CONTROL_FONT.getName(),
//                         LaserConfig.DEFAULT_CONTROL_FONT.getStyle(),
//                         LaserConfig.DEFAULT_CONTROL_FONT.getSize() + 8 );
        // Put the number graphic in the middle of the atom graphic
        numberGraphic = characterGraphics[0];
        addGraphic( numberGraphic, 1000 );

        determineEnergyRadiusAndColor();
        getEnergyGraphic().setColor( getEnergyRepColorStrategy().getColor( atom ) );
        setNumberGraphicText();
        update();
    }

    /**
     * Sets the text to be written on the atom to be the index of the atom's state, or
     * "G" if it's the ground state.
     */
    private void setNumberGraphicText() {
        // Add a number to the middle of the grpahic
        int stateIdx = atom.getCurrStateNumber();
        removeGraphic( numberGraphic );
        numberGraphic = characterGraphics[stateIdx];
        addGraphic( numberGraphic, 1000 );
    }

    /**
     * Overrides parent class behavior to prevent it determining the color of
     * the energy rep.
     */
    public void update() {
        setLocation( (int)( getAtom().getPosition().getX() ),
                     (int)( getAtom().getPosition().getY() ) );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Atom.ChangeListener implementation
    //----------------------------------------------------------------

    /**
     * Sets the color for the representation of the atom's energy level when the atom's state
     * changes
     *
     * @param event
     */
    public void stateChanged( final Atom.ChangeEvent event ) {
        // Let the superclass determine the radius of the energy representation, then override
        // its choice of color
        determineEnergyRadiusAndColor();
        getEnergyGraphic().setColor( getEnergyRepColorStrategy().getColor( atom ) );
        setNumberGraphicText();
        update();
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
//            getEnergyGraphic().setColor( energyRepColorStrategy.getColor( atom ) );
            setBoundsDirty();
            repaint();
        }
    }

    private int getStateIdx( Atom atom ) {
        int stateIdx = -1;
        for( int i = 0; i < atom.getStates().length; i++ ) {
            if( atom.getCurrState() == atom.getStates()[i] ) {
                stateIdx = i;
                break;
            }
        }
        return stateIdx;
    }


//    /**
//     * Picks a Color to represent the energy level of an atom
//     */
//    private interface EnergyRepColorStrategy {
//        Color getColor( Atom atom );
//    }
//
//    /**
//     * Picks an RGB color that renders the color corresponding to the energy level of the atom
//     */
//    private class VisibleColorStrategy implements EnergyRepColorStrategy {
//
//        public Color getColor( Atom atom ) {
//            double de = atom.getCurrState().getEnergyLevel() - atom.getGroundState().getEnergyLevel();
//            double wavelength = PhysicsUtil.energyToWavelength( de );
////            double wavelength = atom.getCurrState().getWavelength();
//            return VisibleColor.wavelengthToColor( wavelength );
//        }
//    }
//
//    /**
//     * Picks a shade of gray for the energy rep color.
//     */
//    private class GrayScaleStrategy implements EnergyRepColorStrategy {
//        private Color[] grayScale = new Color[240];
////        private Color[] grayScale = new Color[220];
//
//        GrayScaleStrategy() {
//            for( int i = 0; i < grayScale.length; i++ ) {
//                grayScale[i] = new Color( i, i, i );
//            }
//        }
//
//        public Color getColor( Atom atom ) {
//            int idx = (int)( grayScale.length * ( ( atom.getCurrState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) /
//                                                  ( atom.getHighestEnergyState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) ) );
////            int idx = (int)( grayScale.length * ( ( atom.getCurrState().getEnergyLevel() - Photon.wavelengthToEnergy( Photon.MAX_VISIBLE_WAVELENGTH ) ) /
////                                     ( Photon.wavelengthToEnergy( Photon.MIN_VISIBLE_WAVELENGTH ) - Photon.wavelengthToEnergy( ( Photon.MAX_VISIBLE_WAVELENGTH ) ) ) ) );
//            idx = Math.min( Math.max( 0, idx ), grayScale.length - 1 );
//            return grayScale[idx];
//        }
//
//    }
}
