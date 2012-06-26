package edu.colorado.phet.buildafraction.view;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

    public static interface Context {
        void startShapesLevelSelection();

        void startNumberLevelSelection();
    }

    public WorldSelectionScreen( final Context context ) {

        final double scale = 1.6;
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

        List<PNode> buttonContentList = icons.map( new F<PNode, PNode>() {
            @Override public PNode f( final PNode p ) {
                return new PaddedIcon( p.getFullBounds().getWidth() + 50, p.getFullBounds().getHeight() + 50, p );
            }
        } );

        ArrayList<ToggleButtonNode> buttonNodes = new ArrayList<ToggleButtonNode>();
        for ( int i = 0; i < buttonContentList.length(); i++ ) {
            final Property<Boolean> selected = new Property<Boolean>( false );
            final int finalI = i;
            ToggleButtonNode button = new ToggleButtonNode( buttonContentList.index( i ), selected, new VoidFunction0() {
                public void apply() {
                    //                    SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, icon.levelName ) );
                    selected.set( true );

                    //                    gameSettings.level.set( icon.levelName );
                    //Show it pressed in for a minute before starting up.
                    new Timer( 100, new ActionListener() {
                        public void actionPerformed( final ActionEvent e ) {

                            //                            startGame.apply();
                            if ( finalI == 0 ) {
                                context.startShapesLevelSelection();
                            }
                            else {
                                context.startNumberLevelSelection();
                            }

                            //prep for next time
                            selected.set( false );
                        }
                    } ) {{
                        setInitialDelay( 100 );
                        setRepeats( false );
                    }}.start();
                }
            } );
            buttonNodes.add( button );
        }

        addChild( new HBox( 100, buttonNodes.toArray( new ToggleButtonNode[buttonNodes.size()] ) ) );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{
                    getCanvas().getLayer().addChild( new WorldSelectionScreen( null ) );
                    setSize( 1024, 768 );
                }}.setVisible( true );
            }
        } );
    }
}