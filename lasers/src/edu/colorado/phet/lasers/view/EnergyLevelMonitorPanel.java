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
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
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

public class EnergyLevelMonitorPanel extends MonitorPanel implements CollimatedBeam.WavelengthChangeListener {

    private int atomDiam = 14;

    private double panelWidth = 400;
    private double panelHeight = 500;
    private double sliderWidth = 100;

    private Point2D origin = new Point2D.Double( 50, panelHeight - 30 );
    private double levelLineOriginX = origin.getX() + 30;
    private double levelLineLength = panelWidth - levelLineOriginX - sliderWidth - 20;

    private EnergyLevelGraphic highLevelLine;
    private EnergyLevelGraphic middleLevelLine;
    private EnergyLevelGraphic groundLevelLine;

    private EnergyLifetimeSlider highLevelLifetimeSlider;
    private EnergyLifetimeSlider middleLevelLifetimeSlider;

    // Number of energy levels to show
    private int numLevels;

    private LaserModel model;
    private double pumpingBeamEnergy;
    private double stimulatingBeamEnergy;
    private ModelViewTx1D energyYTx;
    private BufferedImage stimSquiggle;
    private BufferedImage pumpSquiggle;
    private AffineTransform stimSquiggleTx;
    private AffineTransform pumpSquiggleTx;

    /**
     *
     */
    public EnergyLevelMonitorPanel( LaserModel model ) {

        model.addObserver( this );
        this.model = model;
        model.getPumpingBeam().addListener( this );
        model.getSeedBeam().addListener( this );


        highLevelLine = new EnergyLevelGraphic( this, HighEnergyState.instance(),
                                                Color.blue, levelLineOriginX, levelLineLength );
        middleLevelLine = new EnergyLevelGraphic( this, MiddleEnergyState.instance(),
                                                  Color.red, levelLineOriginX, levelLineLength );
        groundLevelLine = new EnergyLevelGraphic( this, GroundState.instance(),
                                                  Color.black, levelLineOriginX, levelLineLength );

        this.setBackground( Color.white );
        this.addGraphic( highLevelLine );
        this.addGraphic( middleLevelLine );
        this.addGraphic( groundLevelLine );

        // Add lifetime sliders and a title for them
        JLabel legend = new JLabel( SimStrings.get( "EnergyLevelMonitorPanel.EnergyLevelLifetimeLabel" ) );
        legend.setBounds( (int)( levelLineOriginX + levelLineLength + 10 ), 15, 100, 30 );
        this.add( legend );
        middleLevelLifetimeSlider = new EnergyLifetimeSlider( MiddleEnergyState.instance(), this, middleLevelLine,
                                                              SimStrings.get( "EnergyLevelMonitorPanel.MiddleLevelSlider" ) );
        this.add( middleLevelLifetimeSlider );
        highLevelLifetimeSlider = new EnergyLifetimeSlider( HighEnergyState.instance(), this, highLevelLine,
                                                            SimStrings.get( "EnergyLevelMonitorPanel.HighLevelSlider" ) );
        this.add( highLevelLifetimeSlider );

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY(),
                                                             getBounds().getWidth(), getBounds().getHeight() * 0.85 );
                //                Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + getBounds().getHeight() * 0.1,
                //                                                             getBounds().getWidth(), getBounds().getHeight() * 0.8 );
                energyYTx = new ModelViewTx1D( AtomicState.maxEnergy, AtomicState.minEnergy,
                                               (int)bounds.getBounds().getMinY(), (int)bounds.getBounds().getMaxY() );
                //                energyYTx.setModelToViewFunction( new ModelViewTx1D.PowerFunction( 0.98 ) );

                highLevelLine.update( energyYTx );
                middleLevelLine.update( energyYTx );
                groundLevelLine.update( energyYTx );
            }
        } );
    }

    public void setNumLevels( int numLevels ) {
        this.numLevels = numLevels;
        switch( numLevels ) {
            case 2:
                panelHeight = 300;
                highLevelLine.setVisible( false );
                highLevelLifetimeSlider.setVisible( false );
                break;
            case 3:
                panelHeight = 300;
                highLevelLine.setVisible( true );
                highLevelLifetimeSlider.setVisible( true );
                break;
            default:
                throw new RuntimeException( "Number of levels out of range" );
        }
        //        highLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );
        //        middleLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );
        //        groundLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );

        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        revalidate();
        repaint();
        SwingUtilities.getWindowAncestor( this ).pack();
    }

    /**
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw ground level atoms
        g2.setColor( Color.gray );
        for( int i = 0; i < numGroundLevel; i++ ) {
            g2.fillArc( (int)( groundLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                        (int)( groundLevelLine.getPosition().getY() - atomDiam ),
                        atomDiam,
                        atomDiam,
                        0, 360 );
        }

        // Draw middle level atoms
        if( numLevels >= 2 ) {
            Color c = VisibleColor.wavelengthToColor( MiddleEnergyState.instance().getWavelength() );
            g2.setColor( c );
            for( int i = 0; i < numMiddleLevel; i++ ) {
                g2.fillArc( (int)( middleLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                            (int)( middleLevelLine.getPosition().getY() - atomDiam ),
                            atomDiam,
                            atomDiam,
                            0, 360 );
            }
        }

        // Draw high level atoms, if the level is enabled
        if( numLevels >= 3 ) {
            Color c = VisibleColor.wavelengthToColor( HighEnergyState.instance().getWavelength() );
            g2.setColor( c );
            for( int i = 0; i < numHighLevel; i++ ) {
                g2.fillArc( (int)( highLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                            (int)( highLevelLine.getPosition().getY() - atomDiam ),
                            atomDiam,
                            atomDiam,
                            0, 360 );
            }
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

    public void update() {
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();

        middleLevelLifetimeSlider.update();
        highLevelLifetimeSlider.update();

        stimulatingBeamEnergy = Photon.wavelengthToEnergy( model.getSeedBeam().getWavelength() );

        double y0 = energyYTx.modelToView( GroundState.instance().getEnergyLevel() );
        double y1 = energyYTx.modelToView( stimulatingBeamEnergy );
        double y2 = energyYTx.modelToView( pumpingBeamEnergy );

        // Build the images for the squiggles that represent the energies of the stimulating and pumping beam
        int squiggleHeight = 10;
        stimSquiggle = computeSquiggleImage( Photon.energyToWavelength( stimulatingBeamEnergy ), 0,
                                             (int)( y0 - y1 ), squiggleHeight );
        stimSquiggleTx = AffineTransform.getTranslateInstance( origin.getX(),
                                                               energyYTx.modelToView( GroundState.instance().getEnergyLevel() ) );
        stimSquiggleTx.rotate( -Math.PI / 2 );
        pumpSquiggle = computeSquiggleImage( Photon.energyToWavelength( pumpingBeamEnergy ), 0,
                                             (int)( y0 - y2 ), squiggleHeight );
        pumpSquiggleTx = AffineTransform.getTranslateInstance( origin.getX() + squiggleHeight * 3 / 2,
                                                               energyYTx.modelToView( GroundState.instance().getEnergyLevel() ) );
        pumpSquiggleTx.rotate( -Math.PI / 2 );


        this.invalidate();
        this.repaint();
    }

    public class EnergyLifetimeSlider extends JSlider implements AtomicState.MeanLifetimeChangeListener {
        private int maxLifetime = 100;
        private EnergyLevelGraphic graphic;
        private int sliderHeight = 50;

        public EnergyLifetimeSlider( final AtomicState atomicState, Component component,
                                     EnergyLevelGraphic graphic, String label ) {
            super();
            atomicState.addListener( this );
            setMinimum( 0 );
            setMaximum( maxLifetime );
            setValue( maxLifetime / 2 );
            setMajorTickSpacing( maxLifetime );
            setMinorTickSpacing( maxLifetime / 10 );
            setPaintTicks( true );
            setPaintLabels( true );
            setPaintTrack( true );
            this.graphic = graphic;

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

        public void update() {
            this.setBounds( (int)( levelLineOriginX + levelLineLength + 10 ),
                            (int)graphic.getPosition().getY() - sliderHeight / 2,
                            100, sliderHeight );
        }

        public void meanLifetimeChanged( AtomicState.MeanLifetimeChangeEvent event ) {
            this.setEnabled( false );
            this.setValue( (int)event.getMeanLifetime() );
            this.setEnabled( true );
        }
    }

    public void wavelengthChangeOccurred( CollimatedBeam.WavelengthChangeEvent event ) {
        CollimatedBeam beam = (CollimatedBeam)event.getSource();
        if( beam == model.getPumpingBeam() ) {
            pumpingBeamEnergy = Photon.wavelengthToEnergy( beam.getWavelength() );
        }
        if( beam == model.getSeedBeam() ) {
            stimulatingBeamEnergy = Photon.wavelengthToEnergy( beam.getWavelength() );
        }
    }

    /**
     *
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
        double freqFactor = 10 * wavelength / 680;
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
                                arrowHeight, height, 2 );
        Arrow tail = new Arrow( new Point2D.Double( length - arrowHeight, height / 2 ),
                                new Point2D.Double( length, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( head.getShape() );
        g2d.fill( tail.getShape() );
        g2d.dispose();
        return img;
    }
}
