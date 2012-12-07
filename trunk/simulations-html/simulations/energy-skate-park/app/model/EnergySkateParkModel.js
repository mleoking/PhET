//Complete model for Energy Skate Park
define( ['model/vector2d', 'model/Skater', 'model/Property'], function ( Vector2D, Skater, Property ) {

    function EnergySkateParkModel() {
        this.skater = new Skater();
        this.barChartVisible = new Property( false );
    }

    return EnergySkateParkModel;
} );