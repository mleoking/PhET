/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.flourescent.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.flourescent.model.FluorescentLightModel;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;
import edu.colorado.phet.lasers.view.MonitorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 */
public class EnergyLevelMonitorPanel extends MonitorPanel implements ClockStateListener, Atom.ChangeListener {

    // Number of milliseconds between display updates. Energy level populations are averaged over this time
    private long averagingPeriod = 300;

    // The diameter of an atom as displayed on the screen, in pixels
    private int atomDiam = 10;
    // Dimensions of the panel
    private double panelHeight = 230;
    private double panelWidth = 320;
    // Amplitude of the squiggle waves
    // Location and size of energy level lines
    private Point2D origin = new Point2D.Double( 25, panelHeight - 30 );
    private double levelLineOriginX = origin.getX();
    private double levelLineLength = panelWidth - levelLineOriginX - 50;

    // Cache of atom images of varying colors
    private Map colorToAtomImage = new HashMap();

    private EnergyLevelGraphic[] levelGraphics;

    private FluorescentLightModel model;
    private ModelViewTransform1D energyYTx;
    private BufferedImage baseSphereImg;
    // The offset by which all the graphic elements must be placed, caused by the heading text
    private int headingOffsetY = 20;
    private AtomicState[] atomicStates;
    private List atoms;
    private HashMap numAtomsInState;

    /**
     *
     */
    public EnergyLevelMonitorPanel( BaseLaserModule module, AbstractClock clock, AtomicState[] atomicStates ) {

        model = (FluorescentLightModel)module.getLaserModel();
        model.addObserver( this );
        clock.addClockStateListener( this );

        // Create a horizontal line for each energy level, then add them to the panel
        setEnergyLevels( atomicStates );

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

//        // Add the energy level lines to the panel
//        for( int i = 0; i < levelGraphics.length; i++ ) {
//            this.addGraphic( levelGraphics[i] );
//        }
//
        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );

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

        for( int i = 0; i < levelGraphics.length; i++ ) {
            levelGraphics[i].update( energyYTx );
        }
    }

    /**
     *
     * @param atomicStates
     */
    public void setEnergyLevels( AtomicState[] atomicStates) {

        // Remove any energy level graphics we might have
        // Add the energy level lines to the panel
        for( int i = 0; levelGraphics != null && i < levelGraphics.length; i++ ) {
            this.removeGraphic( levelGraphics[i] );
        }

        // Make new graphics and add them
        this.atomicStates = atomicStates;
        levelGraphics = new EnergyLevelGraphic[ atomicStates.length ];
        for( int i = 0; i < levelGraphics.length; i++ ) {
            levelGraphics[i] = new EnergyLevelGraphic(  this, atomicStates[i],
                                                Color.blue, levelLineOriginX,
                                                levelLineLength - levelLineOriginX,
                                                true );
        }
        for( int i = 0; i < levelGraphics.length; i++ ) {
            this.addGraphic( levelGraphics[i] );
        }

        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        revalidate();
        repaint();
    }

    /**
     * Handles updates from the model
     */
    public void update() {
        atoms = model.getAtoms();
        numAtomsInState = new HashMap();
        for( int i = 0; i < atomicStates.length; i++ ) {
            numAtomsInState.put( atomicStates[i], new Integer( 0 ) );
        }
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            int n = ((Integer)numAtomsInState.get( atom.getCurrState() )).intValue();
            numAtomsInState.put( atom.getCurrState(), new Integer( n + 1 ));
        }
        invalidate();
        repaint();
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

        // Draw level atoms
        for( int i = 0; i < atomicStates.length; i++ ) {
            Color c = VisibleColor.wavelengthToColor( atomicStates[i].getWavelength() );
            int n = ((Integer)numAtomsInState.get( atomicStates[i])).intValue();
            drawAtomsInLevel( g2, c, levelGraphics[i], n );
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
            update();
        }
    }

    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }

    //----------------------------------------------------------------
    // Atom.ChangeListener implementation
    //----------------------------------------------------------------

    public void stateChanged( Atom.ChangeEvent event ){
        update();
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
}
