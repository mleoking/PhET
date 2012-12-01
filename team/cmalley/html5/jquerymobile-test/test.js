// Copyright 2002-2012, University of Colorado

require( [], function () {

    // Make the control panel the same height as the window
        $( "#optionsPanel" ).on(
                {
                    popupbeforeposition:function () {
                        var h = $( window ).height();

                        $( "#optionsPanel" ).css( "height", h );
                    }
                } );

    // Stub handlers for UI components ----------------------------------

    var strengthSlider = document.getElementById( "strengthSlider" );
    strengthSlider.change = function() {
        console.log( "strengthSlider.value=" + strengthSlider.value );
    };

    var flipPolarityButton = document.getElementById( "flipPolarityButton" );
    flipPolarityButton.onclick = function () {
        console.log( "flipPolarityButton.onclick" );
    };

    var seeInsideCheckBox = document.getElementById( "seeInsideCheckBox" );
    seeInsideCheckBox.onclick = function () {
        console.log( "seeInsideCheckBox.checked=" + seeInsideCheckBox.checked );
    };

    var compassCheckBox = document.getElementById( "compassCheckBox" );
    compassCheckBox.onclick = function () {
        console.log( "compassCheckBox.checked=" + compassCheckBox.checked );
    };

    var fieldCheckBox = document.getElementById( "fieldCheckBox" );
    fieldCheckBox.onclick = function () {
            console.log( "fieldCheckBox.checked=" + fieldCheckBox.checked );
        };

    var meterCheckBox = document.getElementById( "meterCheckBox" );
    meterCheckBox.onclick = function () {
        console.log( "meterCheckBox.checked=" + meterCheckBox.checked );
    };

    var resetAllButton = document.getElementById( "resetAllButton" );
    resetAllButton.onclick = function () {
        console.log( "resetAllButton.onclick" );
    };
} );
