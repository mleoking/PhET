
package edu.colorado.phet.radiatingcharge {
import edu.colorado.phet.flashcommon.UpdateHandler;

public interface IView {
    function update():void;
    function setNbrMasses():void;
    function onModeChange():void;
}
}
