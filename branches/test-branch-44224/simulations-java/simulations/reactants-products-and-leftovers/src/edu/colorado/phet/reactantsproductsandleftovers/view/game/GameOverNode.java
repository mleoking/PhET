/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.util.TimeUtils;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Upon completion of a Game, this node is used to display a summary of the user's game results.
 * <p>
 * Note that this was originally implemented using a JPanel, wrapped with PSwing.
 * But some problems with PSwing bounds (see #2219) forced a rewrite.
 * Layout is now done using node offsets, PText is used instead of JLabels,
 * and the background is a PPath instead of a JPanel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameOverNode extends PhetPNode {
    
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final Font LABEL_FONT = new PhetFont( 18 );
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final double MIN_SEPARATOR_WIDTH = 175;
    private static final double X_MARGIN = 25;
    private static final double Y_MARGIN = 15;
    private static final double Y_SPACING = 15;
    
    public GameOverNode( final GameModel model ) {
        super();
        
        // title
        PText titleNode = new PText( RPALStrings.TITLE_GAME_OVER ); 
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );
        
        // level
        PText levelNode = new PText( getLevelString( model ) );
        levelNode.setFont( LABEL_FONT );
        addChild( levelNode );
        
        // score
        PText scoreNode = new PText( getScoreString( model ) );
        scoreNode.setFont( LABEL_FONT );
        addChild( scoreNode );
        
        // time
        PText timeNode = new PText( getTimeString( model ) ); 
        timeNode.setFont( LABEL_FONT );
        addChild( timeNode );
        
        // buttons
        JButton newGameButton = new JButton( RPALStrings.BUTTON_NEW_GAME );
        newGameButton.setOpaque( false );
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque( false );
        buttonPanel.add( newGameButton );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.newGame();
            }
        });
        PSwing newGameButtonWrapper = new PSwing( newGameButton );
        newGameButtonWrapper.addInputEventListener( new CursorHandler() );
        addChild( newGameButtonWrapper );
       
        // horizontal separators, compute width after adding other children
        final double separatorWidth = Math.max( PNodeLayoutUtils.getMaxFullWidthChildren( this ), MIN_SEPARATOR_WIDTH );
        PPath separatorNode1 = new PPath( new Line2D.Double( 0, 0, separatorWidth, 0 ) );
        addChild( separatorNode1 );
        PPath separatorNode2 = new PPath( new Line2D.Double( 0, 0, separatorWidth, 0 ) );
        addChild( separatorNode2 );
        
        // create the background, after adding all other stuff
        final double backgroundWidth = separatorWidth + ( 2 * X_MARGIN );
        final double backgroundHeight = PNodeLayoutUtils.sumFullHeightsChildren( this ) + ( Y_SPACING * ( getChildrenCount() - 1 ) ) + ( 2 * Y_MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight ) );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        addChild( backgroundNode );
        backgroundNode.moveToBack();
        
        // layout
        double x = 0;
        double y = 0;
        backgroundNode.setOffset( x, y );
        // title centered at top
        x = ( backgroundNode.getFullBoundsReference().getWidth() - titleNode.getFullBoundsReference().getWidth() ) / 2;
        y = backgroundNode.getFullBoundsReference().getMinY() + Y_MARGIN;
        titleNode.setOffset( x, y );
        // separator centered below title
        x = ( backgroundNode.getFullBoundsReference().getWidth() - separatorNode1.getFullBoundsReference().getWidth() ) / 2;
        y = titleNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        separatorNode1.setOffset( x, y );
        // level, score and time left justified below separator
        y = separatorNode1.getFullBoundsReference().getMaxY() + Y_SPACING;
        levelNode.setOffset( x, y );
        y = levelNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        scoreNode.setOffset( x, y );
        y = scoreNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        timeNode.setOffset( x, y );
        // separator centered below level, score and time
        y = timeNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        separatorNode2.setOffset( x, y );
        // button centered below separator
        x = ( backgroundNode.getFullBoundsReference().getWidth() - newGameButtonWrapper.getFullBoundsReference().getWidth() ) / 2;
        y = separatorNode2.getFullBoundsReference().getMaxY() + Y_SPACING;
        newGameButtonWrapper.setOffset( x, y );
    }
    
    /*
     * Gets the level string.
     */
    private static String getLevelString( GameModel model ) {
        return MessageFormat.format( RPALStrings.LABEL_LEVEL, String.valueOf( model.getLevel() ) );
    }
    
    /*
     * Gets the score string.
     * If we had a perfect score, indicate that.
     */
    private static String getScoreString( GameModel model ) {
        String pointsString = POINTS_FORMAT.format( model.getPoints() );
        String perfectScoreString = POINTS_FORMAT.format( GameModel.getPerfectScore() );
        String scoreString = null;
        if ( model.isPerfectScore() ) {
            scoreString = MessageFormat.format( RPALStrings.LABEL_SCORE_PERFECT, pointsString, perfectScoreString );
        }
        else {
            scoreString = MessageFormat.format( RPALStrings.LABEL_SCORE_IMPERFECT, pointsString, perfectScoreString );
        }
        return scoreString;
    }
    
    /*
     * Gets the time string.
     * If we had an imperfect score, simply show the time.
     * If we had a perfect score, show the best time, and indicate if the time was a "new best".
     */
    private static String getTimeString( GameModel model ) {
        String s = " ";
        if ( model.isTimerVisible() ) {
            // Time: 0:29
            String timeString = MessageFormat.format( RPALStrings.LABEL_TIME, TimeUtils.getTimeString( model.getTime() ) );
            if ( !model.isPerfectScore() ) {
                // Time: 0:29
                s = timeString;
            }
            else if ( model.isNewBestTime() ) {
                // Time: 0:29 (NEW BEST!)
                s = MessageFormat.format( RPALStrings.FORMAT_TIME_BEST, timeString, RPALStrings.LABEL_NEW_BEST );
            }
            else {
                // (Best: 0:20)
                String bestTimeString = MessageFormat.format( RPALStrings.LABEL_BEST, TimeUtils.getTimeString( model.getBestTime() ) );
                // Time: 0:29 (Best: 0:20)
                s = MessageFormat.format( RPALStrings.FORMAT_TIME_BEST, timeString, bestTimeString );
            }
        }
        return s;
    }
}
