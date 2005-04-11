/**
 * Class: LaserApplication
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.controller.*;
import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.view.LaserGraphicFactory;
import edu.colorado.phet.lasers.view.LaserMainPanel;

import javax.swing.*;

public class LaserApplication extends PhetApplication {


    public LaserApplication() {
        super( new LaserSystem() );
        this.run();
    }

    public void start() {
        super.start();
    }

    protected PhetMainPanel createMainPanel() {
        return new LaserMainPanel( this );
    }

    protected JMenu createControlsMenu( PhetFrame phetFrame ) {
        return new ControlsMenu( phetFrame, this );
    }


    protected JMenu createTestMenu() {
        return null;
    }

    public GraphicFactory getGraphicFactory() {
        return LaserGraphicFactory.instance();
    }

    protected PhetAboutDialog getAboutDialog( PhetFrame phetFrame ) {
        return null;
    }

    protected Config getConfig() {
        return LaserConfig.instance();
    }


    //
    // Static fields and methods
    //
    public static void main( String[] args ) {
        LaserApplication application = new LaserApplication();
        application.start();
    }
}
