//Model for skater, with everything in SI
define( ["model/vector2d"], function ( Vector2D ) {
    function SkaterModel() {
        this.mass = 60;
        this.position = new Vector2D( 0, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.thermalEnergy = 0.0;
        this.angle = 0;
        var that = this;

        this.getKineticEnergy = function () { return 0.5 * that.mass * that.velocity.magnitudeSquared(); };
        this.getPotentialEnergy = function () {return that.mass * 9.8 * that.position.y;};
        this.getThermalEnergy = function () { return that.thermalEnergy; };
        this.getTotalEnergy = function () { return that.getKineticEnergy() + that.getPotentialEnergy() + that.getThermalEnergy(); };
        this.getMechanicalEnergy = function () { return that.getKineticEnergy() + that.getPotentialEnergy() };
        this.returnSkater = function () {
            this.position = new Vector2D( 0, 0 );
            this.velocity = new Vector2D( 0, 0 );
            this.thermalEnergy = 0.0;
            this.angle = 0.0;
        };
    }

    return SkaterModel;
} );