package edu.colorado.phet.cck;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.cck.controls.OptionsMenu;
import edu.colorado.phet.cck.piccolo_cck.CCKPiccoloModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetApplication;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 */

public class CCKApplication extends PhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "cck/localization/cck-strings";
    private CCKPiccoloModule cckPiccoloModule;
    public static final String AC_OPTION = "-dynamics";

    public CCKApplication( String[] args ) throws IOException {
        super( new PhetApplicationConfig( args, createFrameSetup(), PhetResources.forProject( "cck" ), isDynamic( args ) ? "cck-ac" : "cck-dc" ) );

        boolean debugMode = false;
        if ( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckPiccoloModule = new CCKPiccoloModule( args );
        cckPiccoloModule.getCckSimulationPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F1 ) {
                    getPhetFrame().setSize( 1024, 768 );
                    getPhetFrame().invalidate();
                    getPhetFrame().validate();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        Module[] modules = new Module[]{cckPiccoloModule};
        setModules( modules );
        if ( getPhetFrame().getTabbedModulePane() != null ) {
            getPhetFrame().getTabbedModulePane().setLogoVisible( false );
        }
        getPhetFrame().addMenu( new OptionsMenu( this, cckPiccoloModule ) );//todo options menu
    }

    private static FrameSetup createFrameSetup() {
        if ( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    private static boolean isDynamic( String[] args ) {
        return Arrays.asList( args ).contains( AC_OPTION );
    }

    public void startApplication() {
        super.startApplication();
        cckPiccoloModule.applicationStarted();
    }

    private static String readVersion() {
        return PhetApplicationConfig.getVersion( "cck" ).formatForTitleBar();
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
//        Locale.setDefault( new Locale( "ar" ) );
//        Locale.setDefault( new Locale( "ja" ) );
//        Locale.setDefault( new Locale( "el" ) );
        Locale loc = PhetResources.readLocale();
        if ( !loc.getLanguage().equalsIgnoreCase( "en" )||true ) {
            UIManager.put( "ColorChooser.hsbBlueText", CCKResources.getString( "ColorChooser.hsbBlueText" ) );
            UIManager.put( "ColorChooser.hsbRedText", CCKResources.getString( "ColorChooser.hsbRedText" ) );
            UIManager.put( "ColorChooser.rgbRedText", CCKResources.getString( "ColorChooser.rgbRedText" ) );
            UIManager.put( "ColorChooser.hsbSaturationText", CCKResources.getString( "ColorChooser.hsbSaturationText" ) );
            UIManager.put( "ColorChooser.rgbGreenText", CCKResources.getString( "ColorChooser.rgbGreenText" ) );
            UIManager.put( "ColorChooser.previewText", CCKResources.getString( "ColorChooser.previewText" ) );
            UIManager.put( "ColorChooser.sampleText", CCKResources.getString( "ColorChooser.sampleText" ) );
            UIManager.put( "ColorChooser.hsbHueText", CCKResources.getString( "ColorChooser.hsbHueText" ) );
            UIManager.put( "ColorChooser.swatchesNameText", CCKResources.getString( "ColorChooser.swatchesNameText" ) );
            UIManager.put( "ColorChooser.resetText", CCKResources.getString( "ColorChooser.resetText" ) );
            UIManager.put( "ColorChooser.rgbBlueText", CCKResources.getString( "ColorChooser.rgbBlueText" ) );
            UIManager.put( "ColorChooser.okText", CCKResources.getString( "ColorChooser.okText" ) );
            UIManager.put( "ColorChooser.hsbGreenText", CCKResources.getString( "ColorChooser.hsbGreenText" ) );
            UIManager.put( "ColorChooser.rgbNameText", CCKResources.getString( "ColorChooser.rgbNameText" ) );
            UIManager.put( "ColorChooser.hsbBrightnessText", CCKResources.getString( "ColorChooser.hsbBrightnessText" ) );
            UIManager.put( "ColorChooser.swatchesRecentText", CCKResources.getString( "ColorChooser.swatchesRecentText" ) );
            UIManager.put( "ColorChooser.cancelText", CCKResources.getString( "ColorChooser.cancelText" ) );
            UIManager.put( "ColorChooser.hsbNameText", CCKResources.getString( "ColorChooser.hsbNameText" ) );

            UIManager.put( "FileChooser.detailsViewActionLabelText", CCKResources.getString( "FileChooser.detailsViewActionLabelText" ) );
            UIManager.put( "FileChooser.detailsViewButtonAccessibleName", CCKResources.getString( "FileChooser.detailsViewButtonAccessibleName" ) );
            UIManager.put( "FileChooser.detailsViewButtonToolTipText", CCKResources.getString( "FileChooser.detailsViewButtonToolTipText" ) );
            UIManager.put( "FileChooser.fileAttrHeaderText", CCKResources.getString( "FileChooser.fileAttrHeaderText" ) );
            UIManager.put( "FileChooser.fileDateHeaderText", CCKResources.getString( "FileChooser.fileDateHeaderText" ) );
            UIManager.put( "FileChooser.fileNameHeaderText", CCKResources.getString( "FileChooser.fileNameHeaderText" ) );
            UIManager.put( "FileChooser.fileNameLabelText", CCKResources.getString( "FileChooser.fileNameLabelText" ) );
            UIManager.put( "FileChooser.fileSizeHeaderText", CCKResources.getString( "FileChooser.fileSizeHeaderText" ) );
            UIManager.put( "FileChooser.fileTypeHeaderText", CCKResources.getString( "FileChooser.fileTypeHeaderText" ) );
            UIManager.put( "FileChooser.filesOfTypeLabelText", CCKResources.getString( "FileChooser.filesOfTypeLabelText" ) );
            UIManager.put( "FileChooser.homeFolderAccessibleName", CCKResources.getString( "FileChooser.homeFolderAccessibleName" ) );
            UIManager.put( "FileChooser.homeFolderToolTipText", CCKResources.getString( "FileChooser.homeFolderToolTipText" ) );
            UIManager.put( "FileChooser.listViewActionLabelText", CCKResources.getString( "FileChooser.listViewActionLabelText" ) );
            UIManager.put( "FileChooser.listViewButtonAccessibleName", CCKResources.getString( "FileChooser.listViewButtonAccessibleName" ) );
            UIManager.put( "FileChooser.listViewButtonToolTipText", CCKResources.getString( "FileChooser.listViewButtonToolTipText" ) );
            UIManager.put( "FileChooser.lookInLabelText", CCKResources.getString( "FileChooser.lookInLabelText" ) );
            UIManager.put( "FileChooser.newFolderAccessibleName", CCKResources.getString( "FileChooser.newFolderAccessibleName" ) );
            UIManager.put( "FileChooser.newFolderActionLabelText", CCKResources.getString( "FileChooser.newFolderActionLabelText" ) );
            UIManager.put( "FileChooser.newFolderToolTipText", CCKResources.getString( "FileChooser.newFolderToolTipText" ) );
            UIManager.put( "FileChooser.refreshActionLabelText", CCKResources.getString( "FileChooser.refreshActionLabelText" ) );
            UIManager.put( "FileChooser.saveInLabelText", CCKResources.getString( "FileChooser.saveInLabelText" ) );
            UIManager.put( "FileChooser.upFolderAccessibleName", CCKResources.getString( "FileChooser.upFolderAccessibleName" ) );
            UIManager.put( "FileChooser.upFolderToolTipText", CCKResources.getString( "FileChooser.upFolderToolTipText" ) );
            UIManager.put( "FileChooser.viewMenuLabelText", CCKResources.getString( "FileChooser.viewMenuLabelText" ) );
            UIManager.put( "FileChooser.acceptAllFileFilterText", CCKResources.getString( "FileChooser.acceptAllFileFilterText" ) );
            UIManager.put( "FileChooser.cancelButtonText", CCKResources.getString( "FileChooser.cancelButtonText" ) );
            UIManager.put( "FileChooser.cancelButtonToolTipText", CCKResources.getString( "FileChooser.cancelButtonToolTipText" ) );
            UIManager.put( "FileChooser.enterFileNameLabelText", CCKResources.getString( "FileChooser.enterFileNameLabelText" ) );
            UIManager.put( "FileChooser.filesLabelText", CCKResources.getString( "FileChooser.filesLabelText" ) );
            UIManager.put( "FileChooser.filterLabelText", CCKResources.getString( "FileChooser.filterLabelText" ) );
            UIManager.put( "FileChooser.foldersLabelText", CCKResources.getString( "FileChooser.foldersLabelText" ) );
            UIManager.put( "FileChooser.helpButtonText", CCKResources.getString( "FileChooser.helpButtonText" ) );
            UIManager.put( "FileChooser.helpButtonToolTipText", CCKResources.getString( "FileChooser.helpButtonToolTipText" ) );
            UIManager.put( "FileChooser.openButtonText", CCKResources.getString( "FileChooser.openButtonText" ) );
            UIManager.put( "FileChooser.openButtonToolTipText", CCKResources.getString( "FileChooser.openButtonToolTipText" ) );
            UIManager.put( "FileChooser.openDialogTitleText", CCKResources.getString( "FileChooser.openDialogTitleText" ) );
            UIManager.put( "FileChooser.pathLabelText", CCKResources.getString( "FileChooser.pathLabelText" ) );
            UIManager.put( "FileChooser.saveButtonText", CCKResources.getString( "FileChooser.saveButtonText" ) );
            UIManager.put( "FileChooser.saveButtonToolTipText", CCKResources.getString( "FileChooser.saveButtonToolTipText" ) );
            UIManager.put( "FileChooser.saveDialogTitleText", CCKResources.getString( "FileChooser.saveDialogTitleText" ) );
            UIManager.put( "FileChooser.updateButtonText", CCKResources.getString( "FileChooser.updateButtonText" ) );
            UIManager.put( "FileChooser.updateButtonToolTipText", CCKResources.getString( "FileChooser.updateButtonToolTipText" ) );
        }

        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                new CCKPhetLookAndFeel().initLookAndFeel();
                try {
                    CCKApplication cckApplication = new CCKApplication( args );
                    cckApplication.startApplication();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

}
