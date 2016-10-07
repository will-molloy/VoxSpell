#!/bin/bash

# Install sdcv
sudo apt-get install sdcv

# Install the dictionary at /usr/share/stardict/dic
sudo mkdir /usr/share/stardict
sudo mkdir /usr/share/stardict/dic
cd Dictionary
sudo tar -xvjf stardict-dictd_www.dict.org_gcide-2.4.2.tar.bz2 -C /usr/share/stardict/dic


