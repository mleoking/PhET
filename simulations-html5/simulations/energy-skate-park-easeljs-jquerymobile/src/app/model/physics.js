define( ["underscore", "model/vector2d", "model/geometry"], function ( _, Vector2D, Geometry ) {
    var Physics = {};

    var groundHeight = 116;
    var groundY = 768 - groundHeight;
    var metersPerPixel = 8.0 / 768.0;

    function getModelX( point ) {return point.x * metersPerPixel;}

    function getModelY( point ) {return -(point.y - groundY) * metersPerPixel;}

//    globalCounter = 0;
    var maxIterations = 5000;
    var numbersToSearch = _.range( -1, maxIterations + 1 );

    Physics.updatePhysics = function ( skater, groundHeight, splineLayer ) {
        var dt = 0.01;
        var originalX = skater.position.x;
        var originalY = skater.position.y;
        var originalEnergy = skater.getTotalEnergy();
        var originalMechanicalEnergy = skater.getMechanicalEnergy();
        if ( skater.attached ) {

            //Could avoid recomputing the splines in this step if they haven't changed.  But it doesn't show up as high in the profiler.
            var s = numeric.linspace( 0, 1, splineLayer.controlPoints.length );
            var splineX = numeric.spline( s, splineLayer.controlPoints.map( getModelX ) );
            var splineY = numeric.spline( s, splineLayer.controlPoints.map( getModelY ) );

            //Find a point on the spline that conserves energy and is near the original point and in the right direction.

            function getUnitParallelVector( alpha ) {
                var point = new Vector2D( splineX.at( alpha ), splineY.at( alpha ) );
                var point2 = new Vector2D( splineX.at( alpha + 1E-6 ), splineY.at( alpha + 1E-6 ) );
                return point2.minus( point ).unit();
            }

            //See trunk\simulations-java\simulations\energy-skate-park\src\edu\colorado\phet\energyskatepark\model\physics\Particle1D.java

            var unitVector = getUnitParallelVector( skater.attachmentPoint );//TODO: Could integrate (average) over adjacent points to get a better estimate.
            var gravityForce = new Vector2D( 0, -skater.mass * 9.8 );

            var forceMagnitude = unitVector.dot( gravityForce );
            var netForce = unitVector.times( forceMagnitude );

            var acceleration = netForce.times( 1.0 / skater.mass );

            skater.velocity = skater.velocity.plus( acceleration.times( dt ) );
            var proposedX = skater.position.x + skater.velocity.x * dt + 0.5 * acceleration.x * dt * dt;
            var proposedY = skater.position.y + skater.velocity.y * dt + 0.5 * acceleration.y * dt * dt;

            //Find a point on the track that has a state similar to [proposedX,proposedY,proposedVx,proposedVy,E0]
//            var targetState = [proposedX, proposedY, skater.velocity.x, skater.velocity.y, originalEnergy];
            var selectedI = _.min( numbersToSearch, function ( i ) {
                var s = i / maxIterations;
                var x = splineX.at( s );
                var y = splineY.at( s );
                var a = (proposedX - x);
                var b = (proposedY - y);
                var c = (skater.velocity.x - (x - originalX) / dt);
                var d = (skater.velocity.y - (y - originalY) / dt );
                var proposedEnergy = proposedY * 9.8 * skater.mass + 0.5 * skater.mass * skater.velocity.magnitudeSquared();
                var e = (proposedEnergy - originalMechanicalEnergy);
                var positionWeight = 10;//TODO: Tune this value?
                return positionWeight * (a * a + b * b) + c * c + d * d + e * e;  //minimizing square same result
            } );
//            console.log( selectedI );
            var s = selectedI / maxIterations;
            skater.attachmentPoint = s;
            var x = splineX.at( s );
            var y = splineY.at( s );
            skater.position.x = x;
            skater.position.y = y;
            skater.velocity = new Vector2D( (skater.position.x - originalX) / dt, (skater.y - originalY) / dt );

            //Conserve energy by tuning the velocity.
            var sqrtArg = 2 * (originalMechanicalEnergy / skater.mass - 9.8 * skater.position.y);
            if ( sqrtArg > 0 ) {
                var speed = Math.sqrt( sqrtArg );
                skater.velocity = skater.velocity.unit().times( speed );
            }
            //constant energy means

            if ( s >= 1.0 || s <= 0 ) {
                skater.attached = false;
                skater.velocity = new Vector2D( skater.position.x - originalX, skater.y - originalY );
            }
        }
        else {
            if ( skater.dragging ) {
                return;
            }
            var acceleration = new Vector2D( 0, -9.8 );
            skater.velocity = skater.velocity.plus( acceleration.times( dt ) );
            var aTerm = acceleration.times( 0.5 * dt * dt );
            var dx = skater.velocity.times( dt ).plus( aTerm );
            skater.position = skater.position.plus( dx );

            //Don't let the skater go below the ground.
            if ( skater.position.y <= 0 ) {
                skater.position.y = 0;
                skater.velocity = new Vector2D();
            }

            //don't let the skater cross the spline
            if ( splineLayer.controlPoints.length > 2 ) {
                var s = numeric.linspace( 0, 1, splineLayer.controlPoints.length );
                var delta = 1E-6;

                function getSides( xvalue, yvalue ) {

                    var splineX = numeric.spline( s, splineLayer.controlPoints.map( getModelX ).map( function ( x ) {return x - xvalue } ) );
                    var splineY = numeric.spline( s, splineLayer.controlPoints.map( getModelY ).map( function ( y ) {return y - yvalue } ) );

                    var xRoots = splineX.roots();
                    var sides = [];
                    for ( var i = 0; i < xRoots.length; i++ ) {
                        var xRoot = xRoots[i];
                        var pre = {x: splineX.at( xRoot - delta ), y: splineY.at( xRoot - delta )};
                        var post = {x: splineX.at( xRoot + delta ), y: splineY.at( xRoot + delta )};
                        var side = Geometry.linePointPosition2DVector( pre, post, {x: 0, y: 0} );
                        sides.push( {xRoot: xRoot, side: side} );
                    }
                    return sides;
                }

                var originalSides = getSides( originalX, originalY );
                var newSides = getSides( skater.position.x, skater.position.y );

                for ( var i = 0; i < originalSides.length; i++ ) {
                    var originalSide = originalSides[i];
                    for ( var j = 0; j < newSides.length; j++ ) {
                        var newSide = newSides[j];

                        var distance = Math.abs( newSide.xRoot - originalSide.xRoot );

                        if ( distance < 1E-4 && Geometry.getSign( originalSide.side ) != Geometry.getSign( newSide.side ) ) {
                            console.log( "crossed over" );
                            skater.attached = true;
                            skater.attachmentPoint = newSide.xRoot;
                        }
                    }
                }
            }
        }
    };

    return Physics;
} );