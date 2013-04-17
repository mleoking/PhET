(function () {

    function vbox( distanceBetweenItems, items ) {

        var maxItemWidth = 0;
        var maxItemHeight = 0;
        var sumHeight = 0;
        for ( var z = 0; z < items.length; z++ ) {
            if ( z != 0 ) {
                sumHeight = sumHeight + distanceBetweenItems;
            }
            maxItemWidth = Math.max( maxItemWidth, items[z].width );
            maxItemHeight = Math.max( maxItemHeight, items[z].height );
            sumHeight = sumHeight + items[z].height;
        }

        var container = new CAAT.ActorContainer().setSize( maxItemWidth, sumHeight );

        var y = 0;
        for ( var k = 0; k < items.length; k++ ) {
            items[k].setLocation( maxItemWidth / 2 - items[k].width / 2, y );
            container.addChild( items[k] );
            y += items[k].height + distanceBetweenItems;
        }

        return container;
    }

    function createSegmentedButton( buttonContents, model ) {

        var maxItemWidth = 0;
        var maxItemHeight = 0;
        for ( var z = 0; z < buttonContents.length; z++ ) {
            maxItemWidth = Math.max( maxItemWidth, buttonContents[z].width );
            maxItemHeight = Math.max( maxItemHeight, buttonContents[z].height );
            buttonContents[z].enableEvents( false );
        }

        var leftRightInset = 10;
        var distanceBetweenButtons = 20;
        var width = leftRightInset + maxItemWidth * buttonContents.length + distanceBetweenButtons * (buttonContents.length - 1) + leftRightInset;

        var container = new CAAT.ActorContainer().setSize( width, 65 );

        var background = new CAAT.Actor().setSize( container.width, container.height ).enableEvents( false );
        background.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.strokeStyle = 'black';
            ctx.lineWidth = 1;
            ctx.fillStyle = model.solidSelected ? 'rgb(200,200,200)' : 'blue';
            roundRectRight( ctx, 0, 0, container.width, container.height, 20, true, true );
            ctx.fillStyle = model.solidSelected ? 'blue' : 'rgb(200,200,200)';
            roundRectLeft( ctx, 0, 0, container.width, container.height, 20, true, true );
            ctx.beginPath();
            ctx.moveTo( container.width / 2, 0 );
            ctx.lineTo( container.width / 2, container.height );
            ctx.closePath();
            ctx.stroke();
        };
        container.addChild( background );

        var x = leftRightInset;
        for ( var k = 0; k < buttonContents.length; k++ ) {
            x += maxItemWidth / 2;
            buttonContents[k].setLocation( x - buttonContents[k].width / 2, container.height / 2 - buttonContents[k].height / 2 );
            container.addChild( buttonContents[k] );
            x += distanceBetweenButtons;
            x += maxItemWidth / 2;
        }

        return container;
    }

    function roundRectLeft( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + radius, y );
        ctx.lineTo( x + width / 2, y );
        ctx.lineTo( x + width / 2, y + height );
        ctx.lineTo( x + radius, y + height );
        ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
        ctx.lineTo( x, y + radius );
        ctx.quadraticCurveTo( x, y, x + radius, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }

    function roundRectRight( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + width / 2, y );
        ctx.lineTo( x + width - radius, y );
        ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
        ctx.lineTo( x + width, y + height - radius );
        ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
        ctx.lineTo( x + width / 2, y + height );
        ctx.lineTo( x + width / 2, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }

    /**
     * http://stackoverflow.com/questions/1255512/how-to-draw-a-rounded-rectangle-on-html-canvas
     * Draws a rounded rectangle using the current state of the canvas.
     * If you omit the last three params, it will draw a rectangle
     * outline with a 5 pixel border radius
     * @param {CanvasRenderingContext2D} ctx
     * @param {Number} x The top left x coordinate
     * @param {Number} y The top left y coordinate
     * @param {Number} width The width of the rectangle
     * @param {Number} height The height of the rectangle
     * @param {Number} radius The corner radius. Defaults to 5;
     * @param {Boolean} fill Whether to fill the rectangle. Defaults to false.
     * @param {Boolean} stroke Whether to stroke the rectangle. Defaults to true.
     */
    function roundRect( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + radius, y );
        ctx.lineTo( x + width - radius, y );
        ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
        ctx.lineTo( x + width, y + height - radius );
        ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
        ctx.lineTo( x + radius, y + height );
        ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
        ctx.lineTo( x, y + radius );
        ctx.quadraticCurveTo( x, y, x + radius, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }

    //English strings obtained from https://github.com/nzakas/props2js
    var englishStrings = {"copperSulfate": "Copper sulfate", "solution": "Solution", "concentration": "Concentration", "beers-law-lab.name": "Beer\u0027s Law Lab", "tab.beersLaw": "Beer\u0027s Law", "tab.concentration": "Concentration", "units.molesPerLiter": "mol/L", "potassiumDichromate": "Potassium dichromate", "removeSolute": "Remove Solute", "solid": "Solid", "fixed": "fixed", "pattern.0percent": "{0}%", "none": "none", "evaporation": "Evaporation", "potassiumChromate": "Potassium chromate", "units.mM": "mM", "lots": "lots", "saturated": "Saturated!", "pattern.0formula.1name": "{0}: {1}", "transmittance": "Transmittance", "water": "water", "pattern.0value.1units": "{0} {1}", "potassiumPermanganate": "Potassium permanganate", "drinkMix": "Drink mix", "pattern.0label": "{0}:", "units.centimeters": "cm", "concentration.name": "Concentration", "pattern.parentheses.0text": "({0})", "variable": "variable", "units.uM": "µM", "nickelIIChloride": "Nickel (II) chloride", "units.M": "M", "cobaltChloride": "Cobalt chloride", "cobaltIINitrate": "Cobalt (II) nitrate", "solute": "Solute", "wavelength": "Wavelength", "absorbance": "Absorbance", "units.liters": "L"};
    var frenchStrings = {"units.uM": "µmol/L", "drinkMix": "mélange type", "none": "aucune", "tab.beersLaw": "Loi de Beer-Lambert", "potassiumPermanganate": "permanganate de potassium", "cobaltIINitrate": "nitrate de cobalt (II)", "fixed": "fixée", "units.mM": "mmol/L", "beers-law-lab.name": "Loi de Beer-Lambert", "lots": "élevée", "removeSolute": "Retirer le soluté", "potassiumChromate": "chromate de potassium", "units.M": "mol/L", "variable": "variable", "potassiumDichromate": "dichromate de potassium", "copperSulfate": "sulfate de cuivre", "solute": "Soluté", "translation.credits": "Ph. Chevallier, Lycée Rotrou, France", "evaporation": "Évaporation", "solid": "Solide", "saturated": "Solution saturée !", "cobaltChloride": "chlorure de cobalt (II)", "wavelength": "longueur d\u0027onde", "nickelIIChloride": "chlorure de nickel (II)", "water": "eau"};
    var arabicStrings = {"copperSulfate": "كبريتات النحاس", "solution": "محلول", "concentration": "التركيز", "beers-law-lab.name": "قانون بير", "tab.beersLaw": "قانون بير", "tab.concentration": "التركيز", "units.molesPerLiter": "mol/L", "potassiumDichromate": "ثنائي كرومات البوتاسيوم", "removeSolute": "إزالة المذيب", "solid": "صلب", "translation.credits": "\u003chtml\u003eSeffar Abdelkhalek \u003cbr\u003eLycée Moulay Idriss, Fès,Maroc\u003cbr\u003ehttp://lewebpedagogique.com/seffar\u003c/html\u003e", "fixed": "ثابت", "pattern.0percent": "{0}%", "none": "لا شيء", "evaporation": "تبخر", "potassiumChromate": "كرومات البوتاسيوم", "units.mM": "mM", "lots": "الكثير", "saturated": "! مشبع", "pattern.0formula.1name": "{0}: {1}", "transmittance": "نفاذية", "water": "ماء", "pattern.0value.1units": "{0} {1}", "potassiumPermanganate": "برمنغنات البوتاسيوم", "drinkMix": "مشروب", "pattern.0label": "{0}:", "units.centimeters": "cm", "concentration.name": "التركيز", "pattern.parentheses.0text": "({0})", "variable": "متغير", "units.uM": "µM", "nickelIIChloride": "II كلورور النيكل", "units.M": "M", "cobaltChloride": "كلورور الكوبالت", "cobaltIINitrate": "II نترات الكوبالت", "solute": "المذاب", "absorbance": "الامتصاص", "wavelength": "طول الموجة", "units.liters": "L"};

    var strings = getParameterByName( "language" ) == "ar" ? arabicStrings :
                  getParameterByName( "language" ) == "fr" ? frenchStrings : englishStrings;

    //Seems to be some bugs with CAAT.ShapeActor when running in CocoonJS, so we have our own implementation.
    function rectangleNode( width, height, fillStyle, strokeThickness, strokeStyle ) {
        var background = new CAAT.Actor().setSize( width, height );
        background.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();
            ctx.strokeStyle = strokeStyle;
            ctx.beginPath();
            ctx.moveTo( 0, 0 );
            ctx.lineTo( width, 0 );
            ctx.lineTo( width, height );
            ctx.lineTo( 0, height );
            ctx.lineTo( 0, 0 );
            ctx.closePath();

            ctx.lineWidth = strokeThickness;
            ctx.lineJoin = 'square';
            ctx.lineCap = 'square';

            ctx.fillStyle = fillStyle;
            ctx.fillRect( 0, 0, background.width, background.height );
            ctx.stroke();
            ctx.restore();
        };
        return background;
    }

    function createKnob( image, minX, y, maxX, mobile ) {
        var knobImage = new CAAT.Actor().setBackgroundImage( image, true ).enableEvents( false );
        var touchAreaWidth = 100;
        var touchAreaHeight = 100;

        //Only make smaller regions on touch devices
        if ( !mobile ) {
            touchAreaWidth = knobImage.width;
            touchAreaHeight = knobImage.height;
        }

        var knob = new CAAT.ActorContainer().setSize( touchAreaWidth, touchAreaHeight );
        var relativeX = touchAreaWidth / 2 - knobImage.width / 2;
        var relativeY = touchAreaHeight / 2 - knobImage.height / 2;
        knobImage.setPosition( relativeX, relativeY );
        knob.setPosition( minX - relativeX, y - relativeY );
        knob.addChild( knobImage );

        knob.enableDrag();
        knob.mouseDrag = function ( mouseEvent ) {

            var pt = knob.modelToView( new CAAT.Point( mouseEvent.x, mouseEvent.y ) );
            knob.parent.viewToModel( pt );

            if ( knob.__d_ax === -1 || this.__d_ay === -1 ) {
                knob.__d_ax = pt.x;
                knob.__d_ay = +1;
                knob.__relativeTouchPoint = knob.x - pt.x;
            }

            var knobX = pt.x + knob.__relativeTouchPoint;
            if ( knobX > maxX - relativeX ) {
                knobX = maxX - relativeX;
            }
            if ( knobX < minX - relativeX ) {
                knobX = minX - relativeX;
            }
            knob.x = knobX;
        };

        //Make the sliders snap back to 0 when released--the user has to hold it down continuously
        var origMouseUP = knob.mouseUp;
        knob.mouseUp = function ( mouseEvent ) {
            origMouseUP.apply( knob, mouseEvent );
            knob.setPosition( minX - relativeX, y - relativeY );
        };

        //Returns a value between 0 and 1
        knob.getValue = function () {
            return (knob.x - minX) / (maxX - minX);
        };
        return knob;
    }

    function createButton( director, text, width, background ) {
        var actor = new CAAT.Actor().setSize( width, 40 );

        actor.paint = function ( director, time ) {

            var ctx = director.ctx;
            ctx.save();

            ctx.fillStyle = this.pointed ? '#f3f' : background;
            ctx.fillRect( 0, 0, this.width, this.height );

            ctx.strokeStyle = this.pointed ? 'gray' : 'black';
            ctx.strokeRect( 0, 0, this.width, this.height );

            ctx.font = '18px sans-serif';
            ctx.fillStyle = 'black';
            ctx.fillText( text, 3, 28 );

            ctx.restore();
        };

        return actor;
    }

    function interpolate( x0, y0, x1, y1, x ) {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
    }

    function rgbToColor( colorInstance ) {
        return 'rgb(' + colorInstance.red + ',' + colorInstance.green + ',' + colorInstance.blue + ')';
    }

    //Interpolate colors with midpoint, see original code
    function getColorWithMidpoint( concentration, solute ) {
        var water = {red: 224, green: 255, blue: 255};
        if ( concentration <= 0 ) {
            return rgbToColor( water );
        }
        else if ( concentration <= solute.minConcentration ) {
            var amountToMin = concentration / solute.minConcentration;
            return getColor( water, amountToMin, solute.minColor );
        }
        else if ( concentration <= solute.maxConcentration ) {
            var aboveMin = (concentration - solute.minConcentration);
            var amount = aboveMin / (solute.maxConcentration - solute.minConcentration);
            return getColor( solute.minColor, amount, solute.maxColor );
        }
        else {
            return rgbToColor( solute.maxColor );
        }
    }

    function getColor( source, distance, destination ) {
        var relative = {
            red: interpolate( 0, source.red, 1, destination.red, distance ),
            green: interpolate( 0, source.green, 1, destination.green, distance ),
            blue: interpolate( 0, source.blue, 1, destination.blue, distance )};

        return 'rgb(' + Math.round( relative.red ) + ',' + Math.round( relative.green ) + ',' + Math.round( relative.blue ) + ')';
    }

    function getFractionTowardSaturation( absorbedCrystals, fluidHeight ) {
        var concentration = absorbedCrystals / fluidHeight * 2;
        return Math.min( 1.0, concentration );
    }

    function isIOS() {
        return navigator.userAgent.match( /OS 5(_\d)+ like Mac OS X/i ) || navigator.userAgent.match( /OS 6(_\d)+ like Mac OS X/i );
    }

    function isAndroid() {
        var ua = navigator.userAgent.toLowerCase();
        return ua.indexOf( "android" ) > -1;
    }

    //http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values/901144#901144
    function getParameterByName( name ) {
        name = name.replace( /[\[]/, "\\\[" ).replace( /[\]]/, "\\\]" );
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp( regexS );
        var results = regex.exec( window.location.search );
        if ( results == null ) {
            return "";
        }
        else {
            return decodeURIComponent( results[1].replace( /\+/g, " " ) );
        }
    }

    //For running under Cocoon, use this code for jQuery
    //and comment out these lines in the html:
//    <script type="text/javascript" src="jquery-1.5.1.min.js"></script>
//    <script type="text/javascript" src="translateerties-1.0.9.js"></script>

    function translate( key ) {
        for ( var k in strings ) {
            if ( key == k ) {
                return strings[k];
            }
        }

        return key;
    }

    /**
     * This function will be called to let you define new scenes that will be
     * shown after the splash screen.
     * @param director
     */
    function createScenes( director ) {

        createScenesAfterResourcesLoaded( director );

        //Uncomment these lines for jQuery i18n
        //Load the properties files and then create scenes
//        jQuery.i18n.properties(
//                {
//                    name:'beers-law-lab-strings',
//                    path:'localization/',
//
//                    //Didn't work at all when specifying "both"
//                    mode:'map',
//                    language:getParameterByName( "language" ),
//                    callback:function () {
//                        createScenesAfterResourcesLoaded( director );
//                    }
//                } );
    }

    function solution( name, minConcentration, minRed, minGreen, minBlue, maxConcentration, maxRed, maxGreen, maxBlue ) {
        return {name: translate( name ), minConcentration: minConcentration, minColor: {red: minRed, green: minGreen, blue: minBlue}, maxConcentration: maxConcentration, maxColor: {red: maxRed, blue: maxBlue, green: maxGreen}};
    }

    function createScenesAfterResourcesLoaded( director ) {

        var model = {};
        model.solidSelected = true;
        model.solutes = [
            solution( "drinkMix", 0.05, 255, 225, 225, 5.96, 255, 0, 0 ),
            solution( "cobaltIINitrate", 0.05, 255, 225, 225, 5.64, 255, 0, 0 ),
            solution( "cobaltChloride", 0.05, 255, 242, 242, 4.33, 255, 106, 106 ),
            solution( "potassiumDichromate", 0.01, 255, 204, 153, 0.51, 255, 127, 0 ),
            solution( "potassiumChromate", 0.05, 255, 255, 153, 3.35, 255, 255, 0 ),
            solution( "nickelIIChloride", 0.2, 170, 255, 170, 5.21, 0, 128, 0 ),
            solution( "copperSulfate", 0.2, 200, 225, 255, 1.38, 30, 144, 255 ),
            solution( "potassiumPermanganate", 0.01, 255, 0, 255, 0.48, 80, 0, 120 )
        ];
        model.solute = model.solutes[0];
        var scene = director.createScene();

        //Set background to white
        scene.fillStyle = 'rgb(255,255,255)';

        scene.enableInputList( 1 );

        var shaker = new CAAT.Actor().setBackgroundImage( director.getImage( 'shaker' ), true ).setRotation( -Math.PI / 4 );
        shaker.enableDrag();
        shaker.x = 389;
        shaker.lastY = shaker.y;
        //Allow interaction even if outside of parent bounds
        scene.addActorToInputList( shaker, 0, 0 );

        var dropperBackground = new CAAT.Actor().setBackgroundImage( director.getImage( 'dropper-background' ), true );
        var dropperForeground = new CAAT.Actor().setBackgroundImage( director.getImage( 'dropper-foreground' ), true );
        var dropperLiquid = new CAAT.Actor().setSize( dropperBackground.width, dropperBackground.height );
        dropperLiquid.paint = function ( director, time ) {
            var ctx = director.ctx;

            ctx.save();
            ctx.fillStyle = rgbToColor( model.solute.maxColor );

            var tipWidth = 15;
            var tipHeight = 5;
            var glassWidth = 46;
            var glassHeight = 150;
            var glassYOffset = tipHeight + 14;
            ctx.beginPath();
            var dx = dropperBackground.width - 40;
            var dy = dropperBackground.height;
            ctx.moveTo( -tipWidth / 2 + dx, 0 + dy );
            ctx.lineTo( -tipWidth / 2 + dx, -tipHeight + dy );
            ctx.lineTo( -glassWidth / 2 + dx, -glassYOffset + dy );
            ctx.lineTo( -glassWidth / 2 + dx, -glassHeight + dy );
            ctx.lineTo( glassWidth / 2 + dx, -glassHeight + dy );
            ctx.lineTo( glassWidth / 2 + dx, -glassYOffset + dy );
            ctx.lineTo( tipWidth / 2 + dx, -tipHeight + dy );
            ctx.lineTo( tipWidth / 2 + dx, 0 + dy );
            ctx.fill();

            ctx.restore();
        };
        var dropperButton = new CAAT.Actor().setBackgroundImage( director.getImage( 'dropper-button-unpressed' ), true ).setScale( 0.4, 0.4 ).setLocation( -26, -28 );
        dropperButton.enableEvents( true );
        dropperButton.mouseEnter = function () { CAAT.setCursor( 'pointer' ); };
        dropperButton.mouseExit = function () { CAAT.setCursor( 'default' ); };
        dropperButton.mouseDown = function () {dropperButton.setBackgroundImage( director.getImage( 'dropper-button-pressed' ), true )};
        dropperButton.mouseUp = function () {dropperButton.setBackgroundImage( director.getImage( 'dropper-button-unpressed' ), true )};
        var dropper = new CAAT.ActorContainer().setSize( dropperBackground.width, dropperBackground.height );

        dropper.addChild( dropperLiquid );
        dropper.addChild( dropperBackground );
        dropper.addChild( dropperForeground );
        dropper.addChild( dropperButton );
        dropper.enableDrag();
        dropper.x = 389;

        model.toggledSolidSelected = function () {
            if ( model.solidSelected ) {
                rootNode.removeChild( dropper );
                rootNode.addChild( shaker );
                scene.removeActorFromInputList( dropper, 0 );
                scene.removeActorFromInputList( dropperButton, 0 );
                scene.addActorToInputList( shaker, 0, 0 );
            }
            else {
                rootNode.removeChild( shaker );
                rootNode.addChild( dropper );
                scene.removeActorFromInputList( shaker, 0 );
                scene.addActorToInputList( dropper, 0, 0 );
                scene.addActorToInputList( dropperButton, 0, 0 );
            }
        };

        var lipWidth = 40;
        var beakerX = 150;
        var beakerY = 200;
        var beakerHeight = 400;
        var beakerWidth = 600;
        var beakerMaxX = beakerX + beakerWidth;
        var beakerMaxY = beakerY + beakerHeight;
        var beaker = new CAAT.Actor().setSize( beakerWidth + lipWidth * 2, beakerHeight + lipWidth ).setLocation( beakerX - lipWidth, beakerY - lipWidth ).enableEvents( false );
        beaker.paint = function ( director, time ) {
            var ctx = director.ctx;

            ctx.save();
            ctx.strokeStyle = 'black';
            ctx.beginPath();
            ctx.moveTo( 0, 0 );
            ctx.lineTo( lipWidth, lipWidth );
            ctx.lineTo( lipWidth, lipWidth + beakerHeight );
            ctx.lineTo( lipWidth + beakerWidth, lipWidth + beakerHeight );
            ctx.lineTo( lipWidth + beakerWidth, lipWidth );
            ctx.lineTo( lipWidth + beakerWidth + lipWidth, 0 );

            ctx.lineWidth = 6;
            ctx.lineJoin = 'round';
            ctx.lineCap = 'round';
            ctx.stroke();

            ctx.restore();
        };

        var topFlowingWater = new CAAT.Actor().setSize( director.width, director.height ).enableEvents( false );
        var topFlowAmount = 0.0;
        topFlowingWater.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            var water = {red: 224, green: 255, blue: 255};
            ctx.fillStyle = 'rgb(' + Math.round( water.red ) + ',' + Math.round( water.green ) + ',' + Math.round( water.blue ) + ')';

            if ( topFlowAmount > 0.1 && fluidHeight < beakerHeight ) {
                ctx.fillRect( beakerX + 7, 180, 50 * topFlowAmount, beakerHeight - fluidHeight + 20 );
            }

            ctx.restore();
        };

        var bottomFlowingWater = new CAAT.Actor().setSize( director.width, director.height ).enableEvents( false );
        var bottomFlowAmount = 0.0;

        var absorbedCrystals = 0;

        bottomFlowingWater.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            var concentration = absorbedCrystals / fluidHeight * 2;
            ctx.fillStyle = getColorWithMidpoint( concentration, model.solute );

            if ( bottomFlowAmount > 0.1 && fluidHeight > 0 ) {
                ctx.fillRect( beakerMaxX + 60, beakerMaxY + 50, 50 * bottomFlowAmount, 800 );
            }
            ctx.restore();
        };

        var fluid = new CAAT.Actor().setSize( beakerWidth, director.height ).enableEvents( false ).setLocation( beakerX, 0 );
        var fluidHeight = beakerHeight / 2;

        fluid.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            var concentration = absorbedCrystals / fluidHeight * 2;
            ctx.fillStyle = getColorWithMidpoint( concentration, model.solute );
            ctx.fillRect( 0, beakerMaxY - fluidHeight, beakerWidth, fluidHeight );

            ctx.restore();
        };

        var topFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 100, 50 ).enableEvents( false );
        var topFaucetPipe = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_pipe_long' ), true ).setPosition( -292, 82 ).enableEvents( false );
        var bottomFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 752, 520 ).enableEvents( false );

        //WORKAROUND: On android Chrome, this size had to be extended for unknown reasons.  It worked fine everywhere else just to use the director's width and height.
        //Luckily this did not seem to disrupt the behavior on other systems too much.
        //Maybe caused by android having a default zoom factor and window.innerWidth mismatching with director.width?
        //This seemed to be relevant: http://tripleodeon.com/2011/12/first-understand-your-screen/
        var rootNode = new CAAT.ActorContainer().setSize( director.width * 10, director.height * 10 );

        //To enable pinch to zoom, use this line instead
        //        var rootNode = new CAAT.ActorContainer().setSize( director.width , director.height  ).enableDrag().setGestureEnabled( true );

        //Consider as mobile if android, ios or cocoon (which shows up as "an unknown OS)
        var mobile = isAndroid() || isIOS() || director.getOSName() == "an unknown OS";

        var topKnob = createKnob( director.getImage( 'slider-knob' ), 90, 34, 177, mobile );
        var bottomKnob = createKnob( director.getImage( 'slider-knob' ), 738, 498, 738 + (177 - 90), mobile );

        var border = new CAAT.Actor().setSize( director.width, director.height ).enableEvents( false );

        border.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            ctx.strokeStyle = 'orange';
            ctx.strokeRect( 0, 0, 1024, 768 );

            ctx.restore();
        };

        function createTick( fraction, extentX ) {
            var tick = new CAAT.Actor().setSize( director.width, director.height ).enableEvents( false );
            tick.paint = function ( director, time ) {
                var ctx = director.ctx;
                ctx.save();

                ctx.strokeStyle = 'black';
                ctx.beginPath();
                ctx.moveTo( beakerX, beakerMaxY - fraction * beakerHeight );
                ctx.lineTo( beakerX + extentX, beakerMaxY - fraction * beakerHeight );

                ctx.lineWidth = 4;
                ctx.stroke();

                ctx.restore();
            };
            return tick;
        }

        function createEvaporationControlPanel( mobile ) {
            var background = rectangleNode( 400, 100, 'rgb(240,240,240)', 2, 'gray' );
            var sliderTrack = rectangleNode( 200, 4, 'white', 1, 'black' );
            var text = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( translate( 'evaporation' ) ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap();

            var container = new CAAT.ActorContainer().setPosition( 175, 612 );
            container.setSize( background.width, background.height );
            container.addChild( background );
            container.addChild( text.setLocation( 2, 30 ) );
            var evaporationKnob = createKnob( director.getImage( 'slider-knob' ), 150, 25, 350, mobile );
            container.addChild( sliderTrack.setPosition( 150 + 10, 25 + evaporationKnob.height / 2 ) );
            container.addChild( evaporationKnob );

            container.evaporationKnob = evaporationKnob;
            return container;
        }

        function createSoluteControlPanel() {
            var soluteControlPanelBackground = 'rgb(220,220,220)';
            var background = rectangleNode( 300, 120, soluteControlPanelBackground, 1, 'black' );
            var text = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( translate( 'solute' ) ).calcTextSize( director ).
                    setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap();

            var container = new CAAT.ActorContainer().setPosition( 700, 20 );
            container.setSize( background.width, background.height );
            container.addChild( background );
            container.addChild( text.setLocation( 2, 4 + 8 ) );
            var offsetY = 20;

            var solidText = new CAAT.TextActor().setFont( "20px sans-serif" ).setText( translate( 'solid' ) ).calcTextSize( director ).setTextFillStyle( 'white' ).cacheAsBitmap().setLocation( 60, 55 + offsetY );
            var solutionText = new CAAT.TextActor().setFont( "20px sans-serif" ).setText( translate( 'solution' ) ).calcTextSize( director ).setTextFillStyle( 'white' ).cacheAsBitmap().setLocation( 180, 55 + offsetY );

            var shakerIcon = new CAAT.Actor().setBackgroundImage( director.getImage( 'shaker-icon' ), true ).enableEvents( false ).setScale( 0.8, 0.8 );
            var dropperIcon = new CAAT.Actor().setBackgroundImage( director.getImage( 'dropper-icon' ), true ).enableEvents( false ).setScale( 0.8, 0.8 );
            var solidOnes = [shakerIcon, solidText ];
            var liquidOnes = [dropperIcon, solutionText ];
            var buttonContents = [vbox( -8, solidOnes ), vbox( -8, liquidOnes )];

            var segmentedButton = createSegmentedButton( buttonContents, model ).enableEvents( true );
            segmentedButton.mouseEnter = function ( mouseEvent ) { CAAT.setCursor( 'pointer' ); };
            segmentedButton.mouseExit = function ( mouseEvent ) { CAAT.setCursor( 'default' ); };
            segmentedButton.mouseClick = function ( mouseEvent ) {
                model.solidSelected = !model.solidSelected;
                model.toggledSolidSelected();
                segmentedButton.dirty = true;
            };
            container.addChild( segmentedButton.setLocation( background.width / 2 - segmentedButton.width / 2, background.height / 2 - 10 ) );

            var comboBox = new CAAT.ShapeActor().setSize( 190, 40 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).
                    setFillStyle( 'white' ).setStrokeStyle( 'black' ).setLocation( 100, 5 );
            comboBox.mouseEnter = function ( mouseEvent ) { CAAT.setCursor( 'pointer' ); };
            comboBox.mouseExit = function ( mouseEvent ) { CAAT.setCursor( 'default' ); };
            container.addChild( comboBox );

            var popup = new CAAT.ShapeActor().setSize( 350, 355 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'white' ).setStrokeStyle( 'black' );

            function createSquareAndTextNode( solute ) {
                var square = rectangleNode( 30, 30, 'rgb(' + solute.maxColor.red + ',' + solute.maxColor.green + ',' + solute.maxColor.blue + ')', 1, 'gray' );
                var entryText = new CAAT.TextActor().setFont( "24px sans-serif" ).setText( solute.name ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setLocation( 40, 5 ).enableEvents( false );
                var container = new CAAT.ActorContainer().setSize( 340, 30 );
                container.backgroundColor = 'white';
                container.paint = function ( director, time ) {
                    var ctx = director.ctx;
                    if ( container.backgroundColor ) {
                        ctx.save();
                        ctx.fillStyle = container.backgroundColor;
                        ctx.fillRect( 0, 0, container.width, container.height );
                        ctx.restore();
                    }
                };
                square.enableEvents( false );
                entryText.enableEvents( false );
                container.addChild( square );
                container.addChild( entryText );
                container.enableEvents( false );
                return container;
            }

            function createSquareAndTextNodeButton( solute ) {
                var result = createSquareAndTextNode( solute );
                result.mouseEnter = function ( mouseEvent ) {
                    result.backgroundColor = 'yellow';
                    CAAT.setCursor( 'pointer' );
                };
                result.mouseExit = function ( mouseEvent ) {
                    result.backgroundColor = 'white';
                    CAAT.setCursor( 'default' );
                };
                result.mouseClick = function ( mouseEvent ) {
                    absorbedCrystals = 0;
                    fluid.dirty = true;
                    rootNode.removeChild( popup );
                    comboBox.removeChild( comboBox.displayedComboBoxItem );
                    model.solute = solute;
                    comboBox.displayedComboBoxItem = createSquareAndTextNode( model.solute ).setLocation( 5, 5 );
                    comboBox.displayedComboBoxItem.background = null;
                    comboBox.addChild( comboBox.displayedComboBoxItem );
                    CAAT.setCursor( 'default' );
                };
                result.enableEvents( true );
                return result;
            }

            var displayedComboBoxItem = createSquareAndTextNode( model.solute ).setLocation( 5, 5 );
            comboBox.displayedComboBoxItem = displayedComboBoxItem;
            comboBox.addChild( displayedComboBoxItem );

            var buttonBox = new CAAT.ShapeActor().setSize( 30, 30 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'rgb(220,220,220)' ).setLocation( comboBox.width - 30 - 5, 5 );
            comboBox.addChild( buttonBox );

            var triangle = new CAAT.Actor().setSize( 30, 30 ).setLocation( 10, 10 );
            triangle.paint = function ( director, time ) {
                var ctx = director.ctx;
                ctx.save();
                ctx.strokeStyle = 'gray';
                ctx.beginPath();
                ctx.moveTo( 0, 0 );
                ctx.lineTo( 10, 0 );
                ctx.lineTo( 5, 10 );
                ctx.closePath();

                ctx.lineWidth = 6;
                ctx.lineJoin = 'square';
                ctx.lineCap = 'square';
                ctx.stroke();

                ctx.restore();
            };

            buttonBox.addChild( triangle );

//            solidCircle.mouseClick = function ( e ) {
//                solidCircle.setFillStyle( 'black' );
//                fluidCircle.setFillStyle( 'white' );
//            };
//            fluidCircle.mouseClick = function ( e ) {
//                solidCircle.setFillStyle( 'white' );
//                fluidCircle.setFillStyle( 'black' );
//            };

            buttonBox.enableEvents( false );
            var popupItemOffsetY = 2;
            var itemSpacing = 15;
            var itemSize = 30 + itemSpacing;
            popup.addChild( createSquareAndTextNodeButton( model.solutes[0] ).setLocation( 2, 0 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[1] ).setLocation( 2, 1 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[2] ).setLocation( 2, 2 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[3] ).setLocation( 2, 3 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[4] ).setLocation( 2, 4 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[5] ).setLocation( 2, 5 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[6] ).setLocation( 2, 6 * itemSize + popupItemOffsetY ) );
            popup.addChild( createSquareAndTextNodeButton( model.solutes[7] ).setLocation( 2, 7 * itemSize + popupItemOffsetY ) );
            popup.setLocation( 640, 66 );
            comboBox.mouseClick = function ( e ) {
                var indexFound = rootNode.findChild( popup );
                if ( indexFound > 0 ) {
                    rootNode.removeChild( popup );
                }
                else {
                    rootNode.addChild( popup );
                }
            };

            container.comboBox = comboBox;
            return container;
        }

        var evaporationControlPanel = createEvaporationControlPanel( mobile );
        var soluteControlPanel = createSoluteControlPanel();

        rootNode.addChild( border );
        rootNode.addChild( fluid );

        rootNode.addChild( bottomFlowingWater );
        rootNode.addChild( topFlowingWater );

        rootNode.addChild( createTick( 0.1, 30 ) );
        rootNode.addChild( createTick( 0.2, 30 ) );
        rootNode.addChild( createTick( 0.3, 30 ) );
        rootNode.addChild( createTick( 0.4, 30 ) );
        rootNode.addChild( createTick( 0.5, 60 ) );
        rootNode.addChild( createTick( 0.6, 30 ) );
        rootNode.addChild( createTick( 0.7, 30 ) );
        rootNode.addChild( createTick( 0.8, 30 ) );
        rootNode.addChild( createTick( 0.9, 30 ) );
        rootNode.addChild( createTick( 1.0, 60 ) );

        rootNode.addChild( beaker );

        rootNode.addChild( topFaucetPipe );
        rootNode.addChild( topFaucet );
        rootNode.addChild( shaker );
        rootNode.addChild( topKnob );

        rootNode.addChild( bottomFaucet );
        rootNode.addChild( bottomKnob );

        rootNode.addChild( evaporationControlPanel );
        rootNode.addChild( soluteControlPanel );

        var oneLiterLabel = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "1 L" ).calcTextSize( director ).
                setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 224, 186 );
        rootNode.addChild( oneLiterLabel );

        var oneHalfLiterLabel = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "1/2 L" ).calcTextSize( director ).
                setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 224, 186 + 200 );
        rootNode.addChild( oneHalfLiterLabel );

        var removeSoluteButton = createButton( director, translate( 'removeSolute' ), 130, 'rgb(240,240,240)' ).setLocation( 589, 641 );
        removeSoluteButton.mouseClick = function ( e ) {
            absorbedCrystals = 0;
            fluid.dirty = true;
        };
        rootNode.addChild( removeSoluteButton );

        var resetAllButton = createButton( director, "Reset All", 80, 'orange' );
        resetAllButton.setPosition( 1024 - resetAllButton.width - 10, 768 - resetAllButton.height - 10 );
        resetAllButton.mouseClick = function ( e ) {
            document.location.reload( true );
        };
        rootNode.addChild( resetAllButton );

        var debugOutput = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "<debug output>" ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).setPosition( 0, 0 );
//        rootNode.addChild( debugOutput );

        var concentrationMeterBodyImage = new CAAT.Actor().setBackgroundImage( director.getImage( 'concentration-meter-body' ), true );
        var concentrationMeterBody = new CAAT.ActorContainer().setPosition( 785, 280 ).enableEvents( false ).setSize( concentrationMeterBodyImage.width, concentrationMeterBodyImage.height );
        concentrationMeterBody.addChild( concentrationMeterBodyImage );
        concentrationMeterBody.addChild( new CAAT.TextActor().setFont( "25px sans-serif" ).setText( translate( "concentration" ) ).calcTextSize( director ).setTextFillStyle( 'white' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 20, 10 ) );
        concentrationMeterBody.addChild( new CAAT.TextActor().setFont( "25px sans-serif" ).setText( '(' + translate( "units.molesPerLiter" ) + ')' ).calcTextSize( director ).setTextFillStyle( 'white' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 60, 35 ) );
        var concentrationMeterReadoutText = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "-" ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 100, 80 );
        concentrationMeterBody.addChild( concentrationMeterReadoutText );

        var concentrationMeterProbe = new CAAT.Actor().setBackgroundImage( director.getImage( 'concentration-meter-probe' ), true ).setPosition( 760, 425 );
        concentrationMeterProbe.enableDrag();

        //Allow interaction even if outside of parent bounds
        scene.addActorToInputList( concentrationMeterProbe, 0, 0 );

        //Uses the same cubic curve defined in Java implementation of ConcentrationMeterNode.WireNode
        var wireNode = new CAAT.Actor().setSize( director.width * 10, director.width * 10 ).enableEvents( false );
        wireNode.paint = function ( director, time ) {

            var ctx = director.ctx;
            ctx.save();
            ctx.strokeStyle = 'gray';
            ctx.beginPath();

            var bodyConnectionX = concentrationMeterBody.x + concentrationMeterBody.width / 2;
            var bodyConnectionY = concentrationMeterBody.y + concentrationMeterBody.height;

            var c1OffsetX = bodyConnectionX + 0;
            var c1OffsetY = bodyConnectionY + (concentrationMeterBody.x + concentrationMeterBody.width / 2 - concentrationMeterProbe.x) / 4;
            var c2OffsetX = concentrationMeterProbe.x + concentrationMeterProbe.width + 0;
            var c2OffsetY = concentrationMeterProbe.y + concentrationMeterProbe.height / 2 + 0;
            ctx.moveTo( bodyConnectionX, bodyConnectionY );
            ctx.bezierCurveTo( c1OffsetX, c1OffsetY, c2OffsetX, c2OffsetY, concentrationMeterProbe.x + concentrationMeterProbe.width, concentrationMeterProbe.y + concentrationMeterProbe.height / 2 );

            ctx.lineWidth = 6;
            ctx.lineJoin = 'round';
            ctx.lineCap = 'round';

            ctx.stroke();
            ctx.restore();
        };


        rootNode.addChild( concentrationMeterBody );
        rootNode.addChild( concentrationMeterProbe );
        rootNode.addChild( wireNode );
        scene.addChild( rootNode );

        //This resize strategy is buggy on ipad if you change orientation more than once
        //{function(director{CAAT.Director}, width{integer}, height{integer})}
        var onResizeCallback = function ( director, width, height ) {
            //Scale up or down to fit the screen
            var designWidth = 1024;
            var designHeight = 768;
            var windowWidth = director.width;
            var windowHeight = director.height;
            var sx = windowWidth / designWidth;
            var sy = windowHeight / designHeight;
            var min = Math.min( sx, sy );
            rootNode.setScaleAnchored( min, min, 0, 0 );

            var innerWidth = window.innerWidth;
            var innerHeight = window.innerHeight;

            //Center on available bounds
            if ( sy == min ) {
                rootNode.setPosition( windowWidth / 2 - designWidth * min / 2, 0 );
            }
            else {
                rootNode.setPosition( 0, windowHeight / 2 - designHeight * min / 2 );
            }
        };

        //http://stackoverflow.com/questions/7814984/detect-ios5-within-mobile-safari-javascript-preferred
        // this helps detect minor versions such as 5_0_1
        //TODO: would be nice to get resizing working properly on ios
        if ( isIOS() ) {
//            document.write( "You have iOS 5 or 6!" );
        }
        else {
            director.enableResizeEvents( CAAT.Director.prototype.RESIZE_BOTH, onResizeCallback );
        }

        //causes buggy behavior on ipad in safari
        onResizeCallback( director, director.width, director.height );

        //TODO: Center content in the window
//        rootNode.setPosition( director.width / 2 - designWidth / 2, director.height / 2 - designHeight / 2 );

        var crystals = [];

        // Array Remove - By John Resig (MIT Licensed)
        //http://stackoverflow.com/questions/500606/javascript-array-delete-elements
        Array.prototype.remove = function ( from, to ) {
            var rest = this.slice( (to || from) + 1 || this.length );
            this.length = from < 0 ? this.length + from : from;
            return this.push.apply( this, rest );
        };

        scene.createTimer( 0, 30,
                           function ( scene_time, timer_time, timertask_instance ) {   // timeout
                               timertask_instance.reset( scene_time );

//                               console.log( topFaucetPipe.x + ", " + topFaucetPipe.y );
//                               console.log( knob.x + ", " + knob.y );
//                               console.log( bottomKnob.x + ", " + bottomKnob.y );
//                               console.log( bottomFlowingWater.x + ", " + bottomFlowingWater.y );
//                               console.log( evaporationControlPanel.x + ", " + evaporationControlPanel.y );
//                               console.log( oneLiterLabel.x + ", " + oneLiterLabel.y );
//                               console.log( removeSoluteButton.x + ", " + removeSoluteButton.y );

                               if ( shaker.y != shaker.lastY ) {
                                   var w = 10;
                                   var x = shaker.x + 20 + Math.random() * 50;
                                   var y = shaker.y + 140 + Math.random() * 20;
                                   if ( x > beakerX && x < beakerMaxX ) {

                                       //TODO: this line breaks MVC a bit
                                       var crystal = new CAAT.Actor().setBounds( x, y, w, w ).setFillStyle( 'rgb(' + model.solute.maxColor.red + ',' + model.solute.maxColor.green + ',' + model.solute.maxColor.blue + ')' );
                                       crystal.velocity = 0;
                                       rootNode.addChild( crystal );
                                       crystal.setRotation( Math.random() * Math.PI * 2 );
                                       crystals.push( crystal );
                                   }
                               }

                               if ( topKnob.getValue() > 0 ) {
                                   //add fluid
                                   fluidHeight = fluidHeight + topKnob.getValue() * 4;
                                   if ( fluidHeight > beakerHeight ) {
                                       fluidHeight = beakerHeight;
                                   }
                               }
                               topFlowAmount = topKnob.getValue();

                               if ( bottomKnob.getValue() > 0 ) {

                                   var initialConcentration = absorbedCrystals / fluidHeight * 2;

                                   //remove fluid
                                   var fluidLost = Math.min( bottomKnob.getValue() * 4, fluidHeight );
                                   fluidHeight = fluidHeight - fluidLost;

                                   //Concentration = absorbedCrystals / fluidHeight * 2
                                   //Solve for absorbedCrystals:
                                   //absorbedCrystals = Concentration * fluidHeight / 2
                                   //take a derivative, with Concentration is fixed.
                                   //d absorbedCrystals = concentration / 2 * d fluidHeight

                                   absorbedCrystals = initialConcentration * fluidHeight / 2;
                                   fluid.dirty = true;

                               }
                               bottomFlowAmount = bottomKnob.getValue();

                               for ( var index = 0; index < crystals.length; index++ ) {
                                   crystals[index].velocity = crystals[index].velocity + 1;
                                   crystals[index].setPosition( crystals[index].x - 2, crystals[index].y + crystals[index].velocity );
                               }

                               for ( var i = 0; i < crystals.length; i++ ) {
                                   var c = crystals[i];
                                   if ( c.y + c.height > beakerMaxY - fluidHeight ) {
                                       crystals.splice( i, 1 );
                                       rootNode.removeChild( c );
                                       i--;
                                       absorbedCrystals++;
                                       fluid.dirty = true;
                                   }
                               }
                               shaker.lastY = shaker.y;

                               var evaporationValue = evaporationControlPanel.evaporationKnob.getValue();
                               if ( evaporationValue > 0 ) {
                                   //remove fluid
                                   fluidHeight = fluidHeight - evaporationControlPanel.evaporationKnob.getValue() * 4;
                                   if ( fluidHeight < 0 ) {
                                       fluidHeight = 0;
                                   }
                                   fluid.dirty = true;
                               }

                               var probeCenterX = concentrationMeterProbe.x + concentrationMeterProbe.width / 2;
                               var probeCenterY = concentrationMeterProbe.y + concentrationMeterProbe.height / 2;
                               var newText = probeCenterX < fluid.x + fluid.width &&
                                             probeCenterX > fluid.x &&
                                             probeCenterY > beakerMaxY - fluidHeight &&
                                             probeCenterY < beakerMaxY ? (Math.min( absorbedCrystals / fluidHeight * 2, model.solute.maxConcentration )).toFixed( 3 ) :
                                             "-";
                               if ( model.lastText != newText ) {
                                   concentrationMeterReadoutText.setText( newText );
                                   model.lastText = newText;

                                   var compareClearStyle = false;
                                   if ( compareClearStyle ) {
                                       if ( newText == "-" ) {
                                           director.setClear( CAAT.Director.CLEAR_ALL );
                                       }
                                       else {
//                                       console.log( "hello there" );
                                           director.setClear( CAAT.Director.CLEAR_DIRTY_RECTS );
                                       }
                                   }
                               }

                           },
                           function ( scene_time, timer_time, timertask_instance ) { }, // tick
                           function ( scene_time, timer_time, timertask_instance ) { } ); // cancel


        //This breaks Win7/Chrome interaction, so only enable on ios + android
//        var allowPinchToZoomOnMobile = false;//Needs to be rewritten to work with stage centering strategy
//
//        if ( mobile && allowPinchToZoomOnMobile ) {
//            //Pinch to zoom in on different parts of the sim, mainly to make up for shortcomings in the user interface on smaller devices.
//            //        var canvasElement = document.getElementById( "canvas" );
//            var canvasElement = document.getElementsByTagName( 'canvas' )[0];
//            var hammer = new Hammer( canvasElement );
////                originalEvent   : event,
////                position:_pos.center,
////                scale:calculateScale( _pos.start, _pos.move ),
////                rotation :  calculateRotation( _pos.start, _pos.move )
////        hammer.ontransformstart = function ( ev ) {
////        };
//            hammer.ontransform = function ( ev ) {
////            var string = "scale = " + ev.scale + ", x = " + ev.position.x + ", centerx = " + centerX;
////            console.log( string );
////            rootNode.setLocation( 0, 0 );
//                rootNode.setScaleAnchored( ev.scale, ev.scale, ev.position.x / 1024, ev.position.y / 768 );
//                rootNode.centerAt( ev.position.x, ev.position.y );
////            rootNode.setScale( ev.scale, ev.scale );
//                debugOutput.setText( string );
//            };
////        hammer.ontransformend = function ( ev ) {
////        };
//        }
    }

//    console.log( "hello" );
    /**
     * Startup it all up when the document is ready.
     * Change for your favorite frameworks initialization code.
     */
    window.addEventListener(
            'load',
            function () {
                CAAT.modules.initialization.init(
                        window.innerWidth, window.innerHeight,

                        /* and will be added to the end of document. set an id of a canvas or div element */
                        undefined,

                        /*
                         load these images and set them up for non splash scenes.
                         image elements must be of the form:
                         {id:'<unique string id>',    url:'<url to image>'}

                         No images can be set too.
                         */
                        [
                            {id: 'shaker', url: 'resources/shaker.png'},
                            {id: 'faucet_front', url: 'resources/faucet_front.png'},
                            {id: 'faucet_pipe_long', url: 'resources/faucet_pipe_long.png'},
                            {id: 'slider-knob', url: 'resources/slider-knob.png'},
                            {id: 'concentration-meter-body', url: 'resources/concentration-meter-body.png'},
                            {id: 'concentration-meter-probe', url: 'resources/concentration-meter-probe.png'},
                            {id: 'shaker-icon', url: 'resources/shaker-icon.png'},
                            {id: 'dropper-icon', url: 'resources/dropper-icon.png'},
                            {id: 'dropper-background', url: 'resources/dropper_background.png'},
                            {id: 'dropper-foreground', url: 'resources/dropper_foreground.png'},
                            {id: 'dropper-button-unpressed', url: 'resources/button_unpressed.png'},
                            {id: 'dropper-button-pressed', url: 'resources/button_pressed.png'}
                        ],

                        /*
                         onEndSplash callback function.
                         Create your scenes on this method.
                         */
                        createScenes

                );
            },
            false );

//    CAAT.modules.initialization.init(
//            window.innerWidth, window.innerHeight,
//
//            /* and will be added to the end of document. set an id of a canvas or div element */
//            undefined,
//
//            /*
//             load these images and set them up for non splash scenes.
//             image elements must be of the form:
//             {id:'<unique string id>',    url:'<url to image>'}
//
//             No images can be set too.
//             */
//            [
//                {id:'shaker', url:'resources/shaker.png'},
//                {id:'faucet_front', url:'resources/faucet_front.png'},
//                {id:'faucet_pipe_long', url:'resources/faucet_pipe_long.png'},
//                {id:'slider-knob', url:'resources/slider-knob.png'},
//                {id:'concentration-meter-body', url:'resources/concentration-meter-body.png'},
//                {id:'concentration-meter-probe', url:'resources/concentration-meter-probe.png'},
//                {id:'shaker-icon', url:'resources/shaker-icon.png'},
//                {id:'dropper-icon', url:'resources/dropper-icon.png'},
//                {id:'dropper-background', url:'resources/dropper_background.png'},
//                {id:'dropper-foreground', url:'resources/dropper_foreground.png'},
//                {id:'dropper-button-unpressed', url:'resources/button_unpressed.png'},
//                {id:'dropper-button-pressed', url:'resources/button_pressed.png'}
//            ],
//
//            /*
//             onEndSplash callback function.
//             Create your scenes on this method.
//             */
//            createScenes
//
//    )
})();