package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Smooth Metal Look And Feel. An enhanced version of the standard Metal Look
 * And Feel by Sun Microsystems. This version uses the capabilities of the Java
 * 2D API to create better looking, fully anti-aliased versions of the
 * controls.
 * <p/>
 * It is mainly intended as a technology demonstration and research project. The
 * code is not optimized for speed or stability. Use it at your own risk.
 *
 * @author Marcel Offermans
 */
public class SmoothLookAndFeel
        extends MetalLookAndFeel
        implements smooth.SmoothLookAndFeel {

    protected static final String SMOOTH_PACKAGE = "smooth.metal.";

    protected void initComponentDefaults( final UIDefaults uidefaults ) {
        super.initComponentDefaults( uidefaults );

        // first line doesn't work, second does, third is a workaround
//		Object buttonBorder = new UIDefaults.ProxyLazyValue("smooth.metal.SmoothBorders", "getButtonBorder");
//		Object buttonBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders", "getButtonBorder");

        // see above, don't use the ProxyLazyValue construction for now, because it
        // does not seem to work very well

        // create a map of all the features we want to modify and install them
        uidefaults.putDefaults( new Object[]{
                "RootPane.frameBorder", SmoothBorders.getFrameBorder(),
                "InternalFrame.border", SmoothBorders.getInternalFrameBorder(),
                "InternalFrame.optionDialogBorder",
                SmoothBorders.getOptionDialogBorder(),
                "InternalFrame.paletteBorder", SmoothBorders.getPaletteBorder(),
                // Use the metal sounds.
                "OptionPane.informationSound",
                "/javax/swing/plaf/metal/sounds/OptionPaneInformation.wav",
                "OptionPane.warningSound",
                "/javax/swing/plaf/metal/sounds/OptionPaneWarning.wav",
                "OptionPane.errorSound",
                "/javax/swing/plaf/metal/sounds/OptionPaneError.wav",
                "OptionPane.questionSound",
                "/javax/swing/plaf/metal/sounds/OptionPaneQuestion.wav",
                "Button.border", SmoothBorders.getButtonBorder(),
                "RadioButton.icon", SmoothIconFactory.getRadioButtonIcon(),
// add in dialog icons
                "OptionPane.errorIcon", makeIcon(
                MetalLookAndFeel.class, "icons/Error.gif" ),
                "OptionPane.informationIcon", makeIcon(
                MetalLookAndFeel.class, "icons/Inform.gif" ),
                "OptionPane.warningIcon", makeIcon(
                MetalLookAndFeel.class, "icons/Warn.gif" ),
                "OptionPane.questionIcon", makeIcon(
                MetalLookAndFeel.class, "icons/Question.gif" ),
        } );
    }

    protected void initClassDefaults( final UIDefaults uidefaults ) {
        super.initClassDefaults( uidefaults );

        // create a map of all the classes we provide and install them
        uidefaults.putDefaults( new Object[]{
                "ButtonUI", SMOOTH_PACKAGE + "SmoothButtonUI",
                "CheckBoxUI", SMOOTH_PACKAGE + "SmoothCheckBoxUI",
                "CheckBoxMenuItemUI", SMOOTH_BASIC + "SmoothCheckBoxMenuItemUI",
                "ComboBoxUI", SMOOTH_PACKAGE + "SmoothComboBoxUI",
                "DesktopIconUI", SMOOTH_PACKAGE + "SmoothDesktopIconUI",
                "EditorPaneUI", SMOOTH_BASIC + "SmoothEditorPaneUI",
                "FileChooserUI", SMOOTH_PACKAGE + "SmoothFileChooserUI",
                "FormattedTextFieldUI",
                SMOOTH_BASIC + "SmoothFormattedTextFieldUI",
                "InternalFrameUI", SMOOTH_PACKAGE + "SmoothInternalFrameUI",
                "LabelUI", SMOOTH_PACKAGE + "SmoothLabelUI",
                "MenuUI", SMOOTH_BASIC + "SmoothMenuUI",
                "MenuBarUI", SMOOTH_BASIC + "SmoothMenuBarUI",
                "MenuItemUI", SMOOTH_BASIC + "SmoothMenuItemUI",
                "PasswordFieldUI", SMOOTH_BASIC + "SmoothPasswordFieldUI",
                "PanelUI", SMOOTH_BASIC + "SmoothPanelUI",
                "ProgressBarUI", SMOOTH_PACKAGE + "SmoothProgressBarUI",
                "PopupMenuSeparatorUI",
                SMOOTH_PACKAGE + "SmoothPopupMenuSeparatorUI",
                "RadioButtonUI", SMOOTH_PACKAGE + "SmoothRadioButtonUI",
                "RadioButtonMenuItemUI",
                SMOOTH_BASIC + "SmoothRadioButtonMenuItemUI",
                "ScrollBarUI", SMOOTH_PACKAGE + "SmoothScrollBarUI",
                "ScrollPaneUI", SMOOTH_PACKAGE + "SmoothScrollPaneUI",
                "SplitPaneUI", SMOOTH_PACKAGE + "SmoothSplitPaneUI",
                "SliderUI", SMOOTH_PACKAGE + "SmoothSliderUI",
                "SeparatorUI", SMOOTH_PACKAGE + "SmoothSeparatorUI",
                "TabbedPaneUI", SMOOTH_PACKAGE + "SmoothTabbedPaneUI",
                "TextAreaUI", SMOOTH_BASIC + "SmoothTextAreaUI",
                "TextFieldUI", SMOOTH_PACKAGE + "SmoothTextFieldUI",
                "TextPaneUI", SMOOTH_BASIC + "SmoothTextPaneUI",
                "ToggleButtonUI", SMOOTH_PACKAGE + "SmoothToggleButtonUI",
                "ToolBarUI", SMOOTH_PACKAGE + "SmoothToolBarUI",
                "ToolTipUI", SMOOTH_PACKAGE + "SmoothToolTipUI",
                "TreeUI", SMOOTH_PACKAGE + "SmoothTreeUI"
        } );
    }

    public String getID() {
        return "SmoothMetal";
    }

    public String getDescription() {
        return "The Smooth Metal Look and Feel";
    }

    public String getName() {
        return "SmoothMetal";
    }

    public boolean isAntiAliasing() {
        return SmoothUtilities.isAntialias();
    }

    public void setAntiAliasing( final boolean on ) {
        SmoothUtilities.setAntialias( on );
    }
}