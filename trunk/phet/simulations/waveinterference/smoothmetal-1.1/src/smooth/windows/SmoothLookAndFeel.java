package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import smooth.util.SmoothUtilities;
import sun.awt.shell.ShellFolder;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;

/**
 * Smooth Windows. Adds anti-aliasing to Windows LnF.
 *
 * @author James Shiell
 * @author Marcel Offermans
 */
public class SmoothLookAndFeel extends WindowsLookAndFeel implements smooth.SmoothLookAndFeel {

    protected static final String SMOOTH_PACKAGE = "smooth.windows.";

    protected void initComponentDefaults( UIDefaults uidefaults ) {
        super.initComponentDefaults( uidefaults );

        // first line doesn't work, second does, third is a workaround
//		Object buttonBorder = new UIDefaults.ProxyLazyValue("smooth.metal.SmoothBorders", "getButtonBorder");
//		Object buttonBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders", "getButtonBorder");
        final Object buttonBorder = SmoothBorders.getButtonBorder();

        // see above, don't use the ProxyLazyValue construction for now, because it
        // does not seem to work very well
        final Object radioButtonIcon = SmoothIconFactory.getRadioButtonIcon();

        // create a map of all the features we want to modify and install them
        final Object classMap[] = {
                "Button.border", buttonBorder,
                "RadioButton.icon", radioButtonIcon,

                // add in dialog icons
                "OptionPane.errorIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Error.gif" ),
                "OptionPane.informationIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Inform.gif" ),
                "OptionPane.warningIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Warn.gif" ),
                "OptionPane.questionIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Question.gif" ),

                // add in tree icons
                "Tree.leafIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/TreeLeaf.gif" ),
                "Tree.closedIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/TreeClosed.gif" ),
                "Tree.openIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/TreeOpen.gif" ),

                // filechoose icons
                "FileChooser.homeFolderIcon", new LazyFileChooserIcon( null, "icons/HomeFolder.gif" ),
                "FileChooser.listViewIcon", new LazyFileChooserIcon( "fileChooserIcon ListView", "icons/ListView.gif" ),
                "FileChooser.detailsViewIcon", new LazyFileChooserIcon( "fileChooserIcon DetailsView", "icons/DetailsView.gif" ),
                "FileChooser.upFolderIcon", new LazyFileChooserIcon( "fileChooserIcon UpFolder", "icons/UpFolder.gif" ),
                "FileChooser.newFolderIcon", new LazyFileChooserIcon( "fileChooserIcon NewFolder", "icons/NewFolder.gif" ),

                // file view icons
                "FileView.directoryIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Directory.gif" ),
                "FileView.fileIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/File.gif" ),
                "FileView.computerIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/Computer.gif" ),
                "FileView.hardDriveIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/HardDrive.gif" ),
                "FileView.floppyDriveIcon", LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/FloppyDrive.gif" ),

                // internal frame icons
                "InternalFrame.icon", getInternalFrameIcon()
        };


        uidefaults.putDefaults( classMap );
    }

    /**
     * Used to determine the internal frame icon.
     * This differs between J2 1.4 and 1.5.
     *
     * @return the object to add to InternalFrame.icon
     */
    private Object getInternalFrameIcon() {
        try {
            if( WindowsLookAndFeel.class.getResource( "icons/JavaCup.gif" ) == null ) {
                // if we cannot find the default icon, use 1.5 code
                // To compile with JDK 1.4.2, though, need to use ProxyLazyValue,
                // not SwingLazyValue which is a JDK5-only feature.
                return new UIDefaults.ProxyLazyValue( "com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane$ScalableIconUIResource",
                                                      // The constructor takes one arg: an array of UIDefaults.LazyValue
                                                      // representing the icons
                                                      new Object[][]{{
                                                              LookAndFeel.makeIcon( BasicLookAndFeel.class, "icons/JavaCup16.png" ),
                                                              LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/JavaCup32.png" )
                                                      }} );
            }

            // no class def found will be thrown if JDK < 1.5
        }
        catch( NoClassDefFoundError ignored ) {}

        // else use 1.4 code
        return LookAndFeel.makeIcon( WindowsLookAndFeel.class, "icons/JavaCup.gif" );
    }

    protected void initClassDefaults( UIDefaults uidefaults ) {
        super.initClassDefaults( uidefaults );

        // create a map of all the classes we provide and install them
        final Object classMap[] =
                {
                        "ButtonUI", SMOOTH_PACKAGE + "SmoothButtonUI",
                        "CheckBoxUI", SMOOTH_PACKAGE + "SmoothCheckBoxUI",
                        "CheckBoxMenuItemUI", SMOOTH_PACKAGE + "SmoothCheckBoxMenuItemUI",
                        "ComboBoxUI", SMOOTH_PACKAGE + "SmoothComboBoxUI",
                        "DesktopIconUI", SMOOTH_PACKAGE + "SmoothDesktopIconUI",
                        "EditorPaneUI", SMOOTH_PACKAGE + "SmoothEditorPaneUI",
                        "FileChooserUI", SMOOTH_PACKAGE + "SmoothFileChooserUI",
                        "FormattedTextFieldUI", SMOOTH_BASIC + "SmoothFormattedTextFieldUI",
                        "InternalFrameUI", SMOOTH_PACKAGE + "SmoothInternalFrameUI",
                        "LabelUI", SMOOTH_PACKAGE + "SmoothLabelUI",
                        "MenuUI", SMOOTH_PACKAGE + "SmoothMenuUI",
                        "MenuBarUI", SMOOTH_PACKAGE + "SmoothMenuBarUI",
                        "MenuItemUI", SMOOTH_PACKAGE + "SmoothMenuItemUI",
                        "PasswordFieldUI", SMOOTH_PACKAGE + "SmoothPasswordFieldUI",
                        "PanelUI", SMOOTH_BASIC + "SmoothPanelUI",
                        "ProgressBarUI", SMOOTH_PACKAGE + "SmoothProgressBarUI",
                        "PopupMenuSeparatorUI", SMOOTH_BASIC + "SmoothPopupMenuSeparatorUI",
                        "RadioButtonUI", SMOOTH_PACKAGE + "SmoothRadioButtonUI",
                        "RadioButtonMenuItemUI", SMOOTH_PACKAGE + "SmoothRadioButtonMenuItemUI",
                        "ScrollBarUI", SMOOTH_PACKAGE + "SmoothScrollBarUI",
                        "ScrollPaneUI", SMOOTH_PACKAGE + "SmoothScrollPaneUI",
                        "SpinnerUI", SMOOTH_PACKAGE + "SmoothSpinnerUI",
                        "SplitPaneUI", SMOOTH_PACKAGE + "SmoothSplitPaneUI",
                        "SliderUI", SMOOTH_PACKAGE + "SmoothSliderUI",
                        "SeparatorUI", SMOOTH_PACKAGE + "SmoothSeparatorUI",
                        "TabbedPaneUI", SMOOTH_PACKAGE + "SmoothTabbedPaneUI",
                        "TextAreaUI", SMOOTH_PACKAGE + "SmoothTextAreaUI",
                        "TextFieldUI", SMOOTH_PACKAGE + "SmoothTextFieldUI",
                        "TextPaneUI", SMOOTH_PACKAGE + "SmoothTextPaneUI",
                        "ToggleButtonUI", SMOOTH_PACKAGE + "SmoothToggleButtonUI",
                        "ToolBarUI", SMOOTH_PACKAGE + "SmoothToolBarUI",
                        "ToolTipUI", SMOOTH_BASIC + "SmoothToolTipUI",
                        "TreeUI", SMOOTH_PACKAGE + "SmoothTreeUI"
                };
        uidefaults.putDefaults( classMap );
    }

    public String getID() {
        return "SmoothWindows";
    }

    public String getDescription() {
        return "The Smooth Windows Look and Feel";
    }

    public String getName() {
        return "SmoothWindows";
    }

    public boolean isAntiAliasing() {
        return SmoothUtilities.isAntialias();
    }

    public void setAntiAliasing( final boolean on ) {
        SmoothUtilities.setAntialias( on );
    }

    /**
     * Get an <code>Icon</code> from the native library (comctl32.dll) if available,
     * otherwise get it from an image resource file.
     * <p/>
     * From com.sun.java.swing.plaf.windows.WindowsLookAndFeel, changed getClass() to WindowsLookAndFeel.class
     *
     * @since 1.4
     */
    private static class LazyFileChooserIcon implements UIDefaults.LazyValue {
        private String nativeImage;
        private String resource;

        LazyFileChooserIcon( String nativeImage, String resource ) {
            this.nativeImage = nativeImage;
            this.resource = resource;
        }

        public Object createValue( UIDefaults table ) {
            if( nativeImage != null ) {
                final Image image = (Image)ShellFolder.get( nativeImage );
                return ( image != null ) ? new ImageIcon( image ) : LookAndFeel.makeIcon( WindowsLookAndFeel.class, resource );
            }
            else {
                return LookAndFeel.makeIcon( WindowsLookAndFeel.class, resource );
            }
        }
    }
}