package edu.colorado.phet.allsims;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 4:34:45 AM
 */
public class AllSimsApplication {
    private Timer frameTimer;
    private JFrame frame = new AllSimsFrame();

    public AllSimsApplication() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "all-sims/all.txt" );
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( stream ) );
        String line = bufferedReader.readLine();
        ArrayList sims = new ArrayList();
        while( line != null && line.trim().length() > 0 ) {
            sims.add( new Simulation( "all-sims/" + line.trim() ) );
            line = bufferedReader.readLine();
        }
        final JList list = new JList( sims.toArray() );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        list.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                if( !e.getValueIsAdjusting() ) {
                    Simulation sim = (Simulation)list.getSelectedValue();
                    sim.launch();
                }
            }
        } );
        frame.setContentPane( new JScrollPane( list ) );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frameTimer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Frame[] f = Frame.getFrames();
                if( f.length != lastFrameCount ) {
                    System.out.println( "Number of frames: " + f.length );
                    lastFrameCount = f.length;
                }
//                System.out.print( "f.length = " + f.length+", " );
                updateFrames( f );
            }
        } );
    }

    private void updateFrames( Frame[] f ) {
        for( int i = 0; i < f.length; i++ ) {
            updateFrame( f[i] );
        }
    }

    private void updateFrame( Frame frame ) {
        if( isOtherSim( frame ) ) {
            JFrame jFrame = (JFrame)frame;
            jFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        }
    }

    private boolean isOtherSim( Frame frame ) {
        return frame instanceof JFrame && frame != this.frame && !( frame instanceof AllSimsFrame );
    }

    int lastFrameCount = -1;

    static class Simulation {
        private Properties properties;

        public Simulation( String url ) throws IOException {
            System.out.println( "url = " + url );
            properties = new Properties();
            properties.load( Thread.currentThread().getContextClassLoader().getResourceAsStream( url ) );
        }

        /**
         * project.depends.data=data
         * project.depends.source=src
         * project.depends.lib=phetcommon
         * project.mainclass=edu.colorado.phet.efield.electron.electricField.EFieldApplication
         * project.name=Electric Field
         * project.description=
         * project.screenshot=
         *
         * @return
         */
        public String toString() {
            return getName();
        }

        public Class getMainClass() throws ClassNotFoundException {
            return Class.forName( properties.getProperty( "project.mainclass" ) );
        }

        private String getName() {
            return properties.getProperty( "project.name" );
        }

        public void launch() {//could start as new process
            Thread t = new Thread( new Runnable() {
                public void run() {
                    try {
                        Class sim = getMainClass();
                        Method m = sim.getMethod( "main", new Class[]{String[].class} );
                        m.invoke( null, new Object[]{new String[0]} );
                    }
                    catch( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                    catch( NoSuchMethodException e ) {
                        e.printStackTrace();
                    }
                    catch( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            } );
            t.start();
        }
    }

    public static void copyPropertiesToData( File simulations, File data ) throws IOException {
        data.mkdirs();
        BufferedWriter allWriter = new BufferedWriter( new FileWriter( new File( data, "all.txt" ) ) );
        BufferedWriter classWriter = new BufferedWriter( new FileWriter( new File( data, "main-classes.txt" ) ) );
        File[] f = simulations.listFiles();
        for( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            File propertyFile = new File( file, file.getName() + ".properties" );
            if( propertyFile.exists() ) {

                copyFile( propertyFile, data );
                allWriter.write( propertyFile.getName() );
                allWriter.newLine();

                Properties prop = new Properties();
                prop.load( new FileInputStream( propertyFile ) );
                String mainClass = prop.getProperty( "project.mainclass" );
                classWriter.write( mainClass );
                if( i < f.length - 1 ) {
                    classWriter.write( " : " );
                }
            }
        }
        allWriter.close();
        classWriter.close();
    }

    private static void copyFile( File propertyFile, File data ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( propertyFile ) );
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( new File( data, propertyFile.getName() ) ) );
        String line = bufferedReader.readLine();
        while( line != null ) {
            bufferedWriter.write( line );
            bufferedWriter.newLine();
            line = bufferedReader.readLine();
        }
        bufferedWriter.close();
        bufferedReader.close();
    }

    static class AllSimsFrame extends JFrame {
        public AllSimsFrame() {
            super( "PhET Simulations" );
        }
    }

    public static void main( String[] args ) throws IOException {
//        copyPropertiesToData( new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" ), new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations\\all-sims\\data\\all-sims" ) );
        new AllSimsApplication().start();
    }

    public void start() {
        frame.setVisible( true );
        frameTimer.start();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
                    Frame[] f = Frame.getFrames();
                    for( int i = 0; i < f.length; i++ ) {
                        if( isOtherSim( f[i] ) ) {
                            f[i].dispose();
                        }
                    }
                }
                return false;// Do not consume event
            }
        } );
    }
}
