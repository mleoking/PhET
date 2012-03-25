/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 3/25/12
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import flash.display.Sprite;

public class MiniTabBar extends Sprite{
    private var tabH: MiniTab;
    private var tabV: MiniTab;
    public function MiniTabBar() {
        this.tabH = new MiniTab() ;
        this.tabV = new MiniTab();
    } //end constructor
} //end class
} //end package
