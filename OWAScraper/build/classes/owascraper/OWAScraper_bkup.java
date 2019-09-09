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
import javax.imageio.ImageIO;
import nicksapps.RobotPlus;

public class OWAScraper_bkup {
    static final Point CONTACT_TOP_LEFT = new Point(20,70);
    static final Point CONTACT_BOTTOM_RIGHT = new Point(450,610);
    static final Point CLOSE = new Point(450,5);
    static final Point PASSWORD = new Point(875,400);
    static final String DIR_FN = "dir.csv";
    static final String LAST_CONTACT = "lastContact.png";
    static final String ADDRESS_BOOK = "addressBook.png";
    static final String ORGANIZATION = "organization.png";
    static final String BOTTOM_RIGHT = "bottom_right.png";
    static final String CLOSE_IM = "close.png";
    static final String ERROR = "error.png";
    static final String TIMEOUT = "timeout.png";
    static final String LOGIN = "login.png";
    static final String MAXIMIZE = "maximize.png";
    static final String LOGMEIN = "logmein.png";
    static final String SIGNIN = "signin.png";
    static final String INWINDOW = "inwindow.png";
    
    public static void main(String[] args) throws AWTException  {
        RobotPlus r = new RobotPlus();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        BufferedImage addressBook = null;
        BufferedImage lastContact = null;
        BufferedImage organization = null;
        BufferedImage bottomRight = null;
        BufferedImage close = null;
        BufferedImage error = null;
        BufferedImage timeout = null;
        BufferedImage login = null;
        BufferedImage maximize = null;
        BufferedImage logmein = null;
        BufferedImage signin = null;
        BufferedImage inwindow = null;
        try {
            addressBook = ImageIO.read(new File(ADDRESS_BOOK));
            lastContact = ImageIO.read(new File(LAST_CONTACT));
            organization = ImageIO.read(new File(ORGANIZATION));
            bottomRight = ImageIO.read(new File(BOTTOM_RIGHT));
            close = ImageIO.read(new File(CLOSE_IM));
            error = ImageIO.read(new File(ERROR));
            timeout = ImageIO.read(new File(TIMEOUT));
            login = ImageIO.read(new File(LOGIN));
            maximize = ImageIO.read(new File(MAXIMIZE));
            logmein = ImageIO.read(new File(LOGMEIN));
            signin = ImageIO.read(new File(SIGNIN));
            inwindow = ImageIO.read(new File(INWINDOW));
        } catch (IOException ex) {
            System.err.println("Could not open all image files.\n" + ex.toString());
            System.exit(0);
        }
        BufferedImage[] loadCases = {organization,error,timeout,login};
        File writeFile = new File(DIR_FN);
        boolean fileStarted = writeFile.exists();
        FileWriter writer = null;
        try {
            writer = new FileWriter(writeFile, true);
            if (!fileStarted)
                writer.write("Name,Alias,Job Title,Office,Department,Company,Manager,Assistant,Phone,E-mail,IM Address,Web Page,\n");
        
        } catch (IOException ex) {
            System.err.println("Could not open " + writeFile.getName() + ".\nIf the file is open, please close it.");
            try {
                writer.flush();
                writer.close();
            } catch (Exception exc) {}
            System.exit(0);
        }
        int count = 0;
        
        // Navigate to OWA Client
        r.holdType(new int[] {KeyEvent.VK_ALT}, new int[] {KeyEvent.VK_TAB});
        r.delayUntilLoad(addressBook);
        boolean finished = false;
        String lastEntry = null;
        while (!finished && count<2) {
            finished = r.screenContains(lastContact);
            // Open Contact
            r.type(KeyEvent.VK_ENTER);
            if (r.delayUntilLoad(organization, 5000)) {
                int loadCase = r.delayUntilLoadAny(loadCases);
                switch (loadCase) {
                    case 1:
                        // If the server won't let me access the contact, notify the user and exit if first contact.
                        System.out.println("Could not load at least one contact " +
                                ((lastEntry==null) ? "at beginning of program run" : "after " + lastEntry.substring(0,lastEntry.indexOf("\n"))) +
                                ". User must extract this contact manually.");
                        if (lastEntry == null) {
                            closeOut(writer, "");
                        }
                        r.mouseMove(1,1);
                        r.delay(1000);
                        goToNextContact(r, close, addressBook);
                        break;
                    case 2:
                        r.type(KeyEvent.VK_F5);
                        break;
                    case 3:
                        r.click(maximize, "C");
                        r.delay(1000);
                        r.click(PASSWORD,100);
                        r.delayUntilLoad(logmein);
                        r.click(logmein, "BC");
                        r.delay(100);
                        r.delayUntilLoad(signin);
                        r.click(signin, "C");
                        r.delay(100);
                        r.holdType(new int[] {KeyEvent.VK_ALT}, new int[] {KeyEvent.VK_F4});
                        r.delayUntilLoad(inwindow);
                        r.click(inwindow, "C");
                        r.delay(100);
                        break;
                    default:
                        lastEntry = copyContact(r, bottomRight, clipboard, writer, writeFile, lastEntry);
                        goToNextContact(r, close, addressBook);
                        break;
                }
            } else {
                lastEntry = copyContact(r, bottomRight, clipboard, writer, writeFile, lastEntry);
                goToNextContact(r, close, addressBook);
            }
//            count++;
        }
        
        // Save and close
        closeOut(writer, "Done.");
    }
    
    public static String copyContact(RobotPlus r, BufferedImage bottomRight, 
            Clipboard clipboard, FileWriter writer, File writeFile, 
            String lastEntry) {
        // Copy All Information to Clipboard
        Point contactBR = r.findInScreen(bottomRight);
        String thisEntry = null;
        do {
            r.mouseMove(CONTACT_TOP_LEFT);
            r.mousePress(r.LEFT);
            r.mouseMove(contactBR);
            r.mouseRelease(r.LEFT);
            r.delay(100);
            r.holdType(new int[] {KeyEvent.VK_CONTROL}, new int[] {KeyEvent.VK_C});
            r.delay(1000);
            try {
                thisEntry = (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                closeOut(writer, "Could not paste data from clipboard.");
            }
            thisEntry = thisEntry.replace(",", "");
        } while (thisEntry.equals(lastEntry));
        // Parse Info from User
        User user = null;
        try {
            user = User.parseUser(thisEntry);
        } catch (UnknownFieldException ex) {
            closeOut(writer, ex.toString());
        }
        try {
            writer.append(user.toCSV() + "\n");
            writer.flush();
        } catch (IOException ex) {
            System.err.println(ex.toString());
            System.exit(0);
        }
        lastEntry = thisEntry;
        return lastEntry;
    }
    
    public static void goToNextContact(RobotPlus r, BufferedImage close, BufferedImage addressBook) {
        r.click(close, "C");
        r.delay(100);
        r.delayUntilLoad(addressBook);
        r.type(KeyEvent.VK_DOWN);
        r.delay(100);
    }
    
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