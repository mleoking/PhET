// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.GridMesh;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLCanvas;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLTab;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLUtils;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoComponentNode;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoPiccoloNode;
import edu.umd.cs.piccolo.PNode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class TestingTab extends LWJGLTab {
    private FloatBuffer specular = LWJGLUtils.floatBuffer( new float[] { 0, 0, 0, 0 } );
    private FloatBuffer shininess = LWJGLUtils.floatBuffer( new float[] { 50 } );
    private FloatBuffer lightPosition = LWJGLUtils.floatBuffer( new float[] { 1, 1, 1, 0 } );

    private long timeElapsed = 0;
    private long lastTime = 0;
    private OrthoComponentNode testSwingNode;
    private OrthoPiccoloNode testPiccoloNode;
    private OrthoComponentNode fpsNode;
    private CanvasTransform canvasTransform;
    private Dimension stageSize;
    private GridMesh testTerrain;
    private GridMesh testGround;

    // TODO: consider a different way of handling this, or moving this up to LWJGLTab
    public final VoidNotifier mouseEventNotifier = new VoidNotifier();

    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );

    public TestingTab( LWJGLCanvas canvas, String title ) {
        super( canvas, title );
    }

    @Override public void start() {
        lastTime = System.currentTimeMillis();

        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        testSwingNode = new OrthoComponentNode( new JPanel() {{
            setOpaque( false );
            add( new JButton( "Swing Button" ) {{
                setOpaque( false );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing button pressed" );
                    }
                } );
            }} );
            add( new JCheckBox( "Swing Checkbox" ) {{
                setOpaque( false );
                setForeground( Color.WHITE );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing checkbox pressed" );
                    }
                } );
            }} );
            add( new JRadioButton( "Swing Radio Button" ) {{
                setOpaque( false );
                setForeground( Color.WHITE );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing radio button pressed" );
                    }
                } );
            }} );
        }}, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 20 ) ), mouseEventNotifier );
        testPiccoloNode = new OrthoPiccoloNode( new PNode() {{
            final PNode button = new TextButtonNode( "Piccolo Button", new PhetFont( 14 ), Color.ORANGE ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Piccolo button pressed" );
                    }
                } );
            }};
            addChild( new Spacer( button.getFullBounds() ) );
            addChild( button );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 150, 30, 10, 10 ), Color.BLUE ) {{
                setOffset( 20, button.getFullBounds().getMaxY() + 10 );
            }} );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 150, 30, 10, 10 ), Color.YELLOW ) {{
                setOffset( 20.5, button.getFullBounds().getMaxY() + 50 );
            }} );
        }}, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 100 ) ), mouseEventNotifier );
        JPanel fpsPanel = new JPanel() {{
            setPreferredSize( new Dimension( 100, 30 ) );
            setOpaque( true );
            add( new JLabel( "(FPS here)" ) {{
                setForeground( Color.WHITE );
                framesPerSecond.addObserver( new SimpleObserver() {
                    public void update() {
                        final double fps = Math.round( framesPerSecond.get() * 10 ) / 10;
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setText( "FPS: " + fps );
                            }
                        } );
                    }
                } );
            }} );
        }};
        fpsNode = new OrthoComponentNode( fpsPanel, this, canvasTransform,
                                          new Property<ImmutableVector2D>( new ImmutableVector2D( 0, stageSize.getHeight() - fpsPanel.getPreferredSize().getHeight() ) ), mouseEventNotifier );

        {
            int terrainRows = 20;
            int groundRows = 100;
            int cols = 100;
            ImmutableVector3D[] terrain = new ImmutableVector3D[terrainRows * cols];
            ImmutableVector3D[] ground = new ImmutableVector3D[groundRows * cols];

            double[] frontHeights = new double[cols];

            for ( int row = 0; row < terrainRows; row++ ) {
                for ( int col = 0; col < cols; col++ ) {
                    double x = ( ( (double) col ) / ( (double) ( cols - 1 ) ) ) * 40 - 20;
                    double z = ( ( (double) row ) / ( (double) ( terrainRows - 1 ) ) ) * 4 - 4;
                    double height = Math.random() / 10;
                    if ( row == terrainRows - 1 ) {
                        frontHeights[col] = height;
                    }
                    terrain[row * cols + col] = convertToRadial( new ImmutableVector3D( x, height, z ) );
                }
            }
            for ( int row = 0; row < groundRows; row++ ) {
                for ( int col = 0; col < cols; col++ ) {
                    double x = ( ( (double) col ) / ( (double) ( cols - 1 ) ) ) * 40 - 20;
                    double y = ( ( (double) row ) / ( (double) ( groundRows - 1 ) ) ) * 40 - 40 + frontHeights[col];
                    ground[row * cols + col] = convertToRadial( new ImmutableVector3D( x, y, 0 ) );
                }
            }
            testTerrain = new GridMesh( terrainRows, cols, terrain );
            testGround = new GridMesh( groundRows, cols, ground ) {
                private final ImmutableVector3D normal = new ImmutableVector3D( 0, 0, 1 );

                @Override public ImmutableVector3D getNormal( int row, int col ) {
                    return normal;
                }
            };
        }
    }

    @Override public void stop() {
        // store state here?
    }

    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    @Override public void loop() {
        Display.sync( 1024 );

        int framesToCount = 10;
        long current = System.currentTimeMillis();
        timeQueue.add( current );
        if ( timeQueue.size() == framesToCount + 1 ) {
            long previous = timeQueue.poll();
            framesPerSecond.set( (double) ( 1000 * ( (float) framesToCount ) / ( (float) ( current - previous ) ) ) );
        }

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }

        testSwingNode.update();
        testPiccoloNode.update();
        fpsNode.update();

        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        guiProjection();
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        long currentTime = System.currentTimeMillis();
        timeElapsed += ( currentTime - lastTime );
        lastTime = currentTime;

        // Clear the screen and depth buffer
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        glPushMatrix();
        {
            glMatrixMode( GL_PROJECTION );
            CanvasTransform.applyAffineTransform( canvasTransform.transform.get() );
            glMatrixMode( GL_MODELVIEW );

            /*---------------------------------------------------------------------------*
            * testing background
            *----------------------------------------------------------------------------*/
            glColor4f( 0f, 1, 1f, 0.1f );

            // draw quad
            glBegin( GL_QUADS );
            glVertex3f( 0, 0, 0 );
            glVertex3f( stageSize.width, 0, 0 );
            glVertex3f( stageSize.width, stageSize.height, 0 );
            glVertex3f( 0, stageSize.height, 0 );
            glEnd();

            glTranslatef( stageSize.width / 2, stageSize.height / 3, 0 );

            /*---------------------------------------------------------------------------*
            * fractal thing
            *----------------------------------------------------------------------------*/
            float angle = (float) ( timeElapsed ) / 200;

            glScaled( 2, 2, 2 );

            // add a fractal-like thing in the background
            fractalThing( angle, 12, 0, 2 );
        }
        glPopMatrix();

        glEnable( GL_DEPTH_TEST );

        {
            glMatrixMode( GL_PROJECTION );
            glLoadIdentity();
            glScalef( 100 / ( (float) canvasSize.get().width ), 100 / ( (float) canvasSize.get().height ), 1 );
            AffineTransform transform = canvasTransform.transform.get();
            glScaled( transform.getScaleX(), transform.getScaleY(), 1 );
            gluPerspective( 40, 1, 1, 5000 );
            glMatrixMode( GL_MODELVIEW );
            glLoadIdentity();
            glTranslatef( 0, -4, -4 );

            glColor4f( 1f, 1, 1f, 1f );

            float size = 100f;

            // TODO: do scene drawing here. the setup is correct

            glMaterial( GL_FRONT, GL_SPECULAR, specular );
//            glMaterial( GL_FRONT, GL_SHININESS, shininess );
            glLight( GL_LIGHT0, GL_POSITION, lightPosition );
            glEnable( GL_LIGHTING );
            glEnable( GL_LIGHT0 );
            glColor4f( 1, 0, 0, 1 );
            testTerrain.render();
            glDisable( GL_LIGHTING );
            glColor4f( 0.5f, 0.5f, 0.5f, 1 );
            testGround.render();

            // wireframe
            {
//                glTranslated( 0, 0, 0.0001 );
//                glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
//                glColor4f( 1, 1, 1, 0.2f );
//                testTerrain.render();
////                testGround.render();
//                glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
            }
        }

        glDisable( GL_DEPTH_TEST );


        {
            guiProjection();

            // our test GUI
            testSwingNode.render();
            testPiccoloNode.render();
            fpsNode.render();
        }

        Display.update();
    }

    private void guiProjection() {
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, getCanvasWidth(), getCanvasHeight(), 0, 1, -1 );
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
    }

    private void fractalThing( float angle, int num, int depth, int limit ) {
        glPushMatrix();
        for ( int i = 0; i < num; i++ ) {
            glTranslatef( 0, 100, 0 );
            glRotatef( angle, 0, 0, 1 );

            // set the color of the quad (R,G,B,A)
            float n = ( (float) ( i ) ) / ( num - 1 );
            glColor4f( 1 - n, 0.0f, n, 0.5f - 0.4f * ( (float) depth ) / ( (float) limit ) );

            // draw quad
            glBegin( GL_QUADS );
            glVertex3f( -50, -50, 0 );
            glVertex3f( 50, -50, 0 );
            glVertex3f( 50, 50, 0 );
            glVertex3f( -50, 50, 0 );
            glEnd();

            if ( depth < limit ) {
                glPushMatrix();
                glScalef( 0.5f, 0.5f, 1f );
                fractalThing( angle, num, depth + 1, limit );
                glPopMatrix();
            }
        }
        glPopMatrix();
    }

    public static final double RADIUS = 100;
    public static final ImmutableVector3D CENTER = new ImmutableVector3D( 0, -RADIUS, 0 );
    public static final ImmutableVector3D RADIAL_Z_0 = new ImmutableVector3D( 1, 1, 0 );

    /**
     * Converts a given "planar" point into a full 3D model point.
     *
     * @param planar "Planar" point, where y is elevation relative to sea level, and x,z are essentially
     *               distance around the circumference of the earth (assumed to be a sphere, not actually
     *               the geoid shape) in the x and z directions. Basically, think of this "planar" point
     *               as spherical coordinates.
     * @return A point in the cartesian coordinate frame in 3D
     */
    public static ImmutableVector3D convertToRadial( ImmutableVector3D planar ) {
        return convertToRadial( getXRadialVector( planar.getX() ), getZRadialVector( planar.getZ() ), planar.getY() );
    }

    /**
     * Decomposed performance shortcut for convertToRadial( Vector3f planar ).
     *
     * @param xRadialVector result of getXRadialVector( x )
     * @param zRadialVector result of getZRadialVector( z )
     * @param y             Same as in the simple version
     * @return A point in the cartesian coordinate frame in 3D
     */
    public static ImmutableVector3D convertToRadial( ImmutableVector3D xRadialVector, ImmutableVector3D zRadialVector, double y ) {
        double radius = y + RADIUS; // add in the radius of the earth, since y is relative to mean sea level
        return xRadialVector.componentTimes( zRadialVector ).times( radius ).plus( CENTER );
    }

    // improved performance version for z=0 plane
    public static ImmutableVector3D convertToRadial( double x, double y ) {
        return convertToRadial( getXRadialVector( x ), y );
    }

    // improved performance version for z=0 plane
    public static ImmutableVector3D convertToRadial( ImmutableVector3D xRadialVector, double y ) {
        return convertToRadial( xRadialVector, RADIAL_Z_0, y );
    }

    public static ImmutableVector3D getXRadialVector( double x ) {
        double theta = Math.PI / 2 - x / RADIUS; // dividing by the radius actually gets us the correct angle
        return new ImmutableVector3D( Math.cos( theta ), Math.sin( theta ), 1 );
    }

    public static ImmutableVector3D getZRadialVector( double z ) {
        double phi = Math.PI / 2 - z / RADIUS; // dividing by the radius actually gets us the correct angle
        double sinPhi = Math.sin( phi );
        return new ImmutableVector3D( sinPhi, sinPhi, Math.cos( phi ) );
    }
}
