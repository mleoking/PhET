define( ['underscore', 'view/EnergySkateParkCanvas'], function ( _, EnergySkateParkCanvas ) {
    function Tab( $tab, Strings, analytics ) {
        var $canvas = $tab.find( 'canvas' );

        new EnergySkateParkCanvas( $canvas, Strings, analytics );
    }

    Tab.prototype.$ = function ( selector ) {
    };

    Tab.prototype.render = function () {
    };

    Tab.prototype.getValue = function () {
    };

    return Tab;
} );
