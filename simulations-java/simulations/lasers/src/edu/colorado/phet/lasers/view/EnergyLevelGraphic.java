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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Beam;
import edu.colorado.phet.lasers.LasersResources;

/**
 * An interactive graphic that represents an energy level for a type of atom. It can be moved up and down with the
 * mouse, and it's range of movement is limited by the energy levels of the next higher and next lower energy states.
 */
public class EnergyLevelGraphic extends CompositePhetGraphic implements EnergyMatchDetector.Listener {
    private AtomicState atomicState;
    private double groundStateEnergy;
    private boolean isAdjustable;
    private double iconLocX;
    private boolean clampTopWorkaround;
    private Color color;
    private double x;
    private double width;
    private EnergyLevelRep energyLevelRep;
    // This transform controls the y location of the line
    private ModelViewTransform1D energyYTx;
    private boolean arrowsEnabled = true;
    // The fewest pixels that will be allowed between energy levels;
    private int minPixelsBetweenLevels = EnergyLifetimeSlider.sliderHeight;

    // Strategy for setting to color of this energy level graphic
    private ColorStrategy colorStrategy = new VisibleColorStrategy();
    private LevelIcon levelIcon;

    public static boolean showLifetimeLabelText = true;//see Unfuddle #431

    /*
     * @param isAdjustable      Determines if the graphic can be moved up and down with the mouse
     */
    public EnergyLevelGraphic( Component component, AtomicState atomicState, double groundStateEnergy, double xLoc, double width,
                               boolean isAdjustable, double iconLocX, boolean clampTopWorkaround ) {
        super( null );

        this.atomicState = atomicState;
        this.groundStateEnergy = groundStateEnergy;
        this.isAdjustable = isAdjustable;
        this.iconLocX = iconLocX;

        //workaround for Unfuddle #571 https://phet.unfuddle.com/projects/9404/tickets/by_number/571
        this.clampTopWorkaround = clampTopWorkaround;

        // Add a listener that will track changes in the atomic state
        atomicState.addListener( new AtomicStateChangeListener() );

        this.x = xLoc;
        this.width = width;
        energyLevelRep = new EnergyLevelRep( component );
        addGraphic( energyLevelRep );

        if ( this.isAdjustable ) {
            setCursorHand();
            addTranslationListener( new EnergyLevelTranslator() );
            addMouseInputListener( new MouseInputAdapter() {
                // implements java.awt.event.MouseListener
                public void mouseReleased( MouseEvent e ) {
                    handleSnapTo();
                }
            } );
        }
    }

    private void handleSnapTo() {
        Set set = matchTable.keySet();
        for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Beam o = (Beam) iterator.next();
            MatchState matchState = (MatchState) matchTable.get( o );
            if ( matchState.isMatch() ) {
                setEnergy( matchState.getMatchingEnergy() );
                break;
            }
        }
    }

    public void setTransform( ModelViewTransform1D tx ) {
        this.energyYTx = tx;
        energyLevelRep.update();
    }

    public Point2D getPosition() {
        return energyLevelRep.getBounds().getLocation();
    }

    public Point2D getLinePosition() {
        return energyLevelRep.getLinePosition();
    }

    public void setArrowsEnabled( boolean arrowsEnabled ) {
        this.arrowsEnabled = arrowsEnabled;
    }

    public void setLevelIcon( LevelIcon levelIcon ) {
        this.levelIcon = levelIcon;
        levelIcon.setCursorHand();
        energyLevelRep.setLevelIcon( levelIcon );
    }

    public void setMinPixelsBetweenLevels( int minPixelsBetweenLevels ) {
        this.minPixelsBetweenLevels = minPixelsBetweenLevels;
    }

    /*
     * Sets the strategy used to pick the color for the graphic
     */
    public void setColorStrategy( ColorStrategy colorStrategy ) {
        this.colorStrategy = colorStrategy;
    }

    HashMap matchTable = new HashMap();

    public void putMatch( Beam beam, MatchState matchState ) {
        matchTable.put( beam, matchState );
        energyLevelRep.update();
        repaint();
    }

    public long getLastMatchTime() {
        Set keys = matchTable.keySet();
        long latestMatch = 0;
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            MatchState value = (MatchState) matchTable.get( o );
            if ( value.isMatch() && value.getTime() > latestMatch ) {
                latestMatch = value.getTime();
            }
        }
        return latestMatch;
    }

    public static void setBlinkRenderer( Color color ) {
        for ( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep energyLevelRep = (EnergyLevelRep) instances.get( i );
            energyLevelRep.setBlinkRenderer( color );
        }
    }

    public static void setFlowRenderer() {
        for ( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep energyLevelRep = (EnergyLevelRep) instances.get( i );
            energyLevelRep.setFlowRenderer();
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Updates the graphic when the energy level of the atomic state changes
     */
    private class AtomicStateChangeListener extends AtomicState.ChangeListenerAdapter {
        public void energyLevelChanged( AtomicState.Event event ) {
            energyLevelRep.update();
            levelIcon.updateEnergy( event.getEnergy() );
        }
    }

    /**
     * Handles translation of the graphic
     */
    private class EnergyLevelTranslator implements TranslationListener {

        public void translationOccurred( TranslationEvent translationEvent ) {
            int dy = translationEvent.getDy();
            double energyChange = energyYTx.viewToModelDifferential( dy );

            // Don't let one level get closer than a certain number of pixels to the one above or below
            double minEnergyDifference = energyYTx.viewToModelDifferential( -minPixelsBetweenLevels );
            final double upperBound = atomicState.getNextHigherEnergyState().getEnergyLevel() - minEnergyDifference;
            final double lowerBound = atomicState.getNextLowerEnergyState().getEnergyLevel() + minEnergyDifference;
            final double desiredValue = atomicState.getEnergyLevel() + energyChange;
            double newEnergy = MathUtil.clamp( lowerBound, desiredValue, upperBound );

            double screenTop = PhysicsUtil.wavelengthToEnergy( QuantumConfig.MIN_WAVELENGTH ) + groundStateEnergy;
//            System.out.println( "newEnergy = " + newEnergy +", des="+desiredValue+", upperbound="+upperBound+" screenTop="+screenTop);

            //prevent the energy level from being dragged off the top of the page
            //necessary in lasers, causes problems in discharge lamps
            if ( clampTopWorkaround ) {
                newEnergy = Math.min( newEnergy, screenTop );
            }
            setEnergy( newEnergy );
        }

    }

    private void setEnergy( double newEnergy ) {
        atomicState.setEnergyLevel( newEnergy );
        atomicState.determineEmittedPhotonWavelength();

        // Update the atom icon
        levelIcon.updateEnergy( newEnergy );
    }

    public static final ArrayList instances = new ArrayList();

    public static void setRenderer( RenderStrategy renderStrategy ) {
        for ( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep levelRep = (EnergyLevelRep) instances.get( i );
            levelRep.setRenderer( renderStrategy );
        }
    }

    /**
     * The graphic class itself
     */
    public class EnergyLevelRep extends CompositePhetGraphic {

        private Rectangle2D energyLevelShape = new Rectangle2D.Double();
        private double height = 2;
        private Arrow arrow1;
        private Arrow arrow2;
        private Rectangle boundingRect;
        private LevelIcon levelIcon;
        private PhetTextGraphic2 textGraphic;

        protected EnergyLevelRep( Component component ) {
            super( component );
            PhetShapeGraphic lineGraphic = new PhetShapeGraphic( component, energyLevelShape, color );
            lineGraphic.setVisible( true );
            addGraphic( lineGraphic );
            instances.add( this );

        }

        public void setRenderer( RenderStrategy renderStrategy ) {
            this.strategy = renderStrategy;
            update();
        }

        private void update() {
            color = colorStrategy.getColor( atomicState, groundStateEnergy );

            // We need to create a new color that can't be transparent. VisibleColor will return
            // an "invisible" color with RGB = 0,0,0 if the wavelenght is not visible. And since our
            // ground state has a wavelength that is below visible, and we want a black line, this
            // is the best hack to use.
            color = new Color( color.getRed(), color.getGreen(), color.getBlue() );
            int y = energyYTx.modelToView( atomicState.getEnergyLevel() );

            //todo: not sure why this was off by 1 pixel
//            energyLevelShape.setRect( x, y - thickness / 2, width, thickness );

            //todo: this one seems to work properly
            energyLevelShape.setRect( x, y, width, height );

            if ( levelIcon != null ) {
                levelIcon.setLocation( (int) ( iconLocX ), (int) ( y - height ) );
            }

            if ( isAdjustable ) {
                double xOffset = width - 30;
                int arrowHt = 16;
                int arrowHeadWd = 10;
                int tailWd = 3;
                arrow1 = new Arrow( new Point2D.Double( x + xOffset, y ),
                                    new Point2D.Double( x + xOffset, y + arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
                arrow2 = new Arrow( new Point2D.Double( x + xOffset, y ),
                                    new Point2D.Double( x + xOffset, y - arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
            }
            if ( textGraphic != null ) {
                textGraphic.setLocation( (int) ( iconLocX + levelIcon.getWidth() / 2 + 6 ), (int) energyLevelShape.getY() - textGraphic.getHeight() / 2 - EnergyLifetimeSlider.sliderHeight - 2 );
            }
//            textGraphic.setLocation( (int)( iconLocX ), (int)levelLine.getY() );
            boundingRect = determineBoundsInternal();
            setBoundsDirty();
            repaint();
        }

        protected Rectangle determineBounds() {
            return boundingRect;
        }

        private Rectangle determineBoundsInternal() {
            if ( arrow1 != null && arrowsEnabled ) {
                Area a = new Area( arrow1.getShape() );
                a.add( new Area( arrow2.getShape() ) );
                a.add( new Area( energyLevelShape ) );
                return a.getBounds();
            }
            else {
                return energyLevelShape.getBounds();
            }
        }

        void setLevelIcon( LevelIcon levelIcon ) {
            this.levelIcon = levelIcon;
            addGraphic( levelIcon );
            if ( isAdjustable && showLifetimeLabelText ) {
                textGraphic = new PhetTextGraphic2( getComponent(), new PhetFont( PhetFont.getDefaultFontSize(), true ), LasersResources.getString( "EnergyLevelMonitorPanel.sliderLabel" ), Color.black );
                addGraphic( textGraphic );
            }
        }

        public boolean contains( int x, int y ) {
            return boundingRect.contains( x, y ) || levelIcon.contains( x, y );
        }

        //        RenderStrategy strategy = new FlowLine();
        //        RenderStrategy strategy = new Blink( Color.gray );
        RenderStrategy strategy = new Blink( QuantumConfig.BLINK_LINE_COLOR );

        //----------------------------------------------------------------
        // Rendering
        //----------------------------------------------------------------
        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            strategy.render( g );
            super.paint( g );
            restoreGraphicsState();
        }

        public Point2D getLinePosition() {
            return energyLevelShape.getBounds().getLocation();
        }

        public void setBlinkRenderer( Color color ) {
            setRenderer( new Blink( color ) );
        }

        public void setFlowRenderer() {
            setRenderer( new FlowLine() );
        }

        public class Blink implements RenderStrategy {
            Color targetColor;

            public Blink( Color targetColor ) {
                this.targetColor = targetColor;
            }

            public void render( Graphics2D g ) {
                if ( isAdjustable && arrowsEnabled ) {
                    g.setColor( Color.DARK_GRAY );
                    g.draw( arrow1.getShape() );
                    g.draw( arrow2.getShape() );
                }
//                boolean timeOn = ( System.currentTimeMillis() / 400 ) % 2 == 0;
//                boolean timeOn = ( System.currentTimeMillis() / 100 ) % 2 == 0;
                boolean timeOn = ( System.currentTimeMillis() / ( (long) ( QuantumConfig.FLASH_DELAY_MILLIS * 2 ) ) ) % 2 == 0;
                long lastMatchTime = getLastMatchTime();
                if ( System.currentTimeMillis() - lastMatchTime > QuantumConfig.TOTAL_FLASH_TIME ) {
                    timeOn = false;
                }
//                g.setColor( timeOn && match ? targetColor : color );
                g.setColor( timeOn ? targetColor : color );
                if ( System.currentTimeMillis() - lastMatchTime < QuantumConfig.TOTAL_FLASH_TIME ) {
                    levelIcon.setVisible( !timeOn );
                }
                else {
                    levelIcon.setVisible( true );
                }
                g.fill( energyLevelShape );
            }

        }

        public class FlowLine implements RenderStrategy {

            float phase = 0f;

            public void render( Graphics2D g ) {

                if ( isAdjustable && arrowsEnabled ) {
                    g.setColor( Color.DARK_GRAY );
                    g.draw( arrow1.getShape() );
                    g.draw( arrow2.getShape() );
                }
                g.setColor( color );
//                if( match ) {
//                    g.setStroke( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 3}, phase ) );
//                    g.draw( new Line2D.Double( levelLine.getMinX(), levelLine.getCenterY(), levelLine.getMaxX(), levelLine.getCenterY() ) );
//                    phase += 8 - 1;
//                }
//                else {
//                    g.setColor( color );
//                    g.fill( levelLine );
//                }

            }
        }
    }

    public interface RenderStrategy {
        public void render( Graphics2D g2 );
    }


    /**
     * Determines the color in which to render lines and atoms for a specified state
     */
    static public interface ColorStrategy {
        Color getColor( AtomicState state, double groundStateEnergy );
    }

    static public class VisibleColorStrategy implements ColorStrategy {
        public Color getColor( AtomicState state, double groundStateEnergy ) {
            double de = state.getEnergyLevel() - groundStateEnergy;
            return VisibleColor.wavelengthToColor( PhysicsUtil.energyToWavelength( de ) );
        }
    }

    static public class BlackStrategy implements ColorStrategy {
        public Color getColor( AtomicState state, double groundStateEnergy ) {
            return Color.black;
        }
    }
}
