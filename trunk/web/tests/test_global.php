<?php

// Turn on errors
error_reporting(E_ERROR | E_ALL | E_STRICT);
ini_set('display_errors', 1);
assert_options(ASSERT_ACTIVE, 1);

// Use the test database 
if (!defined("DB_HOSTNAME")) define("DB_HOSTNAME", "localhost");
if (!defined("DB_NAME")) define("DB_NAME",     "phet_test");
if (!defined("DB_USERNAME")) define("DB_USERNAME", "phet_test");
if (!defined("DB_PASSWORD")) define("DB_PASSWORD", "");

// TEST_SITE_ROOT is an absolute path to the top directory of the website
define('TEST_SITE_ROOT', dirname(dirname(__FILE__)).'/website/');

// TEST_ROOT is an absolute path to the top directory of the tests
define('TEST_ROOT', dirname(__FILE__));

// SIMS_ROOT is at absolute path to the test version of the sims
define('SIMS_ROOT', TEST_ROOT.DIRECTORY_SEPARATOR.'_files'.DIRECTORY_SEPARATOR.'sims_minimal'.DIRECTORY_SEPARATOR);
// Other sims dir options
//define('SIMS_ROOT', TEST_ROOT.DIRECTORY_SEPARATOR.'_files'.DIRECTORY_SEPARATOR.'sims_freezeiom'.DIRECTORY_SEPARATOR);
//define('SIMS_ROOT', TEST_ROOT.DIRECTORY_SEPARATOR.'_files'.DIRECTORY_SEPARATOR.'sims_postiom'.DIRECTORY_SEPARATOR);

// Add both these to the include path
set_include_path(
    join(
        array(get_include_path(), TEST_SITE_ROOT, TEST_ROOT),
        PATH_SEPARATOR)
    );

// This is to disable the website automatically setting up the include path
define("INCLUDE_PATH_SET", "true");

?>