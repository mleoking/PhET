require.config( {
                    deps:["main"],

                    paths:{
                        vendor:"../js/vendor",
                        plugins:"../js/plugins",

                        underscore:"../js/vendor/underscore-min",
                        image:"../js/vendor/image"
                    },

                    shim:{

                        underscore:{
                            exports:"_"
                        },

                        kinetic:{
                            exports:"Kinetic"
                        }

                    }
                } );

