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

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 */
public class LaserEnergyLevelMonitorPanel extends MonitorPanel implements SimpleObserver,
                                                                          CollimatedBeam.WavelengthChangeListener,
                                                                          CollimatedBeam.RateChangeListener,
                                                                          ClockStateListener {

    // Number of milliseconds between display updates. Energy level populations are averaged over this time
    private long averagingPeriod = 300;
    private long lastPaintTime;
    private int numUpdatesToAverage;
    private int numGroundLevelAccum = 0;
    private int numMiddleLevelAccum = 0;
    private int numHighLevelAccum = 0;

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

    private EnergyLevelGraphic highLevelLine;
    private EnergyLevelGraphic middleLevelLine;
    private EnergyLevelGraphic groundLevelLine;

    private EnergyLifetimeSlider highLevelLifetimeSlider;
    private EnergyLifetimeSlider middleLevelLifetimeSlider;

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
    private double seedBeamWavelength;
    private double pumpBeamWavelength;
    private BufferedImage baseSphereImg;
    private BaseLaserModule module;
    // The offset by which all the graphic elements must be placed, caused by the heading text
    private int headingOffsetY = 20;

    /**
     *
     */
    public LaserEnergyLevelMonitorPanel( BaseLaserModule module, AbstractClock clock ) {

        this.module = module;
        model = module.getLaserModel();
        model.addObserver( this );
        clock.addClockStateListener( this );
        model.getPumpingBeam().addWavelengthChangeListener( this );
        model.getPumpingBeam().addRateChangeListener( this );
        model.getSeedBeam().addWavelengthChangeListener( this );
        model.getSeedBeam().addRateChangeListener( this );

        // Create a horizontal line for each energy level, then add them to the panel
        highLevelLine = new EnergyLevelGraphic( this, module.getLaserModel().getHighEnergyState(),
                                                Color.blue, levelLineOriginX,
                                                levelLineLength - levelLineOriginX,
                                                true );
        middleLevelLine = new EnergyLevelGraphic( this, module.getLaserModel().getMiddleEnergyState(),
                                                  Color.red, levelLineOriginX + squiggleHeight * 1.5,
                                                  levelLineLength - ( levelLineOriginX + squiggleHeight * 1.5 ),
                                                  true );
        groundLevelLine = new EnergyLevelGraphic( this, module.getLaserModel().getGroundState(),
                                                  Color.black, levelLineOriginX + squiggleHeight * 3,
                                                  levelLineLength - ( levelLineOriginX + squiggleHeight * 3 ),
                                                  false );
        this.setBackground( Color.white );
        JLabel dummyLabel = new JLabel( "foo" );
        Font font = dummyLabel.getFont();
        String header = null;
        if( module instanceof MultipleAtomModule ) {
            header = SimStrings.get( "EnergyMonitorPanel.header.plural" );
        }
        else {
            header = SimStrings.get( "EnergyMonitorPanel.header.singular" );
        }
        PhetTextGraphic headingText = new PhetTextGraphic( this, font,
                                                           header,
                                                           Color.black );
        headingText.setLocation( 30, 5 );
        this.addGraphic( headingText );
        this.addGraphic( highLevelLine );
        this.addGraphic( middleLevelLine );
        this.addGraphic( groundLevelLine );

        // Add lifetime sliders and a title for them
        middleLevelLifetimeSlider = new EnergyLifetimeSlider( module.getLaserModel().getMiddleEnergyState(),
                                                              middleLevelLine,
                                                              LaserConfig.MIDDLE_ENERGY_STATE_MAX_LIFETIME,
                                                              this );
        this.add( middleLevelLifetimeSlider );
        highLevelLifetimeSlider = new EnergyLifetimeSlider( module.getLaserModel().getHighEnergyState(),
                                                            highLevelLine,
                                                            LaserConfig.HIGH_ENERGY_STATE_MAX_LIFETIME, this );
        this.add( highLevelLifetimeSlider );

        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );
        new EnergyMatchDetector( module.getLaserModel().getHighEnergyState(), model.getPumpingBeam(), highLevelLine );
        new EnergyMatchDetector( module.getLaserModel().getMiddleEnergyState(), model.getSeedBeam(), middleLevelLine );

        adjustPanel();
    }

    /**
     * Adjusts the layout of the panel
     */
    private void adjustPanel() {
        // The beamArea in which the energy levels will be displayed
        Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + 10,
                                                     getBounds().getWidth(), getBounds().getHeight() - 30 );
        energyYTx = new ModelViewTransform1D( AtomicState.maxEnergy, AtomicState.minEnergy,
                                              (int)bounds.getBounds().getMinY() + headingOffsetY, (int)bounds.getBounds().getMaxY() );
        highLevelLine.update( energyYTx );
        middleLevelLine.update( energyYTx );
        groundLevelLine.update( energyYTx );
        updateSquiggles();
    }

    /**
     * Sets the number of energy levels shown on the panel.
     *
     * @param numLevels Valid values are 2 and 3.
     */
    public void setNumLevels( int numLevels ) {
        this.numLevels = numLevels;
        switch( numLevels ) {
            case 2:
                highLevelLine.setVisible( false );
                highLevelLifetimeSlider.setVisible( false );
                break;
            case 3:
                highLevelLine.setVisible( true );
                highLevelLifetimeSlider.setVisible( true );
                break;
            default:
                throw new RuntimeException( "Number of levels out of range" );
        }

        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        revalidate();
        repaint();
    }

    /**
     * Handles updates from the model
     */
    public void update() {
        numGroundLevelAccum += model.getNumGroundStateAtoms();
        numMiddleLevelAccum += model.getNumMiddleStateAtoms();
        numHighLevelAccum += model.getNumHighStateAtoms();

        // todo: these two line might be able to go somewhere they aren't called as often
        middleLevelLifetimeSlider.update();
        highLevelLifetimeSlider.update();

        numUpdatesToAverage++;
        long currTime = System.currentTimeMillis();
        if( currTime - lastPaintTime >= averagingPeriod ) {
            // Compute the average number of atoms in each state. Take care to round off rather than truncate.
            numGroundLevel = (int)( 0.5 + (double)numGroundLevelAccum / numUpdatesToAverage );
            numMiddleLevel = (int)( 0.5 + (double)numMiddleLevelAccum / numUpdatesToAverage );
            numHighLevel = (int)( 0.5 + (double)numHighLevelAccum / numUpdatesToAverage );
            numGroundLevelAccum = 0;
            numMiddleLevelAccum = 0;
            numHighLevelAccum = 0;
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
        double y0 = energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() );
        double y1 = energyYTx.modelToView( seedBeamEnergy );
        double y2 = energyYTx.modelToView( pumpBeamEnergy );

        // Build the images for the squiggles that represent the energies of the stimulating and pumping beam
        if( y0 > y1 ) {
            stimSquiggle = computeSquiggleImage( model.getSeedBeam(), 0, (int)( y0 - y1 ), squiggleHeight );
            stimSquiggleTx = AffineTransform.getTranslateInstance( middleLevelLine.getPosition().getX(),
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            stimSquiggleTx.rotate( -Math.PI / 2 );
        }

        if( y0 > y2 ) {
            pumpSquiggle = computeSquiggleImage( model.getPumpingBeam(), 0, (int)( y0 - y2 ), squiggleHeight );
            pumpSquiggleTx = AffineTransform.getTranslateInstance( highLevelLine.getPosition().getX(),
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            pumpSquiggleTx.rotate( -Math.PI / 2 );
        }

        // Force a repaint
        this.invalidate();
        this.repaint();
    }

    /**
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( CollimatedBeam beam, double phaseAngle, int length, int height ) {
        double wavelength = beam.getWavelength();
        int arrowHeight = height;

        // So that the tip of the arrow will just touch an energy level line when it is supposed to match the line,
        // we need to subtract 1 from the length of the squiggle
        int actualLength = length - 1;

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( actualLength + 2 * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = img.createGraphics();
        int kPrev = height / 2;
        int iPrev = 0;
        Color c = VisibleColor.wavelengthToColor( wavelength );
        double freqFactor = 15 * wavelength / 680;
        for( int i = 0; i < actualLength - arrowHeight * 2; i++ ) {
            int k = (int)( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * height / 2 + height / 2 );
            for( int j = 0; j < height; j++ ) {
                if( j == k ) {
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
    //----------------------------------------------------------------

    /**
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw ground level atoms
        drawAtomsInLevel( g2, Color.darkGray, groundLevelLine, numGroundLevel );

        // Draw middle level atoms
        if( numLevels >= 2 ) {
            Color c = VisibleColor.wavelengthToColor( module.getLaserModel().getMiddleEnergyState().getWavelength() );
            drawAtomsInLevel( g2, c, middleLevelLine, numMiddleLevel );
        }

        // Draw high level atoms, if the level is enabled
        if( numLevels >= 3 ) {
            Color c = VisibleColor.wavelengthToColor( module.getLaserModel().getHighEnergyState().getWavelength() );
            drawAtomsInLevel( g2, c, highLevelLine, numHighLevel );
        }

        // Draw squiggles showing what energy photons the beams are putting out
        if( stimSquiggle != null && model.getSeedBeam().isEnabled() ) {
            double intensity = model.getSeedBeam().getPhotonsPerSecond() / model.getSeedBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, intensity );
            g2.drawRenderedImage( stimSquiggle, stimSquiggleTx );
        }
        if( pumpSquiggle != null && model.getPumpingBeam().isEnabled() ) {
            double intensity = model.getPumpingBeam().getPhotonsPerSecond() / model.getPumpingBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, intensity );
            g2.drawRenderedImage( pumpSquiggle, pumpSquiggleTx );
        }

        gs.restoreGraphics();
    }

    private void drawAtomsInLevel( Graphics2D g2, Color color, EnergyLevelGraphic line, int numInLevel ) {
        BufferedImage bi = getAtomImage( color );
        double scale = (double)atomDiam / bi.getWidth();
        AffineTransform atx = new AffineTransform();
        atx.translate( line.getLinePosition().getX() - atomDiam / 2,
                       line.getLinePosition().getY() - atomDiam );
        atx.scale( scale, scale );
        for( int i = 0; i < numInLevel; i++ ) {
            atx.translate( atomDiam * 0.7 / scale, 0 );
            g2.drawRenderedImage( bi, atx );
        }
    }

    private Map colorToAtomImage = new HashMap();

    private BufferedImage getAtomImage( Color color ) {
        if( baseSphereImg == null ) {
            try {
                baseSphereImg = ImageLoader.loadBufferedImage( "images/particle-red-lrg.gif" );
//                baseSphereImg = ImageLoader.loadBufferedImage( "images/particle-red-med.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
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
    // LeftSystemEvent handlers
    //----------------------------------------------------------------

    public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
        CollimatedBeam beam = (CollimatedBeam)event.getSource();
        if( beam == model.getPumpingBeam() ) {
            pumpBeamWavelength = beam.getWavelength();
            pumpBeamEnergy = Photon.wavelengthToEnergy( pumpBeamWavelength );
        }
        if( beam == model.getSeedBeam() ) {
            seedBeamWavelength = beam.getWavelength();
            seedBeamEnergy = Photon.wavelengthToEnergy( seedBeamWavelength );
        }
        updateSquiggles();
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        updateSquiggles();
    }

    //----------------------------------------------------------------
    // ClockStateListener implementation
    //----------------------------------------------------------------


    /**
     * If the clock pauses, force the update and repaint of energy level populations. We need to do this because
     * when the clock is running, the populations shown are averages over time, and if the clock is paused, we
     * want the populations shown to agree with the actual number of atoms in each state.
     *
     * @param event
     */
    public void stateChanged( ClockStateEvent event ) {
        if( event.getIsPaused() ) {
            numGroundLevel = model.getNumGroundStateAtoms();
            numMiddleLevel = model.getNumMiddleStateAtoms();
            numHighLevel = model.getNumHighStateAtoms();
            this.invalidate();
            this.repaint();
        }
    }

    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Set the beamArea within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            adjustPanel();
        }
    }

    private class EnergyMatchDetector implements AtomicState.Listener, CollimatedBeam.WavelengthChangeListener {
        private AtomicState atomicState;
        private CollimatedBeam collimatedBeam;
        private EnergyLevelGraphic graphic;
        private boolean matched;

        public EnergyMatchDetector( AtomicState atomicState, CollimatedBeam collimatedBeam, EnergyLevelGraphic graphic ) {
            this.atomicState = atomicState;
            this.collimatedBeam = collimatedBeam;
            this.graphic = graphic;
            atomicState.addListener( this );
            collimatedBeam.addWavelengthChangeListener( this );
        }

        public void energyLevelChanged( AtomicState.Event event ) {
            checkForMatch();
        }

        private void checkForMatch() {
            if( Math.abs( Photon.wavelengthToEnergy( collimatedBeam.getWavelength() ) - atomicState.getEnergyLevel() )
                <= LaserConfig.ENERGY_TOLERANCE ) {
                if( !matched ) {
                    flashGraphic();
                    matched = true;
                }
            }
            else {
                matched = false;
            }
        }

        public void meanLifetimechanged( AtomicState.Event event ) {
            // noop
        }

        public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
            checkForMatch();
        }

        boolean hidden;

        private void flashGraphic() {
            GraphicFlasher gf = new GraphicFlasher( graphic );
            gf.start();
        }
    }

    private class GraphicFlasher extends Thread {
        private int numFlashes = 5;
        private EnergyLevelGraphic graphic;

        public GraphicFlasher( EnergyLevelGraphic graphic ) {
            this.graphic = graphic;
        }

        public void run() {
            try {
                for( int i = 0; i < numFlashes; i++ ) {
                    graphic.setVisible( false );
                    Thread.sleep( 100 );
                    graphic.setVisible( true );
                    Thread.sleep( 100 );
                }
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
