import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class GedHandler extends DefaultHandler implements GedConstants {

   Stack<String> path;
   ArrayList<Individual> individuals;
   Individual current;
   ArrayList<Marriage> marriages;
   Name currentName;
   Occupation currentOccupation;
   Marriage currentMarriage;
   TreeSet<String> cities;
   TreeSet<String> states;
   TreeSet<String> countries;
   HashMap<String, String> stateMap;

   public GedHandler() {
      path = new Stack<>();
      individuals = new ArrayList<>();
      marriages = new ArrayList<>();
      current = null;
      currentName = null;
      currentOccupation = null;
      currentMarriage = null;
      cities = new TreeSet<>();
      states = new TreeSet<>();
      stateMap = new HashMap<>();
      countries = new TreeSet<>();
      for(String ci: CITIES) {
         cities.add(ci);
      }
      for(String st: STATES) {
         states.add(st);
      }
      for(String co: COUNTRIES) {
         countries.add(co);
      }
      for(int i = 0; i < STATE_COUNT; i++) {
         stateMap.put(STATES[2 * i], STATES[2 * i + 1]);
      }
   }

   /**
    * <p>Receive notification of the start of an element.</p>
    * <p>By default, do nothing. Application writers may override this method in a subclass to take
    *    specific actions at the start of each element (such as allocating a new tree node or
    *    writing output to a file).</p>
    * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if
    *        Namespace processing is not being performed
    * @param localNameThe local name (without prefix), or the empty string if Namespace processing
    *        is not being performed
    * @param qName The qualified name (with prefix), or the empty string if qualified names are not
    *        available
    * @param attributes The attributes attached to the element. If there are no attributes, it shall
    *        be an empty Attributes object
    */
   public void startElement(String uri, String localName, String qName, Attributes attributes)    {
      path.push(qName);
      switch(qName) {
         case INDIVIDUAL:
            current = new Individual(attributes.getValue("ID"));
            break;
         case BIRTH:
            break;
         case DEATH:
            break;
         case BURIAL:
            break;
         case MARRIED:
            break;
         case DATE:
            break;
         case PLACE:
            break;
         case SOURCE:
            break;
         case NOTE:
            break;
         case TEXT:
            break;
         case RESIDENCE:
            break;
         case SEX:
            break;
         case EDUCATION:
            break;
         case COUNTRY:
            break;
         case RELIGION:
            break;
         case DESCRIPTION:
            break;
         case UID:
            break;
         case APID:
            break;
         case FAMC:
            break;
         case FAMS:
            break;
         case DATA:
            break;
         case PAGE:
            break;
         case EVENT:
            break;
         case TYPE:
            break;
         case BAPM:
            break;
         case FAMILY:
            currentMarriage = new Marriage(attributes.getValue("ID"));
            break;
         case HUSBAND:
         case WIFE:
            if(currentMarriage.spouse_1_id == NULL_ID) {
               currentMarriage.spouse_1_id = Integer.parseInt(attributes.getValue("REF").substring(1));
            }
            else if(currentMarriage.spouse_2_id == NULL_ID) {
               currentMarriage.spouse_2_id = Integer.parseInt(attributes.getValue("REF").substring(1));
            }
            else {
               System.err.println("Too many spice in marriage " + currentMarriage.id + ".");
            }
            break;
         case CHILD:
               currentMarriage.kids.add(Integer.valueOf(attributes.getValue("REF").substring(1)));
            break;
         case MREL:
         case FREL:
            break;
         case DIV:
            break;
         case ANNULLMENT:
            assert currentMarriage != null;
            currentMarriage.reason_for_end = "annulment";
            break;
         case REPO:
            break;
         case ADDRESS:
            break;
         case AUTHOR:
            break;
         case PUBLICATION:
            break;
         case CONCLUSION:
            break;
         case TRLR:
            break;

         case NAME:
            if(peekback(1).equals(INDIVIDUAL)) {
               currentName = new Name(current.id);
            }
            break;
         case SURNAME:
            break;

         case OCCUPATION:
            if(peekback(1).equals(INDIVIDUAL)) {
               currentOccupation = new Occupation(current.id);
            }
            break;

         case OBJECT:
            break;
         case TITLE:
            break;
         case FILE:
            break;

         case GED:
            break;
         case HEAD:
            break;
         case CHARSET:
            break;
         case VERSION:
            break;
         case CORPORATION:
            break;
         case GEDC:
            break;
         case FORM:
            break;
         default:
            System.err.println("Unknown element <" + qName + ">.");
            System.exit(1);
      }
   }

   /**
    * <p>Receive notification of character data inside an element.</p>
    * <p>By default, do nothing. Application writers may override this method to take specific
    *    actions for each chunk of character data (such as adding the data to a node or buffer, or
    *    printing it to a file).</p>
    * @param chars The characters
    * @param start The start position in the character array
    * @param length The number of characters to use from the character array
    */
/*
   public String bio = null;
   public String economic_status = null;
   public String immigration_history = null;
   public String accomplishments = null;
   public ArrayList<String> notes new ArrayList<>();
*/

   public void characters(char[] chars, int start, int length) {
      String content = normalizeSpace(new String(chars, start, length).replaceAll("\\*amp;", "&").
            replaceAll("\\*lt;", "<").replaceAll("\\*gt;", ">"));
      // Some text should be ignored
      if(content.length() == 0 ||
            path.contains(HEAD) ||
            ancestor(MREL) || ancestor(FREL) ||
            peekback(0).equals(APID) ||
            peekback(0).equals(UID)
      ) {
         return;
      }
      else if(peekback(0).equals(DATE) && peekback(1).equals(BIRTH) && peekback(2).equals(INDIVIDUAL)) {
         current.date_of_birth = parseDate(content)[0];
      }
      else if(peekback(0).equals(DATE) && peekback(1).equals(DEATH) && peekback(2).equals(INDIVIDUAL)) {
         current.date_of_death = parseDate(content)[0];
      }
      else if(peekback(0).equals(PLACE) && peekback(1).equals(BIRTH) && peekback(2).equals(INDIVIDUAL)) {
         String[] place = parsePlace(content);
         current.country_of_birth = place[0];
         current.state_of_birth = place[1];
         current.municipality_of_birth = place[2];
      }
      else if(peekback(0).equals(PLACE) && peekback(1).equals(DEATH) && peekback(2).equals(INDIVIDUAL)) {
         String[] place = parsePlace(content);
         current.country_of_death = place[0];
         current.state_of_death = place[1];
         current.municipality_of_death = place[2];
      }
      else if(peekback(0).equals(SEX) && peekback(1).equals(INDIVIDUAL)) {
         if(content.equalsIgnoreCase("m") || content.equalsIgnoreCase("male")) {
            current.gender = "male";
         }
         else if(content.equalsIgnoreCase("f") || content.equalsIgnoreCase("female")) {
            current.gender = "female";
         }
      }
      else if(peekback(0).equals(DEATH)) {
         // Character data directly inside the DEAT tag; tag as note with DEATH prefix
         current.bio.add("Death: " + content);
      }
      // This clause only assigns first and middle names and suffixes. Surnames are handled under
      // the SURN tag
      else if(peekback(0).equals(NAME) && peekback(1).equals(INDIVIDUAL)) {
         assert currentName != null;
         String[] names = content.split("\\s");
         if(names.length > 0) {
            currentName.first = names[0];
            // Check for suffix
            if(names.length > 1 && (names[names.length - 1].matches("^[SsJj][Rr]\\.?$") ||
                  names[names.length - 1].matches("^[IiVvXx]+$"))) {
               currentName.suffix = names[names.length - 1];
               // All remaining names get concatenated into middle name
               if(names.length > 2) {
                  currentName.middle = names[1];
                  for(int i = 2; i < names.length - 1; i++) {
                     currentName.middle += " " + names[i];
                  }
               }
            }
            // All remaining names get concatenated into middle name
            if(names.length > 1) {
               currentName.middle = names[1];
               for(int i = 2; i < names.length; i++) {
                  currentName.middle += " " + names[i];
               }
            }
         }
      }
      else if(peekback(0).equals(SURNAME) && peekback(1).equals(NAME) && peekback(2).equals(INDIVIDUAL)) {
         assert currentName != null;
         currentName.last = content;
      }
      else if(peekback(0).equals(DATE) && peekback(1).equals(MARRIED) && ancestor(FAMILY)) {
         assert currentMarriage != null;
         String[] dates = parseDate(content);
         if(dates[0] == null) {
            currentMarriage.marriage_date = "0000-00-00";
         }
         else {
            currentMarriage.marriage_date = dates[0];
         }
         currentMarriage.marriage_end_date = dates[1];
      }
      else if(peekback(0).equals(OCCUPATION) && peekback(1).equals(INDIVIDUAL)) {
         currentOccupation.occupation = content;
      }
      else if(peekback(0).equals(TEXT) && ancestor(FAMILY)) {
         assert currentMarriage != null;
         // currentMarriage.notes.add(content);
      }
      else if(peekback(0).equals(TEXT) && ancestor(OCCUPATION)) {
         assert currentOccupation != null;
         assert currentOccupation.notes != null;
         assert content != null;
         // currentOccupation.notes.add(content);
      }
      else if(peekback(0).equals(TEXT) && ancestor(NAME)) {
         if(currentName != null) {
            // currentName.notes.add(content);
         }
      }
      else if(peekback(0).equals(TEXT)) {
         if(current != null) {
            current.bio.add(content);
         }
      }
      else {
         System.err.println("Unhandled character data in " + pathString() + ":");
         System.err.println(content + "\n");
         // System.exit(1);
      }
   }

   private String pathString() {
      Stack<String> back = new Stack<>();
      String result = "";
      while(!path.empty()) {
         back.push(path.pop());
      }
      while(!back.empty()) {
         String elem = back.pop();
         result += elem;
         path.push(elem);
         if(!back.empty()) {
            result += "/";
         }
      }
      return result;
   }

   private String peekback(int howFar) {
      Stack<String> back = new Stack<>();
      String result = null;
      try {
         for(int i = 0; i < howFar; i++) {
            back.push(path.pop());
         }
         result = path.peek();
      }
      catch(EmptyStackException ese) {
      }
      while(!back.empty()) {
         path.push(back.pop());
      }
      return result;
   }

   private String[] parsePlace(String place) {
      String[] places = place.split(", *");
      System.err.println("Places: " + java.util.Arrays.toString(places));
      String country = null, city = null, state = null;
      for(int i = 0; i < places.length; i++) {
         if(city == null && cities.contains(places[i].replaceAll(" *[Ww]ard *\\d+", ""))) {
            city = places[i];
            places[i] = null;
         }
         else if(state == null && states.contains(places[i])) {
            state = places[i];
            places[i] = null;
         }
         else if(country == null && countries.contains(places[i])) {
            country = places[i];
            places[i] = null;
         }
/*
         else if(city == null) {
            city = places[i];
            places[i] = null;
         }
*/
         else {
            System.err.println("Unhandled place name \"" + places[i] + "\" in " + pathString() + ":");
            System.err.println(place + "\n");
         }
      }
      String[] result = { country, state, city };
      for(int i = 0; i < places.length; i++) {
         if(places[i] != null) {
            System.err.println("Incompletely handled place name \"" + places[i] + "\" in " + pathString() + ":");
            System.err.println(place + "\n");
         }
      }
      return result;
   }

   /**
    * <p>Receive notification of the end of an element.</p>
    * <p>By default, do nothing. Application writers may override this method in a subclass to take
    *    specific actions at the end of each element (such as finalising a tree node or writing
    *    output to a file).</p>
    * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if
    *        Namespace processing is not being performed.
    * @param localName The local name (without prefix), or the empty string if Namespace processing
    *        is not being performed.
    * @param qName The qualified name (with prefix), or the empty string if qualified names are not
    *        available.
    * @param attributes The attributes attached to the element. If there are no attributes, it shall
    *        be an empty Attributes object.
    */
   public void endElement(String uri, String localName, String qName) {
      path.pop();
      switch(qName) {
         case INDIVIDUAL:
            if(current != null) {
               individuals.add(current);
               current = null;
            }
            break;
         case NAME:
            if(currentName != null) {
               current.names.add(currentName);
               currentName = null;
            }
            break;
         case OCCUPATION:
            if(currentOccupation != null) {
               current.occupations.add(currentOccupation);
               currentOccupation = null;
            }
            break;
         case FAMILY:
            if(currentMarriage != null) {
               marriages.add(currentMarriage);
               currentMarriage = null;
            }
            break;
      }
   }

   /**
    * <p>Receive notification of the end of the document.</p>
    * <p>By default, do nothing. Application writers may override this method in a subclass to take
    *    specific actions at the end of a document (such as finalising a tree or closing an output
    *    file).</p>
    */
   public void endDocument() {
      System.err.println(individuals.size());
      for(Individual ind: individuals) {
         String sig = "(id";
         String values = "(" + ind.id;
         if(ind.date_of_birth != null) {
            sig += ", date_of_birth";
            values += ", \"" + ind.date_of_birth + "\"";
         }
         if(ind.municipality_of_birth != null) {
            sig += ", municipality_of_birth";
            values += ", \"" + ind.municipality_of_birth + "\"";
         }
         if(ind.state_of_birth != null) {
            sig += ", state_of_birth";
            values += ", \"" + stateMap.getOrDefault(ind.state_of_birth, ind.state_of_birth) + "\"";
         }
         if(ind.country_of_birth != null) {
            sig += ", country_of_birth";
            values += ", \"" + ind.country_of_birth + "\"";
         }
         if(ind.date_of_death != null) {
            sig += ", date_of_death";
            values += ", \"" + ind.date_of_death + "\"";
         }
         if(ind.municipality_of_death != null) {
            sig += ", municipality_of_death";
            values += ", \"" + ind.municipality_of_death + "\"";
         }
         if(ind.state_of_death != null) {
            sig += ", state_of_death";
            values += ", \"" + stateMap.getOrDefault(ind.state_of_death, ind.state_of_death) + "\"";
         }
         if(ind.country_of_death != null) {
            sig += ", country_of_death";
            values += ", \"" + ind.country_of_death + "\"";
         }
         if(ind.gender != null) {
            sig += ", gender";
            values += ", \"" + ind.gender + "\"";
         }
         if(ind.economic_status != null) {
            sig += ", economic_status";
            values += ", \"" + ind.economic_status + "\"";
         }
         if(ind.immigration_history != null) {
            sig += ", immigration_history";
            values += ", \"" + ind.immigration_history + "\"";
         }
         if(ind.accomplishments != null) {
            sig += ", accomplishments";
            values += ", \"" + ind.accomplishments + "\"";
         }
         if(ind.bio.size() != 0) {
            String bio = "";
            for(int i = 0; i < ind.bio.size(); i++) {
               if(i != 0) {
                  bio += "\n";
               }
               bio += ind.bio.get(i);
            }
            if(bio.length() > 0) {
               sig += ", bio";
               values += ", \"" + bio + "\"";
            }
         }
         sig += ")";
         values += ");";
         System.out.println("INSERT INTO Individual " + sig + " VALUES " + values);
         for(Name name: ind.names) {
            sig = "(individual_id";
            values ="(" + name.individual_id;
            sig += ", first_name";
            if(name.first == null) {
                values += ", \"[unknown]\"";
            }
            else {
              values += ", \"" + name.first + "\"";
            }
            if(name.middle != null) {
               sig += ", middle_name";
               values += ", \"" + name.middle + "\"";
            }
            sig += ", last_name";
            if(name.last == null) {
               values += ", \"[unknown]\"";
            }
            else {
               values += ", \"" + name.last + "\"";
            }
            if(name.suffix != null) {
               sig += ", suffix";
               values += ", \"" + name.suffix + "\"";
            }
            sig += ")";
            values += ");";
            System.out.println("INSERT INTO Name " + sig + " VALUES " + values);
         }

         for(Occupation occupation: ind.occupations) {
            sig = "(individual_id, occupation";
            values ="(" + occupation.individual_id;
            if(occupation.occupation == null) {
               System.err.println("Unpopulated occupation for " + occupation.individual_id + ".");
            }
            else {
               values += ", \"" + occupation.occupation + "\"";
               if(occupation.occupation_start != null) {
                  sig += ", occupation_start";
                  values += ", \"" + occupation.occupation_start + "\"";
               }
               if(occupation.occupation_end != null) {
                  sig += ", occupation_end";
                  values += ", \"" + occupation.occupation_end + "\"";
               }
               if(occupation.employer != null) {
                  sig += ", employer";
                  values += ", \"" + occupation.employer + "\"";
               }
               if(occupation.country != null) {
                  sig += ", country";
                  values += ", \"" + occupation.country + "\"";
               }
               if(occupation.state != null) {
                  sig += ", state";
                  values += ", \"" + occupation.state + "\"";
               }
               if(occupation.municipality != null) {
                  sig += ", municipality";
                  values += ", \"" + occupation.municipality + "\"";
               }
               if(occupation.notes.size() != 0) {
                  String notes = "";
                  for(int i = 0; i < occupation.notes.size(); i++) {
                     if(i != 0) {
                        notes += "\n";
                     }
                     notes += occupation.notes.get(i);
                  }
                  if(notes.length() > 0) {
                     if(notes.length() > 256) {
                        notes = notes.substring(0, 256);
                     }
                     sig += ", notes";
                     values += ", \"" + notes + "\"";
                  }
               }
               sig += ")";
               values += ");";
               System.out.println("INSERT INTO Occupation " + sig + " VALUES " + values);
            }
         }
      }
      for(Marriage marriage: marriages) {
         if(marriage.spouse_1_id == NULL_ID) {
            System.err.println("Null spouse_1 for marriage " + marriage.id + ".");
         }
         else {
            String sig = "(id, spouse_1_id";
            String values ="(" + marriage.id + ", " + marriage.spouse_1_id;
            if(marriage.spouse_2_id != NULL_ID) {
               sig += ", spouse_2_id";
               values += ", " + marriage.spouse_2_id;
            }
            sig += ", marriage_date";
            if(marriage.marriage_date == null) {
               values += ", \"0000-00-00\"";
            }
            else {
               values += ", \"" + marriage.marriage_date + "\"";
            }
            if(marriage.marriage_end_date == null) {
               // Assume marriage end date is earler DoD of spice
               Individual s1 = null, s2 = null;
               for(Individual ind: individuals) {
                  if(ind.id == marriage.spouse_1_id) {
                     s1 = ind;
                  }
                  else if(ind.id == marriage.spouse_2_id) {
                     s2 = ind;
                  }
                  if(s1 != null && s2 != null) {
                     break;
                  }
               }
               String firstToDie = null;
               if(s1.date_of_death != null && s1.date_of_death != "0000-00-00") {
                  firstToDie = s1.date_of_death;
               }
               if(s2 != null && s2.date_of_death != null && s2.date_of_death != "0000-00-00") {
                  if(firstToDie == null) {
                     firstToDie = s2.date_of_death;
                  }
                  else {
                     String[] dod = { s1.date_of_death, s2.date_of_death };
                     Arrays.sort(dod);
                     firstToDie = dod[0];
                  }
                  marriage.marriage_end_date = firstToDie;
                  sig += ", reason_for_end";
                  values += ", \"death\"";
               }
            }
            if(marriage.marriage_end_date != null) {
               sig += ", marriage_end_date";
               values += ", \"" + marriage.marriage_end_date + "\"";
            }
            if(marriage.reason_for_end != null) {
               sig += ", reason_for_end";
               values += ", \"" + marriage.reason_for_end + "\"";
            }
            if(marriage.notes.size() != 0) {
               String notes = "";
               for(int i = 0; i < marriage.notes.size(); i++) {
                  if(i != 0) {
                     notes += "\n";
                  }
                  notes += marriage.notes.get(i);
               }
               if(notes.length() > 0) {
                  if(notes.length() > 256) {
                     notes = notes.substring(0, 256);
                  }
                  sig += ", notes";
                  values += ", \"" + notes + "\"";
               }
            }
            sig += ")";
            values += ");";
            System.out.println("INSERT INTO Married_to " + sig + " VALUES " + values);
            for(int kid: marriage.kids) {
               System.out.println("INSERT INTO Parent_of (parent_id, child_id) VALUES (" +
                     marriage.spouse_1_id + ", " + kid + ");");
               if(marriage.spouse_2_id != NULL_ID) {
                  System.out.println("INSERT INTO Parent_of (parent_id, child_id) VALUES (" +
                        marriage.spouse_2_id + ", " + kid + ");");
               }
            }
         }
      }
   }

   private boolean ancestor(String tag) {
      for(String element: path) {
         if(tag.equals(element)) {
            return true;
         }
      }
      return false;
   }

   private String[] parseDate(String date) {
      String[] result = { null, null };
      date = date.toLowerCase();
      String[] tokens = date.split("\\s+");
      for(String token: tokens) {
         if(token.matches("^\\d{4}\\-\\d{2}\\-\\d{2}$")) {
            if(result[0] == null) {
               result[0] = token;
            }
            else if(result[1] == null) {
               result[1] = token;
            }
         }
         else if(token.matches("^\\d{4}$")) {
            if(result[0] == null) {
               result[0] = token + "-00-00";
            }
            else if(result[1] == null) {
               result[1] = token + "-00-00";
            }
         }
      }
      return result;
   }

   private String normalizeSpace(String text) {
      return text.replaceAll("[ \\t]+", " ").trim();
   }
}

class Individual {

   /**
    * id INT NOT NULL AUTO_INCREMENT
    */
   public int id;

   /**
    * date_of_birth DATE
    */
   public String date_of_birth;

   /**
    * municipality_of_birth VARCHAR(64)
    */
   public String municipality_of_birth;

   /**
    * state_of_birth VARCHAR(64)
    */
   public String state_of_birth;

   /**
    * country_of_birth VARCHAR(64)
    */
   public String country_of_birth;

   /**
    * date_of_death DATE
    */
   public String date_of_death;

   /**
    * municipality_of_death VARCHAR(64)
    */
   public String municipality_of_death;

   /**
    * state_of_death VARCHAR(64)
    */
   public String state_of_death;

   /**
    * country_of_death VARCHAR(64)
    */
   public String country_of_death;

   /**
    * gender VARCHAR(64)
    */
   public String gender;

   /**
    * economic_status VARCHAR(64)
    */
   public String economic_status;

   /**
    * immigration_history VARCHAR(64)
    */
   public String immigration_history;

   /**
    * accomplishments VARCHAR(256)
    */
   public String accomplishments;

   /**
    * notes VARCHAR(256)
    */
   public ArrayList<String> bio;

   /**
    * Names
    */
   public ArrayList<Name> names;

   /**
    * Occupations
    */
   public ArrayList<Occupation> occupations;

   /**
    * Constructs an Individual.
    * @param id The Individual's ID
    */
   Individual(String id) {
      this.id = Integer.parseInt(id.substring(1));
      date_of_birth = null;
      municipality_of_birth = null;
      state_of_birth = null;
      country_of_birth = null;
      date_of_death = null;
      municipality_of_death = null;
      state_of_death = null;
      country_of_death = null;
      gender = null;
      economic_status = null;
      immigration_history = null;
      accomplishments = null;
      bio = new ArrayList<>();
      names = new ArrayList<>();
      occupations = new ArrayList<>();
   }
}

class Name {

   public int individual_id;
   public String first, last, middle, suffix;
   public ArrayList<String> notes;

   Name(int id) {
      individual_id = id;
      first = null;
      last = null;
      middle = null;
      suffix = null;
      notes = new ArrayList<>();
   }
}

class Occupation {
   public int individual_id;
   public String occupation;
   public String occupation_start;
   public String occupation_end;
   public String employer;
   public String country;
   public String state;
   public String municipality;
   public ArrayList<String> notes;

   Occupation(int id) {
      individual_id = id;
      occupation = null;
      occupation_start = null;
      occupation_end = null;
      employer = null;
      country = null;
      state = null;
      municipality = null;
      notes = new ArrayList<>();
   }
}

class Marriage implements GedConstants {

   public int id;
   public int spouse_1_id;
   public int spouse_2_id;
   public String marriage_date;
   public String marriage_end_date;
   public String reason_for_end;
   public ArrayList<String> notes;
   ArrayList<Integer> kids;

   Marriage(String id) {
      this.id = Integer.parseInt(id.substring(1));
      spouse_1_id = NULL_ID;
      spouse_2_id = NULL_ID;
      marriage_date = null;
      marriage_end_date = null;
      reason_for_end = null;
      notes = new ArrayList<>();
      kids = new ArrayList<>();
   }
}