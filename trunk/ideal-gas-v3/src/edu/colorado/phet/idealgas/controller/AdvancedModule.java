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
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.HeavySpeciesGraphic;
import edu.colorado.phet.idealgas.view.LightSpeciesGraphic;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * AdvancedModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AdvancedModule extends IdealGasModule {
//    public static final Color COLOR_B = Color.orange;
    public static final Color COLOR_B = new Color( 252, 65, 40 );
//    public static final Color COLOR_B = new Color( 200, 100, 0 );
    public static final Color COLOR_A = new Color( 0, 150, 0 );

    private static Font readoutFont = new Font( "Lucida sans", Font.BOLD, 12 );
    protected Wall verticalWall;
    private ParticleCounter leftRegionParticleCounter;
    private ParticleCounter rightRegionParticleCounter;
    private Color orgLightColor;
    private Color orgHeavyColor;

    /**
     * @param clock
     * @param name
     */
    public AdvancedModule( AbstractClock clock, String name ) {
        super( clock, name );
        init();
    }

    /**
     * @param clock
     * @param s
     * @param model
     */
    public AdvancedModule( AbstractClock clock, String s, PChemModel model ) {
        super( clock, s, model );
        init();
    }

    private void init() {

        // We can only use the top pressure-sensing slice because we don't know where the
        // floors will be
        getBox().setMultipleSlicesEnabled( false );

        Box2DGraphic boxGraphic = getBoxGraphic();
        boxGraphic.setIgnoreMouse( true );
        boxGraphic.removeAllMouseInputListeners();

        // Set the two types of particles so they are the same mass and radius
        LightSpecies.setMoleculeMass( HeavySpecies.getMoleculeMass() );
        LightSpecies.setMoleculeRadius( HeavySpecies.getMoleculeRadius() );
    }

    /**
     * Add elements that keep count of the number of particles on either side of the vertical wall
     */
    protected void addParticleCounters( String text1, String text2 ) {
        Rectangle2D boxBounds = getBox().getBoundsInternal();

        // Create the particle counters
        leftRegionParticleCounter = new ParticleCounter( getIdealGasModel() );
        getModel().addModelElement( leftRegionParticleCounter );

        rightRegionParticleCounter = new ParticleCounter( getIdealGasModel() );
        getModel().addModelElement( rightRegionParticleCounter );

        // Set the bounds of the regions the particle counters cound
        setParticleCounterRegions();

        // Put readouts on the apparatus panel
        PhetGraphic leftCounterReadout = new ReadoutGraphic( leftRegionParticleCounter, SimStrings.get( "AdvancedModule.Count" ) + ": " );
        leftCounterReadout.setLocation( (int)boxBounds.getMinX() + 0, (int)boxBounds.getMaxY() + 7 );
        addGraphic( leftCounterReadout, IdealGasConfig.READOUT_LAYER );

        PhetGraphic rightCounterReadout = new ReadoutGraphic( rightRegionParticleCounter, SimStrings.get( "AdvancedModule.Count" ) + ": " );
        rightCounterReadout.setLocation( (int)boxBounds.getMaxX() - 110, (int)boxBounds.getMaxY() + 7 );
        addGraphic( rightCounterReadout, IdealGasConfig.READOUT_LAYER );

        // Put Text graphics above the box that indicate the reactants and products
        PhetTextGraphic leftTextGraphic = new CounterLetter( getApparatusPanel(), readoutFont, text1, COLOR_A, leftRegionParticleCounter );
        leftTextGraphic.setLocation( (int)boxBounds.getMinX() + 50, (int)boxBounds.getMinY() - 30 );
        addGraphic( leftTextGraphic, IdealGasConfig.READOUT_LAYER );
        PhetTextGraphic rightTextGraphic = new CounterLetter( getApparatusPanel(), readoutFont, text2, COLOR_B, rightRegionParticleCounter );
        rightTextGraphic.setLocation( (int)boxBounds.getMaxX() - 60, (int)boxBounds.getMinY() - 30 );
        addGraphic( rightTextGraphic, IdealGasConfig.READOUT_LAYER );

        // Add a pair of arrows that point from one character to the other
        double arrowThickness = 2;
        double headMultiplier = 5;
        Arrow lrArrow = new Arrow( new Point2D.Double( leftTextGraphic.getLocation().x + 50, leftTextGraphic.getLocation().y - 20 ),
                                   new Point2D.Double( rightTextGraphic.getLocation().x - 50, rightTextGraphic.getLocation().y - 20 ),
                                   arrowThickness * headMultiplier, arrowThickness * headMultiplier, arrowThickness );
        Arrow rlArrow = new Arrow( new Point2D.Double( rightTextGraphic.getLocation().x - 50, rightTextGraphic.getLocation().y - 0  ),
                                   new Point2D.Double( leftTextGraphic.getLocation().x + 50, leftTextGraphic.getLocation().y - 0  ),
                                   arrowThickness * headMultiplier, arrowThickness * headMultiplier, arrowThickness );
        addGraphic( new PhetShapeGraphic( getApparatusPanel(), lrArrow.getShape(), Color.black ), IdealGasConfig.READOUT_LAYER );
        addGraphic( new PhetShapeGraphic( getApparatusPanel(), rlArrow.getShape(), Color.black ), IdealGasConfig.READOUT_LAYER );
    }

    /**
     * Specifies the labels for the types of particles in the simulation
     *
     * @return
     */
    protected String[] getSpeciesNames() {
        return new String[]{SimStrings.get( "AdvancedModule.Particle_Type_A" ),
                            SimStrings.get( "AdvancedModule.Particle_Type_B" )};
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
            border = new PhetShapeGraphic( getApparatusPanel(), new Rectangle( 40, 20 ), Color.white, new BasicStroke( 1f ), Color.black );
            this.label = label;
            labelGraphic = new PhetTextGraphic( getApparatusPanel(), readoutFont, label, Color.black );
            this.addGraphic( labelGraphic, 10 );
            readout = new PhetTextGraphic( getApparatusPanel(), readoutFont, "", Color.black );
            this.addGraphic( readout, 10 );
            this.addGraphic( border, 5 );
            border.setLocation( 20, 0 );
            counter.addObserver( this );
            this.counter = counter;
            update();
        }

        public void paint( Graphics2D g2 ) {
            FontRenderContext frc = g2.getFontRenderContext();
            border.setLocation( (int)( readoutFont.getStringBounds( label, frc ).getMaxX() + 5 ), 0 );
            readout.setLocation( (int)( getBounds().getWidth() - 5 ), (int)border.getBounds().getHeight() / 2 );
            readout.setJustification( PhetTextGraphic.EAST );
            labelGraphic.setLocation( 0, (int)border.getBounds().getHeight() / 2 );
            labelGraphic.setJustification( PhetTextGraphic.WEST );
            super.paint( g2 );
        }

        public void update() {
            readout.setText( Integer.toString( counter.getCnt() ) );
            setBoundsDirty();
            repaint();
        }
    }

    /**
     * A text string that grows and shrinks with the number of particles reported by a ParticleCounter
     */
    private class CounterLetter extends PhetTextGraphic implements SimpleObserver {
        private ParticleCounter particleCounter;

        public CounterLetter( Component component, Font font, String text, Color color, ParticleCounter particleCounter ) {
            super( component, font, text, color );
            this.particleCounter = particleCounter;
            particleCounter.addObserver( this );
            setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            setJustification( PhetTextGraphic.SOUTH );
//            setJustification( PhetTextGraphic.CENTER );
        }

        public void update() {
            int size = Math.max( 12, particleCounter.getCnt() );
            Font font = new Font( "Lucida sans", Font.BOLD, size );
            this.setFont( font );
        }
    }


    public void activate( PhetApplication app ) {
        super.activate( app );
        // Set the colors of the particle graphics
        orgLightColor = LightSpeciesGraphic.getColor();
        orgHeavyColor = HeavySpeciesGraphic.getColor();
        LightSpeciesGraphic.setColor( COLOR_B );
        HeavySpeciesGraphic.setColor( COLOR_A );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        LightSpeciesGraphic.setColor( orgLightColor );
        HeavySpeciesGraphic.setColor( orgHeavyColor );
    }

    //----------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------

    abstract public Pump[] getPumps();


}
