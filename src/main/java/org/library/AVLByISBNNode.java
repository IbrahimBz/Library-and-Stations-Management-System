/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main4;

/**
 *
 * @author Munzer
 */
    import java.util.ArrayList;
import java.util.List;

class AVLByISBNNode {
    Book book;
    AVLByISBNNode left, right;
    int height;

    public AVLByISBNNode(Book book) {
        this.book = book;
        this.height = 1;
    }
}

 class AVLTree {
    private AVLByISBNNode root;

    private int height(AVLByISBNNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(AVLByISBNNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private AVLByISBNNode rightRotate(AVLByISBNNode y) {
        AVLByISBNNode x = y.left;
        AVLByISBNNode T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    private AVLByISBNNode leftRotate(AVLByISBNNode x) {
        AVLByISBNNode y = x.right;
        AVLByISBNNode T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    public void insert(Book book) {
        root = insertRec(root, book);
    }

    private AVLByISBNNode insertRec(AVLByISBNNode node, Book book) {
        if (node == null) return new AVLByISBNNode(book);

        if (book.isbn < node.book.isbn)
            node.left = insertRec(node.left, book);
        else if (book.isbn > node.book.isbn)
            node.right = insertRec(node.right, book);
        else
            return node; 

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && book.isbn < node.left.book.isbn)
            return rightRotate(node);

        if (balance < -1 && book.isbn > node.right.book.isbn)
            return leftRotate(node);

        if (balance > 1 && book.isbn > node.left.book.isbn) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && book.isbn < node.right.book.isbn) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public Book search(int isbn) {
        AVLByISBNNode res = searchRec(root, isbn);
        return (res != null) ? res.book : null;
    }

    private AVLByISBNNode searchRec(AVLByISBNNode root, int isbn) {
        if (root == null || root.book.isbn == isbn) return root;
        if (root.book.isbn > isbn) return searchRec(root.left, isbn);
        return searchRec(root.right, isbn);
    }

    public void delete(int isbn) {
        root = deleteRec(root, isbn);
    }

    private AVLByISBNNode deleteRec(AVLByISBNNode root, int isbn) {
        if (root == null) return root;

        if (isbn < root.book.isbn)
            root.left = deleteRec(root.left, isbn);
        else if (isbn > root.book.isbn)
            root.right = deleteRec(root.right, isbn);
        else {
            if ((root.left == null) || (root.right == null)) {
                AVLByISBNNode temp = (root.left != null) ? root.left : root.right;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                AVLByISBNNode temp = minValueNode(root.right);
                root.book = temp.book;
                root.right = deleteRec(root.right, temp.book.isbn);
            }
        }

        if (root == null) return root;

        root.height = Math.max(height(root.left), height(root.right)) + 1;
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private AVLByISBNNode minValueNode(AVLByISBNNode node) {
        AVLByISBNNode current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        inOrder(root, list);
        return list;
    }

    private void inOrder(AVLByISBNNode node, List<Book> list) {
        if (node != null) {
            inOrder(node.left, list);
            list.add(node.book);
            inOrder(node.right, list);
        }
    }
}

