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

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class EnergyLevelMonitorPanel extends MonitorPanel {

    private int atomDiam = 14;

    private double panelWidth = 400;
    private double panelHeight = 300;
    private double sliderWidth = 100;

    private double levelLineOriginX = 30;
    private double levelLineLength = panelWidth - sliderWidth - 60;

    private EnergyLevelGraphic highLevelLine;
    private EnergyLevelGraphic middleLevelLine;
    private EnergyLevelGraphic groundLevelLine;

    private EnergyLifetimeSlider highLevelLifetimeSlider;
    private EnergyLifetimeSlider middleLevelLifetimeSlider;

    // Number of energy levels to show
    private int numLevels;

    private LaserModel model;

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

        this.addGraphic( highLevelLine );
        this.addGraphic( middleLevelLine );
        this.addGraphic( groundLevelLine );

        // Add lifetime sliders and a title for them
        JLabel legend = new JLabel( "<html>Energy Level<br>Lifetime (msec)</html>" );
        legend.setBounds( (int)( levelLineOriginX + levelLineLength + 10 ), 15, 100, 30 );
        this.add( legend );
        middleLevelLifetimeSlider = new EnergyLifetimeSlider( MiddleEnergyState.instance(), this, middleLevelLine, "Middle level" );
        this.add( middleLevelLifetimeSlider );
        highLevelLifetimeSlider = new EnergyLifetimeSlider( HighEnergyState.instance(), this, highLevelLine, "High level" );
        this.add( highLevelLifetimeSlider );

        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );

        model.addObserver( this );
        this.model = model;

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                highLevelLine.update();
                middleLevelLine.update();
                middleLevelLifetimeSlider.update();
                groundLevelLine.update();
            }
        } );
    }

    public void setNumLevels( int numLevels ) {
        this.numLevels = numLevels;
        switch( numLevels ) {
            case 2:
                panelHeight = 200;
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
            g2.setColor( Color.red );
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
            g2.setColor( Color.blue );
            for( int i = 0; i < numHighLevel; i++ ) {
                g2.fillArc( (int)( highLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                            (int)( highLevelLine.getPosition().getY() - atomDiam ),
                            atomDiam,
                            atomDiam,
                            0, 360 );
            }
        }

        gs.restoreGraphics();
    }

    public void update() {
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();

        middleLevelLifetimeSlider.update();
        highLevelLifetimeSlider.update();

        this.invalidate();
        this.repaint();
    }

    private class EnergyLifetimeSlider extends JSlider {
        private int maxLifetime = 5000;
        private EnergyLevelGraphic graphic;
        private int sliderHeight = 60;

        public EnergyLifetimeSlider( final AtomicState atomicState, Component component,
                                     EnergyLevelGraphic graphic, String label ) {
            super();
            setMinimum( 0 );
            setMaximum( maxLifetime );
            setValue( maxLifetime / 2 );
            setMajorTickSpacing( 5000 );
            setMinorTickSpacing( 1000 );
            setPaintTicks( true );
            setPaintLabels( true );
            setPaintTrack( true );
            this.graphic = graphic;

            this.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    atomicState.setMeanLifetime( EnergyLifetimeSlider.this.getValue() );
                }
            } );
            update();
        }

        public void update() {
            this.setBounds( (int)( levelLineOriginX + levelLineLength + 10 ),
                            (int)graphic.getPosition().getY() - sliderHeight / 2,
                            100, sliderHeight );
        }
    }
}
