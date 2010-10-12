package edu.colorado.phet.densityandbuoyancy.common {
import mx.core.UIComponent;

public class Module {
    private var _title: String;
    private var _component: UIComponent;

    public function Module( title: String, component: UIComponent ) {
        this._title = title;
        this._component = component;
    }

    public function get title(): String {
        return _title;
    }

    public function get component(): UIComponent {
        return _component;
    }

    public function init(): void {

    }

    public function get running(): Boolean {
        throw new Error( "abstract method error" );
    }

    public function set running( r: Boolean ): void {
        throw new Error( "abstract method error" );
    }
}
}