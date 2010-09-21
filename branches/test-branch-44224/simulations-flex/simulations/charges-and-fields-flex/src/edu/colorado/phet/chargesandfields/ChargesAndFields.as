import flash.ui.ContextMenu;
import mx.core.Application;
private function init() : void {
    var menu : ContextMenu = new ContextMenu();

    menu.builtInItems.zoom = true;

    Application.application.contextMenu = menu;
}