# VoxSpell



Disclaimer
----------
This program is incomplete.

The Daily Challenges and Statistics have not yet been implemented,
I'm still not sure on how I want to do these.

The GUI look and feel is still a work in progress and most of the 
current images are placeholders.

I have decided to use categories for word lists over the leveling system however,
the list provided still follows the leveling system.

I'm thinking about removing the second attempt given in the spelling quiz.

I have plans to include where the user went wrong for the popup shown
when comparing the users attempts to the actual spelling. 

Some of the code is still a mess and the .fxml files are all over the place
because I had issues when locating them.



Running Voxspell
----------------
You need the following files in your current directory:
	VoxSpell.jar 
	run.sh

You will need to run the run.sh script. 
Type:
	chmod +x run.sh
	./run.sh

	
If you want the VoxSpell program to include definition generation within the
Word List Editor you need to run the install_dict.sh script. 
This will install sdcv a command line dictionary on your system (35.5MB download).
You will need super user and internet access to run this script. 
Type:
	chmod +x install_dict.sh
	./install_dict.sh
I don't think this works in UG4 because you can't use 'apt-get'



Authors
-------
Will Molloy - wmol664
Karim Cisse



Copyright
---------
Festival - http://www.cstr.ed.ac.uk/
sdcv - http://dushistov.github.io/sdcv/
images used:
https://pixabay.com/en/tick-asterisk-cross-red-green-40678/
https://pixabay.com/en/flags-russia-usa-germany-china-1722052/
https://pixabay.com/en/certificate-paper-parchment-roll-154169/

