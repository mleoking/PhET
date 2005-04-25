/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.model.PChemModel;
import edu.colorado.phet.idealgas.model.ParticleCounter;
import edu.colorado.phet.idealgas.view.HeavySpeciesGraphic;
import edu.colorado.phet.idealgas.view.LightSpeciesGraphic;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * AdvancedModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AdvancedModule extends IdealGasModule {
    private static Font readoutFont = new Font( "Lucida sans", Font.BOLD, 12 );
    protected Wall verticalWall;
    private ParticleCounter leftRegionParticleCounter;
    private ParticleCounter rightRegionParticleCounter;

    public AdvancedModule( AbstractClock clock, String name ) {
        super( clock, name );

        // We can only use the top pressure-sensing slice because we don't know where the
        // floors will be
        getBox().setMultipleSlicesEnabled( false );

        // Set the two types of particles so they are the same mass and radius
        LightSpecies.setMoleculeMass( HeavySpecies.getMoleculeMass() );
        LightSpecies.setMoleculeRadius( HeavySpecies.getMoleculeRadius() );

        // Set the colors of the particle graphics
        LightSpeciesGraphic.setColor( new Color (255, 255, 0 ));
        HeavySpeciesGraphic.setColor( new Color (0, 150, 0 ));
    }

    public AdvancedModule( AbstractClock clock, String s, PChemModel model ) {
        super( clock, s, model );
    }

    /**
     * Add elements that keep count of the number of particles on either side of the vertical wall
     */
    protected void addParticleCounters() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();

        // Create the particle counters
        leftRegionParticleCounter = new ParticleCounter( getIdealGasModel() );
        getModel().addModelElement( leftRegionParticleCounter );

        rightRegionParticleCounter = new ParticleCounter( getIdealGasModel() );
        getModel().addModelElement( rightRegionParticleCounter );

        // Set the bounds of the regions the particle counters cound
        setParticleCounterRegions();

        // Put readouts on the apparatus panel
        PhetGraphic leftCounterReadout = new ReadoutGraphic( leftRegionParticleCounter, SimStrings.get( "AdvancedModule.Count") + ": " );
        leftCounterReadout.setLocation( (int)boxBounds.getMinX() + 0, (int)boxBounds.getMaxY() + 7 );
        addGraphic( leftCounterReadout, IdealGasConfig.READOUT_LAYER );

        PhetGraphic rightCounterReadout = new ReadoutGraphic( rightRegionParticleCounter, SimStrings.get( "AdvancedModule.Count") + ": ");
        rightCounterReadout.setLocation( (int)boxBounds.getMaxX() - 110, (int)boxBounds.getMaxY() + 7 );
        addGraphic( rightCounterReadout, IdealGasConfig.READOUT_LAYER );
    }

    /**
     * Adjusts the size of the regions the particle counters cover
     */
    protected void setParticleCounterRegions() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = verticalWall.getBounds();

        leftRegionParticleCounter.setRegion( new Rectangle2D.Double( boxBounds.getMinX(),
                                                                     boxBounds.getMinY(),
                                                                     lowerWallBounds.getMinX() + lowerWallBounds.getWidth() / 2 - boxBounds.getMinX(),
                                                                     boxBounds.getHeight() ) );
        rightRegionParticleCounter.setRegion( new Rectangle2D.Double( lowerWallBounds.getMinX() + lowerWallBounds.getWidth() / 2,
                                                                      boxBounds.getMinY(),
                                                                      boxBounds.getMaxX() - lowerWallBounds.getMaxX() + lowerWallBounds.getWidth() / 2,
                                                                      boxBounds.getHeight() ) );
    }

    /**
     * A text graphic for the counter readouts
     */
    private class ReadoutGraphic extends CompositePhetGraphic implements SimpleObserver {
        private ParticleCounter counter;
        private PhetTextGraphic labelGraphic;
        private PhetTextGraphic readout;
        private PhetShapeGraphic border;
        private String label;

        public ReadoutGraphic( ParticleCounter counter, String label ) {
            super( getApparatusPanel() );
            this.label = label;
            labelGraphic = new PhetTextGraphic( getApparatusPanel(), readoutFont, label, Color.black);
            this.addGraphic( labelGraphic, 10 );
            readout = new PhetTextGraphic( getApparatusPanel(), readoutFont, "", Color.black );
            this.addGraphic( readout, 10 );
            border = new PhetShapeGraphic( getApparatusPanel(), new Rectangle( 40, 15 ), Color.white, new BasicStroke( 1f ), Color.black );
            this.addGraphic( border, 5 );
            border.setLocation( 20, 0);
            counter.addObserver( this );
            this.counter = counter;
            update();
        }

        public void paint( Graphics2D g2 ) {
            FontRenderContext frc = g2.getFontRenderContext();
            border.setLocation( (int)( readoutFont.getStringBounds( label, frc ).getMaxX() + 5 ), 0 );
            readout.setLocation( (int)( readoutFont.getStringBounds( label, frc ).getMaxX() + 8 ), 0 );
            super.paint( g2 );
        }

        public void update() {
            readout.setText( Integer.toString( counter.getCnt() ) );
            setBoundsDirty();
            repaint();
        }
    }
}
