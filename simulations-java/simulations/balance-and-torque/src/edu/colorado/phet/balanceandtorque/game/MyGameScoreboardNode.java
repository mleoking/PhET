// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balanceandtorque.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.EventListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.games.GameTimerFormat;
import edu.colorado.phet.common.games.GamesResources;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Scoreboard for a game.
 * This is a generalization of the scoreboard used in reactants-products-and-leftovers.
 * <p/>
 * TODO: This is a temporary version of the common class to prototype some changes.
 * This should not be retained long term.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MyGameScoreboardNode extends PhetPNode {

    // images
    private static final BufferedImage STOPWATCH_ICON = GamesResources.getImage( "blue-stopwatch.png" );

    // localized strings
    private static final String LABEL_SCORE = PhetCommonResources.getString( "Games.label.score" );
    private static final String BUTTON_NEW_GAME = PhetCommonResources.getString( "Games.button.newGame" );
    private static final String LABEL_LEVEL = PhetCommonResources.getString( "Games.label.level" );
    private static final String LABEL_BEST = PhetCommonResources.getString( "Games.label.best" );
    private static final String FORMAT_TIME_BEST = PhetCommonResources.getString( "Games.format.time.best" );
    private static final String MESSAGE_CONFIRM_NEW_GAME = PhetCommonResources.getString( "Games.message.confirmNewGame" );
    private static final String CONFIRM_TITLE = PhetCommonResources.getString( "Common.title.confirm" );

    // "look" properties
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BUTTON_COLOR = new Color( 235, 235, 235 );
    private static final int FONT_SIZE = 24;
    private static final PhetFont FONT = new PhetFont( FONT_SIZE );
    private static final PhetFont BUTTON_FONT = new PhetFont( Font.BOLD, 18 );

    // layout properties
    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 5;

    // immutable members
    private final NumberFormat pointsFormat;
    private final PText scoreNode, levelNode, timerValue;
    private final PImage timerIcon;
    private final HTMLImageButtonNode newGameButton;
    private final PPath backgroundNode;
    private final Rectangle2D backgroundShape;
    private final EventListenerList listeners;

    // mutable members
    private boolean confirmNewGame; // request confirmation when "New Game" button is pressed?

    /**
     * Constructor
     *
     * @param maxLevel     the maximum level, used to adjust the layout to accommodate the maximum width
     * @param maxScore     the maximum score, used to adjust the layout to accommodate the maximum width
     * @param pointsFormat points are displayed in this format
     */
    public MyGameScoreboardNode( int maxLevel, double maxScore, NumberFormat pointsFormat ) {

        this.pointsFormat = pointsFormat;
        this.confirmNewGame = true;
        this.listeners = new EventListenerList();

        // Level
        levelNode = new PText();
        levelNode.setFont( FONT );
        setLevel( maxLevel ); // start with this, so we have a reasonable size for layout

        // Score
        scoreNode = new PText();
        scoreNode.setFont( FONT );
        setScore( maxScore ); // start with this, so we have a reasonable size for layout

        // timer
        timerIcon = new PImage( STOPWATCH_ICON );
        timerValue = new PText();
        timerValue.setFont( FONT );
        setTime( 0 ); // start with this, so we have a reasonable size for layout

        // New Game button
        newGameButton = new HTMLImageButtonNode( BUTTON_NEW_GAME, BUTTON_FONT, BUTTON_COLOR );
        newGameButton.addActionListener( new ActionListener() {
            ControlPanelNode dialog = new ControlPanelNode( new HBox( new PhetPText( "Are you sure?", new PhetFont( 16 ) ),
                                                                      new HTMLImageButtonNode( "Yes", Color.yellow ) {{
                                                                          addActionListener( new ActionListener() {
                                                                              public void actionPerformed( ActionEvent e ) {
                                                                                  fireNewGamePressed();
                                                                                  MyGameScoreboardNode.this.removeChild( dialog );
                                                                              }
                                                                          } );
                                                                      }},
                                                                      new HTMLImageButtonNode( "No", Color.yellow ) {{
                                                                          addActionListener( new ActionListener() {
                                                                              public void actionPerformed( ActionEvent e ) {
                                                                                  MyGameScoreboardNode.this.removeChild( dialog );
                                                                              }
                                                                          } );
                                                                      }}
            ) );

            public void actionPerformed( ActionEvent e ) {
                dialog.setOffset( newGameButton.getFullBounds().getCenterX() - dialog.getFullBounds().getWidth() / 2, newGameButton.getFullBounds().getY() - dialog.getFullBounds().getHeight() );
                if ( !getChildrenReference().contains( dialog ) ) {
                    addChild( dialog );
                }
            }
        } );

        // rendering order
        addChild( levelNode );
        addChild( scoreNode );
        addChild( timerIcon );
        addChild( timerValue );
        addChild( newGameButton );

        // layout, everything in a row, vertically centered, offsets were set by eyeballing them
        final double maxChildHeight = getMaxChildHeight();
        // level
        double x = X_MARGIN;
        double y = Y_MARGIN + ( ( maxChildHeight - levelNode.getFullBoundsReference().getHeight() ) / 2 );
        levelNode.setOffset( x, y );
        // score
        x = levelNode.getFullBoundsReference().getMaxX() + 80;
        y = Y_MARGIN + ( ( maxChildHeight - scoreNode.getFullBoundsReference().getHeight() ) / 2 );
        scoreNode.setOffset( x, y );
        // timer
        x = scoreNode.getFullBoundsReference().getMaxX() + 60;
        y = Y_MARGIN + ( ( maxChildHeight - timerIcon.getFullBoundsReference().getHeight() ) / 2 );
        timerIcon.setOffset( x, y );
        x = timerIcon.getFullBoundsReference().getMaxX() + 5;
        y = Y_MARGIN + ( ( maxChildHeight - timerValue.getFullBoundsReference().getHeight() ) / 2 );
        timerValue.setOffset( x, y );
        // New Game button
        x = timerValue.getFullBoundsReference().getMaxX() + 20;
        y = Y_MARGIN + ( ( maxChildHeight - newGameButton.getFullBoundsReference().getHeight() ) / 2 );
        newGameButton.setOffset( x, y );

        // background, added last since it's sized to fit the child nodes above
        PBounds b = getFullBoundsReference();
        backgroundShape = new Rectangle2D.Double( 0, 0, b.getMaxX() + X_MARGIN, b.getMaxY() + Y_MARGIN );
        backgroundNode = new PPath( backgroundShape );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        addChild( backgroundNode );
        backgroundNode.moveToBack();
    }

    public void setBackground( Paint background ) {
        backgroundNode.setPaint( background );
    }

    /**
     * Changes the width of the scoreboard's background, while keeping the "New Game" button
     * at the right edge.  Used to adjust the scoreboard width to other stuff in the play area.
     *
     * @param width
     */
    public void setBackgroundWidth( double width ) {
        backgroundShape.setRect( backgroundShape.getX(), backgroundShape.getY(), width, backgroundShape.getHeight() );
        backgroundNode.setPathTo( backgroundShape );
        double x = backgroundNode.getFullBoundsReference().getMaxX() - newGameButton.getFullBoundsReference().getWidth() - X_MARGIN;
        newGameButton.setOffset( x, newGameButton.getYOffset() );
    }

    public void setScore( double points ) {
        String s = MessageFormat.format( LABEL_SCORE, pointsFormat.format( points ) );
        scoreNode.setText( s );
    }

    public void setLevel( int level ) {
        String s = MessageFormat.format( LABEL_LEVEL, String.valueOf( level ) );
        levelNode.setText( s );
    }

    public void setTimerVisible( boolean visible ) {
        timerIcon.setVisible( visible );
        timerValue.setVisible( visible );
    }

    /**
     * Sets the time without a "best time".
     *
     * @param time time in milliseconds
     */
    public void setTime( long time ) {
        setTime( time, 0 /* bestTime */ );
    }

    /**
     * Sets the time and a "best time" for comparison.
     * If you don't want to display a best time, use the other setTime method.
     *
     * @param time     milliseconds
     * @param bestTime best time in milliseconds, zero means "no best time"
     */
    public void setTime( long time, long bestTime ) {
        String valueString = null;
        if ( bestTime == 0 ) {
            // 0:29
            valueString = GameTimerFormat.format( time );
        }
        else {
            // (Best: 0:34)
            String bestTimeString = MessageFormat.format( LABEL_BEST, GameTimerFormat.format( bestTime ) );
            // 0:29 (Best: 0:34)
            valueString = MessageFormat.format( FORMAT_TIME_BEST, GameTimerFormat.format( time ), bestTimeString );
        }
        timerValue.setText( valueString );
    }

    public void setConfirmNewGame( boolean confirmNewGame ) {
        this.confirmNewGame = confirmNewGame;
    }

    public boolean isConfirmNewGame() {
        return confirmNewGame;
    }

    /*
    * Handle the "New Game" button.
    * If confirmation is enabled, posts a confirmation dialog.
    * When confirmed, listeners are notified.
    */
    private void handleNewGame() {
        if ( confirmNewGame ) {
            // request confirmation via a Yes/No dialog
            Frame parent = PhetApplication.getInstance().getPhetFrame();
            int option = showNewGameConfirmationDialog( parent, "Confirm" );
            if ( option == JOptionPane.YES_OPTION ) {
                fireNewGamePressed();
            }
        }
        else {
            fireNewGamePressed();
        }
    }

    private int showNewGameConfirmationDialog( Component parent, String title ) {
        // Use a JOptionPane to get the right dialog look and layout
        JOptionPane pane = new JOptionPane( "Are you sure?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION );
        pane.selectInitialValue();

        // Create our own dialog to solve issue #89
        final JDialog dialog = createDialog( parent, title );
        dialog.setFont( new PhetFont( 14 ) );
        dialog.setBackground( Color.YELLOW );
        dialog.getContentPane().add( pane );

        // Close the dialog when the user makes a selection
        pane.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( JOptionPane.VALUE_PROPERTY ) ) {
                    dialog.setVisible( false );
                }
            }
        } );

        // pack the dialog first so it will be centered correctly
        dialog.pack();
        SwingUtils.centerDialog( dialog, parent );
        dialog.setVisible( true );

        // blocks here until user makes a choice
        dialog.dispose();

        // any not-int selection assumes the user closed the dialog via the window decoration
        int returnValue = JOptionPane.CLOSED_OPTION;
        Object paneValue = pane.getValue();
        if ( paneValue instanceof Integer ) {
            returnValue = ( (Integer) paneValue ).intValue();
        }
        return returnValue;

    }

    /*
     * Creates a PaintImmediateDialog.
     */
    private static JDialog createDialog( Component parent, String title ) {
        JDialog dialog = null;
        Window window = getWindowForComponent( parent );
        if ( window instanceof Frame ) {
            dialog = new PaintImmediateDialog( (Frame) window, title );
        }
        else {
            dialog = new PaintImmediateDialog( (Dialog) window, title );
        }
        dialog.setModal( true );
        dialog.setResizable( false );
        return dialog;
    }

    /*
    * JOptionPane.getWindowForComponent isn't public, reproduced here
    */
    private static Window getWindowForComponent( Component parentComponent ) throws HeadlessException {
        if ( parentComponent == null ) {
            return JOptionPane.getRootFrame();
        }
        if ( parentComponent instanceof Frame || parentComponent instanceof Dialog ) {
            return (Window) parentComponent;
        }
        return getWindowForComponent( parentComponent.getParent() );
    }

    // max height of this nodes children, used for layout alignment
    private double getMaxChildHeight() {
        double maxHeight = 0;
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            maxHeight = Math.max( maxHeight, getChild( i ).getFullBoundsReference().getHeight() );
        }
        return maxHeight;
    }

    public interface GameScoreboardListener extends EventListener {
        public void newGamePressed();
    }

    public void addGameScoreboardListener( GameScoreboardListener listener ) {
        listeners.add( GameScoreboardListener.class, listener );
    }

    public void removeGameScoreboardListener( GameScoreboardListener listener ) {
        listeners.remove( GameScoreboardListener.class, listener );
    }

    private void fireNewGamePressed() {
        for ( GameScoreboardListener listener : listeners.getListeners( GameScoreboardListener.class ) ) {
            listener.newGamePressed();
        }
    }
}