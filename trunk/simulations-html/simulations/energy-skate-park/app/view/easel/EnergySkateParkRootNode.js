//One of the three tabs in Energy Skate Park
define( ['easel',
            'model/skater-model',
            'view/skater-view',
            'view/easel/Background',
            'spline',
            'model/physics',
            'view/easel-util',
            'view/easel/PieChart',
            'view/easel/Grid',
            'view/easel/BarChart',
            'view/speedometer'
        ], function ( Easel, SkaterModel, Skater, Background, Spline, Physics, EaselUtil, PieChart, Grid, BarChart, Speedometer ) {

    //id is the string that identifies the tab for this module, used for creating unique ids.
    return function ( skaterModel, groundHeight, groundY, analytics ) {
        var root = new createjs.Container();

        var skater = Skater.createSkater( skaterModel, groundHeight, groundY, analytics );

        var splineLayer = Spline.createSplineLayer( groundHeight );
        root.addChild( Background.createBackground( groundHeight ) );
        var grid = new Grid( groundY );
        grid.visible = false;
        root.addChild( grid );
        root.addChild( splineLayer );
        var barChart = BarChart.createBarChart( skater );
        barChart.x = 50;
        barChart.y = 50;
        barChart.visible = false;
        root.addChild( barChart );

        root.addChild( skater );

        var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
        fpsText.x = 4;
        fpsText.y = 50;
        root.addChild( fpsText );
        root.fpsText = fpsText;
        var pieChart = new PieChart( skater );
        pieChart.visible = false;
        root.addChild( pieChart );

        var speedometer = Speedometer.createSpeedometer( skater );
        speedometer.visible = false;
        root.addChild( speedometer );

        root.tick = function () {
            skater.updateFromModel();
            if ( barChart.visible ) {
                barChart.tick();
            }
            if ( speedometer.visible ) {
                speedometer.tick();
            }
            if ( pieChart.visible ) {
                pieChart.tick();
            }
        };
        root.resetAll = function () {
            barChart.visible = false;
            pieChart.visible = false;
            grid.visible = false;
            speedometer.visible = false;
        };
        root.splineLayer = splineLayer;
        root.pieChart = pieChart;
        root.grid = grid;
        root.speedometer = speedometer;
        root.barChart = barChart;
        return root;
    };
} );