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
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        HashMap<String,BufferedImage> screenshot = MainPlus.loadImages("screenshots");
        
        // Setup directory file to write
        File writeFile = new File(DIR_FN);
        FileWriter writer = null;
        try {
            writer = new FileWriter(writeFile, true);
        } catch (IOException ex) {
            System.err.println("Could not open " + writeFile.getName() + ".\nIf the file is open, please close it.");
            System.exit(0);
        }
        
        // Navigate to OWA Client
        r.holdType(new int[] {KeyEvent.VK_ALT}, new int[] {KeyEvent.VK_TAB});
        r.delayUntilLoad(screenshot.get("owa"));
        if (!r.delayUntilLoad(screenshot.get("selected_html"), 1000)) {
            r.type(KeyEvent.VK_F12);
            r.delayUntilLoad(screenshot.get("selected_html"));
        }

        // Record position of HTML body tag
        Point selected_html = r.findInScreen(screenshot.get("selected_html"), "C");
        
//        int ii=0;
        while (true) {
            // Copy HTML
            r.mouseMove(selected_html);
            r.delay(100);
            r.click(RobotPlus.RIGHT);
            r.delay(100);
            for (int d=0;d<11;d++) {
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
            } catch (IllegalStateException exc) {
                r.delay(5000);
                try {
                    html = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (Exception exce) {
                    closeOut(writer, exce.toString());
                }
            }
            
            
            // Parse HTML
            Matcher m = userPattern.matcher(html);
            while (m.find()) {
                try {
                    writer.append(m.group(1) + ",");
                } catch (IOException e) {
                    closeOut(writer, "Ended on: " + m.group(1));
                }
                if (m.group(1).equals("zzpacker")) {
                    closeOut(writer, "Completed successfully.");
                }
            }
            try {
                writer.flush();
            } catch (IOException e) {
                closeOut(writer, "Could not flush data to file.");
            }
            
            // Scroll down to next set of contacts
            r.delayUntilLoad(screenshot.get("selected_content"), 1000);
            if (!r.screenContains(screenshot.get("selected_content"))) {
                r.mouseMove(0, 0);
                r.delay(1000);
                r.mouseMove(selected_html);
                r.delay(1000);
            }
            r.click(screenshot.get("selected_content"), "C");
            r.delay(500);
            for (int d=0; d<5; d++) {
                r.type(KeyEvent.VK_PAGE_DOWN);
                r.delay(700);
            }
            while (!r.screenContains(screenshot.get("half_selected_content"))) {
                r.type(KeyEvent.VK_UP);
                r.delay(700);
            }
//            ii++;
//            if (ii>3) {
//                closeOut(writer,"Done");
//            }
        }
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
    
    public static void playDing(RobotPlus r) {
//        r.click(new Point(365,750), 100);
//        r.click(new Point(680,670), 100);
    }
    
    public static void downContacts(RobotPlus r, int n) {
        for (int d=0;d<n;d++) {
            r.type(KeyEvent.VK_DOWN);
            r.delay(200);
        }
    }
}