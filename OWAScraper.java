/*
To use this program, navigate to https://owa.exchange.mit.edu/owa/ in Firefox,
log in, and open the address book by clicking the icon next to "Find Someone."
Before running this program, ensure that the address book window is full screen
and the second-to-the-top window behind your terminal or IDE you're using to run
this program. To confirm, ALT+TAB between your terminal and the address book to
make sure both windows are on top (alternatively, just close out of all other
applications). When you run the program, it will use ALT+TAB to switch to the
address book, then a Robot to scrape the contacts' indeces, names, and emails
into a file. Because its a bot, it will take a LONG TIME to run, but you will be
able to observe the progress in real time and halt the program without losing
your work.
*/

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.datatransfer.StringSelection;
import nicksapps.MainPlus;
import nicksapps.RobotPlus;

public class OWAScraper {
    // User parameters
    private static final String DIR_FN = "dir.csv";     // File into which to write data.
    
    // Precompiled regex patterns for HTML extraction
    private static final Pattern userPattern = Pattern.compile("email=(.+?)%40");
    
    public static void main(String[] args) throws AWTException, UnsupportedFlavorException, IOException {
        // Setup bot
        RobotPlus r = new RobotPlus();
        
        // Setup clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection clear = new StringSelection("");
        clipboard.setContents(clear, null);
        
        // Navigate to OWA Client
        r.holdType(new int[] {KeyEvent.VK_ALT}, new int[] {KeyEvent.VK_TAB});
        r.delay(1000);

        // Scroll
        while (! paste(clipboard).contains("zzpacker") ) {
        	copy(clipboard, clear);
            r.type(KeyEvent.VK_DOWN);
            while (paste(clipboard).equals("")) { };
        }
    }
    
    public static String paste(Clipboard clipboard) throws UnsupportedFlavorException, IOException {
    	String contents;
    	while (true) {
    		try {
    			contents = (String) clipboard.getData(DataFlavor.stringFlavor);
    			return contents;
    		} catch (IllegalStateException e) { }
    	}
    }

    public static void copy(Clipboard clipboard, StringSelection contents) {
    	while (true) {
    		try {
    			clipboard.setContents(contents, null);
    			return;
    		} catch (IllegalStateException e) { }
    	}
    }
}