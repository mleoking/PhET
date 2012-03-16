// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;
import edu.colorado.phet.opticalquantumcontrol.module.OQCModule;


/**
 * MoleculeAnimation is the animation of a molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MoleculeAnimation extends CompositePhetGraphic implements ModelElement, SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Dimension BACKGROUND_SIZE = new Dimension( 250, 250 );

    private static final double MAX_DISTANCE = 10;  // distance in pixels we would move if closeness=1

    private static final double EXPLOSION_ACCELERATION_RATE = 0.10;

    private static final Point MOLECULE_POINT = new Point( -70, 300 );

    private static final float DASH_SIZE = 3f;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private OQCModule _module;
    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;

    // Graphics
    private PhetShapeGraphic _animationFrame;
    private CompositePhetGraphic _moleculeGraphic;
    private PhetImageGraphic _moleculePart1;
    private PhetImageGraphic _moleculePart2;
    private PhetImageGraphic _moleculePart3;
    private HTMLGraphic _closenessGraphic;
    private String _closenessFormat;
    private PhetImageGraphic _explosionGraphic;
    private Point _moleculeHome; // starting point for the graphics in the animation

    // State information
    private double _closeness; // how close we are to matching
    private Random _random; // random number generator
    private double _dx1, _dy1; // animation deltas for part1 of the molecule graphic
    private double _dx2, _dy2; // animation deltas for part2 of the molecule graphic
    private double _dx3, _dy3; // animation deltas for part3 of the molecule graphic
    private boolean _isExploding; // true means that we're in the middle of animating the explosion
    private boolean _animationDone; // true means that the animation is done
    private boolean _isAdjusting; // true means that we should ignore model updates

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component
     * @param module
     * @param userFourierSeries
     * @param outputFourierSeries
     */
    public MoleculeAnimation( Component component, OQCModule module,
            FourierSeries userFourierSeries, FourierSeries outputFourierSeries ) {
        super( component );

        _module = module;
        _userFourierSeries = userFourierSeries;
        _outputFourierSeries = outputFourierSeries;

        _userFourierSeries.addObserver( this );
        _outputFourierSeries.addObserver( this );

        setIgnoreMouse( true );

        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _random = new Random();
        _isAdjusting = false;

        // background that everything sits on
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        background.setColor(  new Color( 215, 215, 215 ) );
        addGraphic( background );

        // Visual cues to indicate that this is a "zoomed in" view.
        {
            // Dashed line strokes
            float[] dashArray = { DASH_SIZE, DASH_SIZE };
            BasicStroke whiteDashStroke = new BasicStroke( 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, dashArray, 0f );
            BasicStroke blackDashStroke = new BasicStroke( 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, dashArray, DASH_SIZE );

            /*
             * 2 lines starting at the top-level corner,
             * the inverse of each other so they show up on all backgrounds.
             */
            {
                GeneralPath topLineShape = new GeneralPath();
                topLineShape.moveTo( 0, 0 );
                topLineShape.lineTo( MOLECULE_POINT.x, MOLECULE_POINT.y );

                PhetShapeGraphic topWhiteDashedLine = new PhetShapeGraphic( component );
                topWhiteDashedLine.setShape( topLineShape );
                topWhiteDashedLine.setColor( Color.WHITE );
                topWhiteDashedLine.setStroke( whiteDashStroke );

                addGraphic( topWhiteDashedLine );
                PhetShapeGraphic topBlackDashedLine = new PhetShapeGraphic( component );
                topBlackDashedLine.setShape( topLineShape );
                topBlackDashedLine.setColor( Color.BLACK );
                topBlackDashedLine.setStroke( blackDashStroke );
                addGraphic( topBlackDashedLine );
            }

            /*
             * 2 lines starting at the bottom-right corner,
             * the inverse of each other so they show up on all backgrounds.
             */
            {
                GeneralPath bottomLineShape = new GeneralPath();
                bottomLineShape.moveTo( BACKGROUND_SIZE.width, BACKGROUND_SIZE.height );
                bottomLineShape.lineTo( MOLECULE_POINT.x, MOLECULE_POINT.y );

                PhetShapeGraphic bottomWhiteDashedLine = new PhetShapeGraphic( component );
                bottomWhiteDashedLine.setShape( bottomLineShape );
                bottomWhiteDashedLine.setColor( Color.WHITE );
                bottomWhiteDashedLine.setStroke( whiteDashStroke );
                addGraphic( bottomWhiteDashedLine );

                PhetShapeGraphic bottomBlackDashedLine = new PhetShapeGraphic( component );
                bottomBlackDashedLine.setShape( bottomLineShape );
                bottomBlackDashedLine.setColor( Color.BLACK );
                bottomBlackDashedLine.setStroke( blackDashStroke );
                addGraphic( bottomBlackDashedLine );
            }

            /*
             * Tiny rectangle that indicates where the molecule actually is.
             * All of the above lines terminate at the center of this rectangle.
             */
            Rectangle2D r = new Rectangle2D.Double( 0, 0, 8, 8 );
            PhetShapeGraphic tinyMolecule = new PhetShapeGraphic( component );
            tinyMolecule.setShape( r );
            tinyMolecule.setStroke( new BasicStroke( 1f ) );
            tinyMolecule.setBorderColor( Color.WHITE );
            tinyMolecule.centerRegistrationPoint();
            tinyMolecule.setLocation( MOLECULE_POINT );
            addGraphic( tinyMolecule );

            // Magnifying glass, lens centered on the tiny rectangle
            MagnifyingGlass magnifyingGlass = new MagnifyingGlass( component );
            magnifyingGlass.setLocation( MOLECULE_POINT.x + 31, MOLECULE_POINT.y + 2 );
            magnifyingGlass.rotate( Math.toRadians( 140 ) );
            addGraphic( magnifyingGlass );
        }

        // The frame around the molecule animation
        _animationFrame = new PhetShapeGraphic( component );
        Rectangle frameShape = new Rectangle( 0, 0, BACKGROUND_SIZE.width - 30, BACKGROUND_SIZE.height - 45 );
        _animationFrame.setShape( frameShape );
        _animationFrame.setColor( Color.BLACK );
        _animationFrame.setBorderColor( Color.BLACK );
        _animationFrame.setStroke( new BasicStroke(1f) );
        addGraphic( _animationFrame );
        _animationFrame.setRegistrationPoint( _animationFrame.getWidth()/2, 0 ); // top center
        _animationFrame.setLocation( BACKGROUND_SIZE.width/2, 15 );

        // The read-out that shows how "close" we are to matching the output pulse.
        _closenessGraphic = new HTMLGraphic( component );
        _closenessGraphic.setColor( Color.BLACK );
        _closenessGraphic.setFont( new PhetFont( Font.PLAIN, 18 ) );
        _closenessFormat = OQCResources.CLOSENESS_READOUT;
        Object[] args = { "-000" };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
        addGraphic( _closenessGraphic );
        _closenessGraphic.setRegistrationPoint( _closenessGraphic.getWidth()/2, _closenessGraphic.getHeight() ); // bottom center
        _closenessGraphic.setLocation( BACKGROUND_SIZE.width/2, BACKGROUND_SIZE.height - 5 );

        // explosion (Kaboom!)
        _explosionGraphic = new PhetImageGraphic( component, OQCResources.KABOOM_IMAGE );
        _explosionGraphic.setRegistrationPoint( _explosionGraphic.getWidth()/2, 0 ); // top center
        _explosionGraphic.setLocation( _animationFrame.getLocation() );
        addGraphic( _explosionGraphic );

        // molecule
        _moleculeGraphic = new CompositePhetGraphic( component );
        {
            _moleculePart1 = new PhetImageGraphic( component );
            _moleculePart2 = new PhetImageGraphic( component );
            _moleculePart3 = new PhetImageGraphic( component );
            _moleculeGraphic.addGraphic( _moleculePart1 );
            _moleculeGraphic.addGraphic( _moleculePart2 );
            _moleculeGraphic.addGraphic( _moleculePart3 );
        }
        _moleculeHome = new Point( 30, 15 );
        _moleculeGraphic.setLocation( _moleculeHome );
        addGraphic( _moleculeGraphic );

        reset();   // put the animation at t=0
        update();  // sync with the model
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
        _outputFourierSeries.removeObserver( this );
        _outputFourierSeries = null;
    }

    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------

    /**
     * Resets the animation.
     */
    public void reset() {
        _animationDone = false;
        _isExploding = false;
        setCloseness( 0 );
        _explosionGraphic.setVisible( false );
        _moleculeGraphic.setLocation( _moleculeHome );
        _moleculePart1.setLocation( 0, 0 );
        _moleculePart2.setLocation( 0, 0 );
        _moleculePart3.setLocation( 0, 0 );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the molecule graphic.
     *
     * @param index
     */
    public void setMolecule( int index ) {
        BufferedImage image1 = OQCResources.getImage( "molecule" + index + "_part1.png" );
        BufferedImage image2 = OQCResources.getImage( "molecule" + index + "_part2.png" );
        BufferedImage image3 = OQCResources.getImage( "molecule" + index + "_part3.png" );
        _moleculePart1.setImage( image1 );
        _moleculePart2.setImage( image2 );
        _moleculePart3.setImage( image3 );
    }

    /**
     * Enables or disables clipping of the molecules animation.
     * If clipping is enabled, then the molecule will be drawn
     * only inside the animation frame.
     */
    public void setClipEnabled( boolean enabled ) {
        if ( enabled ) {
            Shape clip = _animationFrame.getNetTransform().createTransformedShape( _animationFrame.getShape() );
            _moleculeGraphic.setClip( clip );
        }
        else {
            _moleculeGraphic.setClip( null );
        }
    }

    /*
     * Sets the "closeness", which determines how fast the molecule
     * is vibrating, and what value appears in the closeness read-out.
     *
     * @param closeness 1=exact match
     */
    private void setCloseness( double closeness ) {

        _closeness = closeness;

        int percent = (int)( 100 * closeness );
        Object[] args = { new Integer( percent ) };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /**
     * Animates the molecule when the clock ticks.
     */
    public void stepInTime( double dt ) {

        if ( _animationDone ) {
            // Don't do anything if the animation is done.
        }
        else if ( _isExploding ) {
            /*
             * We're in the middle of the explosion, so advance to the next animation frame.
             */

            // Advance each part of the molecule graphic.
            _moleculePart1.setLocation( (int) ( _moleculePart1.getX() + _dx1 ), (int) ( _moleculePart1.getY() + _dy1 ) );
            _moleculePart2.setLocation( (int) ( _moleculePart2.getX() + _dx2 ), (int) ( _moleculePart2.getY() + _dy2 ) );
            _moleculePart3.setLocation( (int) ( _moleculePart3.getX() + _dx3 ), (int) ( _moleculePart3.getY() + _dy3 ) );

            // Accelerate
            final double a = EXPLOSION_ACCELERATION_RATE;
            _dx1 += _dx1 * a;
            _dy1 += _dy1 * a;
            _dx2 += _dx2 * a;
            _dy2 += _dy2 * a;
            _dx3 += _dx3 * a;
            _dy3 += _dy3 * a;

            // Check to see if we're done with the animation.
            if ( _moleculeGraphic.getClip() != null ) {
                // Are we still visbile in the animation frame?
                if ( Math.abs( _moleculePart1.getX() ) > 2 * _animationFrame.getWidth() && Math.abs( _moleculePart1.getY() ) > 2 * _animationFrame.getHeight() ) {
                    _animationDone = true; // animation is done
                    gameOver();
                }
            }
            else {
                // Are we still visible in the apparatus panel?
                if ( Math.abs( _moleculePart1.getX() ) > 2 * OQCConstants.FRAME_WIDTH && Math.abs( _moleculePart1.getY() ) > 2 * OQCConstants.FRAME_HEIGHT ) {
                    _animationDone = true; // animation is done
                    gameOver();
                }
            }
        }
        else if ( _closeness >= OQCConstants.CLOSENESS_MATCH ) {
            /*
             * We're close enough to be considered a "match", so start the explosion.
             * Make the 3 parts of the molecule graphic move away from each other
             * in random directions.
             */
            _explosionGraphic.setVisible( true );
            _isExploding = true;
            double theta1 = Math.random() * Math.PI * 2;
            double theta2 = theta1 + Math.toRadians( 120 );
            double theta3 = theta2 + Math.toRadians( 120 );
            _dx1 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta1 );
            _dy1 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta1 );
            _dx2 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta2 );
            _dy2 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta2 );
            _dx3 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta3 );
            _dy3 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta3 );
        }
        else if ( _closeness > 0 ) {
            /*
             * We're not close enough to be considered a "match",
             * so randomly move the molecule around so that it appears
             * to vibrate in a way that is related to the closeness.
             */
            double d = ( _random.nextGaussian() * _closeness * _closeness * MAX_DISTANCE ) * ( Math.random() > 0.5 ? 1 : -1 );
            double theta = Math.random() * Math.PI * 2;
            double dx = d * Math.cos( theta );
            double dy = d * Math.sin( theta );
            _moleculeGraphic.setLocation( (int) ( _moleculeHome.x + dx ), (int) ( _moleculeHome.y + dy ) );
        }
    }

    /*
     * Encapsulates everything that should be done when the game is over.
     * We open a dialog telling the user that they've matched the output pulse.
     * When the dialog is closed, we ask the module to start a new "game".
     */
    private void gameOver() {

        _isAdjusting = true;

        // Tell the user they won.
        JFrame frame = PhetApplication.getInstance().getPhetFrame();
        String message = OQCResources.WIN_DIALOG_MESSAGE;
        String title = OQCResources.WIN_DIALOG_TITLE;
        PhetOptionPane.showMessageDialog( frame, message, title, JOptionPane.PLAIN_MESSAGE );

        // Start a new "game".
        _module.newGame();

        _isAdjusting = false;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates this graphic to match the Fourier series that it is observing.
     */
    public void update() {

        if ( !_isAdjusting && !_isExploding ) {

            /*
             * Compare the Fourier series.
             *
             * The calculation for "closeness" is:
             *
             *     closeness = 1 - ( Math.sqrt( (U1-D1)^2 + (U2-D2)^2 + ...) / Math.sqrt( ( 1+abs(D1))^2 + (1+abs(D2))^2 + ... ) )
             *
             * where:
             *     Un is the user's amplitude for component n
             *     Dn is the desired amplitude for component n
             */
            double numerator = 0;
            double denominator = 0;
            int numberOfHarmonics = _userFourierSeries.getNumberOfHarmonics();
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                double userAmplitude = _userFourierSeries.getHarmonic( i ).getAmplitude();
                double outputAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
                numerator += Math.pow( Math.abs( userAmplitude - outputAmplitude ), 2 );
                denominator += Math.pow( 1 + Math.abs( outputAmplitude ), 2 );
            }

            if ( denominator != 0 ) {
                double closeness = 1.0 - ( Math.sqrt( numerator ) / Math.sqrt( denominator ) );

                // Update the animation
                setCloseness( closeness );

                /*
                 * WORKAROUND: If we have a match, make sure that all
                 * other views are updated before the molecule animation
                 * happens and the "you've won" dialog is shown.
                 */
                if ( closeness >= OQCConstants.CLOSENESS_MATCH ) {
                    _userFourierSeries.removeObserver( this );
                    _userFourierSeries.notifyObservers();
                    _userFourierSeries.addObserver( this );
                }
            }
        }
    }
}
