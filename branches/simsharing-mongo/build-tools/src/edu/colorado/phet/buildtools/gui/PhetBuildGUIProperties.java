package edu.colorado.phet.buildtools.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Properties file that holds persisent information for PhetBuildGUI.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetBuildGUIProperties extends AbstractPropertiesFile {

    private static final String FILENAME = System.getProperty( "user.home" ) + "/.phet/phet-build-gui.properties";

    // property names
    private static final String PROPERTY_PROJECT_SELECTED = "project.selected";
    private static final String PROPERTY_SIM_SELECTED = "sim.selected"; //the last sim that the user selected in the list, see #2336
    private static final String PROPERTY_FRAME_X = "frame.x";
    private static final String PROPERTY_FRAME_Y = "frame.y";
    private static final String PROPERTY_FRAME_WIDTH = "frame.width";
    private static final String PROPERTY_FRAME_HEIGHT = "frame.height";

    // default property values
    private static final int DEFAULT_FRAME_X = 0;
    private static final int DEFAULT_FRAME_Y = 0;
    private static final int DEFAULT_FRAME_WIDTH = 1024;
    private static final int DEFAULT_FRAME_HEIGHT = 550;


    public static final PhetBuildGUIProperties INSTANCE = new PhetBuildGUIProperties();

    public static PhetBuildGUIProperties getInstance() {
        return INSTANCE;
    }

    /* singleton */
    private PhetBuildGUIProperties() {
        super( new File( FILENAME ) );
    }

    public String getProjectSelected() {
        return getProperty( PROPERTY_PROJECT_SELECTED );
    }

    public void setProjectSelected( String value ) {
        setProperty( PROPERTY_PROJECT_SELECTED, value );
    }

    public String getSimSelected() {
        return getProperty( PROPERTY_SIM_SELECTED );
    }

    public void setSimSelected( String value ) {
        setProperty( PROPERTY_SIM_SELECTED, value );
    }

    public Point getFrameLocation() {
        return new Point( getFrameX(), getFrameY() );
    }

    public void setFrameLocation( Point p ) {
        setFrameX( p.x );
        setFrameY( p.y );
    }

    private int getFrameX() {
        return getPropertyInt( PROPERTY_FRAME_X, DEFAULT_FRAME_X );
    }

    private void setFrameX( int x ) {
        setProperty( PROPERTY_FRAME_X, x );
    }

    private int getFrameY() {
        return getPropertyInt( PROPERTY_FRAME_Y, DEFAULT_FRAME_Y );
    }

    private void setFrameY( int y ) {
        setProperty( PROPERTY_FRAME_Y, y );
    }

    public Dimension getFrameSize() {
        return new Dimension( getFrameWidth(), getFrameHeight() );
    }

    public void setFrameSize( Dimension size ) {
        setFrameWidth( size.width );
        setFrameHeight( size.height );
    }

    private int getFrameWidth() {
        return getPropertyInt( PROPERTY_FRAME_WIDTH, DEFAULT_FRAME_WIDTH );
    }

    private void setFrameWidth( int width ) {
        setProperty( PROPERTY_FRAME_WIDTH, width );
    }

    private int getFrameHeight() {
        return getPropertyInt( PROPERTY_FRAME_HEIGHT, DEFAULT_FRAME_HEIGHT );
    }

    private void setFrameHeight( int height ) {
        setProperty( PROPERTY_FRAME_HEIGHT, height );
    }


}
