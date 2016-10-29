#!/bin/bash

# Install festival
sudo apt-get install festival

# Install sdcv
sudo apt-get install sdcv

# Download dictionary 
mkdir .Dictionary 
cd .Dictionary
wget https://web.archive.org/web/20140917131745/http://abloz.com/huzheng/stardict-dic/dict.org/stardict-dictd_www.dict.org_gcide-2.4.2.tar.bz2

# Install the dictionary at /usr/share/stardict/dic - this is where sdcv looks
sudo mkdir /usr/share/stardict
sudo mkdir /usr/share/stardict/dic
sudo tar -xvjf stardict-dictd_www.dict.org_gcide-2.4.2.tar.bz2 -C /usr/share/stardict/dic
cd ..
rm -rf .Dictionary


