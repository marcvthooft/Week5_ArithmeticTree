package tree;

import java.util.ArrayList;

/**
 * Deze klas heeft de verantwoording voor het genereren van de
 * rekenkundige binaire boom.
 * @author Marc
 */
public class TreeGenerator {
    ArrayList<String> list;
    ArrayList<Node> nodeTree;
    Node leftChild, rightChild, nextNode;
    int position;
    private Integer depth;
    String prevSpace = "";

    /**
     * Vanuit deze methode wordt de nodeTree gecreëerd. De enige methode die uit deze klasse
     * kan worden aangeroepen.
     * @param list
     * @return nodeTree
     */
    public ArrayList<Node> createNodeTree(ArrayList<String> list) {
        nodeTree = new ArrayList<Node>();
        this.list = list;
        Node node = setRoot();
        setChildren(node);

        while (nextNode!=null) {
            node = nextNode;
            nextNode = null;
            switch (node.getDepth()) {
            case 1: depth1(node);
                    break;
            case 2: depth2(node);
                    break;
            case 3: depth3(node);
                    break;
            }
        }
        return nodeTree;
    }
    
    /**
     * De root bepalen/opsporen.
     * @return root
     */
    private Node setRoot() {
        Node root;
        Boolean finished = false;
        String listItem;
        int rootPos = 0;
        String[] operators;
        String[] opPrecedences = {"precedence1", "precedence2"};
        String[] parentheses = {"(", ")"};
        int operatorI = 0;
        Boolean haakjeMag = false;

        /*
         * Operators met de hoogste voorrang horen als laagste in de boom,
         * want wat het laagst staat wordt als eerste berekend. De root staat aan
         * de top(en heeft de laagste voorrang in de expressie). 
         * (), voorrang 1 
         * / *, voorrang 2
         * + -, voorrang 3
         * 
         * While, for en while in elkaar:
         * Eerst while, voorang 1 regelen.
         * For, zet operators eerst op voorrang 3, daarna op voorrang 2.
         * Tweede while, operator controle.
         */
        while (finished==false && operatorI<2) {
            for (String opPrecedence : opPrecedences) {
                int pos = 0;
                if (opPrecedence.equals("precedence1")) {
                    operators = new String[]{"+","-"};
                } else {
                    operators = new String[]{"*","/"};
                }
                while (pos > -1 && pos < list.size() && finished == false) {
                    listItem = list.get(pos);
                    if (haakjeMag == false) {
                        //Ervoor zorgen dat het listItem niet tussen haakjes staat
                            if (parentheses[0].equals(listItem)) {
                                int haakjesLoop = 1;
                                pos++;
                                while (haakjesLoop!=0) {
                                    while (parentheses[1].equals(list.get(pos))==false) {
                                        if (parentheses[0].equals(list.get(pos))) {
                                            haakjesLoop++;
                                        }
                                        pos++;
                                    }
                                    pos++;
                                    haakjesLoop--;  
                                }
                                pos--;
                                listItem = list.get(pos);
                            }
                    } else if (parentheses[0].equals(listItem)) {
                        haakjeMag = false;
                    }
                    //Wanneer er een match is, is de root gevonden
                    if (operators[0].equals(listItem) || operators[1].equals(listItem)) {
                        rootPos = pos;
                        finished = true;
                    }
                    pos++;
                }
            }
            haakjeMag = true;
            operatorI++;
        } 
        //Wanneer er één getal staat, is dit de root.
        if (finished == false) {
            rootPos = 0;
        }

        root = new Node(list.get(rootPos), rootPos, 0);
        nodeTree.add(root);
        return root;
    }

    /**
     * Op de eerste diepte staan 2 nodes die behandeld moeten worden.
     * @param node 
     */
    private void depth1(Node node) {
        setChildren(node);
        setChildren(node.getSibling());
    }

    /**
     * Op de tweede diepte kunnen 4 nodes staan die behandeld moeten worden.
     * @param node 
     */
    private void depth2(Node node) {
        depth1(node);
        if (node.getParent().getSibling().getLeftChild()!=null) {
            node = node.getParent().getSibling().getLeftChild();
            depth1(node);
        }		
    }

    /**
     * Op de derde diepte kunnen 8 nodes staan die behandeld moeten worden.
     * @param node 
     */
    private void depth3(Node node) {
        depth2(node);
        try {
            if (node.getParent().getParent().getSibling().getLeftChild().getLeftChild()!=null) {
                node = node.getParent().getParent().getSibling().getLeftChild().getLeftChild();
                depth2(node);
            } else {
                throw new NullPointerException();
            }            
        } catch (NullPointerException e) {
            try {
                node = node.getParent().getParent().getSibling().getRightChild().getLeftChild();
                depth2(node);
            } catch (NullPointerException f) {}
        }	
    }

    /**
     * Kinderen van node toevoegen, behalve wanneer de node
     * een blad(leaf) is.
     * @param node 
     */
    private void setChildren(Node node) {
        if (node.isLeaf()) {
            return;
        }

        depth = node.getDepth() + 1;
        int[] pos = getChildPos(node);
        leftChild = new Node(node, list.get(pos[0]), pos[0], depth);
        rightChild = new Node(node, list.get(pos[1]), pos[1], depth);
        leftChild.setSibling(rightChild);
        rightChild.setSibling(leftChild);
        node.setChildren(leftChild, rightChild);
        nodeTree.add(leftChild);
        nodeTree.add(rightChild);
        setNextNode(leftChild);
    }
    
    /**
     * Posities van kinderen opsporen.
     * @param node
     * @return childPositions
     */
    private int[] getChildPos(Node node) {
        int pos = node.getPosition();
        int childPos = 0;
        int posLeftChild = 0;
        int posRightChild = 0;
        int leftWall;
        int rightWall;
        int[] walls;
        String listItem;
        Boolean finished;
        String[] operators;
        String[] parentheses;
        String[] opPrecedences = {"precedence1", "precedence2"};
        String[] children = {"leftChild", "rightChild"};
        
        walls = setWalls(node);
        leftWall = walls[0];
        rightWall = walls[1];

        /*
         * For-, while-, for- en whileloop in elkaar.
         * Deze for loop wordt twee keer uitgevoerd,
         * 1x voor het linkerkind, 1x voor het rechter.
         */
        for (String child : children) {
            finished = false;

            //set direction
            int direction;
            if (child.equals("leftChild")) {
                direction = -1;
                parentheses = new String[]{")", "("};
            } else {
                direction = 1;
                parentheses = new String[]{"(", ")"};
            }

            /*
             * Hoogste voorrang wordt als laatst behandeld:
             * ()   Voorrang 1 (zitten voorrang 2 en 3 weer in)
             * / *  Voorrang 2
             * + -  Voorrang 3
             * While, for en while loop in elkaar,
             * om ervoor te zorgen dat er eerst op voorrang 3 en 2 wordt
             * gecontroleerd zonder voorrang 1, daarna met voorrang 1.
             */
            Boolean haakjeMag;
            int whileI = 0;
            /*
             * Realiseerd voorrang 1.
             * Wordt 2x uitgevoerd. 
             */
            while (finished==false && whileI<2) {
                /*
                 * Wordt 2x uitgevoerd, eerste controle is
                 * op operators met laagste voorrang(+ en -), daarna
                 * met hoogste voorrang(* en /).
                 */
                for (String opPrecedence : opPrecedences) {
                    haakjeMag = whileI==1;
                    pos = node.getPosition() + direction;
                    if (opPrecedence.equals("precedence1")) {
                        operators = new String[]{"+","-"};
                    } else {
                        operators = new String[]{"*","/"};
                    }
                    //Doorlopen van de lijst in het aangegeven gebied(linker- en rechtermuur)
                    while (pos > leftWall && pos < rightWall && finished == false) {
                        listItem = list.get(pos);
                        if (haakjeMag==false) {
                            //Ervoor zorgen dat het listItem niet tussen haakjes staat
                            if (parentheses[0].equals(listItem)) {
                                int haakjesLoop = 1;
                                pos = pos + direction;
                                while (haakjesLoop!=0) {
                                    while (parentheses[1].equals(list.get(pos))==false) {
                                        if (parentheses[0].equals(list.get(pos))) {
                                            haakjesLoop++;
                                        }
                                        pos = pos + direction;
                                    }
                                    pos = pos + direction;
                                    haakjesLoop--;  
                                }
                                pos = pos - direction;
                                listItem = list.get(pos);
                            }
                        } 
                        //Er wordt toegestaan tussen 1 paar haakjes te kijken, eventuele
                        //haakjes binnen deze expressie worden niet toegestaan.
                        else if (parentheses[0].equals(listItem)) {
                            haakjeMag = false;
                        }
                        //Wanneer er een match is, is het kind gevonden.
                        if (operators[0].equals(listItem) || operators[1].equals(listItem)) {
                            childPos = pos;
                            finished = true;
                        }
                        pos = pos + direction;
                    }
                }
                whileI++;
            }

            //Wanneer er geen operator als kind is gevonden, is de operand het kind.
            if (finished == false) {
                childPos = node.getPosition() + direction;
            }

            //Zet de positie per kind.
            if (child.equals("leftChild")) {
                posLeftChild = childPos;
            } else {
                posRightChild = childPos;
            }				
        }

        //return posisities van kinderen.
        int[] childPositions = {posLeftChild, posRightChild};
        return childPositions;
    }
    
    /**
     * De muren geven het bereik in de expressie weer, waar naar kinderen mag worden gezocht.
     * Voorbeeld: 2+2, voor de kinderen van +, linkermuur is -1, rechtermuur is list.size().
     * @param node
     * @return 
     */
    private int[] setWalls(Node node) {
        Integer leftWall = 0;
        Integer rightWall = 0;
        
        Integer posNode = node.getPosition();
        Integer posP = null;                    //positie van de parent
        Integer posPp = null;                   //positie van de parent zijn parent
        Integer posPpp = null;                  //positie van de parent, parent zijn parent
        
        //set walls
        if (node.isRoot()) {
            leftWall = -1;
            rightWall = list.size();
        } else {
            try {
                posP = node.getParent().getPosition();
                posPp = node.getParent().getParent().getPosition();
                posPpp = node.getParent().getParent().getParent().getPosition();
            } catch (NullPointerException n) {}
            /*
             * posNode<posP geeft parent is rechts
             * posNode>posP geeft parent is links
             */        
            if (posNode>posP && (posPp==null || posNode>posPp) && (posPpp==null || posNode>posPpp)) {
                leftWall = posP;
                rightWall = list.size();
            } else if (posNode<posP && (posPp==null || posNode<posPp) && (posPpp==null || posNode<posPpp)) {
                leftWall = -1;
                rightWall = posP;
            } else if (posPp!=null && posNode<posP && posNode>posPp) {                            
                leftWall = posPp;
                rightWall = posP;
            } else if (posPp!=null && posNode>posP && posNode<posPp) {                     
                leftWall = node.getParent().getPosition();
                rightWall = posPp;
            } else if (posPpp!=null && posNode<posP && posNode>posPpp) {
                leftWall = posPpp;
                rightWall = posP;
            } else if (posPpp!=null && posNode>posP && posNode<posPpp) {
                leftWall = posP;
                rightWall = posPpp;
            } else {
                System.out.println("Er is een fout op getreden in klas TreeGenerator, methode setWalls(Node node)");
            }
        }
        
        int[] walls = {leftWall, rightWall};
        return walls;
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
