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
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.coreadditions.VisibleColor;
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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EnergyLevelMonitorPanel extends MonitorPanel implements CollimatedBeam.WavelengthChangeListener {

    private int atomDiam = 14;

    private double panelWidth = 400;
    private double panelHeight = 500;
    private double sliderWidth = 100;

    private Point2D origin = new Point2D.Double( 50, panelHeight - 10 );
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

    /**
     *
     */
    public EnergyLevelMonitorPanel( LaserModel model ) {
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
        middleLevelLifetimeSlider = new EnergyLifetimeSlider( MiddleEnergyState.instance(), this, middleLevelLine, SimStrings.get( "EnergyLevelMonitorPanel.MiddleLevelSlider" ) );
        this.add( middleLevelLifetimeSlider );
        highLevelLifetimeSlider = new EnergyLifetimeSlider( HighEnergyState.instance(), this, highLevelLine, SimStrings.get( "EnergyLevelMonitorPanel.HighLevelSlider" ) );
        this.add( highLevelLifetimeSlider );

        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );

        model.addObserver( this );
        this.model = model;

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + getBounds().getHeight() * 0.1,
                                                             getBounds().getWidth(), getBounds().getHeight() * 0.8 );
                energyYTx = new ModelViewTx1D( AtomicState.maxEnergy, AtomicState.minEnergy,
                                               (int)bounds.getBounds().getMinY(), (int)bounds.getBounds().getMaxY() );

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
        highLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );
        middleLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );
        groundLevelLine.setBasePosition( levelLineOriginX, panelHeight - 10 );

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
        double modelHeight = energyYTx.modelToView( stimulatingBeamEnergy );
        System.out.println( "stimulatingBeamEnergy = " + stimulatingBeamEnergy );
        System.out.println( "modelHeight = " + modelHeight );
        Line2D stimBeamEnergy = new Line2D.Double( origin.getX() + 5, origin.getY(),
                                                   origin.getX() + 5, modelHeight );
        Color c = VisibleColor.wavelengthToColor( Photon.energyToWavelength( stimulatingBeamEnergy ) );
        g2.setColor( c );
        g2.setStroke( new BasicStroke( 2 ) );
        g2.draw( stimBeamEnergy );

        gs.restoreGraphics();
    }

    public void update() {
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();

        middleLevelLifetimeSlider.update();
        highLevelLifetimeSlider.update();

        double lambda = model.getStimulatingBeam().getWavelength();
        double energy = Photon.wavelengthToEnergy( lambda );
        stimulatingBeamEnergy = Photon.wavelengthToEnergy( model.getStimulatingBeam().getWavelength() );

        this.invalidate();
        this.repaint();
    }

    private class EnergyLifetimeSlider extends JSlider {
        private int maxLifetime = 100;
        private EnergyLevelGraphic graphic;
        private int sliderHeight = 50;

        public EnergyLifetimeSlider( final AtomicState atomicState, Component component,
                                     EnergyLevelGraphic graphic, String label ) {
            super();
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
    }

    public void wavelengthChangeOccurred( CollimatedBeam.WavelengthChangeEvent event ) {
        CollimatedBeam beam = (CollimatedBeam)event.getSource();
        if( beam == model.getPumpingBeam() ) {
            pumpingBeamEnergy = Photon.wavelengthToEnergy( beam.getWavelength() );
        }
        if( beam == model.getStimulatingBeam() ) {
            stimulatingBeamEnergy = Photon.wavelengthToEnergy( beam.getWavelength() );
        }
    }
}
