// Jacob Yanicak
// CS610
// PrP
// huffman encoding

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.*;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Comparator;
import java.nio.file.*;
import java.util.Arrays;

// node for minheap
class HuffmanNode_7528 {

    int freq;
    char c;

    HuffmanNode_7528 left;
    HuffmanNode_7528 right;

    public HuffmanNode_7528() {
        freq = 0;
        c = (char) "\0";
        left = null;
        right = null;
    }

    public HuffmanNode_7528(int freq, char c, HuffmanNode_7528 left, HuffmanNode_7528 right) {
        this.freq = freq;
        this.c = c;
        this.left = left;
        this.right = right;
    }

}

// class to serialize object to file
class Compressor_7528 implements java.io.Serializable {

    public HuffmanNode_7528 root;

    public Compressor_7528() {
            this.root = null;
    }

    public Compressor_7528(HuffmanNode_7528 root) {
        this.root = root;
    }

}

// class instructing program to compare on freq value
class MyComparator_7528 implements Comparator<HuffmanNode_7528> {
    public int compare(HuffmanNode_7528 x, HuffmanNode_7528 y)
    {
        return x.freq - y.freq;
    }
}

// class to test the implementation
class henc_7528 {

    public static void main(String[] args) throws IOException {

        File file = null; // input file
        File cmpfile = new File("compressed.huf"); // output file
        long num = 0;
        HuffmanNode_7528[] existingArr = new HuffmanNode_7528[0];

        // command line validation & setup input file
        if (args.length != 1) {
            System.out.println("Incorrect number of args used! Only 1 allowed!");
            System.exit(0);
        } else {
            try {
                file = new File(args[0]);
            } catch (Exception e) {
                System.out.println("Unable to load file, error : " + e);
                System.exit(0);
            }
        }

        // read in file content by byte
        InputStream in = new FileInputStream(args[0]);
        Reader r = new InputStreamReader(in, "US-ASCII");
        int intch;
        // read char by char
        while ((intch = r.read()) != -1) {
            char ch = (char) intch;
            if (incrementCharArr(existingArr, ch) == false) {
                existingArr = appendNode(existingArr, ch);
            }
        }

        for (int i = 0; i < existingArr.length; i++) {
            String s = String.format("%8s", Integer.toBinaryString(existingArr[i].b & 0xFF)).replace(' ', '0');
            //System.out.println(s + " : " + existingArr[i].freq);
        }

        long temptot = 0;
        for (int i = 0; i < existingArr.length; i++) {
            temptot += existingArr[i].freq;
        }
        System.out.println("Total bits : " + temptot);

        // heapify nodeArr using array with heap property here -- TBD


        // creating a min-priority queue(min-heap).
        PriorityQueue<HuffmanNode_7528> q =
            new PriorityQueue<HuffmanNode_7528>(existingArr.length, new MyComparator_7528());
        for (int i = 0; i < existingArr.length; i++) {
            q.add(existingArr[i]);
        }

        // creating root node
        HuffmanNode_7528 root = null;
        // consolidate node until only 1 remains
        while (q.size() > 1) {
            // extract min 2 objects
            HuffmanNode_7528 x = q.poll();
            HuffmanNode_7528 y = q.poll();
            // new node f to hold combined freq
            HuffmanNode_7528 f = new HuffmanNode_7528();
            // to the sum of the frequencies of the two nodes
            f.freq = x.freq + y.freq;
            f.b = 0;
            // set children for new node
            f.left = x;
            f.right = y;
            // make f the root node.
            root = f;
            // add new node to the priority-queue.
            q.add(f);
        }

        // create empty file to send compression to
        if (setupFile(cmpfile) == false) {
            System.out.println("Error setting up file!");
            System.exit(0);
        }

        // print the codes by traversing the tree
        System.out.println("Printing tree : ");
        printCode(root, "");

        Compressor_7528 serializeThis = new Compressor_7528();

        try {
            FileOutputStream fileOut = new FileOutputStream("compressed.huf");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(serializeThis);
            out.close();
            fileOut.close();
            System.out.printf("Compressed data is saved in compressed.huf\n");
        } catch (IOException i) {
            i.printStackTrace();
            System.exit(0);
        }



    }

    // recursively runs and prints huffman code
    public static void printCode(HuffmanNode_7528 node, String s) {
        // if the left and right are null = leaf node
        if (node.left == null && node.right == null) {
            String s2 = String.format("%8s", Integer.toBinaryString(node.b & 0xFF)).replace(' ', '0');
            System.out.println(s2 + " : " + s);
            return;
        }
        // recursive calls
        printCode(node.left, s + "0");
        printCode(node.right, s + "1");
    }

    // func to append byte to byte array
    public static HuffmanNode_7528[] appendNode(HuffmanNode_7528[] a, char car) {
        HuffmanNode_7528 hn = new HuffmanNode_7528(0, car, null, null);
        HuffmanNode_7528[] c = new HuffmanNode_7528[a.length + 1];
        for(int i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        c[a.length]=hn;
        return c;
    }

    // increments byte freq value for arg byte
    public static Boolean incrementCharArr(HuffmanNode_7528[] a, char c) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].c == c) {
                a[i].freq += 1;
                return true;
            }
        }
        return false;
    }

    // create empty compression file
    public static Boolean setupFile(File cmpfile) throws IOException {
        if (cmpfile.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
            if(cmpfile.delete()) {
                System.out.println("File deleted successfully");
                if (cmpfile.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("Unable to delete file!");
                    return false;
                }
            }
            else {
                System.out.println("Failed to delete the file");
                return false;
            }
        }
        return true;
    }

}

//// write content to file by byte array
// try (FileOutputStream stream = new FileOutputStream("compressed.pdf")) {
//     stream.write(fileContent);
// }

//// idk what this is
