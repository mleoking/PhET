/**Subclass of Property that adds methods specific to boolean values*/
define( ['phetcommon/model/property/Property', 'util/Inheritance'], function ( Property, Inheritance ) {

    Inheritance.inheritPrototype( BooleanProperty, Property );

    function BooleanProperty( value ) {
        Property.call( this, value );
    }

    BooleanProperty.prototype.toggle = function () {this.set( !this.get() );};

    return BooleanProperty;
} );