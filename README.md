# Scrape-OWA-LDAP
Tools to scrape alases from an Outlook Web App address book. The following scripts were designed to be run in Ubuntu.

## Installation Instructions
Run the following commands in a terminal to install dependencies:
```
$ sudo apt-get update
$ sudo apt-get install default-jre default-jdk python-pexpect ldap-utils
```

Compile the Java scraper:
```
$ javac OWAScraper.java nicksapps/*.java
```

Install the MuScrape browser extension:
1. Open Firefox.
2. Open the [about:debugging](https://developer.mozilla.org/en-US/docs/Tools/about:debugging) page.
3. Click "This Firefox".
4. Click "Load Temporary Add-on".
5. Select any file in the [browser_extension directory](./browser_extension).
After completing the above steps, the MuScrape icon should appear in the top right corner of your browser.

## Scrape the OWA Address Book
To download the complete list of aliases from an OWA client, we have built a browser extension, MuScrape, that listens to HTTP responses and extracts email addresses. MuScrape works in parallel with a bot based on the Java Robot class that manually scrolls through the OWA People directory. Because the browser extension and Java bot use the system clipboard to communicate, any data you had copied to the clipboard will be deleted. Due to the quantity of bugs in Microsoft's OWA client, this bot will sometimes hang. After extensive use, OWA starts lagging dramatically, failing to load contacts, loading duplicate contacts in a loop, or logging out the user. As far as I can tell, this is the result of shoddy workmanship by Microsoft, but the result is unfortunately robust security to bots. Therefore, this step may require several attempts to work correctly. It usually takes approximately eight hours to run per attempt, and you cannot use your computer for other things while it is running. The output of this step is a list of all aliases stored in the MuScrape extension. The MuScrape extension is adapted from the browser extension [Exity](https://addons.mozilla.org/en-US/firefox/addon/exity/) written by [Cyd](https://addons.mozilla.org/en-US/firefox/user/12774831/), which intercepts live HTTP requests and responses based upon a URL pattern. Exity is licensed under the [MIT License](https://opensource.org/licenses/mit-license.php).

Navigate to your OWA client and log in if prompted.

Click "People", then "All Email Users". You should now see the beginning of the address book.

Without switching to any other windows, open a terminal, navigate to your local copy of this repository, and enter the following command:
```
$ java OWAScraper
```
The above Java bot should switch windows back to the browser and begin scrolling through contacts, enabling MuScrape to extract their aliases.

After the scraper finishes, click on the MuScrape browser extension icon in the top right corner of the browser. Copy the extracted aliases to a CSV file titled `dir.csv` in your local copy of this repository.

Optionally, you can run the following in a terminal to remove duplicates. This step is not required, but will speed up the rest of the process.
```
$ ./rm_duplicates.sh dir.csv
```

## Repository Overview
| File Name | Description |
| --------- | ----------- |
| browser_extension/ | The browser extension, MuScrape. |
| nicksapps/ | Java classes imported by OWAScraper. See [here](https://github.com/rupumped/NicksAPPS/tree/master/Java) for more information. |
| LICENSE | The Unlicense. |
| OWAScraper.java | Main Java script. |
| README.md | This document. |
| rm_duplicates.sh | A shell script that removes all duplicate entries from a CSV. |
