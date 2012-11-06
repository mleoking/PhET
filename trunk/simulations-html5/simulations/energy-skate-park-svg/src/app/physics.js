define( ["vector2d", "geometry"], function ( Vector2D, Geometry ) {
    var Physics = {};

    Physics.updatePhysics = function ( skater, groundHeight, splineLayer ) {
        var originalX = skater.x;
        var originalY = skater.y;
        var originalEnergy = 0.5 * skater.mass * skater.velocity.magnitude() * skater.velocity.magnitude() + skater.mass * 9.8 * (768 - groundHeight - skater.y);
        //            console.log( originalEnergy );
        if ( skater.attached ) {

            var speed = skater.velocity.magnitude();
            skater.attachmentPoint = skater.attachmentPoint + speed / 1.8 * 0.003;

            //Find a point on the spline that conserves energy and is near the original point and in the right direction.


            //Could avoid recomputing the splines in this step if they haven't changed.  But it doesn't show up as high in the profiler.
            var s = numeric.linspace( 0, 1, splineLayer.controlPoints.length );
            var splineX = numeric.spline( s, splineLayer.controlPoints.map( getX ) );
            var splineY = numeric.spline( s, splineLayer.controlPoints.map( getY ) );
            skater.x = splineX.at( skater.attachmentPoint );
            skater.y = splineY.at( skater.attachmentPoint );

            if ( skater.attachmentPoint > 1.0 || skater.attachmentPoint < 0 ) {
                skater.attached = false;
                skater.velocity = new Vector2D( skater.x - originalX, skater.y - originalY );
            }

        }
        else {

            var newY = skater.y;
            var newX = skater.x;
            if ( !skater.dragging ) {
                skater.velocity = skater.velocity.plus( 0, 0.5 );
                newY = skater.y + skater.velocity.times( 1 ).y;
                newX = skater.x + skater.velocity.times( 1 ).x;
            }
            skater.x = newX;
            skater.y = newY;

            //Don't let the skater go below the ground.
            var maxY = 768 - groundHeight;
            var newSkaterY = Math.min( maxY, newY );
            skater.y = newSkaterY;
            if ( newSkaterY == maxY ) {
                skater.velocity = new Vector2D();
            }

            //don't let the skater cross the spline

            function getX( point ) {return point.x;}

            function getY( point ) {return point.y;}

            if ( splineLayer.controlPoints.length > 2 ) {
                var s = numeric.linspace( 0, 1, splineLayer.controlPoints.length );
                var delta = 1E-6;

                function getSides( xvalue, yvalue ) {
                    var splineX = numeric.spline( s, splineLayer.controlPoints.map( getX ).map( function ( x ) {return x - xvalue } ) );
                    var splineY = numeric.spline( s, splineLayer.controlPoints.map( getY ).map( function ( y ) {return y - yvalue } ) );

                    var xRoots = splineX.roots();
                    var sides = [];
                    for ( var i = 0; i < xRoots.length; i++ ) {
                        var xRoot = xRoots[i];
                        var pre = {x:splineX.at( xRoot - delta ), y:splineY.at( xRoot - delta )};
                        var post = {x:splineX.at( xRoot + delta ), y:splineY.at( xRoot + delta )};
                        var side = Geometry.linePointPosition2DVector( pre, post, {x:0, y:0} );
                        sides.push( {xRoot:xRoot, side:side} );
                    }
                    return sides;
                }

                var originalSides = getSides( originalX, originalY );
                var newSides = getSides( skater.x, skater.y );

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

        //Only draw when necessary because otherwise performance is worse on ipad3
        if ( skater.x != originalX || skater.y != originalY ) {

            //                skaterDebugShape.setX( skater.getX() + skater.getWidth() / 2 );
            //                skaterDebugShape.setY( skater.getY() + skater.getHeight() );
            //                skaterLayer.draw();
        }
    };

    return Physics;
} );