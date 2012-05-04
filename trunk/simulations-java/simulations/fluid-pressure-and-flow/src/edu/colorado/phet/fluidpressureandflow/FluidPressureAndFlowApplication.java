// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory.Listener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;

/**
 * Main application class for "Fluid Pressure and Flow"
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowApplication extends PiccoloPhetApplication {
    public static final Property<Color> dirtTopColor = new Property<Color>( new Color( 157, 139, 97 ) );
    public static final Property<Color> dirtBottomColor = new Property<Color>( new Color( 100, 90, 60 ) );

    public FluidPressureAndFlowApplication( PhetApplicationConfig config ) {
        super( config );
        getPhetFrame().getDeveloperMenu().add( new JMenuItem( "Dirt top color" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    ColorChooserFactory.showDialog( "Dirt top color", getPhetFrame(), dirtTopColor.get(), new Listener() {
                        public void colorChanged( final Color color ) {
                            dirtTopColor.set( color );
                        }

                        public void ok( final Color color ) {
                            dirtTopColor.set( color );
                        }

                        public void cancelled( final Color originalColor ) {
                            dirtTopColor.set( originalColor );
                        }
                    }, true );
                }
            } );
        }} );
        getPhetFrame().getDeveloperMenu().add( new JMenuItem( "Dirt bottom color" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    ColorChooserFactory.showDialog( "Dirt bottom color", getPhetFrame(), dirtBottomColor.get(), new Listener() {
                        public void colorChanged( final Color color ) {
                            dirtBottomColor.set( color );
                        }

                        public void ok( final Color color ) {
                            dirtBottomColor.set( color );
                        }

                        public void cancelled( final Color originalColor ) {
                            dirtBottomColor.set( originalColor );
                        }
                    }, true );
                }
            } );
        }} );
        addModule( new FluidPressureModule() );
        addModule( new FluidFlowModule() );
        addModule( new WaterTowerModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, FluidPressureAndFlowResources.PROJECT_NAME, FluidPressureAndFlowApplication.class );
    }
}