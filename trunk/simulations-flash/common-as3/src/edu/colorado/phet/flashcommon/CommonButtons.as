package edu.colorado.phet.flashcommon {
import flash.display.Sprite;
import flash.events.MouseEvent;

import org.aswing.AsWingConstants;
import org.aswing.CenterLayout;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JWindow;

public class CommonButtons extends JWindow {

    // shorthand for debugging function
    public function debug( str: String ): void {
        FlashCommon.getInstance().debug( str );
    }

    public function CommonButtons( root: Sprite ) {
        super( root, false );
        debug( "CommonButtons initializing\n" );

        // flow layout will be left-to-right. it is possible to either reverse the direction,
        // or if vertical layout is desired use SoftBoxLayout(SoftBoxLayout.Y_AXIS) instead
        getContentPane().setLayout( new FlowLayout( AsWingConstants.LEFT, 0, 0 ) );

        var aboutButton: JButton = new JButton( CommonStrings.get( "About...", "About..." ) );
        aboutButton.addEventListener( MouseEvent.CLICK, aboutButtonClicked );
        getContentPane().append( aboutButton );

        if ( !FlashCommon.getInstance().fromPhetWebsite() ) {
            var preferencesButton: JButton = new JButton( CommonStrings.get( "Preferences", "Preferences..." ) );
            preferencesButton.addEventListener( MouseEvent.CLICK, preferencesButtonClicked );
            getContentPane().append( preferencesButton );
        }

        pack();
        show();

        //		common.tabHandler.insertControl(_level0.aboutButton.trigger_mc, 0);
        //		common.tabHandler.registerButton(_level0.aboutButton.trigger_mc);
        //		common.tabHandler.insertControl(_level0.preferencesButton.trigger_mc, 1);
        //		common.tabHandler.registerButton(_level0.preferencesButton.trigger_mc);

    }

    public function aboutButtonClicked( evt: MouseEvent ): void {
        CommonDialog.openAboutDialog();
    }

    public function preferencesButtonClicked( evt: MouseEvent ): void {
        CommonDialog.openPreferencesDialog();
    }

    // creates padding around the text inside a button, and allows the button
    // to shrink down to the preferred size in the center of its parent location.
    // this is done by creating an anonymous panel, adding the button to it, and
    // adding the panel to the container
    public static function padButtonAdd( button: JButton, container: Container ): void {

        // create panel which will center the button
        var panel: JPanel = new JPanel( new CenterLayout() );

        // pad the text by 10 pixels to the left and right
        button.setMargin( new Insets( 0, 10, 0, 10 ) );

        // connect everything together
        panel.append( button );
        container.append( panel );
    }
}
}