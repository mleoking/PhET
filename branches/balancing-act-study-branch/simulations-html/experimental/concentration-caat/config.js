require.config( {
                    deps:["main"],

                    paths:{
                        vendor:"../js/vendor",
                        plugins:"../js/plugins",

                        underscore:"../js/vendor/underscore-min",
                        caat:"../js/vendor/caat.js",
                        hammer:"../js/vendor/hammer.js",

                        // plugins
                        tpl:"../js/plugins/tpl",

                        // dev only
                        text:"../js/plugins/text",
                        json:"../js/plugins/json"

                    },

                    shim:{

                        underscore:{
                            exports:"_"
                        },

                        'main-deprecated':[]

                    }
                } );

