package edu.colorado.phet.buildafraction.view;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractionmatcher.view.PaddedIcon;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

/**
 * Node with buttons that show progress on each of the worlds {pictures, numbers}, which take you to the world when you start it up.
 *
 * @author Sam Reid
 */
public class WorldSelectionScreen extends PNode {

    public static @Data class Level {
        public final String title;
        public final PNode icon;
        public final int levels;
        public final int played;
        public final int perfect;
    }

    public WorldSelectionScreen() {

        //Title text, only shown when the user is choosing a level
        final PNode titleText = new PNode() {{
            addChild( new PhetPText( "Build a Fraction", new PhetFont( 38, true ) ) );
        }};

        final double scale = 1.75;
        final PatternNode patternNode = new PatternNode( FilledPattern.sequentialFill( Pattern.pie( 20 ), 0 ), Colors.LIGHT_RED ) {{scale( 1.5 * scale );}};
        patternNode.scaleStrokes( 1.0 / scale );
        Level pictures = new Level( "Shapes", patternNode, 20, 0, 0 );
        Level numbers = new Level( "Numbers", new FractionNode( 0, 20 ) {{scale( 0.5 * scale );}}, 20, 0, 0 );

        final PhetFont titleFont = new PhetFont( 38 );
        final PNode shapesText = new PhetPText( pictures.title, titleFont );
        final PNode numbersText = new PhetPText( numbers.title, titleFont );
        List<PNode> normalizedText = PaddedIcon.normalize( List.list( shapesText, numbersText ) );
        List<PNode> normalizedIcon = PaddedIcon.normalize( List.list( pictures.icon, numbers.icon ) );
        List<PNode> icons = List.list( (PNode) new VBox( normalizedText.index( 0 ), normalizedIcon.index( 0 ) ),
                                       (PNode) new VBox( normalizedText.index( 1 ), normalizedIcon.index( 1 ) ) );

        List<ToggleButtonNode> toggleButtons = icons.map( new F<PNode, PNode>() {
            @Override public PNode f( final PNode p ) {
                return new PaddedIcon( p.getFullBounds().getWidth() + 50, p.getFullBounds().getHeight() + 50, p );
            }
        } ).map( new F<PNode, ToggleButtonNode>() {
            @Override public ToggleButtonNode f( final PNode icon ) {
                final Property<Boolean> selected = new Property<Boolean>( false );
                ToggleButtonNode button = new ToggleButtonNode( icon, selected, new VoidFunction0() {
                    public void apply() {
                        //                    SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, icon.levelName ) );
                        selected.set( true );
                        //                    gameSettings.level.set( icon.levelName );

                        //Show it pressed in for a minute before starting up.
                        new Timer( 100, new ActionListener() {
                            public void actionPerformed( final ActionEvent e ) {

                                //                            startGame.apply();

                                //prep for next time
                                selected.set( false );
                            }
                        } ) {{
                            setInitialDelay( 100 );
                            setRepeats( false );
                        }}.start();
                    }
                } );
                return button;
            }
        } );
        addChild( new VBox( 100, titleText,
                            new HBox( 100, toggleButtons.array( ToggleButtonNode[].class ) ) {{
                                setOffset( 200, 200 );
                            }} ) );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{
                    getCanvas().getLayer().addChild( new WorldSelectionScreen() );
                    setSize( 1024, 768 );
                }}.setVisible( true );
            }
        } );
    }
}