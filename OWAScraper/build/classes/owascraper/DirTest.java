package owascraper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import nicksapps.NumberList;

public class DirTest {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("dir.csv"));
        String line;
        int lastInd = -1;
        NumberList n = new NumberList();
        while ((line = br.readLine()) != null) {
            String[] thisLine = line.split(",");
            int thisInd = Integer.parseInt(thisLine[0]);
            if (lastInd+1 != thisInd) {
                n.add(thisInd);
                System.out.println(lastInd + " -> " + thisInd);
            }
            lastInd = thisInd;
        }
        br.close();
        System.out.println(n);
        System.out.println(n.deltaList());
    }
}