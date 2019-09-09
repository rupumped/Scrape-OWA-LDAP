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
    
    private static final Pattern isrPattern = Pattern.compile("isr=\"(\\d+)");
    private static final Pattern ierPattern = Pattern.compile("ier=\"(\\d+)");
    
    public static void main(String[] args) throws AWTException, UnsupportedFlavorException, IOException {
        RobotPlus r = new RobotPlus();
        r.mouseMove(500,300);
        System.exit(0);
        
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String html = (String) clipboard.getData(DataFlavor.stringFlavor);
        String[] vlvChnk = html.split("<div[^>]+class=\"vlvChnk\"");
        for (String chnk : Arrays.copyOfRange(vlvChnk,1,vlvChnk.length)) {
            Matcher m = isrPattern.matcher(chnk);
            m.find();
            int isr = Integer.parseInt(m.group(1));
            m = ierPattern.matcher(chnk);
            m.find();
            int ier = Integer.parseInt(m.group(1));
            
            String[] userChnk = chnk.split("<b>");
            for (String user : Arrays.copyOfRange(userChnk,1,userChnk.length)) {
                String name = user.substring(0,user.indexOf("&nbsp;</b>"));
                String email = user.substring(user.indexOf("em=\"")+4);
                email = email.substring(0,email.indexOf("\""));
                System.out.println(name + ',' + email);
            }
        }
        //System.out.println(vlvChnk[1]);
    }
}