package edu.colorado.phet.glaciers.view;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.GlaciersImages;


public class SnowfallNode extends PhetPNode {

    public static Icon createIcon() {
        return new ImageIcon( GlaciersImages.SNOWFLAKE );
    }
}
