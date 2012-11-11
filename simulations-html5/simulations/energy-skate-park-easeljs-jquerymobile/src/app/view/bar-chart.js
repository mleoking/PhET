define( ["view/easel-util"], function ( EaselUtil ) {
    return {createBarChart: function ( skater ) {
        var that = new createjs.Container();
        var shape = new createjs.Shape();
        shape.graphics.beginFill( "white" ).beginStroke( "black" ).setStrokeStyle( 2 ).drawRoundRect( 0, 0, 200, 600, 10 ).endStroke().endFill();
        that.shape = shape;
        that.addChild( shape );

        var energyLabel = new createjs.Text( 'Energy (J)', '24px "Arial",Tahoma', 'blue' );
        energyLabel.x = 10;
        energyLabel.y = 10;
        energyLabel.shadow = new createjs.Shadow( 'black', 1, 1, 2 );
        that.addChild( energyLabel );

        var yAxisArrow = new createjs.Shape();
        var arrowX = 34;
        yAxisArrow.graphics.beginStroke( 'black' ).setStrokeStyle( 4 ).moveTo( arrowX, 500 ).lineTo( arrowX, 40 ).endStroke();
        var dx = arrowX;
        var dy = 40 - 3;
        yAxisArrow.graphics.beginFill( 'black' ).moveTo( 0 + dx, 0 + dy ).lineTo( 7 + dx, 10 + dy ).lineTo( -7 + dx, 10 + dy ).lineTo( 0 + dx, 0 + dy ).endFill();
        that.addChild( yAxisArrow );

        var xAxis = new createjs.Shape();
        xAxis.graphics.beginStroke( 'red' ).setStrokeStyle( 4 ).moveTo( arrowX, 500 ).lineTo( arrowX + 150, 500 ).endStroke();
        that.addChild( xAxis );

        EaselUtil.makeDraggable( that );

        var fields = [
            {name: 'Kinetic', color: 'green'},
            {name: 'Potential', color: 'blue'},
            {name: 'Thermal', color: 'red'},
            {name: 'Total', color: 'yellow'}
        ];

        for ( var i = 0; i < fields.length; i++ ) {
            var kineticLabel = new createjs.Text( fields[i].name, '20px "Arial",Tahoma', fields[i].color );
            kineticLabel.rotation = 270;
            kineticLabel.x = 40 + i * 35;
            kineticLabel.y = 590;
            kineticLabel.shadow = new createjs.Shadow( 'black', 1, 1, 1 );
            that.addChild( kineticLabel );

            var bar = new createjs.Shape();
            var barHeight = 100 + i * 100;
            bar.graphics.beginStroke( 'black' ).beginFill( fields[i].color ).rect( kineticLabel.x + 5, 500 - barHeight, 20, barHeight ).endFill().endStroke();
            that.addChild( bar );
        }

        that.onMouseOver = function () { document.body.style.cursor = "pointer"; };
        that.onMouseOut = function () { document.body.style.cursor = "default"; };

        return that;
    }};
} );