// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.test;

/**
 * ActiveCircles.java, an example of active rendering while double buffering
 * The article on this source code can be found at:
 * http://jamesgames.org/resources/double_buffer/double_and_active.html
 * Code demonstrates: - properly set width and height of a JFrame using Insets
 *                    - double buffering and active rendering via BufferStrategy
 *                    - usage of a high resolution timer
 *                    - setting up a BufferStrategy on JFrame instead of Canvas
 *                    - actively rendering Swing components
 *                    - easy location painting such as location 0,0
 * @author James Murphy
 * @version 04/16/10
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;

public class ActiveCircles extends JFrame implements ActionListener {
    private Insets insets;
    private Random random;
    private MovingCircle[] circles;
    private BufferStrategy bufferStrategy;
    private boolean isRunning;
    private boolean isFpsLimited;
    private BufferedImage drawing;
    private JButton changeColor;
    private JButton limitFps;
    private Color circleColor;
    private int fps;


    public static void main( String[] args ) {
//        ActiveCircles activeCirclesExample = new ActiveCircles(50, 700, 500);
        ActiveCircles activeCirclesExample = new ActiveCircles( 1, 700, 500 );
    }

    /**
     * Constructor for ActiveCircles
     *
     * @param numberOfCircles The number of circles you want the program to display
     * @param width           The width of the program's window
     * @param height          The height of the program's window
     */
    public ActiveCircles( int numberOfCircles, int width, int height ) {
        super();

        setTitle( "Active rendering with Swing and double buffering circles" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setVisible( true );
        setIgnoreRepaint( true ); // don't need Java painting for us
        setResizable( false ); // don't want someone resizing our "game" for us

        // set up our UnRepaintManager
        RepaintManager repaintManager = new UnRepaintManager();
        repaintManager.setDoubleBufferingEnabled( false );
        RepaintManager.setCurrentManager( repaintManager );

        // Correct change width and height of window so that the available
        // screen space actually cooresponds to what is passed, another
        // method is the Canvas object + pack()
        setSize( width, height );
        insets = this.getInsets();
        int insetWide = insets.left + insets.right;
        int insetTall = insets.top + insets.bottom;
        setSize( getWidth() + insetWide, getHeight() + insetTall );

        circleColor = Color.BLUE;
        circles = new MovingCircle[numberOfCircles];
        random = new Random();
        int circleWidth = 50;
        int circleHeight = 50;
        float maxSpeed = .5f;
        for ( int i = 0; i < circles.length; i++ ) {
            circles[i] = new MovingCircle( random.nextFloat() * ( getWidth() - circleWidth ),
                                           random.nextFloat() * ( getHeight() - circleHeight ), circleWidth,
                                           circleHeight, random.nextBoolean(), random.nextBoolean(),
                                           1f / 10f );
        }

        // add Swing components now to JFrame
        JPanel programTitlePanel = new JPanel( new FlowLayout() );
        programTitlePanel.add( new JLabel( "Actively rendering graphics with Swing components with Swing Integration" ) );
        changeColor = new JButton( "Change color" );
        changeColor.addActionListener( this );
        JPanel changeColorPanel = new JPanel( new FlowLayout() );
        changeColorPanel.add( changeColor );
        limitFps = new JButton( "Unlimit FPS" );
        limitFps.addActionListener( this );
        JPanel limitFpsPanel = new JPanel( new FlowLayout() );
        limitFpsPanel.add( limitFps );

        JPanel holder = new JPanel( new GridLayout( 2, 1 ) ); // 2 rows, 1 column
        holder.add( programTitlePanel );
        holder.add( changeColorPanel );

        // now add these Swing components to JFrame
        add( BorderLayout.NORTH, holder );
        add( BorderLayout.SOUTH, limitFpsPanel );

        isFpsLimited = true;

        // The JFrame's content pane's background will paint over any other graphics
        // we painted ourselves, so let's turn it transparent
        ( (JComponent) getContentPane() ).setOpaque( false );
        // now set the JPanel's opaque, along with other Swing components whose
        // backgrounds we don't want shown
        changeColorPanel.setOpaque( false );
        programTitlePanel.setOpaque( false );
        limitFpsPanel.setOpaque( false );
        holder.setOpaque( false );

        // create a buffer strategy using two buffers
        createBufferStrategy( 2 );
        // set this JFrame's BufferStrategy to our instance variable
        bufferStrategy = getBufferStrategy();

        isRunning = true;
        gameLoop(); // enter the game loop
    }

    /**
     * Method containing the game's loop.
     * Each iteration of the loop updates all animations and sprite locations
     * and draws the graphics to the screen
     */
    public void gameLoop() {
        long oldTime = System.nanoTime();
        long nanoseconds = 0;
        int frames = 0;
        fps = 0;

        // create a image to draw to to match 0,0 up correctly.
        drawing = (BufferedImage) this.createImage( getWidth(), getHeight() );

        while ( isRunning ) {
            // relating to updating animations and calculating FPS
            long elapsedTime = System.nanoTime() - oldTime;
            oldTime = oldTime + elapsedTime; //update for the next loop iteration
            nanoseconds = nanoseconds + elapsedTime;
            frames = frames + 1;
            if ( nanoseconds >= 1000000000 ) {
                fps = frames;
                nanoseconds = nanoseconds - 1000000000;
                frames = 0;
            }
            // enter the method to update everything
            update( elapsedTime );

            // related to drawing
            Graphics2D g = null;
            try {
                g = (Graphics2D) bufferStrategy.getDrawGraphics();
                draw( g ); // enter the method to draw everything
            }
            finally {
                g.dispose();
            }
            if ( !bufferStrategy.contentsLost() ) {
                bufferStrategy.show();
            }
            Toolkit.getDefaultToolkit().sync(); // prevents possible event queue problems in Linux

            if ( isFpsLimited ) {
                // sleep to let the processor handle other programs running
                try {
                    // comment this out to not limit the FPS
//                    LockSupport.parkNanos( 30000000 );
//                    Thread.sleep(30,1);
//                    Thread.yield();
                }
                catch ( Exception e ) {}
                ;
            }
        }
    }

    /**
     * Updates any objects that need to know how much time has elapsed
     * to update animations and locations
     *
     * @param elapsedTime How much time has elapsed since the last update
     */
    public void update( long elapsedTime ) {
        for ( MovingCircle circle : circles ) {
            circle.move( elapsedTime );
        }
    }

    /**
     * Draws the whole program, including all animations and Swing components
     *
     * @param g The program's window's graphics object to draw too
     */
    public void draw( Graphics2D g ) {
        Graphics2D drawingBoard = drawing.createGraphics();
        drawingBoard.setColor( Color.LIGHT_GRAY );
        drawingBoard.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                                       RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        drawingBoard.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON );
        // draw over it to create a blank background again, (or you could draw
        // a background image if you had one
        drawingBoard.fillRect( 0, 0, drawing.getWidth(), drawing.getHeight() );

        // now draw everything to drawingBoard, location 0,0 will be top left corner
        // within the borders of the window
        drawingBoard.setColor( circleColor );
        for ( MovingCircle circle : circles ) {
            circle.draw( drawingBoard );
        }
        drawingBoard.setColor( Color.WHITE );
        drawingBoard.drawString( "FPS: " + fps, 0, drawingBoard.getFont().getSize() );
        // NOTE: this will now cap the FPS (frames per second), of the program to
        // a max of 100 (1000 nanoseconds in a second, divided by 10 nanoseconds
        // of rest per update = 100 updates max).

        getLayeredPane().paintComponents( drawingBoard ); // paint our Swing components
        // NOTE: make sure you do paint your own graphics first

        // now draw the drawing board to correct area of the JFrame's buffer
        g.drawImage( drawing, insets.left, insets.top, null );

        drawingBoard.dispose();
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == changeColor ) {
            circleColor = new Color( random.nextInt( 256 ), random.nextInt( 256 ), random.nextInt( 256 ) );
        }
        if ( e.getSource() == limitFps ) {
            isFpsLimited = !isFpsLimited;
            if ( isFpsLimited ) {
                limitFps.setText( "Unlimit FPS" );
            }
            else {
                limitFps.setText( "Limit FPS" );
            }
        }
    }

    /**
     * A moving circle is a circle that moves around the screen bouncing off walls
     *
     * @author James Murphy
     */
    class MovingCircle {
        private float x;
        private float y;
        private int width;
        private int height;
        private boolean down;
        private boolean right;
        private float speed; // pixels per nanosecond

        public MovingCircle( float x, float y, int width, int height,
                             boolean down, boolean right, float speed ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.down = down;
            this.right = right;
            // convert pixels per millisecond to nano second
            // a lot easier to originally think about speeds in milliseconds
            this.speed = speed / 1000000;
        }

        /**
         * Move the circle, and detect collisions
         */
        public void move( long elapsedTime ) {
            float pixelMovement = elapsedTime * speed;
//            if (down)
//            {
//                y = y + pixelMovement;
//            }
//            else
//            {
//                y = y - pixelMovement;
//            }
            if ( right ) {
                x = x + pixelMovement;
            }
            else {
                x = x - pixelMovement;
            }

            // test if circle hit a side of the window
            // move the circle off the wall also to prevent collision sticking
            if ( y < 0 ) {
                down = !down;
                y = 0;
            }
            if ( y > ( getHeight() - insets.top - insets.bottom - height ) ) {
                down = !down;
                y = getHeight() - insets.top - insets.bottom - height;
            }
            if ( x < 0 ) {
                right = !right;
                x = 0;
            }
            if ( x > ( getWidth() - insets.left - insets.right - width ) ) {
                right = !right;
                x = getWidth() - insets.left - insets.right - width;
            }
        }

        /**
         * Draw the circle
         */
        public void draw( Graphics g ) {
            g.fillOval( (int) x, (int) y, width, height );
        }
    }

    /**
     * UnRepaintManager is a RepaintManager that removes the functionality
     * of the original RepaintManager for us so we don't have to worry about
     * Java repainting on it's own.
     */
    class UnRepaintManager extends RepaintManager {
        public void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
        }

        public void addInvalidComponent( JComponent invalidComponent ) {
        }

        public void markCompletelyDirty( JComponent aComponent ) {
        }

        public void paintDirtyRegions() {
        }
    }
}