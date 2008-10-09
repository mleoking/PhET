package phet;

import geom.jgv.gui.JGVPanel;

import java.io.*;

import javax.swing.JFrame;


public class MalleyTest extends JFrame {
    
    private static final String GEOMETRY_FILENAME = "cube.xml";
    
    public MalleyTest() {
        super( "Malley Test" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( 640, 480 );
        
        JGVPanel jgvPanel = new JGVPanel( this );
        setContentPane( jgvPanel.cameraCanvas );
       
        jgvPanel.addXMLFile( GEOMETRY_FILENAME );
//        jgvPanel.addGeom( readFile( GEOMETRY_FILENAME ) );
    }

    public static void main( String[] args ) {
        new MalleyTest().setVisible( true );
    }
    
    private static String readFile( String filename ) {
        String text = new String();
        try {
            InputStream inputStream = new FileInputStream( filename );
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                text += line;
                line = bufferedReader.readLine();
                if ( line != null ) {
                    text += System.getProperty( "line.separator" );
                }
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return text;
    }
}
