// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.control.RulerNode3D.RulerNode2D;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a toolbox that contains meters (ruler, thermometer, density meter, composition meter), and display options
 */
public class Toolbox extends OrthoPiccoloNode {
    private static final double INSET = 5;

    public Toolbox( final PlateTectonicsTab tab, final ToolboxState toolboxState ) {
        super( new ControlPanelNode( new PNode() {{
            final float kmToViewUnit = 0.75f;
            final ZeroOffsetNode rulerNode2D = new ZeroOffsetNode( new RulerNode2D( kmToViewUnit, tab ) {{
                // make the ruler visible when it is in the toolbox
                toolboxState.rulerInToolbox.addObserver( LWJGLUtils.swingObserver( new Runnable() {
                    public void run() {
                        setVisible( toolboxState.rulerInToolbox.get() );
                    }
                } ) );

                // remove it from the toolbox when pressed
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                toolboxState.rulerInToolbox.set( false );
                            }
                        } );
                    }
                } );

                scale( 0.5 );
            }} ); // wrap it in a zero-offset node, since we are rotating and scaling it (bad origin)

            addChild( rulerNode2D ); // approximate scaling to get the size right

            final PNode thermometer = new ZeroOffsetNode( new ThermometerNode2D( kmToViewUnit ) ) {{

                //Move it to the right of the ruler
                setOffset( rulerNode2D.getFullBounds().getWidth() + INSET, rulerNode2D.getFullBounds().getMaxY() - getFullBounds().getHeight() );

                // make it visible when it is in the toolbox
                toolboxState.thermometerInToolbox.addObserver( LWJGLUtils.swingObserver( new Runnable() {
                    public void run() {
                        setVisible( toolboxState.thermometerInToolbox.get() );
                    }
                } ) );

                // remove it from the toolbox when pressed
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                toolboxState.thermometerInToolbox.set( false );
                            }
                        } );
                    }
                } );
            }};
            addChild( thermometer );

            final PNode densitySensor = new ZeroOffsetNode( new DensitySensorNode2D( kmToViewUnit ) ) {{
                setOffset( thermometer.getFullBounds().getMaxX() + INSET, rulerNode2D.getFullBounds().getMaxY() - getFullBounds().getHeight() );

                // make it visible when it is in the toolbox
                toolboxState.densitySensorInToolbox.addObserver( LWJGLUtils.swingObserver( new Runnable() {
                    public void run() {
                        setVisible( toolboxState.densitySensorInToolbox.get() );
                    }
                } ) );

                // remove it from the toolbox when pressed
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                toolboxState.densitySensorInToolbox.set( false );
                            }
                        } );
                    }
                } );
            }};

            addChild( densitySensor );

            addChild( new PText( "Toolbox" ) {{
                setFont( new PhetFont( 16, true ) );
                setOffset( rulerNode2D.getFullBounds().getWidth() + 10, 0 ); // TODO: change positioning once we have added other toolbox elements
            }} );
        }} ), tab, tab.getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), tab.mouseEventNotifier );
    }
}