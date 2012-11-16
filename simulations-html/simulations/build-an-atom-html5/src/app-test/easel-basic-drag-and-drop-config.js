require.config({
                   deps: ["easel-basic-drag-and-drop"],

                   paths: {
                       vendor: "../js/vendor",
                       plugins: "../js/plugins",
                       kinetic: "../js/vendor/kinetic-v4.0.4",
                       easel:"../js/vendor/easeljs-0.5.0.min"
                   },

                   shim: {

                       underscore: {
                           exports: "_"
                       },

                       kinetic: {
                           exports: "Kinetic"
                       },

                       easel:{
                           exports:"createjs"
                       }
                   }
               });

