// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.jmephet.input.WrappedInputManager;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.errorMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.jmephet.JMEPhetSimsharing.Actions.erred;
import static edu.colorado.phet.jmephet.JMEPhetSimsharing.Objects.jmePhetApplication;

/**
 * PhET-specific behavior needed instead of the generic SimpleApplication JME3 class.
 * <p/>
 * Has a "background" GUI in the back, a scene in the middle, and the main GUI in front.
 * TODO: further cleanup on the exported interface
 */
public class PhetJMEApplication extends Application {

    /*---------------------------------------------------------------------------*
    * modules
    *----------------------------------------------------------------------------*/

    private final List<JMETab> tabs = new ArrayList<JMETab>();
    public final Property<JMETab> activeTab = new Property<JMETab>( null );

    /*---------------------------------------------------------------------------*
    * global properties
    *----------------------------------------------------------------------------*/

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );
    public final Property<Dimension> canvasSize = new Property<Dimension>( null ); // updated on the Swing EDT

    private List<SimpleObserver> updateObservers = new ArrayList<SimpleObserver>();

    // statistics that can be shown on the screen
    public JMEStatistics statistics = new JMEStatistics();

    // our main "root" nodes for different layers
    private Node sceneNode = new Node( "Scene Node" );
    private Node guiNode = new Node( "Gui Node" );
    private Node backgroundGuiNode = new Node( "Background Gui Node" );

    // nodes that will get updated every frame
    private List<Node> liveNodes = new ArrayList<Node>();

    private volatile Dimension stageSize = null;
    private final Frame parentFrame;

    private JMEInputHandler directInputHandler;

    /*---------------------------------------------------------------------------*
    * construction
    *----------------------------------------------------------------------------*/

    public PhetJMEApplication( Frame parentFrame ) {
        super();
        this.parentFrame = parentFrame;

        // let everyone know that this is the one unique global instance
        JMEUtils.setApplication( this );
    }

    /*---------------------------------------------------------------------------*
    * modules
    *----------------------------------------------------------------------------*/

    public void addTab( final JMETab tab ) {
        tabs.add( tab );

        // when modules are made active, record that so we can update our active modules
        tab.active.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean newValue, Boolean oldValue ) {
                if ( newValue ) {
                    activeTab.set( tab );
                }
            }
        } );
    }

    /*---------------------------------------------------------------------------*
    * JMonkeyEngine overrides
    *----------------------------------------------------------------------------*/

    @Override
    public void start() {
        // setup default settings, in case
        if ( settings == null ) {
            setSettings( new AppSettings( true ) );
        }

        //re-setting settings they can have been merged from the registry.
        setSettings( settings );
        super.start();
    }

    @Override
    public void initialize() {
        super.initialize();
//        guiViewPort.getCamera().resize( canvasSize.get().width, canvasSize.get().height, true );
//        guiViewPort.getCamera().update();
//        cam.resize( canvasSize.get().width, canvasSize.get().height, true );
//        cam.update();

        directInputHandler = new WrappedInputManager( inputManager );

        // setup a GUI behind the main scene
        // TODO: consider removing this and just adding a background-color layer!
        backgroundGuiNode.setQueueBucket( Bucket.Gui );
        backgroundGuiNode.setCullHint( CullHint.Never );
        Camera backgroundGuiCam = new Camera( settings.getWidth(), settings.getHeight() );
        final ViewPort backgroundGuiViewPort = renderManager.createPreView( "Background GUI", backgroundGuiCam );
        backgroundGuiViewPort.setClearFlags( true, true, true );
        backgroundGuiViewPort.attachScene( backgroundGuiNode );
        backgroundColor.addObserver( new SimpleObserver() {
            public void update() {
                backgroundGuiViewPort.setBackgroundColor( backgroundColor.get() );
            }
        } );
        liveNodes.add( backgroundGuiNode );
        JMEView backgroundGui = new JMEView( this, backgroundGuiViewPort, backgroundGuiCam, backgroundGuiNode );

        // make the "main" viewport not clear what is behind it
        viewPort.setClearFlags( false, true, true );
        viewPort.attachScene( sceneNode );
        liveNodes.add( sceneNode );

        // hook up the main "gui"
        guiNode.setQueueBucket( Bucket.Gui );
        guiNode.setCullHint( CullHint.Never );
        guiViewPort.attachScene( guiNode );
        liveNodes.add( guiNode );
        JMEView gui = new JMEView( this, guiViewPort, guiViewPort.getCamera(), guiNode );

        statistics.initialize( this, guiNode );

        if ( inputManager != null ) {
            inputManager.setCursorVisible( true );
        }
    }

    @Override
    public void update() {
        super.update(); // make sure to call this
        if ( speed == 0 || paused ) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        stateManager.update( tpf );

        // simple update and root node
        updateState( tpf );

        for ( SimpleObserver observer : updateObservers ) {
            observer.update();
        }

        for ( Node node : liveNodes ) {
            node.updateLogicalState( tpf );
        }

        for ( Node node : liveNodes ) {
            node.updateGeometricState();
        }

        // render states
        stateManager.render( renderManager );
        if ( context.isRenderable() ) {
            renderManager.render( tpf );
        }
        simpleRender( renderManager );
        stateManager.postRender();
    }

    /*---------------------------------------------------------------------------*
    * view and node utilities
    *----------------------------------------------------------------------------*/

    public Camera createDefaultCamera() {
        return new Camera( canvasSize.get().width, canvasSize.get().height );
    }

    public static enum RenderPosition {
        BACK, // behind main and front
        MAIN, // between back and front
        FRONT; // in front of back and main

        public Function2<RenderManager, Camera, ViewPort> getViewportFactory( final String name ) {
            return new Function2<RenderManager, Camera, ViewPort>() {
                public ViewPort apply( RenderManager renderManager, Camera camera ) {
                    switch( RenderPosition.this ) {
                        case BACK:
                            return JMEUtils.getApplication().renderManager.createPreView( name + " (back viewport)", camera );
                        case MAIN:
                            return JMEUtils.getApplication().renderManager.createMainView( name + " (main viewport)", camera );
                        case FRONT:
                            return JMEUtils.getApplication().renderManager.createPostView( name + " (front viewport)", camera );
                        default:
                            throw new IllegalArgumentException( "unknown position: " + RenderPosition.this );
                    }
                }
            };
        }
    }

    public JMEView createRegularView( String name, Camera camera, RenderPosition position ) {
        Node scene = new Node( name + " Node" );

        final ViewPort viewport = position.getViewportFactory( name ).apply( renderManager, camera );
        viewport.attachScene( scene );
        addLiveNode( scene );
        return new JMEView( this, viewport, camera, scene );
    }

    public JMEView createGUIView( String name, RenderPosition position ) {
        JMEView view = createRegularView( name, createDefaultCamera(), position );

        view.getScene().setQueueBucket( Bucket.Gui );
        view.getScene().setCullHint( CullHint.Never );

        return view;
    }

    public Timer getTimer() {
        return timer;
    }

    public void addLiveNode( Node node ) {
        liveNodes.add( node );
    }

    public void removeLiveNode( Node node ) {
        liveNodes.remove( node );
    }

    public void addUpdateObserver( SimpleObserver observer ) {
        updateObservers.add( observer );
    }

    public void removeUpdateObserver( SimpleObserver observer ) {
        updateObservers.remove( observer );
    }

    public void updateState( float tpf ) {
        if ( activeTab.get() != null ) {
            activeTab.get().updateState( tpf );
        }
    }

    public void simpleRender( RenderManager rm ) {
    }

    public void onResize( Dimension canvasSize ) {
        if ( stageSize == null ) {
            stageSize = canvasSize;
        }

        this.canvasSize.set( canvasSize );

        // notify all of our modules about the size change. they might even be hidden
        for ( JMETab tab : tabs ) {
            tab.updateLayout( canvasSize );
        }
    }

    public Dimension getStageSize() {
        return stageSize;
    }

    @Override public void handleError( String errMsg, final Throwable t ) {
        super.handleError( errMsg, t );
        SimSharingManager.sendSystemMessage( jmePhetApplication, SystemComponentTypes.application, erred, parameterSet( errorMessage, errMsg ) );
        showErrorDialog( t );
    }

    public void showErrorDialog( final Throwable t ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // TODO: i18n?
                String stackTrace = "";
                for ( StackTraceElement element : t.getStackTrace() ) {
                    stackTrace += element.toString() + "\n";
                }
                String capabilities = renderer != null ? renderer.getCaps().toString() : "null renderer";

                final String text = "<html><body>Error information:<br/>" +
                                    "" + t.getMessage() + "<br/>" +
                                    "stack trace:<br/>" +
                                    stackTrace + "<br/>" +
                                    "Renderer Capabilities:<br/>" + capabilities + "</body></html>";

                JDialog frame = new JDialog( getParentFrame() ) {{
                    setContentPane( new JPanel( new GridLayout( 1, 1 ) ) {{
                        add( new JPanel( new GridBagLayout() ) {{
                            add( new VerticalLayoutPanel() {{
                                     add( new JLabel( PhetCommonResources.getString( "Jme.thisSimulationWasUnableToStart" ) ) {{
                                         setFont( new PhetFont( 20, true ) );
                                         setForeground( Color.RED );
                                     }} );
                                     String troubleshootingUrl = "http://phet.colorado.edu/en/troubleshooting#3d-driver";
                                     String troubleshootingLink = "<a href=\"" + troubleshootingUrl + "\">" + troubleshootingUrl + "</a>";
                                     String email = "phethelp@colorado.edu";
                                     String emailLink = "<a href=\"mailto:" + email + "\">" + email + "</a>";
                                     String body = PhetCommonResources.getString( "Jme.moreInformation" );
                                     add( new InteractiveHTMLPane( MessageFormat.format( body, troubleshootingLink, emailLink ) ) {{
                                         setOpaque( false );
                                         setFont( new PhetFont( 16, true ) );
                                     }} );
                                 }},
                                 new GridBagConstraints() {{
                                     fill = GridBagConstraints.HORIZONTAL;
                                     insets = new Insets( 10, 10, 10, 10 );
                                 }}
                            );
                            add( new JScrollPane( new JEditorPane( "text/html", text ) {{
                                setCaretPosition( 0 );
                            }} ), new GridBagConstraints() {{
                                gridx = 0;
                                gridy = 1;
                                fill = GridBagConstraints.BOTH;
                                weightx = 1;
                                weighty = 1;
                            }} );
                            add( new JButton( PhetCommonResources.getString( "Jme.copyThisToTheClipboard" ) ) {{
                                     setClipboard( text );
                                 }}, new GridBagConstraints() {{
                                gridy = 2;
                                anchor = GridBagConstraints.LINE_END;
                                insets = new Insets( 5, 5, 5, 5 );
                            }}
                            );
                            setPreferredSize( new Dimension( 800, 600 ) );
                        }} );
                    }} );
                    pack();
                }};
                SwingUtils.centerInParent( frame );
                frame.setVisible( true );
//                PhetOptionPane.showMessageDialog( getParentFrame(), text );
            }
        } );
    }

    public static void setClipboard( String str ) {
        StringSelection ss = new StringSelection( str );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( ss, null );
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public JMEInputHandler getDirectInputHandler() {
        return directInputHandler;
    }

    public static void main( String[] args ) {
        final Frame parentFrame1 = new JFrame() {{
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }};
        SwingUtils.centerWindowOnScreen( parentFrame1 );
        parentFrame1.setVisible( true );

        new PhetJMEApplication( parentFrame1 ) {{
        }}.showErrorDialog( new RuntimeException( "test" ) );
    }
}
