package edu.colorado.phet.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
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
    public WorldSelectionScreen() {
        List<PNode> icons = List.list( new PatternNode( FilledPattern.sequentialFill( Pattern.pie( 4 ), 3 ), Colors.LIGHT_RED ),
                                       new FractionNode( 3, 4 ) {{scale( 0.5 );}} );
        List<PNode> normalize = PaddedIcon.normalize( icons );

        List<ToggleButtonNode> toggleButtons = icons.map( new F<PNode, ToggleButtonNode>() {
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
        addChild( new HBox( toggleButtons.array( ToggleButtonNode[].class ) ) );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{
                    getCanvas().getLayer().addChild( new WorldSelectionScreen() );
                }}.setVisible( true );
            }
        } );
    }
}