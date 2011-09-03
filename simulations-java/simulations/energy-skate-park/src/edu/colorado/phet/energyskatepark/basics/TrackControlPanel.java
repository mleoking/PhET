// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.basics.EnergySkateParkBasicsModule.CONTROL_FONT;

/**
 * Control panel that allows the user to change the track friction and stickiness (roller-coaster mode).
 *
 * @author Sam Reid
 */
public class TrackControlPanel extends PNode {

    public TrackControlPanel( final EnergySkateParkBasicsModule module, final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final PNode energyGraphControlPanel ) {
        addChild( new ControlPanelNode(
                new VBox( 10,

                          //Control box for track friction
                          new VBox( 0,
                                    new PText( "Track Friction" ) {{setFont( CONTROL_FONT );}},
                                    new OnOffPanel( module.frictionEnabled ),
                                    new PSwing( new JSlider() {{
                                        setPreferredSize( new Dimension( 150, getPreferredSize().height ) );
                                        setLabelTable( new Hashtable<Object, Object>() {{
                                            put( 0, new JLabel( "none" ) );
                                            put( 100, new JLabel( "lots" ) );//0.01
                                        }} );
                                        setPaintLabels( true );
                                        final ChangeListener frictionChanged = new ChangeListener() {
                                            public void stateChanged( ChangeEvent e ) {
                                                module.setCoefficientOfFriction( module.frictionEnabled.get() ? getValue() / 100.0 / 100.0 : 0.0 );
                                            }
                                        };
                                        addChangeListener( frictionChanged );
                                        module.frictionEnabled.addObserver( new VoidFunction1<Boolean>() {
                                            public void apply( Boolean frictionEnabled ) {
                                                setEnabled( frictionEnabled );
                                                frictionChanged.stateChanged( null );
                                            }
                                        } );
                                    }} )
                          ),

                          //Separator
                          new PhetPPath( new Line2D.Double( 0, 0, 100, 0 ) ),

                          //Control box for stickiness
                          new VBox( 0,
                                    new PText( "Stick to Track" ) {{setFont( CONTROL_FONT );}},
                                    new OnOffPanel( module.stickToTrack ) )
                ),
                EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, energyGraphControlPanel.getFullBounds().getMaxY() + EnergySkateParkBasicsModule.INSET );
                }
            } );
        }} );
    }
}