import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

public class Main {

   public static void main(String[] args) {

      File in = new File("Abbie_s Family Tree GEDCOM.xml");
      File out = new File("sql.txt");

      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         xmlReader.setContentHandler(new GedHandler());
         xmlReader.parse(in.toURI().toURL().toString());
      }
      catch(Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
}