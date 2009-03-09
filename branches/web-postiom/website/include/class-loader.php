<?php

// classLoader()
//
// Searches the specified class directories to find the class to
// automaticall load
function classLoader($class_name) {

    // For efficiency, these should be in relative order of the
    // frequency of their use
    static $class_dirs = array(
	'Utils',
        'Simulation',
        'Exception',
        );

    foreach ($class_dirs as $dir) {
        $path = dirname(__FILE__)."/{$dir}/{$class_name}.class.php";
        if (file_exists($path)) {
            require_once($path);
            return;
        }
    }
}

// Nullify any existing autoloads
spl_autoload_register(null, false);
    
// Specify extensions that may be loaded
spl_autoload_extensions('.php, .class.php');

// Register the loader functions
spl_autoload_register('classLoader');

?>
