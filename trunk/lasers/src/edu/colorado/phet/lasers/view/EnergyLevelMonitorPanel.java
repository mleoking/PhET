/**
 * Class: ThreeEnergyLevelPanel
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.math.ModelViewTx1D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class EnergyLevelMonitorPanel extends MonitorPanel implements CollimatedBeam.WavelengthChangeListener,
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

    private double panelHeight = 230;
//    private double panelHeight = 170;
//    private double panelHeight = 190;
    private double panelWidth = 320;
//    private double panelWidth = 400;
//    private double panelHeight = 200;
    private double sliderWidth = 100;
    private int squiggleHeight = atomDiam;

    private Point2D origin = new Point2D.Double( 25, panelHeight - 30 );
    private double levelLineOriginX = origin.getX();
    private double levelLineLength = panelWidth - levelLineOriginX - 50;
//    private double levelLineLength = panelWidth - levelLineOriginX - sliderWidth - 20;

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
    private ModelViewTx1D energyYTx;
    private BufferedImage stimSquiggle;
    private BufferedImage pumpSquiggle;
    private AffineTransform stimSquiggleTx;
    private AffineTransform pumpSquiggleTx;
    private double seedBeamWavelength;
    private double pumpBeamWavelength;
    private BufferedImage baseSphereImg;
    private BaseLaserModule module;

    /**
     *
     */
    public EnergyLevelMonitorPanel( BaseLaserModule module, AbstractClock clock ) {

        this.module = module;
        model = module.getLaserModel();
        model.addObserver( this );
        clock.addClockStateListener( this );
        model.getPumpingBeam().addWavelengthChangeListener( this );
        model.getSeedBeam().addWavelengthChangeListener( this );

        // Create a horizontal line for each energy level, then add them to the panel
        highLevelLine = new EnergyLevelGraphic( this, HighEnergyState.instance(),
                                                Color.blue, levelLineOriginX,
                                                levelLineLength - levelLineOriginX,
                                                true );
        middleLevelLine = new EnergyLevelGraphic( this, MiddleEnergyState.instance(),
                                                  Color.red, levelLineOriginX + squiggleHeight * 1.5,
                                                  levelLineLength - ( levelLineOriginX + squiggleHeight * 1.5 ),
                                                  true );
        groundLevelLine = new EnergyLevelGraphic( this, GroundState.instance(),
                                                  Color.black, levelLineOriginX + squiggleHeight * 3,
                                                  levelLineLength - ( levelLineOriginX + squiggleHeight * 3 ),
                                                  false );
        this.setBackground( Color.white );
        this.addGraphic( highLevelLine );
        this.addGraphic( middleLevelLine );
        this.addGraphic( groundLevelLine );

        // Add lifetime sliders and a title for them
//        JLabel legend = new JLabel( SimStrings.get( "EnergyLevelMonitorPanel.EnergyLevelLifetimeLabel" ) );
//        legend.setBounds( (int)( levelLineOriginX + levelLineLength ), 15, 150, 30 );
//        this.add( legend );
        middleLevelLifetimeSlider = new EnergyLifetimeSlider( MiddleEnergyState.instance(),
                                                              middleLevelLine,
                                                              LaserConfig.MIDDLE_ENERGY_STATE_MAX_LIFETIME );
        this.add( middleLevelLifetimeSlider );
        highLevelLifetimeSlider = new EnergyLifetimeSlider( HighEnergyState.instance(), highLevelLine,
                                                            LaserConfig.HIGH_ENERGY_STATE_MAX_LIFETIME );
        this.add( highLevelLifetimeSlider );

        this.addComponentListener( new PanelResizer() );
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
//        SwingUtilities.getWindowAncestor( this ).pack();
    }

    /**
     * Handles updates from the model
     */
    public void update() {
        numGroundLevelAccum += module.getNumGroundStateAtoms();
        numMiddleLevelAccum += module.getNumMiddleStateAtoms();
        numHighLevelAccum += module.getNumHighStateAtoms();

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
        double y0 = energyYTx.modelToView( GroundState.instance().getEnergyLevel() );
        double y1 = energyYTx.modelToView( seedBeamEnergy );
        double y2 = energyYTx.modelToView( pumpBeamEnergy );

        // Build the images for the squiggles that represent the energies of the stimulating and pumping beam
        if( y0 > y1 ) {
            stimSquiggle = computeSquiggleImage( seedBeamWavelength, 0, (int)( y0 - y1 ), squiggleHeight );
            stimSquiggleTx = AffineTransform.getTranslateInstance( middleLevelLine.getPosition().getX(),
                                                                   energyYTx.modelToView( GroundState.instance().getEnergyLevel() ) );
            stimSquiggleTx.rotate( -Math.PI / 2 );
        }

        if( y0 > y2 ) {
            pumpSquiggle = computeSquiggleImage( pumpBeamWavelength, 0, (int)( y0 - y2 ), squiggleHeight );
            pumpSquiggleTx = AffineTransform.getTranslateInstance( highLevelLine.getPosition().getX(),
                                                                   energyYTx.modelToView( GroundState.instance().getEnergyLevel() ) );
            pumpSquiggleTx.rotate( -Math.PI / 2 );
        }

        // Force a repaint
        this.invalidate();
        this.repaint();
    }

    /**
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( double wavelength, double phaseAngle, int length, int height ) {

        int arrowHeight = height;

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( length + 2 * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = img.createGraphics();
        int kPrev = height / 2;
        int iPrev = 0;
        Color c = VisibleColor.wavelengthToColor( wavelength );
        double freqFactor = 15 * wavelength / 680;
        for( int i = 0; i < length - arrowHeight * 2; i++ ) {
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
        Arrow tail = new Arrow( new Point2D.Double( length - arrowHeight, height / 2 ),
                                new Point2D.Double( length, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( head.getShape() );
        g2d.fill( tail.getShape() );
        g2d.dispose();
        return img;
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
            Color c = VisibleColor.wavelengthToColor( MiddleEnergyState.instance().getWavelength() );
            drawAtomsInLevel( g2, c, middleLevelLine, numMiddleLevel );
        }

        // Draw high level atoms, if the level is enabled
        if( numLevels >= 3 ) {
            Color c = VisibleColor.wavelengthToColor( HighEnergyState.instance().getWavelength() );
            drawAtomsInLevel( g2, c, highLevelLine, numHighLevel );
        }

        // Draw squiggles showing what energy photons the beams are putting out
        if( stimSquiggle != null && model.getSeedBeam().isEnabled() ) {
            g2.drawRenderedImage( stimSquiggle, stimSquiggleTx );
        }
        if( pumpSquiggle != null && model.getPumpingBeam().isEnabled() ) {
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

    //----------------------------------------------------------------
    // ClockStateListener implementation
    //----------------------------------------------------------------

    /**
     * If the clock pauses, force the update and repaint of energy level populations. We need to do this because
     * when the clock is running, the populations shown are averages over time, and if the clock is paused, we
     * want the populations shown to agree with the actual number of atoms in each state.
     *
     * @param b
     */
    public void pausedStateChanged( boolean b ) {
        if( b ) {
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

    public void setAveragingPeriod( long value ) {
        averagingPeriod = value;
    }

    public long getAveragingPeriod() {
        return averagingPeriod;
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    public class EnergyLifetimeSlider extends JSlider implements AtomicState.Listener {
        // Needs to be accessible to the EnergyLevelGraphic class
        public final static int sliderHeight = 40;

        private EnergyLevelGraphic graphic;
        private int maxSliderWidth = 100;
        private int sliderWidthPadding = 20;
        private int sliderWidth;

        public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic, int maxLifetime ) {
            super();
            atomicState.addListener( this );
            setMinimum( 0 );
            setMaximum( maxLifetime );
            sliderWidth = (int)( (double)( maxSliderWidth - sliderWidthPadding ) * ( (double)getMaximum() / LaserConfig.MAXIMUM_STATE_LIFETIME ) ) + sliderWidthPadding;
            sliderWidth = Math.min( sliderWidth, maxSliderWidth );
            setValue( maxLifetime / 2 );
            setMajorTickSpacing( maxLifetime );
            setMinorTickSpacing( maxLifetime / 10 );
//            setPaintTicks( true );
//            setPaintLabels( true );
//            setPaintTrack( true );
            this.graphic = graphic;
            this.setLayout( new BorderLayout() );
            this.add( new JLabel( SimStrings.get( "EnergyLevelMonitorPanel.sliderLabel" ), JLabel.CENTER ), BorderLayout.NORTH );

            this.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    atomicState.setMeanLifetime( EnergyLifetimeSlider.this.getValue() );
                }
            } );
            this.setEnabled( false );
            setValue( (int)atomicState.getMeanLifeTime() );
            this.setEnabled( true );

            this.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            update();
        }

        /**
         * Positions the slider on the screen
         */
        public void update() {
            this.setBounds( (int)( panelWidth - maxSliderWidth ),
//            this.setBounds( (int)( levelLineOriginX + levelLineLength + 10 ),
                            (int)graphic.getPosition().getY(),
                            sliderWidth, sliderHeight );
        }

        public void energyLevelChanged( AtomicState.Event event ) {
            // noop
        }

        public void meanLifetimechanged( AtomicState.Event event ) {
            this.setEnabled( false );
            this.setValue( (int)event.getMeanLifetime() );
            this.setEnabled( true );
        }

        public void meanLifetimeChanged( AtomicState.MeanLifetimeChangeEvent event ) {
            this.setEnabled( false );
            this.setValue( (int)event.getMeanLifetime() );
            this.setEnabled( true );
        }
    }

    /**
     * Set the bounds within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            // The bounds in which the energy levels will be displayed
            Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + 10,
                                                         getBounds().getWidth(), getBounds().getHeight() - 30 );
            energyYTx = new ModelViewTx1D( AtomicState.maxEnergy, AtomicState.minEnergy,
                                           (int)bounds.getBounds().getMinY(), (int)bounds.getBounds().getMaxY() );
            highLevelLine.update( energyYTx );
            middleLevelLine.update( energyYTx );
            groundLevelLine.update( energyYTx );
            updateSquiggles();
        }
    }
}
