package tree;

/**
 * Deze klas representeerd een node met bijbehorende eigenschappen.
 * Elk teken uit de expressie is een node met eigenschappen.
 * @author Marc
 */
public final class Node {
    private String node;
    private Integer position;
    private Integer depth;
    private Node leftChild;
    private Node rightChild;
    private Node sibling;
    private Node parent;
    private Boolean isLeaf;
    private Boolean isRoot;
    private Boolean isLeft;

    /**
     * Creëer node.
     * @param parent
     * @param node
     * @param position
     * @param depth 
     */
    public Node(Node parent, String node, Integer position, Integer depth) {
        this.node = node;
        this.position = position;
        this.depth = depth;
        this.parent = parent;

        //set isleaf
        try {
            Integer.parseInt(node);
            isLeaf(true);
        } catch(NumberFormatException e) {
            isLeaf(false);
        }
        //set isleft
        if (parent.isRoot()) {
            if (position < parent.getPosition()) {
                isLeft(true);
            } else {
                isLeft (false);
            }
        } else {
            isLeft(parent.isLeft);
        }
        isRoot = false;
    }

    /**
     * Creëer root node.
     * @param node
     * @param position
     * @param depth 
     */
    public Node(String node, Integer position, Integer depth) {
        this.node = node;
        this.position = position;
        this.depth = depth;

        //set isleaf
        try {
            Integer.parseInt(node);
            isLeaf(true);
        } catch(NumberFormatException e) {
            isLeaf(false);
        }
        isRoot = true;
    }

    /**
     * De waarde van de node is de String.
     * @return node
     */
    public String getNode() {
        return node;
    }

    /**
     * De positie van de node is de positie in de input String.
     * @return position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * De boom bestaat uit lagen, de root staat op laag 0.
     * Voorbeeld: 1+1*3
     * depth 0:   +
     * depth 1: 1   *
     * depth 2:    1 3
     * @return depth
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Elke innerNode heeft twee kinderen.
     * @param leftChild
     * @param rightChild 
     */
    public void setChildren(Node leftChild, Node rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    /**
     * @return leftChild
     */
    public Node getLeftChild() {
        return leftChild;
    }

    /**
     * @return rightChild
     */
    public Node getRightChild() {
        return rightChild;
    }

    /**
     * @param sibling 
     */
    public void setSibling(Node sibling) {
        this.sibling = sibling;
    }

    /**
     * @return sibling
     */
    public Node getSibling() {
        return sibling;
    }

    /**
     * @param parent 
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * De root is de ouder van alle nodes(staat bovenaan de boom)
     * @param isRoot 
     */
    public void isRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    /**
     * De root is de ouder van alle nodes(staat bovenaan de boom)
     * @return isRoot
     */
    public Boolean isRoot() {
        return isRoot;
    }

    /**
     * Een leaf(blad) heeft geen kinderen, staat onderaan de boom.
     * Bij deze boom staat een leaf gelijk aan een getal.
     * @param isLeaf 
     */
    public void isLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * Een leaf(blad) heeft geen kinderen, staat onderaan de boom.
     * Bij deze boom staat een leaf gelijk aan een getal.
     * @return isLeaf
     */
    public Boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Geeft aan of de node links staat van de root.
     * Anders rechts.
     * @param isLeft 
     */
    public void isLeft(Boolean isLeft) {
        this.isLeft = isLeft;
    }

    /**
     * Geeft aan of de node links staat van de root.
     * Anders rechts.
     * @return 
     */
    public Boolean isLeft() {
        return isLeft;
    }
}
