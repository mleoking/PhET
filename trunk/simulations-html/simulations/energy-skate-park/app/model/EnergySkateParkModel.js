//Complete model for Energy Skate Park
define( ['underscore', 'model/vector2d', 'model/Skater', 'model/Property', 'model/BooleanProperty'], function ( _, Vector2D, Skater, Property, BooleanProperty ) {

    function EnergySkateParkModel() {
        this.skater = new Skater();
        this.barChartVisible = new BooleanProperty( false );
        this.pieChartVisible = new BooleanProperty( false );
        this.gridVisible = new BooleanProperty( false );
        this.speedometerVisible = new BooleanProperty( false );
        this.playing = new BooleanProperty( true );
        this.slowMotion = new BooleanProperty( false );

        //Pixels
        this.groundHeight = 116;
        this.groundY = 768 - this.groundHeight;
    }

    EnergySkateParkModel.prototype.resetAll = function () {
        //Find all resettable fields
        var resettable = _.filter( this, function ( element ) {return element.reset && typeof element.reset == 'function'} );

        //Call reset on them
        _.each( resettable, function ( element ) {element.reset()} );
    };

    return EnergySkateParkModel;
} );