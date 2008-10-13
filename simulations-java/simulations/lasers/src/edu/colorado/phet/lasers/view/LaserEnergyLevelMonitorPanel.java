/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Beam;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 */
public class LaserEnergyLevelMonitorPanel extends MonitorPanel implements SimpleObserver,
                                                                          Beam.WavelengthChangeListener,
                                                                          Beam.RateChangeListener,
                                                                          ClockListener {
    private static final double LEVEL_GRAPHIC_LEVEL = 1E3;

    // Number of milliseconds between display updates. Energy level populations are averaged over this time
    private long averagingPeriod = 0;
    //    private long averagingPeriod = 300;
    private long lastPaintTime;
    private int numUpdatesToAverage;
    // The diameter of an atom as displayed on the screen, in pixels
    private int atomDiam = 10;
    // Dimensions of the panel
    private double panelHeight = 230;
    private double panelWidth = 320;
    // Amplitude of the squiggle waves
    private int squiggleHeight = atomDiam;
    // Location and size of energy level lines
    private Point2D origin = new Point2D.Double( 25, panelHeight - 30 );
    private double levelLineOriginX = origin.getX();
    private double levelLineLength = panelWidth - levelLineOriginX - 50;

    private EnergyLevelGraphic[] levelGraphics = new EnergyLevelGraphic[3];
    private EnergyLifetimeSlider[] lifetimeSliders = new EnergyLifetimeSlider[3];
    private HashMap defaultLifetimes = new HashMap();
    private int[] numAtomsInLevel = new int[3];
    private int[] atomCntAccums = new int[3];

    // Number of energy levels to show
    private int numLevels;

    private LaserModel model;
    private double pumpBeamEnergy;
    private double seedBeamEnergy;
    private ModelViewTransform1D energyYTx;
    private BufferedImage stimSquiggle;
    private BufferedImage pumpSquiggle;
    private AffineTransform stimSquiggleTx;
    private AffineTransform pumpSquiggleTx;
    private BufferedImage baseSphereImg;
    private BaseLaserModule module;
    // The offset by which all the graphic elements must be placed, caused by the heading text
    private int headerOffsetY = 20;
    private int footerOffsetY = 10;
    private IClock clock;

    /**
     *
     */
    public LaserEnergyLevelMonitorPanel( BaseLaserModule module, IClock clock ) {

        this.module = module;
        this.clock = clock;
        model = module.getLaserModel();
        model.addObserver( this );
        clock.addClockListener( this );
        model.getPumpingBeam().addWavelengthChangeListener( this );
        model.getPumpingBeam().addRateChangeListener( this );
        model.getSeedBeam().addWavelengthChangeListener( this );
        model.getSeedBeam().addRateChangeListener( this );

        // Add a listener to the model that will adjust the panel if energy states change
        model.addLaserListener( new LaserModel.ChangeListenerAdapter() {
            public void atomicStatesChanged( LaserModel.ChangeEvent event ) {
                relayout();
            }
        } );

        // Create the graphics and controls that represent the energy levels of the atoms
        createEnergyLevelReps();

        this.setBackground( Color.white );
        JLabel dummyLabel = new JLabel( "foo" );
        Font font = dummyLabel.getFont();
        String header = null;
        if ( module instanceof MultipleAtomModule ) {
            header = LasersResources.getString( "EnergyMonitorPanel.header.plural" );
        }
        else {
            header = LasersResources.getString( "EnergyMonitorPanel.header.singular" );
        }
        PhetTextGraphic headingText = new PhetTextGraphic( this, font,
                                                           header,
                                                           Color.black );
        headingText.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                           RenderingHints.VALUE_ANTIALIAS_ON ) );
        headingText.setLocation( 30, 5 );
        this.addGraphic( headingText );

        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );
    }

    /**
     * Creates a line for each of the energy levels, with a slider to control the lifetime
     * of the level
     */
    private void createEnergyLevelReps() {

        // Clear any existing graphics and controls
        for ( int i = 0; i < levelGraphics.length; i++ ) {
            EnergyLevelGraphic levelGraphic = levelGraphics[i];
            removeGraphic( levelGraphic );
        }
        for ( int i = 0; i < lifetimeSliders.length; i++ ) {
            EnergyLifetimeSlider lifetimeSlider = lifetimeSliders[i];
            if ( lifetimeSlider != null ) {
                remove( lifetimeSlider );
            }
        }
        levelGraphics = new EnergyLevelGraphic[3];
        lifetimeSliders = new EnergyLifetimeSlider[3];

        AtomicState[] states = model.getStates();
        for ( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            double xIndent = 5;
            double xLoc = levelLineOriginX + xIndent;

            boolean isAdjustable = i == 0 ? false : true;

            EnergyLevelGraphic elg = new EnergyLevelGraphic( this,
                                                             state,
                                                             states[0].getEnergyLevel(),
                                                             xLoc,
                                                             levelLineLength - levelLineOriginX,
                                                             isAdjustable,
                                                             levelLineOriginX + levelLineLength - 25, true );
            addGraphic( elg, LEVEL_GRAPHIC_LEVEL );
            levelGraphics[i] = elg;

            // Don't add a lifetime adjustment slider for the ground state,
            if ( i > 0 ) {
                // Set the minimum lifetime to be two clock ticks, so we will always see an energy halo.
                int minLifetime = (int) clock.getSimulationTimeChange() * 2;

                // See Unfuddle #444
                int maxLifetime = i == 1 ? LaserConfig.MAXIMUM_STATE_LIFETIME : LaserConfig.MAXIMUM_STATE_LIFETIME / 4;
//                int maxLifetime = LaserConfig.MAXIMUM_STATE_LIFETIME;
                state.setMeanLifetime( maxLifetime );

                lifetimeSliders[i] = new EnergyLifetimeSlider( state, elg, maxLifetime, minLifetime, this );
//                System.out.println( "Constructed: modelvalue" + state.getMeanLifeTime() + ", slidermax=" + lifetimeSliders[i].getValue() );
//                lifetimeSliders[i].setValue( (int) Math.max( minLifetime, state.getMeanLifeTime() ) );
//                lifetimeSliders[i].setValue( maxLifetime );
                this.add( lifetimeSliders[i] );
                defaultLifetimes.put( lifetimeSliders[i], new Integer( lifetimeSliders[i].getValue() ) );

                // Add a listener that will flash the line when it matches the wavelength of
                // either of the beams
                new EnergyMatchDetector( model, state, model.getSeedBeam(), elg );
                new EnergyMatchDetector( model, state, model.getPumpingBeam(), elg );
            }
//            displayDebugInfoIntermittently( lifetimeSliders );

            // Add an icon to the level. This requires a dummy atom in the state the icon is to represent
            // Create copies of the states to assign to the dummy atom, and give them max lifetimes so they
            // don't time out and change
            Atom atom = new Atom( model, levelGraphics.length, true );
            AtomicState[] newStates = new AtomicState[states.length];
            for ( int j = 0; j < states.length; j++ ) {
                newStates[j] = new AtomicState( states[j] );
                newStates[j].setMeanLifetime( Double.MAX_VALUE );
            }
            atom.setStates( newStates );
            atom.setCurrState( newStates[i] );
            levelGraphics[i].setLevelIcon( new edu.colorado.phet.lasers.view.LevelIcon( this, atom ) );
        }
        relayout();
    }

    private void displayDebugInfoIntermittently( final EnergyLifetimeSlider[] slider ) {
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    System.out.println( "###" );
                    for ( int i = 0; i < slider.length; i++ ) {
                        if ( slider[i] != null ) {
                            System.out.println( "i=" + i + ", slider.getMin=" + slider[i].getMinimum() + ", max=" + slider[i].getMaximum() + ", value=" + slider[i].getValue() + ", slider.modelvalue=" + slider[i].getModelValue() );
                        }
                    }

                    try {
                        Thread.sleep( 1000 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    /**
     * Adjusts the layout of the panel
     */
    public void relayout() {
        // The beamArea in which the energy levels will be displayed
        Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + 10,
                                                     getBounds().getWidth(), getBounds().getHeight() - 30 );
        double groundStateEnergy = model.getGroundState().getEnergyLevel();
        energyYTx = new ModelViewTransform1D( groundStateEnergy + PhysicsUtil.wavelengthToEnergy( VisibleColor.MIN_WAVELENGTH ),
                                              groundStateEnergy,
                                              (int) bounds.getBounds().getMinY() + headerOffsetY,
                                              (int) bounds.getBounds().getMaxY() - footerOffsetY );
        for ( int i = 0; i < levelGraphics.length; i++ ) {
            if ( levelGraphics[i] != null ) {
                levelGraphics[i].setTransform( energyYTx );
            }
        }
        updateSquiggles();
    }

    /**
     * Sets the number of energy levels shown on the panel.
     *
     * @param numLevels Valid values are 2 and 3.
     */
    public void setNumLevels( int numLevels ) {
        this.numLevels = numLevels;
        createEnergyLevelReps();
        setPreferredSize( new Dimension( (int) panelWidth, (int) panelHeight ) );
        revalidate();
        repaint();
    }

    /**
     * Resets the lifetime sliders to their default values
     */
    public void reset() {
        Set sliders = defaultLifetimes.keySet();
        for ( Iterator iterator = sliders.iterator(); iterator.hasNext(); ) {
            EnergyLifetimeSlider slider = (EnergyLifetimeSlider) iterator.next();
            slider.setValue( ( (Integer) defaultLifetimes.get( slider ) ).intValue() );
        }
    }

    /**
     * Handles updates from the model
     */
    public void update() {
        atomCntAccums[0] += model.getNumGroundStateAtoms();
        atomCntAccums[1] += model.getNumMiddleStateAtoms();
        atomCntAccums[2] += model.getNumHighStateAtoms();

        // todo: these two line might be able to go somewhere they aren't called as often
        for ( int i = 1; i < numLevels; i++ ) {
            if ( lifetimeSliders[i] != null ) {
                lifetimeSliders[i].update();
            }
        }

        numUpdatesToAverage++;
        long currTime = System.currentTimeMillis();
        if ( currTime - lastPaintTime >= averagingPeriod ) {
            // Compute the average number of atoms in each state. Take care to round off rather than truncate.
            numAtomsInLevel[0] = (int) ( 0.5 + (double) atomCntAccums[0] / numUpdatesToAverage );
            numAtomsInLevel[1] = (int) ( 0.5 + (double) atomCntAccums[1] / numUpdatesToAverage );
            numAtomsInLevel[2] = (int) ( 0.5 + (double) atomCntAccums[2] / numUpdatesToAverage );
            atomCntAccums[0] = 0;
            atomCntAccums[1] = 0;
            atomCntAccums[2] = 0;
            numUpdatesToAverage = 0;
            lastPaintTime = currTime;
            this.invalidate();
            this.repaint();
        }
    }

    /**
     * Recomputes the squiggle images for both beams
     */
    private void updateSquiggles() {
        double groundStateEnergy = model.getGroundState().getEnergyLevel();
        double y0 = energyYTx.modelToView( groundStateEnergy );
        double y1 = energyYTx.modelToView( groundStateEnergy + seedBeamEnergy );
        double y2 = energyYTx.modelToView( groundStateEnergy + pumpBeamEnergy );

        // Build the images for the squiggles that represent the energies of the stimulating and pumping beam
        if ( y0 > y1 ) {
            double squiggleOffsetX = squiggleHeight;
            stimSquiggle = computeSquiggleImage( model.getSeedBeam(), 0, (int) ( y0 - y1 ), squiggleHeight );
            stimSquiggleTx = AffineTransform.getTranslateInstance( levelGraphics[1].getPosition().getX() + squiggleOffsetX,
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            stimSquiggleTx.rotate( -Math.PI / 2 );
        }

        if ( y0 > y2 ) {
            pumpSquiggle = computeSquiggleImage( model.getPumpingBeam(), 0, (int) ( y0 - y2 ), squiggleHeight );

            // Which level graphic we use to set the x location of the pump beam's squiggle depends on how many
            // levels are being displayed.
            int idx = numLevels > 2 ? 2 : 1;
            pumpSquiggleTx = AffineTransform.getTranslateInstance( levelGraphics[idx].getPosition().getX(),
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            pumpSquiggleTx.rotate( -Math.PI / 2 );
        }

        // Force a repaint
        this.invalidate();
        this.repaint();
    }

    /*
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( Beam beam, double phaseAngle, int length, int height ) {
        double wavelength = beam.getWavelength();
        int arrowHeight = height;

        // So that the tip of the arrow will just touch an energy level line when it is supposed to match the line,
        // we need to subtract 1 from the length of the squiggle
        int actualLength = length - 1;

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( actualLength + 2 * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2d = img.createGraphics();
        int kPrev = height / 2;
        int iPrev = 0;
        Color c = VisibleColor.wavelengthToColor( wavelength );
        double freqFactor = 15 * wavelength / 680;
        for ( int i = 0; i < actualLength - arrowHeight * 2; i++ ) {
            int k = (int) ( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * height / 2 + height / 2 );
            for ( int j = 0; j < height; j++ ) {
                if ( j == k ) {
                    g2d.setColor( c );
                    g2d.drawLine( iPrev + arrowHeight, kPrev, i + arrowHeight, k );
                    iPrev = i;
                    kPrev = k;
                }
            }
        }
        Arrow head = new Arrow( new Point2D.Double( arrowHeight, height / 2 ),
                                new Point2D.Double( 0, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        Arrow tail = new Arrow( new Point2D.Double( actualLength - arrowHeight, height / 2 ),
                                new Point2D.Double( actualLength, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( head.getShape() );
        g2d.fill( tail.getShape() );
        g2d.dispose();
        return img;
    }

    public void setAveragingPeriod( long value ) {
        averagingPeriod = value;
    }

    public long getAveragingPeriod() {
        return averagingPeriod;
    }

    //----------------------------------------------------------------
    // Rendering
    //---
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D) graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw the atoms on each of the levels
        for ( int i = 0; i < numLevels; i++ ) {
            EnergyLevelGraphic levelGraphic = levelGraphics[i];
            drawAtomsInLevel( g2, Color.darkGray, levelGraphic, numAtomsInLevel[i] );
        }

        // Draw squiggles showing what energy photons the beams are putting out
        if ( stimSquiggle != null && model.getSeedBeam().isEnabled() ) {
            double intensity = model.getSeedBeam().getPhotonsPerSecond() / model.getSeedBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, Math.pow( intensity, 0.5 ) );
            g2.drawRenderedImage( stimSquiggle, stimSquiggleTx );
        }
        if ( pumpSquiggle != null && model.getPumpingBeam().isEnabled() ) {
            double intensity = model.getPumpingBeam().getPhotonsPerSecond() / model.getPumpingBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, Math.sqrt( intensity ) );
            g2.drawRenderedImage( pumpSquiggle, pumpSquiggleTx );
        }

        gs.restoreGraphics();
    }

    private void drawAtomsInLevel( Graphics2D g2, Color color, EnergyLevelGraphic line, int numInLevel ) {
        BufferedImage bi = getAtomImage( color );
        double scale = (double) atomDiam / bi.getWidth();
        AffineTransform atx = new AffineTransform();
        double offsetX = squiggleHeight * 2;
        atx.translate( line.getLinePosition().getX() + offsetX - atomDiam / 2,
                       line.getLinePosition().getY() - atomDiam );
//        atx.translate( line.getLinePosition().getX() - atomDiam / 2,
//                       line.getLinePosition().getY() - atomDiam );
        atx.scale( scale, scale );
        for ( int i = 0; i < numInLevel; i++ ) {
            atx.translate( atomDiam * 0.7 / scale, 0 );
            g2.drawRenderedImage( bi, atx );
        }
    }

    private Map colorToAtomImage = new HashMap();

    private BufferedImage getAtomImage( Color color ) {
        if ( baseSphereImg == null ) {
            try {
                baseSphereImg = ImageLoader.loadBufferedImage( "lasers/images/particle-red-lrg.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        // Look for the image in the cache
        BufferedImage atomImg = (BufferedImage) colorToAtomImage.get( color );
        if ( atomImg == null ) {
            atomImg = new BufferedImage( baseSphereImg.getWidth(), baseSphereImg.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
            op.filter( baseSphereImg, atomImg );
            colorToAtomImage.put( color, atomImg );
        }
        return atomImg;
    }

    //----------------------------------------------------------------
    // LeftSystemEvent handlers
    //----------------------------------------------------------------

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        Beam beam = (Beam) event.getSource();
        if ( beam == model.getPumpingBeam() ) {
            double pumpBeamWavelength = beam.getWavelength();
            pumpBeamEnergy = PhysicsUtil.wavelengthToEnergy( pumpBeamWavelength );
        }
        if ( beam == model.getSeedBeam() ) {
            double seedBeamWavelength = beam.getWavelength();
            seedBeamEnergy = PhysicsUtil.wavelengthToEnergy( seedBeamWavelength );
        }
        updateSquiggles();
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        updateSquiggles();
    }

    //----------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------


    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }

    public void clockTicked( ClockEvent clockEvent ) {

    }

    public void clockStarted( ClockEvent clockEvent ) {

    }

    /**
     * If the clock pauses, force the update and repaint of energy level populations. We need to do this because
     * when the clock is running, the populations shown are averages over time, and if the clock is paused, we
     * want the populations shown to agree with the actual number of atoms in each state.
     *
     * @param clockEvent
     */
    public void clockPaused( ClockEvent clockEvent ) {
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();
        this.invalidate();
        this.repaint();
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {

    }

    public void simulationTimeReset( ClockEvent clockEvent ) {

    }
    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Set the beamArea within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            relayout();
        }
    }

}
