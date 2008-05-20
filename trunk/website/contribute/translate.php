<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../"); include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class TranslationInstructionsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $example_sim = "http://".PHET_DOMAIN_NAME."/new/simulations/sims.php?sim=Models_of_the_Hydrogen_Atom";

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
            <p>
                This is a beta version of the PhET Translation Utility. PLEASE READ THIS PAGE COMPLETELY.
            </p>
            <p>The Translation Utility can be used to create a translation for a new language, or to edit an existing translation. In both cases, the instructions are the same.</p>

            <h2>
                General instructions
            </h2>

            <ol>
                <li>
                    Download <a href="get_translate_utility.php">translation-utility.jar</a> to your computer.
                </li>

                <li>
                    Go to the PhET website and download the JAR file for the simulation that you want to translate (press the "Run Offline" button on a simulation page).
                </li>

                <li>
                    Double-click on <a href="translation-utility.jar">translation-utility.jar</a> to start the Translation Utility.
                </li>

                <li>
                    Enter the full path name of the simulation's JAR file.
                </li>

                <li>
                    Select the language that you're translating from the option menu. If your language is not listed in the menu, contact <a href="mailto:{$phet_help_email}?subject=Translation%20Utility"><span class="red">{$phet_help_email}</span></a> to request a custom language code. When you receive your custom language code, select CUSTOM from the option menu (the last option in the menu) and enter your customer language code in the text box that appears.
                </li>

                <li>
                    Press the "Continue" button.
                </li>

                <li>
                    Enter the translated strings in the right-most column of text fields.
                </li>

                <li>
                    Press the "Test" button to run the simulation with the translated strings.
                </li>

                <li>
                    Use the "Save" and "Load" buttons to save and load your work.
                </li>

                <li>
                    Press the "Submit to PhET" button when you've finished your translations. This saves the translations as a Java properties file. Note the properties file name and email address shown in the dialog.
                </li>

                <li>
                    Email the translation file to PhET.
                </li>
            </ol>

            <h2>
                Example Usage
            </h2>

            <p>
                This example shows how to translate "Models of the Hydrogen Atom" to French.
            </p>

            <ol>
                <li>
                    Download translation-utility.jar to your computer.
                </li>

                <li>
                    Go <a href="{$example_sim}" >here</a> and click the "Run Offline" button. This will download hydrogen-atom.jar.
                </li>

                <li>
                    Double-click on translation-utility.jar to start the Translation Utility.
                </li>

                <li>
                    Press the "Browse" button and locate hydrogen-atom.jar that you downloaded.
                </li>

                <li>
                    Select "French (fr) from the language option menu.
                </li>

                <li>
                    Press the "Continue" button.
                </li>

                <li>
                    Enter the French translations in the right-most column of text fields. For example, for hydrogen-atom.name, enter "My French Title".
                </li>

                <li>
                    Press the "Test" button to run the simulation with the translated strings. You'll see the simulation start with "My French Title" in the progress dialog.
                </li>

                <li>
                    Save your work to a file using the "Save" button. You can exit the Translation Utility and load that file later using the "Load" button.
                </li>

                <li>
                    Press the "Submit to PhET" button. Your translations will be saved in hydrogen-atom-strings_fr.properties.
                </li>

                <li>
                    Email hydrogen-atom-strings_fr.properties to <a href="mailto:{$phet_help_email}?Subject=Simulation%20Translation"><span class="red">{$phet_help_email}</span></a>
                </li>
            </ol>

            <h2>
                Caveats
            </h2>

            <ul>
                <li>
                    The Translation Utility works only with Java-based simulations. PhET's Flash-based simulations do not currently support translation. PhET expects to support translation of our Flash-based simulations in a future release.
                </li>

                <li>
                    Some strings that appear in a simulation are part of a library of "common components" that are used in all PhET simulations. These "common strings" are not yet translatable via the Translation Utility. Examples of common strings include: the "File" menu, the "Help" menu, the Play/Pause/Step buttons that controls the clock. PhET expects to makes common strings translatable in a future release.
                </li>

                <li>
                    Some of the strings that appear in the Translation Utility may not appear in the simulation that you are translating. This is because similar simulations (eg, the variants of Circuit Construction Kit) share one translation file. If a string does not appear in the simulation that you're translating, feel free to leave it blank.
                </li>
            </ul>

            <h2>
                Mailing List
            </h2>

            <p>
                Join the PhET Translation Utility mailing list by visiting <a href="https://lists.sourceforge.net/lists/listinfo/phet-translation">https://lists.sourceforge.net/lists/listinfo/phet-translation</a>.
            </p>

            <h2>
                Bug Reports
            </h2>

            <p>
                Report bugs to <a href="mailto:{$phet_help_email}?subject=Translation%20Utility"><span class="red">{$phet_help_email}</span></a>. Please include the version number (shown in the title bar) in your correspondence.
            </p>

EOT;
    }

}

$page = new TranslationInstructionsPage("PhET Translation Utility (beta)", NAV_CONTRIBUTE, null);
$page->update();
$page->render();

?>
