/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.view.KaboomGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kaboom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Kaboom implements ModelElement {

    private MultipleAtomModule module;
    private LaserModel model;
    private PhetImageGraphic graphic;
    private boolean kaboomed;
    private List kaboomGraphics = new ArrayList();
    private PhetMultiLineTextGraphic message;
    private JLabel labelMessage;
    private String[] messageStrings = new String[]{"You blew up your laser!", "It couldn't take", "the power."};
    private PhetShapeGraphic backgroundGraphic;
    private double blackBacgroundLayer = Double.MAX_VALUE - 2;
    private double tileLayer = Double.MAX_VALUE;
    private double mewssageLayer = Double.MAX_VALUE - 1;
    private PhetImageGraphic wholeBackground;


    public Kaboom( MultipleAtomModule multipleAtomModule ) {
        this.module = multipleAtomModule;
        model = multipleAtomModule.getLaserModel();
    }

    public void stepInTime( double dt ) {
        int numPhotons = model.getNumPhotons();
        if( numPhotons > LaserConfig.KABOOM_THRESHOLD && !kaboomed ) {
            LaserModel laserModel = (LaserModel)model;
            model.reset();
            kaboom3();
            kaboomed = true;
        }
    }

    private void kaboom2() {
        double blackBacgroundLayer = Double.MAX_VALUE - 2;
        double mewssageLayer = Double.MAX_VALUE - 1;
        final ApparatusPanel2 ap = (ApparatusPanel2)module.getApparatusPanel();
        backgroundGraphic = new PhetShapeGraphic( ap,
                                                  new Rectangle( ap.getSize() ), Color.white );
        ap.addGraphic( backgroundGraphic,
                       blackBacgroundLayer );
        SwingTimerClock clock = new SwingTimerClock( 1, 40, AbstractClock.FRAMES_PER_SECOND );
        clock.start();

        // Make a bunch of images from the current state of the apparatus panel
        BufferedImage snapshot = BufferedImageUtils.toBufferedImage( ap.getSnapshot() );

        // Warp the image
        final AffineTransform atx = new AffineTransform();
        atx.setToShear( .2, .3 );
        final PhetImageGraphic imgGraphic = new PhetImageGraphic( ap, snapshot ) {
            public void paint( Graphics2D g ) {
                GraphicsState gs = new GraphicsState( g );
                g.transform( atx );
                super.paint( g );
                gs.restoreGraphics();
            }
        };
        ap.addGraphic( imgGraphic, this.tileLayer );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                atx.shear( .01, .01 );
                MakeDuotoneImageOp op = new MakeDuotoneImageOp( Color.red );
                op.filter( imgGraphic.getImage(), imgGraphic.getImage() );
                imgGraphic.repaint();
            }
        } );

        // Add the message to the user
        message = new PhetMultiLineTextGraphic( ap, messageStrings, new Font( "Lucida sans", Font.BOLD, 32 ),
                                                ap.getWidth() / 2 - 80, ap.getHeight() / 2 - 10,
                                                Color.red );
        ap.addGraphic( message,
                       mewssageLayer );

        // Hide the Swing controls on the panel
        module.setSwingComponentsVisible( false );
    }

    private void kaboom() {
        Random random = new Random();
        final ApparatusPanel2 ap = (ApparatusPanel2)module.getApparatusPanel();
        backgroundGraphic = new PhetShapeGraphic( ap,
                                                  new Rectangle( ap.getSize() ), Color.black );
        ap.addGraphic( backgroundGraphic,
                       blackBacgroundLayer );
        SwingTimerClock clock = new SwingTimerClock( 1, 40, AbstractClock.FRAMES_PER_SECOND );

        // Make a bunch of images from the current state of the apparatus panel
        BufferedImage snapshot = BufferedImageUtils.toBufferedImage( ap.getSnapshot() );

        // The whole apparatus panel as one spinning image
        Dimension apSize = ap.getSize();
        int numCols = 6;
        int tileWidth = apSize.width / numCols;
        int tileHeight = tileWidth;
        for( int x = 0; x < apSize.width - 1; x += tileWidth ) {
            for( int y = 0; y < apSize.height - 1; y += tileHeight ) {
                x = Math.min( x, apSize.width - tileWidth - 1 );
                y = Math.min( y, apSize.height - tileHeight - 1 );
                BufferedImage tile = snapshot.getSubimage( x, y, tileWidth, tileHeight );

                double spin = random.nextDouble() * 10 * ( random.nextBoolean() ? 1 : -1 );
                double zoom = 0;
                double flipFactorX = random.nextDouble();
                while( flipFactorX > 0.2 ) {
                    flipFactorX = random.nextDouble();
                }
                double flipFactorY = random.nextDouble();
                while( flipFactorY > 0.2 ) {
                    flipFactorY = random.nextDouble();
                }
                while( zoom < .9 || zoom > .99 ) {
                    zoom = random.nextDouble();
                }
                double txX = ( ( x + tile.getWidth() / 2 ) - ap.getWidth() / 2 ) * 0.01;
                double txY = ( ( y + tile.getHeight() / 2 ) - ap.getHeight() / 2 ) * 0.01;
                graphic = new KaboomGraphic( ap, tile, clock, new Point( x + tile.getWidth() / 2,
                                                                         y + tile.getHeight() / 2 ),
                                             zoom, spin, 0, 0, flipFactorX, flipFactorY, txX, txY );
                ap.addGraphic( graphic, tileLayer );
                graphic.setPosition( x, y );
                kaboomGraphics.add( graphic );
            }
        }

        // Add the message to the user
        message = new PhetMultiLineTextGraphic( ap, messageStrings, new Font( "Lucida sans", Font.BOLD, 32 ),
                                                ap.getWidth() / 2 - 80, ap.getHeight() / 2 - 10,
                                                Color.red );
        ap.addGraphic( message,
                       mewssageLayer );

        // Hide the Swing controls on the panel
        module.setSwingComponentsVisible( false );
    }

    private void kaboom3() {
        Random random = new Random();
        final ApparatusPanel2 ap = (ApparatusPanel2)module.getApparatusPanel();

        ResonatingCavity cavity = module.getCavity();
        Rectangle2D bounds = cavity.getBounds();
        bounds = RectangleUtils.expand( bounds, 30, 20 );
        bounds.setRect( bounds.getMinX(), bounds.getMinY(), ap.getWidth() - bounds.getMinX(), bounds.getHeight() );

        // Make a bunch of images from the current state of the apparatus panel
        BufferedImage snapshot = BufferedImageUtils.toBufferedImage( ap.getSnapshot() );

        // Make a static image of the current state of the apparatus panel, and use it as a background
        wholeBackground = new PhetImageGraphic( ap, snapshot );
        ap.addGraphic( wholeBackground, blackBacgroundLayer - 1 );

        // Make a white rectangle to cover the cavity and the area to the right of it
        backgroundGraphic = new PhetShapeGraphic( ap,
                                                  bounds, Color.white );
        ap.addGraphic( backgroundGraphic,
                       blackBacgroundLayer );
        SwingTimerClock clock = new SwingTimerClock( 1, 40, AbstractClock.FRAMES_PER_SECOND );

        int numCols = 4;
        int tileWidth = (int)( bounds.getWidth() / numCols );
        int tileHeight = tileWidth;
        for( double x = bounds.getMinX(); x < bounds.getMaxX() - 1; x += tileWidth ) {
            for( double y = bounds.getMinY(); y < bounds.getMaxY() - 1; y += tileHeight ) {
                x = Math.min( x, bounds.getMaxX() - tileWidth - 1 );
                y = Math.min( y, bounds.getMaxY() - tileHeight - 1 );
                BufferedImage tile = snapshot.getSubimage( (int)x, (int)y, tileWidth, tileHeight );

                double spin = random.nextDouble() * 10 * ( random.nextBoolean() ? 1 : -1 );
                double zoom = 0;
                double flipFactorX = random.nextDouble();
                while( flipFactorX > 0.2 ) {
                    flipFactorX = random.nextDouble();
                }
                double flipFactorY = random.nextDouble();
                while( flipFactorY > 0.2 ) {
                    flipFactorY = random.nextDouble();
                }
                while( zoom < .97 || zoom > .99 ) {
                    zoom = random.nextDouble();
                }
                double txX = ( ( x + tile.getWidth() / 2 ) - ap.getWidth() / 2 ) * 0.03;
                double txY = ( ( y + tile.getHeight() / 2 ) - ap.getHeight() / 2 ) * 0.03;
                graphic = new KaboomGraphic( ap, tile, clock, new Point( (int)x + tile.getWidth() / 2,
                                                                         (int)y + tile.getHeight() / 2 ),
                                             zoom, spin, 0, 0, flipFactorX, flipFactorY, txX, txY );
                ap.addGraphic( graphic, tileLayer );
                graphic.setPosition( (int)x, (int)y );
                kaboomGraphics.add( graphic );
            }
        }

        // Add the flames
        Flames flames = new Flames( ap, clock );
        flames.setPosition( (int)cavity.getBounds().getMinX(), (int)cavity.getBounds().getMaxY() - flames.getHeight() );
        ap.addGraphic( flames, tileLayer - .5 );

        // Add the message to the user
        message = new PhetMultiLineTextGraphic( ap, messageStrings, new Font( "Lucida sans", Font.BOLD, 32 ),
                                                ap.getWidth() / 2 - 80, ap.getHeight() / 2 - 10,
                                                Color.red );
        labelMessage = new JLabel( SimStrings.get( "Kaboom.message" ) );
        labelMessage.setFont( new Font( "Lucida sans", Font.BOLD, 32 ) );
        labelMessage.setForeground( Color.red );
        labelMessage.setLocation( 10, 10 );
        ap.add( labelMessage );
        labelMessage.reshape( ap.getWidth() / 2 - 80, ap.getHeight() / 2 - 10,
                              labelMessage.getPreferredSize().width,
                              labelMessage.getPreferredSize().height );
        ap.revalidate();
    }

    public void clearGraphics( ApparatusPanel apparatusPanel ) {
        List kaboomGraphics = getKaboomGraphics();
        for( int i = 0; i < kaboomGraphics.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)kaboomGraphics.get( i );
            apparatusPanel.removeGraphic( graphic );
        }
        apparatusPanel.removeGraphic( backgroundGraphic );
        apparatusPanel.removeGraphic( wholeBackground );
        apparatusPanel.removeGraphic( message );
        apparatusPanel.remove( labelMessage );
        apparatusPanel.revalidate();
        apparatusPanel.repaint();
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public List getKaboomGraphics() {
        return kaboomGraphics;
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class Flames extends PhetImageGraphic {
        Animation frames;

        protected Flames( final Component component, final AbstractClock clock ) {
            super( component );
            try {
                frames = new Animation( "images/flames", 15 );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setImage( frames.getFrame( 0 ) );

            clock.addClockTickListener( new ClockTickListener() {
                public void clockTicked( AbstractClock c, double dt ) {
                    if( frames.getCurrFrameNum() == ( frames.getNumFrames() - 1 ) ) {
                        clock.removeClockTickListener( this );
                        ( (ApparatusPanel)component ).removeGraphic( Flames.this );
                    }
                    else {
                        nextFrame();
                    }
                }
            } );
        }

        public void nextFrame() {
            setImage( frames.getNextFrame() );
        }
    }
}
