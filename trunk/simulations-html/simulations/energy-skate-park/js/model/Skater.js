//Model for skater, with everything in SI
define( ["model/vector2d"], function ( Vector2D ) {
    function SkaterModel() {
        this.mass = 60;
        this.position = new Vector2D( 5, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.thermalEnergy = 0.0;
        this.angle = 0;
        this.attached = false;
    }

    SkaterModel.prototype.getKineticEnergy = function () {return 0.5 * this.mass * this.velocity.magnitudeSquared();};
    SkaterModel.prototype.getPotentialEnergy = function () {return this.mass * 9.8 * this.position.y;};
    SkaterModel.prototype.getThermalEnergy = function () { return this.thermalEnergy; };
    SkaterModel.prototype.getTotalEnergy = function () { return this.getKineticEnergy() + this.getPotentialEnergy() + this.getThermalEnergy(); };
    SkaterModel.prototype.getMechanicalEnergy = function () { return this.getKineticEnergy() + this.getPotentialEnergy() };

    SkaterModel.prototype.returnSkater = function () {
        this.position = new Vector2D( 5, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.angle = 0.0;
        this.attached = false;
    };

    SkaterModel.prototype.reset = function () {
        this.returnSkater();
        this.mass = 60;
        this.thermalEnergy = 0.0;
    };

    return SkaterModel;
} );