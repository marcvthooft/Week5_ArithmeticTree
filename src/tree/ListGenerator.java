package tree;

import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

/**
 * Deze klas heeft de verantwoording om uit een String een ArrayList te maken en
 * de lijst te controleren op fouten.
 * @author Marc
 */
public class ListGenerator {
    ArrayList<String> list;
    StyledDocument doc;
    Style errorStyle;
    
    /**
     * Constructor van ListGenerator. De noodzakelijke variabelen om te communiceren met de
     * GUI worden hier gedefinieerd.
     * @param gui 
     */
    public ListGenerator(ArithmeticTreeGUI gui) {
        doc = gui.getDoc();
        errorStyle = gui.getErrorStyle();
    }
    
    /**
     * Een arrayList maken van alle tekens uit de
     * input String.
     * @param userInput
     * @return list
     */
    public ArrayList<String> makeList(String userInput) {
        list = new ArrayList<String>();
        int i;
        int numberIterator;		
        String listItem;
        //Itereer door de input String en zet tekens in lijst.
        for(i=0; i<userInput.length(); i++) {
            listItem = String.valueOf(userInput.charAt(i));
            //Ervoor zorgen dat meerdere integers naast elkaar één getal vormen.
            if (Character.isDigit(userInput.charAt(i))) {
                numberIterator = i + 1;
                while (numberIterator<userInput.length() && Character.isDigit(userInput.charAt(numberIterator))) {
                    listItem = listItem + String.valueOf(userInput.charAt(numberIterator));
                    numberIterator++;
                }
                list.add(listItem);
                i = numberIterator - 1;
            } 
            //Voorbeeld: 4(5+1) wordt 4*(5+1)
            else if (listItem.equals("(") && (i-1)>=0 && (Character.isDigit(userInput.charAt(i-1)) || userInput.charAt(i-1) == ')')) {
                list.add("*");
                list.add(listItem);
            } else if (listItem.equals(")") && (i+1)<userInput.length() && (Character.isDigit(userInput.charAt(i+1)) || userInput.charAt(i-1) == '(')) {
                list.add(listItem);
                list.add("*");
            } else {
                list.add(listItem);
            }
        }
        removeNeedlessParentheses();
        return list;
    }
    
    /**
     * Wanneer alleen nummer tussen haakjes staat, haal haakjes weg.
     */
    private ArrayList<String> removeNeedlessParentheses() {
        for (int i = 0; i<list.size(); i++) {
            try {
                Integer.parseInt(list.get(i));
                if (list.get(i-1).equals("(") && list.get(i+1).equals(")")) {
                    list.remove(i-1);   //verwijder linker haakje
                    list.remove(i);     //verwijder rechter haakje(nu 1 positie verschoven)
                    i--;                //huidige positie is één positie verschoven, schuif mee
                }
            } catch (NumberFormatException s) {} 
            catch (IndexOutOfBoundsException y) {}
        }
        return list;
    }

    /**
     * Controleert de lijst op fouten.
     * @return 
     */
    public int checkList() {
        int errors = 0;
        //Controleer op even gebruik van haakjes
        if (list.contains("(") == list.contains(")")) {
            //alles is goed
        } else {
            try { doc.insertString(doc.getLength(), "De expressie bevat een oneven aantal haakjes! \n", errorStyle); } 
            catch (BadLocationException e){}
            errors++;
        }

        //Controleer per listItem of deze juist is en juist wordt gebruikt.
        int i = 0;      //Zorgt ervoor dat de positie van listItem beschikbaar is.
        for (String listItem: list) {
            try {
                //Gaat naar catch wanneer listItem geen nummer is, de tekens controleren.
                Integer.parseInt(listItem);
            } catch (NumberFormatException s) {
                //Kijken of juiste tekens naast operators staan
                if (listItem.equals("*") || listItem.equals("/") || listItem.equals("+") || listItem.equals("-")) {
                    try {
                        String leftItem = list.get(i-1);
                        String rightItem = list.get(i+1);
                        if (leftItem.equals(")")==false) {
                            Integer.parseInt(leftItem);
                        } if (rightItem.equals("(")==false) {
                            Integer.parseInt(rightItem);
                        }
                    } catch (IndexOutOfBoundsException o) {
                        try { doc.insertString(doc.getLength(), "De " + listItem + " kan niet aan de buitenkant van een expressie staan! \n", errorStyle); } 
                        catch (BadLocationException e){}
                        errors++;
                    } catch (NumberFormatException n) {
                        try { doc.insertString(doc.getLength(), "Naast de " + listItem + " hoort een getal te staan! \n", errorStyle); } 
                        catch (BadLocationException e){}
                        errors++;
                    }
                } 
                //Kijken of de ) niet links aan de buitenkant staat
                else if (listItem.equals("(")) {
                    try {
                        list.get(i+1);
                    } catch (IndexOutOfBoundsException a) {
                        try { doc.insertString(doc.getLength(), "De " + listItem + " kan niet aan het einde van de expressie staan! \n", errorStyle); } 
                        catch (BadLocationException e){}
                        errors++;
                    }
                }
                //Kijken of de ( niet rechts aan de buitenkant staat
                else if (listItem.equals(")")) {
                    try {
                        list.get(i-1);
                    } catch (IndexOutOfBoundsException a) {
                        try { doc.insertString(doc.getLength(), "De " + listItem + " kan niet aan het begin van de expressie staan! \n", errorStyle); } 
                        catch (BadLocationException e){}
                        errors++;
                    }
                } 
                //Wanneer niks heeft gematched is het teken ongeldig
                else {
                    try {doc.insertString(doc.getLength(), listItem + " is ongeldige karakter! \n", errorStyle); } 
                    catch (BadLocationException e){}
                    errors++;
                }
            }           
            i++;
        }
        return errors;
    }
}
