/**Subclass of Property that adds methods specific to boolean values*/
define( ['model/Property', 'util/Inheritance'], function ( Property, Inheritance ) {

    Inheritance.inheritPrototype( BooleanProperty, Property );

    function BooleanProperty( value ) {
        Property.call( this, value );
    }

    BooleanProperty.prototype.toggle = function () {this.set( !this.get() );};

    BooleanProperty.prototype.setTrue = function () {this.set( true );};
    BooleanProperty.prototype.setTrueBound = function () { return this.setTrue.bind( this );};

    return BooleanProperty;
} );

