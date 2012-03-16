
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.flashcommon.UpdateHandler;

public interface IView {
    function update():void;
    function setNbrMasses():void;
    function onModeChange():void;
}
}
