
package edu.colorado.phet.triglab {
import edu.colorado.phet.flashcommon.UpdateHandler;

public interface IView {
    function update():void;
    function setNbrMasses():void;
    function onModeChange():void;
}
}
