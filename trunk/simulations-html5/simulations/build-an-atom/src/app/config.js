require.config({
  deps: ["main"],

  paths: {
    vendor: "../js/vendor",
    plugins: "../js/plugins",

    underscore: "../js/vendor/underscore-min",

    // plugins
    tpl: "../js/plugins/tpl",

    // dev only
    text: "../js/plugins/text",
    json: "../js/plugins/json"

  },

  shim: {

    underscore: {
      exports: "_"
    },

    'main-deprecated': []

  }
});

