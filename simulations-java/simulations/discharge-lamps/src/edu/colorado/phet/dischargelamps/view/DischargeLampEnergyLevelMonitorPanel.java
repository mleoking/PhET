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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.GroundState;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.model.ConfigurableElementProperties;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.HydrogenProperties;
import edu.colorado.phet.dischargelamps.quantum.model.Electron;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;
import edu.colorado.phet.lasers.view.MonitorPanel;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 * <p/>
 * This class is completely wrapped by DischargeLampEnergyLevelMonitorPanel2 and should be folded into it. No other
 * classes instantiate this one.
 */
public class DischargeLampEnergyLevelMonitorPanel extends MonitorPanel implements Atom.ChangeListener {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static BufferedImage baseSphereImg;

    static {
        try {
            baseSphereImg = ImageLoader.loadBufferedImage( "lasers/images/particle-red-lrg.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

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
    private int footerOffsetY = 20;
    private AtomicState[] atomicStates;
    private List atoms;
    private HashMap numAtomsInState = new HashMap();
    private int electronXLoc;
    private int levelLineOffsetX = 20;
    private int numAtoms;
    private double atomGraphicOverlap = 0.3;
    // Minimum space between energy levels (in pixels)
    private int minEnergyLevelSpacing = 3;

    // The minimum energy and maximum energies of the states we are representing
    private double groundStateEnergy = Double.MAX_VALUE;
    private double maxEnergy = -Double.MAX_VALUE;

    // The strategy to use for picking the color of atom graphics and energy level lines
    private EnergyLevelGraphic.ColorStrategy colorStrategy = new EnergyLevelGraphic.BlackStrategy();

    private boolean squigglesEnabled;
    private PhetTextGraphic2 groundStateTextGraphic;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------


    /**
     * @param model
     * @param atomicStates
     * @param panelWidth
     * @param panelHeight
     */
    public DischargeLampEnergyLevelMonitorPanel( DischargeLampModel model, AtomicState[] atomicStates,
                                                 int panelWidth, int panelHeight ) {
        this.model = model;

        // Add a listener that will catch changes in the energy levels
        model.addChangeListener( new DischargeLampModel.ChangeListenerAdapter() {
            public void energyLevelsChanged( DischargeLampModel.ChangeEvent event ) {
                setEnergyLevels( event.getDischargeLampModel().getAtomicStates() );
                setEnergyLevelsMovable( event.getDischargeLampModel().getElementProperties().isLevelsMovable() );
            }
        } );

        // Determine locations and dimensions
        this.origin = new Point( 25, panelHeight - 30 );
        this.levelLineOriginX = (int) origin.getX() + levelLineOffsetX;
        this.levelLineLength = panelWidth - levelLineOriginX - 20;
        electronXLoc = (int) origin.getX();

        setPanelSize( panelWidth, panelHeight, atomicStates );

        this.setBackground( Color.white );

        // Create a horizontal line for each energy level, then add them to the panel
        setEnergyLevels( atomicStates );
        setEnergyLevelsMovable( model.getElementProperties().isLevelsMovable() );

        // Add listeners to all the atoms in the model
        addAtomListeners();

        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );
        relayout();
    }

    /**
     * Computes the height of the panel normalized to a specified height for the range of energies
     * needed to represent hydrogen. If the current element properties are configurable, then
     * set the size as if the element were hydrogen
     *
     * @param panelWidth
     * @param panelHeight
     * @param atomicStates
     */
    private void setPanelSize( int panelWidth, int panelHeight, AtomicState[] atomicStates ) {
        this.panelHeight = panelHeight;
        this.minPanelWidth = panelWidth;
        HydrogenProperties hProps = new HydrogenProperties();
        double hPropsEnergyRange = hProps.getEnergyLevels()[hProps.getEnergyLevels().length - 1] - hProps.getEnergyLevels()[0];
        double atomicStatesEnergyRange = atomicStates[atomicStates.length - 1].getEnergyLevel() - atomicStates[0].getEnergyLevel();
        double scaleY = 0;
        if ( model.getElementProperties() instanceof ConfigurableElementProperties ) {
            scaleY = 1;
        }
        else {
            scaleY = atomicStatesEnergyRange / hPropsEnergyRange;
        }
        setPreferredSize( new Dimension( (int) panelWidth, (int) ( panelHeight * scaleY ) ) );
    }

    /**
     * Clears the map that maps states to the number of atoms in each, and repopulates it.
     */
    private void initializeStateCounters() {
        atoms = model.getAtoms();
        numAtomsInState.clear();
        // Make the map of atomic states to the number of atoms in each state
        for ( int i = 0; i < atomicStates.length; i++ ) {
            numAtomsInState.put( atomicStates[i], new Integer( 0 ) );
        }
        // Populate the map we just made
        for ( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom) atoms.get( i );
            Integer num = (Integer) numAtomsInState.get( atom.getCurrState() );
            if ( num != null ) {
                numAtomsInState.put( atom.getCurrState(), new Integer( num.intValue() + 1 ) );
            }
        }
        invalidate();
        repaint();
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
        int n = ( (Integer) numAtomsInState.get( atom.getCurrState() ) ).intValue();
        numAtomsInState.put( atom.getCurrState(), new Integer( n + 1 ) );
    }

    /**
     * @param squigglesEnabled
     */
    public void setSquigglesEnabled( boolean squigglesEnabled ) {
        this.squigglesEnabled = squigglesEnabled;
    }

    /**
     * @param movable
     */
    public void setEnergyLevelsMovable( boolean movable ) {
        for ( int i = 0; i < levelGraphics.length; i++ ) {
            EnergyLevelGraphic levelGraphic = levelGraphics[i];
            levelGraphic.setIgnoreMouse( !movable );
        }
    }

    /**
     * @param atomicStates
     */
    public void setEnergyLevels( AtomicState[] atomicStates ) {
        //reset the groundStateEnergy so that computation below is correct (instead of taking min over previous value)
//        groundStateEnergy=Double.MAX_VALUE;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        // Find the minimum and maximum energy levels
        for ( int i = 0; i < atomicStates.length; i++ ) {
            double energy = atomicStates[i].getEnergyLevel();
//            groundStateEnergy = energy < groundStateEnergy ? energy : groundStateEnergy;
            min = Math.min( min, energy );
            max = Math.max( min, energy );
        }
        groundStateEnergy = min;
        maxEnergy = max;
        //see Unfuddle #575
        //https://phet.unfuddle.com/projects/9404/tickets/by_number/575?cycle=true

        // Max energy is 0, in all cases (requested by Sam M., 10/24/06)
//        maxEnergy = DischargeLampsConfig.MAX_ENERGY_LEVEL;

        // Remove any energy level graphics we might have
        // Add the energy level lines to the panel
        for ( int i = 0; levelGraphics != null && i < levelGraphics.length; i++ ) {
            this.removeGraphic( levelGraphics[i] );
        }

        // Make new graphics and add them
        this.atomicStates = atomicStates;
        levelGraphics = new EnergyLevelGraphic[atomicStates.length];
        levelLineLength = (int) ( ( numAtoms - 1 ) * ( atomDiam * ( 1 - atomGraphicOverlap ) ) + atomDiam * 1.5 );
        for ( int i = 0; i < levelGraphics.length; i++ ) {
            levelGraphics[i] = new EnergyLevelGraphic( this, atomicStates[i],
                                                       atomicStates[0].getEnergyLevel(),
                                                       levelLineOriginX,
                                                       levelLineLength,
                                                       //SRR changed this on 3-7-2008
                                                       //changed so that isAdjustable=false to hide lifetime text (may have unintended consequences) 
                                                       atomicStates[i] instanceof GroundState ? false : true,
//                                                       false,
levelLineOriginX + levelLineLength + 20, false );
            levelGraphics[i].setArrowsEnabled( false );
            // Set the strategy the level graphic uses to pick its color
            levelGraphics[i].setColorStrategy( this.colorStrategy );

            // Add an icon to the level. This requires a dummy atom in the state the icon is to represent
            // Create copies of the states to assign to the dummy atom, and give them max lifetimes so they
            // don't time out and change
            Atom atom = new Atom( model, levelGraphics.length, true );
            AtomicState[] newStates = new AtomicState[atomicStates.length];
            for ( int j = 0; j < atomicStates.length; j++ ) {
                newStates[j] = new AtomicState( atomicStates[j] );
                newStates[j].setMeanLifetime( Double.MAX_VALUE );
            }
            atom.setStates( newStates );
            atom.setCurrState( newStates[i] );
            levelGraphics[i].setLevelIcon( new edu.colorado.phet.lasers.view.LevelIcon( this, atom ) );

            // Set the minimum distance this graphic must have between it and the ones next to it
            levelGraphics[i].setMinPixelsBetweenLevels( minEnergyLevelSpacing );
            this.addGraphic( levelGraphics[i] );

            // Add a listener that will reinitialize the map of states to the number of atoms in each.
            // We need this because the state instances themselves may change when a level changes
            levelGraphics[i].addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    initializeStateCounters();
                }
            } );

        }

        // Set the width of the panel so it can show all the atoms. 35 gives us a margin for the level icon
        int width = Math.max( this.minPanelWidth, levelLineLength + levelLineOriginX + 35 );
        setPanelSize( width, panelHeight, atomicStates );

        // Needed to set the energyYTx
        relayout();

        // Set up the counters for the number of atoms in each state
        initializeStateCounters();

        revalidate();
        repaint();
    }

    /**
     * Adds us as a listener to all the atoms in the model, so we will know if they change state
     */
    private void addAtomListeners() {
        atoms = model.getAtoms();
        for ( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom) atoms.get( i );
            atom.addChangeListener( this );
        }
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    /**
     * Adjusts the ModelViewTranform1D that maps energies to pixels, and updates all the level graphics
     * that use it
     * //Todo: remove consolidate with duplicated code from LaserEnergyLevelMonitorPanel
     */
    private void relayout() {
        // The area in which the energy levels will be displayed
        Rectangle2D bounds = new Rectangle2D.Double( 0, 0, getPreferredSize().getWidth(), getPreferredSize().getHeight() );

        // Set the model-to-view transform so that there will be a reasonable margin below the zero point
        energyYTx = new ModelViewTransform1D( maxEnergy, groundStateEnergy,
                                              (int) bounds.getBounds().getMinY() + headingOffsetY,
                                              (int) bounds.getBounds().getMaxY() - footerOffsetY );
//        System.out.println( "energyYTx = " + energyYTx );
        for ( int i = 0; levelGraphics != null && i < levelGraphics.length; i++ ) {
            levelGraphics[i].setTransform( energyYTx );
            if ( i == 0 && groundStateTextGraphic != null ) {
                int y = energyYTx.modelToView( atomicStates[0].getEnergyLevel() );
                groundStateTextGraphic.setLocation( (int) groundStateTextGraphic.getLocation().getX(),
                                                    y - groundStateTextGraphic.getHeight() / 2 );
            }
        }

        changeListenerProxy.energyTxChanged( energyYTx );
    }

    /**
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D) graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw level atoms
        // todo: cache Color instances to improve performance
        for ( int i = 0; i < atomicStates.length; i++ ) {
            Color c = colorStrategy.getColor( atomicStates[i], atomicStates[0].getEnergyLevel() );
            Integer numAtoms = (Integer) numAtomsInState.get( atomicStates[i] );
            if ( numAtoms != null ) {
                int n = numAtoms.intValue();

                drawAtomsInLevel( g2, c, levelGraphics[i], n );
            }
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
        double scale = (double) atomDiam / bi.getWidth();
        AffineTransform atx = new AffineTransform();
        atx.translate( line.getLinePosition().getX() - atomDiam / 2,
                       line.getLinePosition().getY() - atomDiam );
        atx.scale( scale, scale );
        for ( int i = 0; i < numInLevel; i++ ) {
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
        BufferedImage atomImg = (BufferedImage) colorToAtomImage.get( color );
        if ( atomImg == null ) {
            atomImg = new BufferedImage( baseSphereImg.getWidth(),
                                         baseSphereImg.getHeight(),
                                         BufferedImage.TYPE_INT_ARGB_PRE );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
            op.filter( baseSphereImg, atomImg );
            colorToAtomImage.put( color, atomImg );
        }
        return atomImg;
    }

    /**
     * Returns the tranform that converts energy levels to y coordinates in the panel
     *
     * @return
     */
    public ModelViewTransform1D getEnergyYTx() {
        return energyYTx;
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
        Integer nPrev = (Integer) (Integer) numAtomsInState.get( prevState );
        if ( nPrev != null ) {
            numAtomsInState.put( prevState, new Integer( nPrev.intValue() - 1 ) );
        }
        Integer nCurr = (Integer) numAtomsInState.get( currState );
        if ( nCurr != null ) {
            numAtomsInState.put( currState, new Integer( nCurr.intValue() + 1 ) );
        }

        // Display a squiggle to show the transition. Remove it after a brief time
        if ( squigglesEnabled ) {
            displaySquiggle( prevState, currState );
        }
        invalidate();
        repaint();
    }

    /**
     * Creates a squiggle on the panel, and remove it after a brief time. The time during which the
     * squiggle remains on the screen is defined in DischargeLampsConfig
     *
     * @param prevState
     * @param currState
     */
    private void displaySquiggle( AtomicState prevState, AtomicState currState ) {
        double dE = prevState.getEnergyLevel() - currState.getEnergyLevel();
        if ( dE > 0 ) {
            double wavelength = PhysicsUtil.energyToWavelength( dE );
            // We need to get the absolute value here because positive energy transforms to a negative number when
            // mapped to screen coordinates in the Y direction.
            int length = Math.abs( energyYTx.modelToView( prevState.getEnergyLevel() )
                                   - energyYTx.modelToView( currState.getEnergyLevel() ) );
            final EnergySquiggle squiggle = new EnergySquiggle( this, wavelength, 0, length, 10,
                                                                EnergySquiggle.VERTICAL );
            squiggle.setLocation( 50, energyYTx.modelToView( prevState.getEnergyLevel() ) );
            this.addGraphic( squiggle );
            squiggle.setBoundsDirty();
            squiggle.repaint();

            final IClock clock = PhetUtilities.getActiveClock();
            clock.addClockListener( new ClockAdapter() {
                double t0 = clock.getSimulationTime();
                double timeout = DischargeLampsConfig.ENERGY_SQUIGGLE_PERSISTENCE;

                public void clockTicked( ClockEvent clockEvent ) {
                    double t1 = clock.getSimulationTime();
                    if ( t1 - t0 > timeout ) {
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                removeGraphic( squiggle );
                            }
                        } );
                        clock.removeClockListener( this );
                    }
                }
            } );
        }
    }

    public void positionChanged( Atom.ChangeEvent event ) {
        // noop
    }

    //----------------------------------------------------------------
    // Electron-related methods
    //----------------------------------------------------------------

    /**
     * Add a representation for an electron to the panel. We create a
     * manager object that will create the graphic and add it to the
     * panel. It will take care of removing it from the panel when the
     * time comes.
     *
     * @param electron
     */
    public void addElectron( Electron electron ) {
        new ElectronGraphicManager( electron );
    }

    public void addGroundStateLabel( final PhetTextGraphic2 groundStateTextGraphic, int layer ) {
        this.groundStateTextGraphic = groundStateTextGraphic;
        addGraphic( groundStateTextGraphic, layer );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Set the area within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            relayout();
        }
    }

    /**
     * Creates an electron graphic for an electron, and removes it if the electron leaves the system
     */
    private class ElectronGraphicManager implements Electron.ChangeListener {
        PhetGraphic electronGraphic;

        public ElectronGraphicManager( Electron electron ) {
            electronGraphic = new PhetImageGraphic( DischargeLampEnergyLevelMonitorPanel.this,
                    DischargeLampsResources.getImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME ) );
            // The -10 is a hack to get the electron to be even with everything else on the panel. I can't figure
            // out why I need it
            int yLoc = (int) energyYTx.modelToView( electron.getEnergy() + groundStateEnergy );
//            int yLoc = (int)energyYTx.modelToView( electron.getEnergy() + groundStateEnergy ) - 10;
            electronGraphic.setLocation( electronXLoc, yLoc );
            addGraphic( electronGraphic );

            electron.addChangeListener( this );
        }

        public void leftSystem( Electron.ChangeEvent changeEvent ) {
            removeGraphic( electronGraphic );
        }

        public void energyChanged( Electron.ChangeEvent changeEvent ) {
            // The -10 is a hack to get the electron to be even with everything else on the panel. I can't figure
            // out why I need it
            int yLoc = energyYTx.modelToView( changeEvent.getElectron().getEnergy() + groundStateEnergy ) - 10;
            electronGraphic.setLocation( electronXLoc, yLoc );
            electronGraphic.setBoundsDirty();
            electronGraphic.repaint();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface ChangeListener extends EventListener {
        void energyTxChanged( ModelViewTransform1D energyTx );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
