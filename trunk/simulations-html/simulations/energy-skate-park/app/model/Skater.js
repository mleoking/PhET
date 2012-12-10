//Model for skater, with everything in SI
define( ["model/vector2d"], function ( Vector2D ) {
    function SkaterModel() {
        this.mass = 60;
        this.position = new Vector2D( 0, 0 );
        this.velocity = new Vector2D( 0, 0 );
        this.thermalEnergy = 0.0;
        this.angle = 0;
        this.attached = false;
        var self = this;

        //TODO: move to prototype
        this.getKineticEnergy = function () { return 0.5 * self.mass * self.velocity.magnitudeSquared(); };
        this.getPotentialEnergy = function () {return self.mass * 9.8 * self.position.y;};
        this.getThermalEnergy = function () { return self.thermalEnergy; };
        this.getTotalEnergy = function () { return self.getKineticEnergy() + self.getPotentialEnergy() + self.getThermalEnergy(); };
        this.getMechanicalEnergy = function () { return self.getKineticEnergy() + self.getPotentialEnergy() };
    }

    SkaterModel.prototype.returnSkater = function () {
        this.position = new Vector2D( 0, 0 );
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