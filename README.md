# MIT-Address-Book
Tools to update the List of Individuals

The following scripts were designed to be run in Ubuntu and require access to MIT's Outlook Web App client and the MIT network either by being physically present on campus or through the MIT VPN.

## Step 0: Installation Instructions
Run the following commands in a terminal to install dependencies:
```
$ sudo apt-get update
$ sudo apt-get install default-jre default-jdk python-pexpect ldap-utils
```

Compile the Java scraper:
```
$ javac OWAScraper.java nicksapps/*.java
```

If you do not plan to run Step 2 on an MIT internet connection, download and install Cisco AnyConnect from [IS&T's website](https://ist.mit.edu/cisco-anyconnect/all).

## Step 1: Scrape the MIT OWA Address Book
The first step is to download the complete list of MIT aliases from the OWA client. To accomplish this, we have built a bot based on the Java Robot class that manually scrolls through the MIT OWA People directory, copying large segments of HTML and using regular expressions to extract the aliases. Due to the quantity of bugs in Microsoft's OWA client, this is the least stable component of the repository. After extensive use, OWA starts lagging dramatically, failing to load contacts, loading duplicate contacts in a loop, or logging out the user. As far as I can tell, this is the result of shoddy workmanship by Microsoft, but the result is unfortunately robust security to bots. Therefore, this step may require several attempts to work correctly. It usually takes approximately eight hours to run per attempt, and you cannot use your computer for other things while it is running. The output of this step is a CSV file, `dir.csv`, containing a list of all MIT aliases.

Navigate to the [MIT OWA client](https://owa.exchange.mit.edu/owa) and log in if prompted.

Click "People", then "All Email Users". You should now see the beginning of the address book.

Without switching to any other windows, open a terminal and enter the following command:
```
$ java OWAScraper
```

## Step 2: Scrape the MIT LDAP Directory
This step requires a complete list of all MIT aliases in `dir.csv` generated by the previous step. It queries the MIT LDAP directory for information about each alias in `dir.csv`. If the alias corresponds to a graduate student-worker, the script catalogues the individual's name and department in a new CSV file, `dir_w.csv`.

Before starting this step, you must either be logged into MIT internet either via MIT SECURE WiFi, MIT's hardline internet, or the MIT VPN. 

After ensuring `dir.csv` and `ldap_scrape.py` are in the current directory, run the following command in the terminal:
```
$ python ldap_scrape.py
```
The script takes a few hours to run depending on the strength of the internet connection. It is very stable and displays the progress as a percentage in the terminal. You may use your computer for other tasks while this step runs.

## Step 3: Merge the Updates into the List of MIT Individuals
TODO: Write this section.

## Repository Overview
| File Name | Description |
| --------- | ----------- |
| nicksapps/ | Java classes imported by OWAScraper in Step 1. |
| screenshots/ | Images used by OWAScraper to identify important parts of the screen in Step 1. |
| ldap_scrape.py | Main Python script used in Step 2. |
| LICENSE | The Unlicense. |
| OWAScraper.java | Main Java script used in Step 1. |
| README.md | This document. |