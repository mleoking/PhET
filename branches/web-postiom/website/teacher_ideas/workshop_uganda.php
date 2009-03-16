<?php


// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class UgandaWorkshopPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $installer_url = SITE_ROOT.'get_phet/full_install.php';

        $publications_location = PHET_DIST_ROOT."publications/";
        $workshop_materials_location = PHET_DIST_ROOT."workshops/";
        $uganda_materials_location = PHET_DIST_ROOT."workshops/Uganda/";

        print <<<EOT

<p><a href="{$uganda_materials_location}UgandaPhETWorkshops.m4v">Watch a video about the workshops</a> (Video courtesy of <a href="http://sokotapictures.com">Sokota Pictures</a>)</p>

<p>
<a href="workshop_uganda_photos.php">See photos of the workshops</a>
</p>

<p>
<a href="{$publications_location}McKagan_UgandaPaper.pdf">An article about the workshops</a>
</p>

<p>
Workshop materials:
</p>
<ul>
<li><a href="{$uganda_materials_location}UgandaPhETWorkshop.ppt">Uganda PhET Workshop Presentation</a></li>
<li><a href="{$workshop_materials_location}PhETWorkshopHandouts.zip">Handouts used in PhET Workshops</a></li>
</ul>


<h1>Guide to Running PhET Workshops in Developing Countries</h1>
<p>
We encourage others to download our <a href="{$workshop_materials_location}PhETWorkshopHandouts.zip">workshop materials</a> and <a href="{$installer_url}">installers</a> and run workshops to help teachers learn about PhET.  Here are some tips for running workshops in developing countries, based on what I learned from the first PhET workshop in Uganda. <strong>- Sam McKagan</strong>
</p>

<ul>
<li>
Work with a local charitable organization with connections to schools.  Ask them to help you recruit teachers and find a location.  The location should have electricity and/or a generator in case the power goes out.
</li>

<li>
If possible, try to find a way to bring computers to donate.  You can get refurbished computers in bulk on E-bay for quite cheap.  Although desktops are more difficult to transport than laptops, they are better for donating to schools as they are less likely to be stolen.
</li>

<li>
If you can bring textbooks to donate, these will also be very much appreciated.  Many rural schools have only one or two physics textbooks for 50 students to share.
</li>

<li>
Perhaps one of the most valuable things you can do for the teachers is to teach them some physics.  This was one of the most tangible results from my workshop.
</li>

<li>
Another valuable thing you can do for teachers is to teach them about inquiry-based teaching ideas.  The teachers in my workshop were very open to new ideas and enthusiastic about inquiry-based teaching, but it's not clear how much I could really impact the way they teach with a short workshop.
</li>

<li>
Bring CD's with all the software needed to run PhET.  Don't expect to be able to access the internet. Directions for making installation CDs are available at: <a href="http://phet.colorado.edu/get_phet/full_install.php">http://phet.colorado.edu/get_phet/full_install.php</a> 
</li>

<li>
Most schools in Uganda do not have sufficient laboratory equipment.
</li>

<li>
Many schools do not have computers at all.  However, buying a single computer is significantly cheaper than buying laboratory equipment, so if school directors can see the value gained by a computer, they may be willing to buy one.  The simulations are a powerful tool for helping school directors see the value of computers.
</li>

<li>
Every computer I encountered in Uganda was a PC running Windows.  As far as I can tell, Macintosh and Linux are pretty much non-existent.
</li>

<li>
Many of the computers I encountered were as much as 10 years old.  The laptops I brought were a few years old and would not be considered top of the line here, but the IT people there were very impressed with their specs.
</li>

<li>
Software piracy is rampant and universal.  Nearly every computer I encountered there was running Windows XP, even though many were so old that they might have been better off running an earlier version of Windows.  I saw very little use of open-source software, probably because pirated proprietary software is so readily available and people assume it is better.
</li>

<li>
The installation CDs that I brought included all software needed to run the simulations, including a web browser, Java, and Flash, in every possible version that might be needed for every possible operating system.  While the Java and Flash installers turned out to be crucial (you cannot assume that computers have this software, or that you will have internet access), the array of options was overwhelming for first-time computer users.  I would recommend bringing one CD with every piece of software you could ever possibly need for yourself, but the CDs you distribute should include only Windows software that installs automatically.  Any directions should include every single step a user will have to make, including details such as whether to double click or single click.
</li>

<li>
Any time you install software, there is a licensing agreement that you have to agree to.  The teachers I worked with did not understand what the licensing agreement was, and found it overwhelming.  Directions should warn users that they will encounter such a license agreement, and tell them what to do with it.
</li>

<li>
In assisting teachers in installing software, I found that they did not understand the purpose of the file called "readme".  They understood it better when I referred to it as the "instructions".  They also gave me blank stares when I used the term "operating system".  "Type of computer" seemed to work better.
</li>

<li>
I did not think to bring mice for the laptops.  This was a mistake, because learning to use the touchpad was a large obstacle for many of the teachers.  While there were spare mice there for the desktops, most had an old-fashioned mouse plug rather than a USB plug and therefore could not be plugged into the laptops.
</li>

<li>
Many teachers had trouble using the mouse to drag objects.  I found that it helped to tell them to think of holding down the mouse button as holding onto the object, so that it moves with the mouse when you move it, and to think of letting go of the mouse button as letting go of the object.
</li>

<li>
Many teachers had trouble figuring out when to double click and when to single click.  I had trouble coming up with a general rule to help them with this issue.
</li>

<li>
Power is not reliable, and when the power went out, being able to run the laptops on battery power until they got the generator working was critical for not halting the workshops.
</li>

<li>
While laptops may seem preferable to desktops because they can run on batteries, locals recommended buying desktops for schools, as laptops were likely to be stolen.
</li>

<li>
Because power is not reliable, surge protectors are vital.  Do not plug a computer into the wall in Africa without a surge protector!  They don't necessarily use the term "surge protector," so look at the power strip to see if it has a fuse.
</li>

<li>
With sufficient jury-rigging, it is possible to plug nearly any kind of plug into nearly any kind of outlet without an adaptor.
</li>

<li>
It is possible to plug over 10 computers into a single wall outlet without blowing a fuse.
</li>

<li>
Nothing ever starts on time in Uganda, and it is very difficult to get anyone to tell you any concrete plans about anything, such as how long something will take or who will be there.  Plans for workshops should be adaptable to any situation, such as a shortage of computers, projectors, or power, or having more or less time, or more or fewer participants, than you anticipated.
</li>

<li>
I worked with a Christian organization and we prayed several times a day during my workshops.  American physicists tend to be secular to the point of being anti-religious, and many might be uncomfortable with this practice.  Christian relief organizations do an incredible amount of good work in Africa and it is likely that any work you do there will be facilitated through one of these organizations.  I advise you to get over it.
</li>
</ul>

EOT;
    }
}

$page = new UgandaWorkshopPage("PhET Workshops in Soroti, Uganda (Jan 29 - Feb 1, 2008)", NAV_TEACHER_IDEAS, null);
$page->update();
$page->render();

?>