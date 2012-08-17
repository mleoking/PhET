// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.MessageFormat;

import javax.swing.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

/**
 * Useful utility functions related to LWJGL
 */
public class LWJGLUtils {

    private static int nextDisplayList = 1;

    // common spot to handle display list names (identifiers)
    public static synchronized int getDisplayListName() {
        return nextDisplayList++;
    }

    // to support all needed video card architectures, we need to be able to make texture dimensions a power of 2 in many cases.
    public static int toPowerOf2( int n ) {
        int result = 1;
        while ( result < n ) {
            result *= 2;
        }
        return result;
    }

    public static Dimension toPowerOf2( Dimension dim ) {
        return new Dimension( toPowerOf2( dim.width ), toPowerOf2( dim.height ) );
    }

    public static boolean isPowerOf2( int n ) {
        return n == toPowerOf2( n );
    }

    /*---------------------------------------------------------------------------*
    * convenience methods
    *----------------------------------------------------------------------------*/

    public static void color4f( Color color ) {
        glColor4f( (float) color.getRed() / 255f,
                   (float) color.getGreen() / 255f,
                   (float) color.getBlue() / 255f,
                   (float) color.getAlpha() / 255f
        );
    }

    public static void clearColor( Color color ) {
        glClearColor( (float) color.getRed() / 255f,
                      (float) color.getGreen() / 255f,
                      (float) color.getBlue() / 255f,
                      (float) color.getAlpha() / 255f
        );
    }

    public static void vertex3f( Vector3F v ) {
        glVertex3f( v.x, v.y, v.z );
    }

    public static void vertex2fxy( Vector2F v ) {
        glVertex3f( v.x, v.y, 0 );
    }

    /*---------------------------------------------------------------------------*
    * buffer creation
    *----------------------------------------------------------------------------*/

    public static FloatBuffer floatBuffer( float[] floats ) {
        FloatBuffer result = BufferUtils.createFloatBuffer( floats.length );
        result.put( floats );
        result.rewind();
        return result;
    }

    public static ShortBuffer shortBuffer( short[] shorts ) {
        ShortBuffer result = BufferUtils.createShortBuffer( shorts.length );
        result.put( shorts );
        result.rewind();
        return result;
    }

    /*---------------------------------------------------------------------------*
    * threading
    *----------------------------------------------------------------------------*/

    public static void invoke( Runnable runnable ) {
        LWJGLCanvas.addTask( runnable );
    }

    public static boolean isLWJGLRendererThread() {
        return Thread.currentThread().getName().equals( LWJGLCanvas.LWJGL_THREAD_NAME );
    }

    public static SimpleObserver swingObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    public static SimpleObserver jmeObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                invoke( runnable );
            }
        };
    }

    public static UpdateListener swingUpdateListener( final Runnable runnable ) {
        return new UpdateListener() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    /*---------------------------------------------------------------------------*
    * capability handling (if in a GLNode, use the behavior there instead)
    *----------------------------------------------------------------------------*/

    public static void withEnabled( int glCapability, Runnable runnable ) {
        glEnable( glCapability );
        runnable.run();
        glDisable( glCapability );
    }

    public static void withClientEnabled( int glClientCapability, Runnable runnable ) {
        glEnableClientState( glClientCapability );
        runnable.run();
        glDisableClientState( glClientCapability );
    }

    /*---------------------------------------------------------------------------*
    * error handling
    *----------------------------------------------------------------------------*/

    public static void showErrorDialog( final Frame parentFrame, final Throwable t ) {
        final ContextCapabilities capabilities = GLContext.getCapabilities();
        final String glslVersion = ( capabilities != null && capabilities.OpenGL20 ) ? glGetString( GL_SHADING_LANGUAGE_VERSION ) : "(none)";
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // TODO: i18n?
                String stackTrace = "";
                for ( StackTraceElement element : t.getStackTrace() ) {
                    stackTrace += element.toString() + "\n";
                }

                StringBuilder capabilityString = new StringBuilder();
                if ( capabilities != null ) {
                    capabilityString.append( " OpenGL11:" ).append( capabilities.OpenGL11 );
                    capabilityString.append( " OpenGL12:" ).append( capabilities.OpenGL12 );
                    capabilityString.append( " OpenGL13:" ).append( capabilities.OpenGL13 );
                    capabilityString.append( " OpenGL14:" ).append( capabilities.OpenGL14 );
                    capabilityString.append( " OpenGL15:" ).append( capabilities.OpenGL15 );
                    capabilityString.append( " OpenGL20:" ).append( capabilities.OpenGL20 );
                    capabilityString.append( " OpenGL21:" ).append( capabilities.OpenGL21 );
                    capabilityString.append( " OpenGL30:" ).append( capabilities.OpenGL30 );
                    capabilityString.append( " OpenGL31:" ).append( capabilities.OpenGL31 );
                    capabilityString.append( " OpenGL32:" ).append( capabilities.OpenGL32 );
                    capabilityString.append( " OpenGL33:" ).append( capabilities.OpenGL33 );
                    capabilityString.append( " OpenGL40:" ).append( capabilities.OpenGL40 );
                    capabilityString.append( " OpenGL41:" ).append( capabilities.OpenGL41 );
                    capabilityString.append( " GL_ARB_texture_non_power_of_two:" ).append( capabilities.GL_ARB_texture_non_power_of_two );
                    capabilityString.append( " GL_EXT_framebuffer_object:" ).append( capabilities.GL_EXT_framebuffer_object );
                    capabilityString.append( " GL_ARB_draw_buffers:" ).append( capabilities.GL_ARB_draw_buffers );
                    capabilityString.append( " GL_ARB_vertex_program:" ).append( capabilities.GL_ARB_vertex_program );
                    capabilityString.append( " GL_ARB_fragment_program:" ).append( capabilities.GL_ARB_fragment_program );
                    capabilityString.append( " GLSL version:" ).append( glslVersion );
                }

                final String text = "<html><body>Error information:<br/>" +
                                    "" + t.getMessage() + "<br/>" +
                                    "stack trace:<br/>" +
                                    stackTrace + "<br/>" +
                                    "Renderer Capabilities:<br/>" + capabilityString.toString() + "</body></html>";

                JDialog frame = new JDialog( parentFrame ) {{
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
                                     add( new HTMLUtils.InteractiveHTMLPane( MessageFormat.format( body, troubleshootingLink, emailLink ) ) {{
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

    private static void setClipboard( String str ) {
        StringSelection ss = new StringSelection( str );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( ss, null );
    }
}
