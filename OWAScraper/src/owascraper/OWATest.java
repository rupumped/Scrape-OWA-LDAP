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
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import nicksapps.RobotPlus;

public class OWATest {
    static final Point CONTACT_TOP_LEFT = new Point(20,70);
    static final Point CONTACT_BOTTOM_RIGHT = new Point(450,610);
    static final Point CLOSE = new Point(450,5);
    static final String DIR_FN = "dir.csv";
    static final Point PASSWORD = new Point(875,400);
    
    private static final Pattern userPattern = Pattern.compile("email=(.+?)%40");

    
    public static void main(String[] args) throws AWTException, UnsupportedFlavorException, IOException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String html = (String) clipboard.getData(DataFlavor.stringFlavor);
        Matcher m = userPattern.matcher(html);
        if (m.find()) {
            System.out.println(m.group(1));
        }
        
    }
}