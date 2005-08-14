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

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;
import edu.colorado.phet.lasers.view.MonitorPanel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 */
public class DischargeLampEnergyLevelMonitorPanel extends MonitorPanel implements Atom.ChangeListener,
                                                                                  Electron.ChangeListener {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static BufferedImage baseSphereImg;

    static {
        try {
            baseSphereImg = ImageLoader.loadBufferedImage( "images/particle-red-lrg.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    // Number of milliseconds between display updates. Energy level populations are averaged over this time
//    private long averagingPeriod = 300;

    // The diameter of an atom as displayed on the screen, in pixels
    private int atomDiam = 10;
    // Dimensions of the panel
    private int panelHeight = 230;
    private int minPanelWidth = 100;
    // Location and size of energy level lines
    private Point2D origin;
    private int levelLineOriginX;
    private int levelLineLength;

    // Cache of atom images of varying colors
    private Map colorToAtomImage = new HashMap();

    private EnergyLevelGraphic[] levelGraphics;

    private DischargeLampModel model;
    private ModelViewTransform1D energyYTx;
    // The offset by which all the graphic elements must be placed, caused by the heading text
    private int headingOffsetY = 20;
    private AtomicState[] atomicStates;
    private List atoms;
    private HashMap numAtomsInState = new HashMap();
    private PhetImageGraphic electronGraphic;
    private int electronXLoc;
    private int levelLineOffsetX = 20;
    private int numAtoms;
    private double atomGraphicOverlap = 0.3;

    // The minimum energy and maximum energies of the states we are representing
    private double groundStateEnergy = Double.MAX_VALUE;
    private double maxEnergy = Double.MIN_VALUE;

    // The strategy to use for picking the color of atom graphics and energy level lines
    private EnergyLevelGraphic.ColorStrategy colorStrategy = new EnergyLevelGraphic.BlackStrategy();

    /**
     *
     */
    public DischargeLampEnergyLevelMonitorPanel( BaseLaserModule module, AbstractClock clock, AtomicState[] atomicStates,
                                                 int panelWidth, int panelHeight ) {
        model = (DischargeLampModel)module.getLaserModel();

        // Determine locations and dimensions
//        this.minPanelWidth = panelWidth;
        this.panelHeight = panelHeight;
        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        this.origin = new Point( 25, panelHeight - 30 );
        this.levelLineOriginX = (int)origin.getX() + levelLineOffsetX;
        this.levelLineLength = panelWidth - levelLineOriginX - 20;
        electronXLoc = (int)origin.getX();

        this.setBackground( Color.white );

        // Create a horizontal line for each energy level, then add them to the panel
        setEnergyLevels( atomicStates );

        // Set up the counters for the number of atoms in each state
        initializeStateCounters();

        // Add listeners to all the atoms in the model
        addAtomListeners();

        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );
    }

    /**
     * Adjusts the layout of the panel
     */
    private void adjustPanel() {
        // The area in which the energy levels will be displayed
        Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + 10,
                                                     getBounds().getWidth(), getBounds().getHeight() - 30 );

        // Set the model-to-view transform so that there will be a reasonable margin below the zero point
        energyYTx = new ModelViewTransform1D( maxEnergy, groundStateEnergy,
//        energyYTx = new ModelViewTransform1D( AtomicState.maxEnergy, -AtomicState.groundStateEnergy,
                                              (int)bounds.getBounds().getMinY() + headingOffsetY, (int)bounds.getBounds().getMaxY() );
        for( int i = 0; i < levelGraphics.length; i++ ) {
            levelGraphics[i].update( energyYTx );
        }
    }

    /**
     * Adds a graphic to the panel for the specified atom, and adds the panel to the atom
     * as a state change listener. Adjusts the length of the line to be long enough to hold
     * all the atoms
     *
     * @param atom
     */
    public void addAtom( Atom atom ) {
        atom.addChangeListener( this );
        numAtoms++;
        int n = ( (Integer)numAtomsInState.get( atom.getCurrState() ) ).intValue();
        numAtomsInState.put( atom.getCurrState(), new Integer( n + 1 ) );
    }

    /**
     * @param atomicStates
     */
    public void setEnergyLevels( AtomicState[] atomicStates ) {

        // TODO: this next bit of code can be done away with now that atom.getGroundState() and
        // atom.getHighestEnergyState() are around
        // Find the minimum and maximum energy levels
        maxEnergy = Double.MIN_VALUE;
        groundStateEnergy = Double.MAX_VALUE;
        for( int i = 0; i < atomicStates.length; i++ ) {
            AtomicState atomicState = atomicStates[i];
            double energy = atomicState.getEnergyLevel();
            maxEnergy = energy > maxEnergy ? energy : maxEnergy;
            groundStateEnergy = energy < groundStateEnergy ? energy : groundStateEnergy;
        }

        // Remove any energy level graphics we might have
        // Add the energy level lines to the panel
        for( int i = 0; levelGraphics != null && i < levelGraphics.length; i++ ) {
            this.removeGraphic( levelGraphics[i] );
        }

        // Make new graphics and add them
        this.atomicStates = atomicStates;
        levelGraphics = new EnergyLevelGraphic[atomicStates.length];
        levelLineLength = (int)( ( numAtoms - 1 ) * ( atomDiam * ( 1 - atomGraphicOverlap ) ) + atomDiam * 1.5 );
        for( int i = 0; i < levelGraphics.length; i++ ) {
            levelGraphics[i] = new EnergyLevelGraphic( this, atomicStates[i],
                                                       Color.blue, levelLineOriginX,
                                                       levelLineLength,
                                                       atomicStates[i] instanceof GroundState ? false : true );
            levelGraphics[i].setArrowsEnabled( false );
            // Set the strategy the level graphic uses to pick its color
            levelGraphics[i].setColorStrategy( this.colorStrategy );
            // Add an icon to the level
            levelGraphics[i].setLevelIcon( new LevelIcon( this, i ) );
            this.addGraphic( levelGraphics[i] );
        }

        // Set the width of the panel so it can show all the atoms. 20 gives us a margin for the level icon        
        int width = Math.max( this.minPanelWidth, levelLineLength + levelLineOriginX + 35 );
//        int width = Math.max( this.minPanelWidth, levelLineLength + levelLineOriginX + 10 );
        setPreferredSize( new Dimension( width, (int)panelHeight ) );
        // Needed to set the energyYTx
        adjustPanel();
        // Set up the counters for the number of atoms in each state
        initializeStateCounters();

        revalidate();
        repaint();
    }

    private void initializeStateCounters() {
        atoms = model.getAtoms();
        numAtomsInState.clear();
        // Make the map of atomic states to the number of atoms in each state
        for( int i = 0; i < atomicStates.length; i++ ) {
            numAtomsInState.put( atomicStates[i], new Integer( 0 ) );
        }
        // Populate the map we just made, and add ourself as a listener to each atom
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            Integer num = (Integer)numAtomsInState.get( atom.getCurrState() );            
//            int n = ( (Integer)numAtomsInState.get( atom.getCurrState() ) ).intValue();
            if( num != null ) {
                numAtomsInState.put( atom.getCurrState(), new Integer( num.intValue() + 1 ) );
            }
        }
        invalidate();
        repaint();
    }

    /**
     * Adds us as a listener to all the atoms in the model, so we will know if they change state
     */
    private void addAtomListeners() {
        atoms = model.getAtoms();
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            atom.addChangeListener( this );
        }
    }

//    public void setAveragingPeriod( long value ) {
//        averagingPeriod = value;
//    }
//
//    public long getAveragingPeriod() {
//        return averagingPeriod;
//    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    /**
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw level atoms
        // todo: cache Color instances to improve performance
        for( int i = 0; i < atomicStates.length; i++ ) {
            Color c = colorStrategy.getColor( atomicStates[i] );
            if( numAtomsInState.get( atomicStates[i] ) == null ) {
                System.out.println( "DischargeLampEnergyLevelMonitorPanel.paintComponent" );
            }
            int n = ( (Integer)numAtomsInState.get( atomicStates[i] ) ).intValue();
            drawAtomsInLevel( g2, c, levelGraphics[i], n );
        }

        gs.restoreGraphics();
    }

    /**
     * Draws the atom graphics on a specified EnergyLevelGraphic
     *
     * @param g2
     * @param color
     * @param line
     * @param numInLevel
     */
    private void drawAtomsInLevel( Graphics2D g2, Color color, EnergyLevelGraphic line, int numInLevel ) {
        BufferedImage bi = getAtomImage( color );
        double scale = (double)atomDiam / bi.getWidth();
        AffineTransform atx = new AffineTransform();
        atx.translate( line.getLinePosition().getX() - atomDiam / 2,
                       line.getLinePosition().getY() - atomDiam );
        atx.scale( scale, scale );
        for( int i = 0; i < numInLevel; i++ ) {
            atx.translate( atomDiam * ( 1 - atomGraphicOverlap ) / scale, 0 );
            g2.drawRenderedImage( bi, atx );
        }
    }

    /**
     * Gets the image for an atom of a specified color
     *
     * @param color
     * @return
     */
    private BufferedImage getAtomImage( Color color ) {
        // Look for the image in the cache
        BufferedImage atomImg = (BufferedImage)colorToAtomImage.get( color );
        if( atomImg == null ) {
            atomImg = new BufferedImage( baseSphereImg.getWidth(), baseSphereImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
            op.filter( baseSphereImg, atomImg );
            colorToAtomImage.put( color, atomImg );
        }
        return atomImg;
    }

    //----------------------------------------------------------------
    // Atom.ChangeListener implementation
    //----------------------------------------------------------------

    /**
     * Keeps track of how many atoms are in each state
     *
     * @param event
     */
    public void stateChanged( Atom.ChangeEvent event ) {
        AtomicState prevState = event.getPrevState();
        AtomicState currState = event.getCurrState();

        // Adjust the number of atoms in each state
        Integer nPrev = (Integer)(Integer)numAtomsInState.get( prevState );
        if( nPrev != null ) {
            numAtomsInState.put( prevState, new Integer( nPrev.intValue() - 1 ) );
        }
        Integer nCurr = (Integer)numAtomsInState.get( currState );
        if( nCurr != null ) {
            numAtomsInState.put( currState, new Integer( nCurr.intValue() + 1 ) );
        }

        // Display a squiggle to show the transition. Remove it after a bried time
        double dE = prevState.getEnergyLevel() - currState.getEnergyLevel();
        if( dE > 0 ) {
            double wavelength = PhysicsUtil.energyToWavelength( dE );
            // We need to get the absolute value here because positive energy transforms to a negative number when
            // mapped to screen coordinates in the Y direction.
            int length = Math.abs( energyYTx.modelToView( dE ) );
            final EnergySquiggle squiggle = new EnergySquiggle( this, wavelength, 0, length, 10,
                                                                EnergySquiggle.VERTICAL );
            squiggle.setLocation( 50, energyYTx.modelToView( prevState.getEnergyLevel() ) );
            this.addGraphic( squiggle );
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep( 200 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    removeGraphic( squiggle );
                }
            };
            Thread t = new Thread( r );
            t.start();
        }
        invalidate();
        repaint();
    }

    //----------------------------------------------------------------
    // Electron-related methods
    //----------------------------------------------------------------

    /**
     * Add a representation for an electron to the panel
     *
     * @param electron
     */
    public void addElectron( Electron electron ) {
        electron.addChangeListener( this );
        electronGraphic = new PhetImageGraphic( this, DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        int yLoc = (int)energyYTx.modelToView( electron.getEnergy() );
        electronGraphic.setLocation( electronXLoc, yLoc );
        addGraphic( electronGraphic );
    }

    /**
     * Implementation of Electron.ChangeListener
     *
     * @param changeEvent
     */
    public void leftSystem( Electron.ChangeEvent changeEvent ) {
        removeGraphic( electronGraphic );
        changeEvent.getElectrion().removeListener( this );
    }

    /**
     * Implementation of Electron.ChangeListener
     *
     * @param changeEvent
     */
    public void energyChanged( Electron.ChangeEvent changeEvent ) {
        int yLoc = (int)energyYTx.modelToView( changeEvent.getElectrion().getEnergy() + groundStateEnergy );
//        int yLoc = (int)energyYTx.modelToView( changeEvent.getElectrion().getEnergy() );
        electronGraphic.setLocation( electronXLoc, yLoc );
        electronGraphic.repaint();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Set the area within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            adjustPanel();
        }
    }

    private class LevelIcon extends CompositePhetGraphic {
        public LevelIcon( Component component, int idx ) {
            super( component );

            // Make a dummy atom for which we can then make an atom graphic
            DischargeLampAtom atom = new DischargeLampAtom( model, atomicStates );
            AtomicState state = new AtomicState();
            state.setEnergyLevel( atomicStates[idx].getEnergyLevel() );
            state.setMeanLifetime( Double.MAX_VALUE );
            atom.setCurrState( state );
//            atom.setCurrState( atomicStates[idx] );
            atom.setRadius( 5 );
            DischargeLampAtomGraphic atomGraphic = new DischargeLampAtomGraphic( DischargeLampEnergyLevelMonitorPanel.this,
                                                                                 atom );
            addGraphic( atomGraphic );

//            String idxStr = idx == 0 ? "G" : Integer.toString( idx );
//            PhetGraphic text = new PhetTextGraphic( DischargeLampEnergyLevelMonitorPanel.this,
//                                 DischargeLampsConfig.defaultControlFont,
//                                 idxStr, Color.black );
//            addGraphic( text );
        }
    }
}
