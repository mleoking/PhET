//Model for skater, with everything in SI
define( ["model/vector2d"], function ( Vector2D ) {
    function SkaterModel() {
        this.mass = 60;
        this.position = new Vector2D( 0, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.thermalEnergy = 0.0;
        this.angle = 0;
    }

    var p = SkaterModel.prototype;
    p.getKineticEnergy = function () { return 0.5 * this.mass * this.velocity.magnitudeSquared(); };
    p.getPotentialEnergy = function () {return this.mass * 9.8 * this.position.y;};
    p.getThermalEnergy = function () { return this.thermalEnergy; };
    p.getTotalEnergy = function () { return this.getKineticEnergy() + this.getPotentialEnergy() + this.getThermalEnergy(); };
    p.getMechanicalEnergy = function () { return this.getKineticEnergy() + this.getPotentialEnergy() };
    p.returnSkater = function () {
        this.position = new Vector2D( 0, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.angle = 0.0;
    };

    p.reset = function () {
        this.returnSkater();
        this.mass = 60;
        this.thermalEnergy = 0.0;
    };

    return SkaterModel;
} );