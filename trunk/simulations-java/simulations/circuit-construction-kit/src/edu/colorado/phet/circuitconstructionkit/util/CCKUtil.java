package edu.colorado.phet.circuitconstructionkit.util;

import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Created by: Sam
 * Feb 13, 2008 at 2:56:31 PM
 */
public class CCKUtil {
    public static void setupLanguagesForSwingComponents() {
        Locale loc = PhetResources.readLocale();
        if ( !loc.getLanguage().equalsIgnoreCase( "en" ) ) {
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
    }
}
