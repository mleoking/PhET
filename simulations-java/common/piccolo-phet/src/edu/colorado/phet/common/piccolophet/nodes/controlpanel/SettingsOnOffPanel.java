// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.controlpanel;

import fj.F;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getString;

/**
 * Shows nodes in a grid, for on/off for timer and/or sound and other features
 *
 * @author Sam Reid
 */
public class SettingsOnOffPanel extends PNode {

    public static final PhetFont FONT = new PhetFont( 20, true );

    //Class that encapsulates the information for a settings feature (such as audio on/off).
    public static final class Feature {

        //The icon to show when the feature is off
        public final PNode off;

        //The icon to show when the feature is on
        public final PNode on;

        //Property that views/sets the state of whether the feature is enabled.
        public final BooleanProperty onProperty;

        //Sim sharing component
        public final IUserComponent component;

        public Feature( PNode off, PNode on, BooleanProperty onProperty, IUserComponent component ) {
            this.off = off;
            this.on = on;
            this.onProperty = onProperty;
            this.component = component;
        }
    }

    public SettingsOnOffPanel( final List<Feature> icons ) {
        final List<PNode> all = icons.bind( nodes );
        List<Feature> padded = icons.map( new F<Feature, Feature>() {
            @Override public Feature f( final Feature element ) {
                return new Feature( new PaddedNode( PhetPNode.getMaxSize( all ), element.off ),
                                    new PaddedNode( PhetPNode.getMaxSize( all ), element.on ), element.onProperty, element.component );
            }
        } );
        VBox box = new VBox( 4 );

        //For each feature, wire it up and add to the panel
        for ( final P2<Feature, Integer> e : padded.zipIndex() ) {
            final Feature feature = e._1();

            //Wire up to the property
            final PBasicInputEventHandler toggle = new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    feature.onProperty.toggle();
                }
            };

            //Add mouse listeners
            feature.on.addInputEventListener( toggle );
            feature.on.addInputEventListener( new CursorHandler() );
            feature.off.addInputEventListener( toggle );
            feature.off.addInputEventListener( new CursorHandler() );

            //Add to the layout
            box.addChild( createRowNode( feature ) );

            //Set initial transparency.  When feature is enabled/disabled it will cross-fade.
            feature.on.setTransparency( feature.onProperty.get() ? 1 : 0 );
            feature.off.setTransparency( feature.onProperty.get() ? 0 : 1 );

            //Show a separator line if there is another element after this.
            if ( e._2() < padded.length() - 1 ) {
                box.addChild( new PhetPPath( new Line2D.Double( 0, 0, box.getFullWidth(), 0 ) ) );
            }
        }
        addChild( new ZeroOffsetNode( box ) );
    }

    //Create the HBox for a single feature that will show the on/off icon and "on"/"off" radio buttons.
    private static HBox createRowNode( final Feature feature ) {
        PNode offButton = new PSwing( new PropertyRadioButton<Boolean>( UserComponentChain.chain( feature.component, "off" ), getString( "Games.radioButton.off" ), feature.onProperty, false ) {{
            setFont( FONT );
            setOpaque( false );
        }} );
        PNode onButton = new PSwing( new PropertyRadioButton<Boolean>( UserComponentChain.chain( feature.component, "on" ), getString( "Games.radioButton.on" ), feature.onProperty, true ) {{
            setFont( FONT );
            setOpaque( false );
        }} );
        return new HBox( 4, new PNode() {{
            addChild( feature.on );
            addChild( feature.off );

            //Cross fade when toggled on/off
            feature.onProperty.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean on ) {
                    if ( on ) {
                        feature.on.animateToTransparency( 1, 300 );
                        feature.off.animateToTransparency( 0, 300 );
                    }
                    else {
                        feature.on.animateToTransparency( 0, 300 );
                        feature.off.animateToTransparency( 1, 300 );
                    }
                }
            } );
        }}, offButton, onButton );
    }

    //Get the nodes for an element.
    public static final F<Feature, List<PNode>> nodes = new F<Feature, List<PNode>>() {
        @Override public List<PNode> f( final Feature feature ) {
            return List.list( feature.on, feature.off );
        }
    };
}