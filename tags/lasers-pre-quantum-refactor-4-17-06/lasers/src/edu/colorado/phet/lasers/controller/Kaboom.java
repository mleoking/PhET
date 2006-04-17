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
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.quantum.model.Tube;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Kaboom
 * <p>
 * An animated graphic that declared that the laser has blown up.
 * <p>
 * Unued, commented code is still in here that makes an image of the apparatus panel, then
 * gractures it nad makes the pieces spin away, off the frame.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Kaboom implements ModelElement {

    private BaseLaserModule module;
    private LaserModel model;
    private boolean kaboomed;
    private List kaboomGraphics = new ArrayList();
    private JLabel labelMessage;
    private PhetShapeGraphic backgroundGraphic;
    private double blackBacgroundLayer = Double.MAX_VALUE - 2;
    private double tileLayer = Double.MAX_VALUE;


    public Kaboom( BaseLaserModule module ) {
        // Unless the module has an apparatus panel at this time things won't work right
        if( module.getApparatusPanel() == null ) {
            throw new RuntimeException( "Module doesn't have an apparatus panel" );
        }
        this.module = module;
        model = module.getLaserModel();
    }

    public void stepInTime( double dt ) {
        int numPhotons = model.getNumLasingPhotons();
        if( numPhotons > LaserConfig.KABOOM_THRESHOLD && !kaboomed ) {
            model.reset();
            kaboom();
            kaboomed = true;
        }
    }

    private void kaboom() {

        final ApparatusPanel2 ap = (ApparatusPanel2)module.getApparatusPanel();

        Tube cavity = module.getCavity();
        Rectangle2D bounds = cavity.getBounds();
        bounds = RectangleUtils.expand( bounds, 30, 20 );
        bounds.setRect( bounds.getMinX(), bounds.getMinY(), ap.getWidth() - bounds.getMinX(), bounds.getHeight() );

        // Make a bunch of images from the current state of the apparatus panel
//        BufferedImage snapshot = BufferedImageUtils.toBufferedImage( ap.getSnapshot() );

        // Make a static image of the current state of the apparatus panel, and use it as a background
//        wholeBackground = new PhetImageGraphic( ap, snapshot );
//        ap.addGraphic( wholeBackground, blackBacgroundLayer - 1 );

        // Make a white rectangle to cover the cavity and the area to the right of it
        backgroundGraphic = new PhetShapeGraphic( ap,
                                                  bounds, Color.white );
        ap.addGraphic( backgroundGraphic,
                       blackBacgroundLayer );
        SwingClock clock = new SwingClock( 1000 / 40, 1 );

//        int numCols = 4;
//        int tileWidth = (int)( bounds.getBeamWidth() / numCols );
//        int tileHeight = tileWidth;
//        for( double x = bounds.getMinX(); x < bounds.getMaxX() - 1; x += tileWidth ) {
//            for( double y = bounds.getMinY(); y < bounds.getMaxY() - 1; y += tileHeight ) {
//                x = Math.min( x, bounds.getMaxX() - tileWidth - 1 );
//                y = Math.min( y, bounds.getMaxY() - tileHeight - 1 );
//                BufferedImage tile = snapshot.getSubimage( (int)x, (int)y, tileWidth, tileHeight );
//
//                double spin = random.nextDouble() * 10 * ( random.nextBoolean() ? 1 : -1 );
//                double zoom = 0;
//                double flipFactorX = random.nextDouble();
//                while( flipFactorX > 0.2 ) {
//                    flipFactorX = random.nextDouble();
//                }
//                double flipFactorY = random.nextDouble();
//                while( flipFactorY > 0.2 ) {
//                    flipFactorY = random.nextDouble();
//                }
//                while( zoom < .97 || zoom > .99 ) {
//                    zoom = random.nextDouble();
//                }
//                double txX = ( ( x + tile.getBeamWidth() / 2 ) - ap.getBeamWidth() / 2 ) * 0.03;
//                double txY = ( ( y + tile.getLength() / 2 ) - ap.getLength() / 2 ) * 0.03;
//                graphic = new KaboomGraphic( ap, tile, clock, new Point( (int)x + tile.getBeamWidth() / 2,
//                                                                         (int)y + tile.getLength() / 2 ),
//                                             zoom, spin, 0, 0, flipFactorX, flipFactorY, txX, txY );
//                ap.addGraphic( graphic, tileLayer );
//                graphic.setPosition( (int)x, (int)y );
//                kaboomGraphics.add( graphic );
//            }
//        }

        // Add the flames
        clock.start();
        Flames flames = new Flames( ap, clock );
        flames.setLocation( (int)cavity.getBounds().getMinX(), (int)cavity.getBounds().getMaxY() - flames.getHeight() );
        ap.addGraphic( flames, tileLayer - .5 );

        // Add the message to the user
        labelMessage = new JLabel( SimStrings.get( "Kaboom.message" ) );
        labelMessage.setFont( new Font( "Lucida sans", Font.BOLD, 24 ) );
        labelMessage.setForeground( Color.red );
        labelMessage.setLocation( -20, 10 );
        ap.add( labelMessage );
        labelMessage.reshape( ap.getWidth() / 2 - 200, ap.getHeight() / 2 - 70,
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
        if( backgroundGraphic != null ) {
            apparatusPanel.removeGraphic( backgroundGraphic );
        }
        if( labelMessage != null ) {
            apparatusPanel.remove( labelMessage );
        }
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
        FrameSequence frames;

        protected Flames( final Component component, final IClock clock ) {
            super( component );
            try {
                frames = new FrameSequence( "images/flames", 15 );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setImage( frames.getFrame( 0 ) );

            clock.addClockListener( new ClockAdapter() {
                public void clockTicked( ClockEvent event ) {
                    if( frames.getCurrFrameNum() == ( frames.getNumFrames() - 1 ) ) {
                        clock.removeClockListener( this );
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
