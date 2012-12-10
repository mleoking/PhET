define( [], function () {
    window.console = window.console || {};
    window.console.log = window.console.log || function () {};

    if ( !Object.__proto__ ) {
        Object.defineProperty(
                Object.prototype,
                '__proto__', {
                    get: function () {
                        return Object.getPrototypeOf( this );
                    }
                }
        );
    }
} );
