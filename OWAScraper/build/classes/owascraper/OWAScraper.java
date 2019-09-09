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

package owascraper;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nicksapps.MainPlus;
import nicksapps.RobotPlus;

public class OWAScraper {
    // User parameters
    private static final int NUM_CONTACTS = 88403;     // Number of contacts to scrape. Must be exact.
    private static final String DIR_FN = "dir.csv";     // File into which to write data.
    
    // Precompiled regex patterns for HTML extraction
    private static final Pattern isrPattern = Pattern.compile("isr=\"(\\d+)");
    private static final Pattern ierPattern = Pattern.compile("ier=\"(\\d+)");
    private static final Pattern userPattern = Pattern.compile("uri=\"sip:([^@]+)@.*?mit\\.edu");
    
    public static void main(String[] args) throws AWTException, UnsupportedFlavorException  {
        // Setup bot
        RobotPlus r = new RobotPlus();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        HashMap<String,BufferedImage> screenshot = MainPlus.loadImages("screenshots");
        
        // Setup directory file to write
        File writeFile = new File(DIR_FN);
        FileWriter writer = null;
        try {
            writer = new FileWriter(writeFile, true);
        
        } catch (IOException ex) {
            System.err.println("Could not open " + writeFile.getName() + ".\nIf the file is open, please close it.");
            try {
                writer.flush();
                writer.close();
            } catch (IOException exc) {}
            System.exit(0);
        }
        
        // Navigate to OWA Client
        r.holdType(new int[] {KeyEvent.VK_ALT}, new int[] {KeyEvent.VK_TAB});
        r.delayUntilLoad(screenshot.get("addressBook"));
        if (!r.delayUntilLoad(screenshot.get("iframe_select"), 1000)) {
            r.type(KeyEvent.VK_F12);
            r.delayUntilLoad(screenshot.get("iframe_select"));
        }
        r.clickSequence("C", screenshot.get("iframe_select"), screenshot.get("iframe_recipients"));
        r.delay(500);
        if (r.screenContains(screenshot.get("html_body_unsel"))) {
            r.click(screenshot.get("html_body_unsel"), "C");
            r.delayUntilLoad(screenshot.get("html_body_sel"));
        }
        int ier=0;
        while (ier<NUM_CONTACTS) {
            // Copy HTML
            r.mouseMove(screenshot.get("html_body_sel"), "C");
            r.delay(100);
            r.click(RobotPlus.RIGHT);
            r.delay(100);
            for (int d=0;d<9;d++) {
                r.type(KeyEvent.VK_DOWN);
                r.delay(100);
            }
            r.type(KeyEvent.VK_RIGHT);
            r.delay(100);
            r.type(KeyEvent.VK_ENTER);
            r.delay(100);
            
            // Process HTML
            String html = null;
            try {
                html = (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                closeOut(writer, "Could not paste data from clipboard.");
            }
            /* The iframe loads div's of 50 contacts at a time, each one of
             * class "vlvChnk." Each vlvChnk is labelled with the index of the
             * first contact it contains, called "isr," and that of the last
             * contact it contains, called "ier." These values can be useful for
             * confirming that no contacts were lost nor duplicates recorded. */
            String[] vlvChnk = html.split("<div[^>]+class=\"vlvChnk\"");
            for (String chnk : Arrays.copyOfRange(vlvChnk,1,vlvChnk.length)) {
                Matcher m = isrPattern.matcher(chnk);
                m.find();
                int isr = Integer.parseInt(m.group(1));
                m = ierPattern.matcher(chnk);
                m.find();
                int tryIER = Integer.parseInt(m.group(1));
                if (ier>=tryIER) {
                    r.click(new Point(500,300),1000);
                    System.out.println("Tried to keep the program from stalling.");
                }
                ier = tryIER;
                int ind = isr;

                Matcher matcher = userPattern.matcher(chnk);
                while (matcher.find()) {
                    try {
                        writer.append((ind++) + "," + matcher.group(1) + "\n");
                    } catch (IOException e) {
                        closeOut(writer, "Ended on: " + (ind-1) + "," + matcher.group(1));
                    }
                }
                try {
                    writer.flush();
                } catch (IOException e) {
                    closeOut(writer, "Could not flush data to file. Ended on: " + ier);
                }
                System.out.println(isr + "-" + ier);
            }
            
            // Scroll down to next set of contacts
            r.click(screenshot.get("selected_contact"), "C");
            r.delay(500);
            for (int d=0;d<100;d++) {
                r.type(KeyEvent.VK_DOWN);
                r.delay(200);
            }
        }
        
        // Save and close
        closeOut(writer, "Done.");
    }
    
    /**
     * Attempts to flush and close the file, then exits.
     * 
     * @param writer  FileWriter to close
     * @param message Custom String to write to the console for debugging
     */
    public static void closeOut(FileWriter writer, String message) {
        System.err.println(message);
        try {
            writer.flush();
            writer.close();
        } catch (IOException exc) {
            System.err.println("Could not close file.");
        }
        System.exit(0);
    }
}