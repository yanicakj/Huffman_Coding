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
import java.util.*;

// node for minheap
class HuffmanNode_7528 implements java.io.Serializable {

    int freq;
    byte b;
    String stringRep;

    HuffmanNode_7528 left;
    HuffmanNode_7528 right;

    public HuffmanNode_7528() {
        freq = 0;
        b = (byte) 0;
        left = null;
        right = null;
        stringRep = "";
    }

    public HuffmanNode_7528(int freq, byte b, HuffmanNode_7528 left, HuffmanNode_7528 right) {
        this.freq = freq;
        this.b = b;
        this.left = left;
        this.right = right;
        stringRep = "";
    }

}

// class to serialize object to file
class Compressor_7528 implements java.io.Serializable {

    public HuffmanNode_7528 root;
    public BitSet bits;

    public Compressor_7528() {
        this.root = null;
        this.bits = null;
    }

    public Compressor_7528(HuffmanNode_7528 root, BitSet bits) {
        this.root = root;
        this.bits = bits;
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

    private static String[] lookupArr = new String[256*2];
    private static String[] lookupArr2 = new String[256*2];
    static long compSize = 0;
    static long compSize2 = 0;

    public static void main(String[] args) throws IOException {

        File file = null; // input file
        File cmpfile = new File("compressed.huf"); // output file
        long num = 0;
        HuffmanNode_7528[] existingArr = new HuffmanNode_7528[0];
        BitSet myBitSet = null;

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

        byte[] fileContent = Files.readAllBytes(file.toPath());

        for (byte indByte : fileContent) {
            if (incrementByteArr(existingArr, indByte) == false) {
                existingArr = appendNode(existingArr, indByte);
            }
        }

        // for (int i = 0; i < existingArr.length; i++) {
        //     String s = String.format("%8s", Integer.toBinaryString(existingArr[i].b & 0xFF)).replace(' ', '0');
        //     System.out.println(s + " : " + existingArr[i].freq);
        // }

        long temptot = 0;
        for (int i = 0; i < existingArr.length; i++) {
            temptot += existingArr[i].freq;
        }
        //System.out.println("Total bytes : " + (temptot));

        // IMPLEMENT HEAP ARRAY WITH MIN HEAP PROPERTY
        MinHeap_7528 minHeap = new MinHeap_7528(existingArr.length);
        for (int i = 0; i < existingArr.length; i++) {
            minHeap.insert(existingArr[i]);
        }

        // FOR TESTING
        PriorityQueue<HuffmanNode_7528> q =
            new PriorityQueue<HuffmanNode_7528>(existingArr.length, new MyComparator_7528());
        for (int i = 0; i < existingArr.length; i++) {
            q.add(existingArr[i]);
        }

        // array with min heap property
        HuffmanNode_7528 root2 = null;
        // consolidate node until only 1 remains
        while (minHeap.size > 1) {
            // extract min 2 objects
            HuffmanNode_7528 x = q.remove();
            HuffmanNode_7528 y = q.remove();
            // new node f to hold combined freq
            HuffmanNode_7528 f = new HuffmanNode_7528();
            // to the sum of the frequencies of the two nodes
            f.freq = x.freq + y.freq;
            f.b = (byte) 0;
            // set children for new node
            f.left = x;
            f.right = y;
            // make f the root node.

            //System.out.println("removed " + x.b + " & " + y.b + " made freq : " + f.freq);

            root2 = f;
            // add new node to the priority-queue.
            minHeap.insert(f);
        }

        // FOR TESTING
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
            f.b = (byte) 0;
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

        // generate codes for each byte
        printCode(root, "");

        int compBytes = (int) compSize / 8;

        // was here

        myBitSet = returnBitSet(fileContent, root, cmpfile);

        Compressor_7528 serializeThis = new Compressor_7528(root, myBitSet);

        String regularString = Base64.getEncoder().encodeToString(fileContent);
        //System.out.println("Regular String length : " + regularString.length());
        //System.out.println("Compressed String length : " + compressedString);

        try {
            FileOutputStream fileOut = new FileOutputStream("compressed.huf");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(serializeThis);
            out.close();
            fileOut.close();
            //System.out.printf("Compressed data is saved in compressed.huf\n");
        } catch (IOException i) {
            i.printStackTrace();
            System.exit(0);
        }

        System.out.println("compressed file size : " + cmpfile.length() + " bytes");
        System.out.println("compressed content size : " + compBytes + " bytes");

    }

    // recursively runs and prints huffman code
    public static void printCode(HuffmanNode_7528 node, String s) {
        // if the left and right are null = leaf node
        if (node.left == null && node.right == null) {
            String s2 = String.format("%8s", Integer.toBinaryString(node.b & 0xFF)).replace(' ', '0');
            //System.out.println(s2 + " : freq : " + node.freq + " : string rep : " + s);
            int tempInt = (int) node.b + 256;
            lookupArr[tempInt] = s;
            node.stringRep = s;
            compSize += s.length() * node.freq;
            return;
        }
        // recursive calls
        printCode(node.left, s + "0");
        printCode(node.right, s + "1");
    }

    // recursively runs and prints huffman code
    public static void printCode2(HuffmanNode_7528 node, String s) {
        // if the left and right are null = leaf node
        if (node.left == null && node.right == null) {
            String s2 = String.format("%8s", Integer.toBinaryString(node.b & 0xFF)).replace(' ', '0');
            //System.out.println(s2 + " : freq : " + node.freq + " : string rep : " + s);
            int tempInt = (int) node.b + 256;
            lookupArr2[tempInt] = s;
            node.stringRep = s;
            compSize2 += s.length() * node.freq;
            return;
        }
        // recursive calls
        printCode(node.left, s + "0");
        printCode(node.right, s + "1");
    }

    // func to append byte to byte array
    public static HuffmanNode_7528[] appendNode(HuffmanNode_7528[] a, byte b) {
        HuffmanNode_7528 hn = new HuffmanNode_7528(0, b, null, null);
        HuffmanNode_7528[] c = new HuffmanNode_7528[a.length + 1];
        for(int i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        c[a.length]=hn;
        return c;
    }

    // increments byte freq value for arg byte
    public static Boolean incrementByteArr(HuffmanNode_7528[] a, byte b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].b == b) {
                a[i].freq += 1;
                return true;
            }
        }
        return false;
    }

    // create empty compression file
    public static Boolean setupFile(File cmpfile) throws IOException {
        if (cmpfile.createNewFile()) {
            //System.out.println("File is created!");
        } else {
            //System.out.println("File already exists.");
            if(cmpfile.delete()) {
                //System.out.println("File deleted successfully");
                if (cmpfile.createNewFile()) {
                    //System.out.println("File is created!");
                } else {
                    //System.out.println("Unable to delete file!");
                    return false;
                }
            }
            else {
                //System.out.println("Failed to delete the file");
                return false;
            }
        }
        return true;
    }

    private static BitSet fromString(final String s) {
        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
    }; // semi-colon for syntax highlighting

    private static String toString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }

    public static BitSet returnBitSet(byte[] fileContent, HuffmanNode_7528 root, File cmpfile) {

        String s = "";
        int iter = 1;
        int spot = 0;
        BitSet newBitSet = new BitSet();
        // traverse file --> append to lookupArrp val of each
        for (byte indByte : fileContent) {
            if (iter % 10 == 0) {
                //System.out.println("iter : " + iter);
            }
            int tempInt = (int) indByte;
            s = lookupArr[tempInt + 256];

            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '1') {
                    newBitSet.set(spot);
                }
                spot++;
            }
            iter++;
        }
        return newBitSet;
    }


}

// min heap -- priority queue -- based off implemenation on GeeksForGeeks.com
class MinHeap_7528 {
    private HuffmanNode_7528[] Heap;
    public int size;
    private int maxsize;

    private static final int FRONT = 1;

    public MinHeap_7528(int maxsize) {
        this.maxsize = maxsize;
        this.size = 0;
        //System.out.println("maxsize : " + maxsize);
        Heap = new HuffmanNode_7528[this.maxsize];
        HuffmanNode_7528 nod = new HuffmanNode_7528(0, (byte)0, null, null);
        Heap[0] = nod;
    }

    private int parent(int pos) {
        return pos / 2;
    }
    private int leftChild(int pos) {
        return (2 * pos);
    }
    private int rightChild(int pos) {
        return (2 * pos) + 1;
    }

    private boolean isLeaf(int pos) {
        if (pos >= (size / 2) && pos <= size) {
            return true;
        }
        return false;
    }

    private void swap(int fpos, int spos) {
        HuffmanNode_7528 tmp = null;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    private void minHeapify(int pos) {

        if (!isLeaf(pos)) {
            if (Heap[pos].freq > Heap[leftChild(pos)].freq || Heap[pos].freq > Heap[rightChild(pos)].freq) {

                if (Heap[leftChild(pos)].freq < Heap[rightChild(pos)].freq) {
                    swap(pos, leftChild(pos));
                    minHeapify(leftChild(pos));
                }

                else {
                    swap(pos, rightChild(pos));
                    minHeapify(rightChild(pos));
                }
            }
        }
    }

    public void insert(HuffmanNode_7528 element) {

        if (size == 0) {
            Heap[0] = element;
        } else if (size == 1) {
            Heap[1] = element;
            if (Heap[0].freq < Heap[1].freq) {
                swap(0, 1);
            }
            size++;
        } else {

            Heap[++size] = element;
            int current = size;

            while (Heap[current].freq < Heap[parent(current)].freq) {
                swap(current, parent(current));
                current = parent(current);
            }
        }
    }

    public void insert2(HuffmanNode_7528 element)
    {
        Heap[++size] = element;
        int current = size;

        while (Heap[current].freq < Heap[parent(current)].freq) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    public void minHeap() {
        for (int pos = (size / 2); pos >= 1; pos--) {
            minHeapify(pos);
        }
    }

    public HuffmanNode_7528 remove() {
        HuffmanNode_7528 popped = Heap[FRONT];
        Heap[FRONT] = Heap[size--];
        minHeapify(FRONT);
        return popped;
    }
}
//// write content to file by byte array
// try (FileOutputStream stream = new FileOutputStream("compressed.pdf")) {
//     stream.write(fileContent);
// }
