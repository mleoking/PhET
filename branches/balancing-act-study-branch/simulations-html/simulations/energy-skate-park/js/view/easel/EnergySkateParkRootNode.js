//One of the three tabs in Energy Skate Park
define( ['easel',
            'model/Skater',
            'view/easel/SkaterView',
            'view/easel/Background',
            'view/easel/Spline',
            'model/Physics',
            'view/easel-util',
            'view/easel/PieChart',
            'view/easel/Grid',
            'view/easel/BarChart',
            'view/easel/Speedometer'
        ], function ( Easel, Skater, SkaterView, Background, Spline, Physics, EaselUtil, PieChart, Grid, BarChart, Speedometer ) {

    //id is the string that identifies the tab for this module, used for creating unique ids.
    return function ( model, analytics ) {
        var groundHeight = model.groundHeight,
                groundY = model.groundY;
        var root = new createjs.Container();

        var skaterView = SkaterView.createSkater( model.skater, groundHeight, groundY, analytics );
        var splineLayer = Spline.createSplineLayer( groundHeight, model );
        root.addChild( Background.createBackground( groundHeight ) );
        var grid = new Grid( groundY );
        model.gridVisible.addObserver( function ( value ) {grid.visible = value;} );
        root.addChild( grid );
        root.addChild( splineLayer );
        var barChart = BarChart.createBarChart( model.skater );
        barChart.x = 50;
        barChart.y = 50;
        model.barChartVisible.addObserver( function ( value ) {barChart.visible = value;} );
        root.addChild( barChart );

        root.addChild( skaterView );
        var pieChart = new PieChart( model.skater, skaterView );
        model.pieChartVisible.addObserver( function ( value ) {pieChart.visible = value;} );

        root.addChild( pieChart );

        var speedometer = Speedometer.createSpeedometer( skaterView );
        model.speedometerVisible.addObserver( function ( value ) {speedometer.visible = value;} );
        root.addChild( speedometer );

        root.tick = function () {
            skaterView.updateFromModel();
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