package tree;

import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

/**
 * Deze klas heeft de verantwoording voor het printen van de gegenereerde boom.
 * @author Marc
 */
public class TreePrinter {
    JTextPane boomPane;
    StyledDocument doc;
    Style style;
    Style errorStyle;
    String prevSpace = "";
    Node nextNode;
    
    /**
     * Constructor van TreePrinter. De noodzakelijke variabelen om te communiceren met de
     * GUI worden hier gedefinieerd.
     * @param gui 
     */
    public TreePrinter(ArithmeticTreeGUI gui) {
        boomPane = gui.getBoomPane();
        doc = gui.getDoc();
        style = gui.getStyle();
        errorStyle = gui.getErrorStyle();
    }
    
    /**
     * Verzorgt het printen van de tree. De enige methode die uit deze klasse
     * kan worden aangeroepen.
     * @param nodeTree 
     */
    public void printAritmeticTree(ArrayList<Node> nodeTree) {
        Node node;
        nextNode = nodeTree.get(0);

        /*
         * Een switch in een while loop, om ervoor te zorgen dat
         * elke node geprint wordt, tot er geen volgende node meer is.
         */
        while (nextNode != null) {
            node = nextNode;
            nextNode = null;
            //switched op diepte van ouder
            switch (node.getDepth()) {
            case 0:
                try { 
                    doc.insertString(doc.getLength(), addSpace(30) + node.getNode() + "\n\n", style);
                    doc.insertString(doc.getLength(), addSpace(14), style);
                    printChildren(node);
                    removeSpace(31);
                    doc.insertString(doc.getLength(), "\n\n",style);
                } catch (BadLocationException e){}
                break;
            case 1:
                try { 
                    doc.insertString(doc.getLength(), addSpace(6), style);
                    printDepth1(node);
                    removeSpace(15);
                    doc.insertString(doc.getLength(), "\n\n", style);
                } catch (BadLocationException e){}
                break;
            case 2:
                try { 
                    doc.insertString(doc.getLength(), prevSpace + addSpace(2),style);
                    printDepth2(node);
                    removeSpace(7);
                    doc.insertString(doc.getLength(), "\n\n", style);
                } catch (BadLocationException e){}
                break;
            case 3:
                try { 
                    doc.insertString(doc.getLength(), prevSpace, style);
                    printDepth3(node);
                    removeSpace(3);
                    doc.insertString(doc.getLength(), "\n", style);
                } catch (BadLocationException e){}
                break;
            case 4:
                if (controlDepth4(nodeTree)==false) {
                    try { doc.insertString(doc.getLength(), "De rest van de expressie valt buiten dit prototype \n", errorStyle); }
                    catch (BadLocationException e){}
                }
                break;                
            }
        }
        try { doc.insertString(doc.getLength(), "\n", style); }
        catch (BadLocationException e){}
        prevSpace = "";
    }

    /**
     * Op de eerste diepte staan 2 nodes die behandeld moeten worden.
     * @param node 
     */
    private void printDepth1(Node node) {
        printChildren(node);
        printChildren(node.getSibling());
    }

    /**
     * Op de tweede diepte kunnen 4 nodes staan die behandeld moeten worden.
     * @param node 
     */
    private void printDepth2(Node node) {
        printDepth1(node);
        if (node.getParent().getSibling().getLeftChild()!=null) {
            node = node.getParent().getSibling().getLeftChild();
            printDepth1(node);
        } else {
            String space = getSpace(node.getDepth() + 1);
            try { doc.insertString(doc.getLength(), " " + space + " " + space + " " + space + " " + space, style); } 
            catch (BadLocationException e){}
        }
    }

    /**
     * Op de derde diepte kunnen 8 nodes staan die behandeld moeten worden.
     * @param node 
     */
    private void printDepth3(Node node) {
        printDepth2(node);
        try {
            if (node.getParent().getParent().getSibling().getLeftChild().getLeftChild()!=null) {
                node = node.getParent().getParent().getSibling().getLeftChild().getLeftChild();
                printDepth2(node);
            } else {
                String space = getSpace(4);
                try { doc.insertString(doc.getLength(), " " + space + " " + space + " " + space + " " + space, style); } 
                catch (BadLocationException e){}
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            try {
                node = node.getParent().getParent().getSibling().getRightChild().getLeftChild();
                printDepth2(node);
            } catch (NullPointerException f) {}
        }
    }
    
    /**
     * Depth 4 mag alleen leaves bevatten, controleer dit.
     * @param node 
     */
    private Boolean controlDepth4(ArrayList<Node> nodeTree) {
        for (Node node: nodeTree) {
            if (node.getDepth().equals(4) && node.isLeaf()==false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Kinderen van node printen.
     * @param node 
     */
    private void printChildren(Node node) {
        String space;
        space = getSpace(node.getDepth() + 1);
        if (node.isLeaf()==false) {
            try { doc.insertString(doc.getLength(), node.getLeftChild().getNode() + space + node.getRightChild().getNode() + space, style); } 
            catch (BadLocationException e){}
            setNextNode(node.getLeftChild());
        } else {
            try { doc.insertString(doc.getLength(), " " + space + " " + space, style); } 
            catch (BadLocationException e){}
        }
        if (nextNode == null) {
            prevSpace = prevSpace + " " + space + " " + space;
        }
    }

    /**
     * Verkrijg tussenruimte tussen nodes, bij ingeven van diepte.
     * @param childDepth
     * @return space
     */
    private String getSpace(int childDepth) {
        String space = "";
        switch (childDepth) {
        case 1: space = addSpace(31);
                break;
        case 2: space = addSpace(15);
                break;
        case 3: space = addSpace(7);
                break;
        case 4: space = addSpace(3);
                break;
        }
        return space;
    }
    
    /**
     * Ruimte tussen nodes, aangegeven in spaties.
     * Nodig voor de vormgeving van de boom.
     * @param amount
     * @return
     */
    private String addSpace(int amount) {
        String space = "";
        int i;
        for (i = 1; i<=amount; i++) {
            space = space + " ";
        }
        return space;
    }
    
    /**
     * Verwijderd de spaties aan het einde van
     * een depth.
     * @param space 
     */
    private void removeSpace(int space) {
        int currentPos = doc.getLength();
        int offSet = currentPos - space;
        try { doc.remove(offSet, space); }
        catch (BadLocationException e){} 
    }
    
    /**
     * De nextNode zal het eerste linker kind zijn.
     * @param node 
     */
    private void setNextNode(Node node) {
        if (nextNode == null) {
            nextNode = node;
        }
    }
}
