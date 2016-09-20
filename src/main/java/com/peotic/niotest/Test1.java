package com.peotic.niotest;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;

public class Test1 {
    
    static int s = 1;
    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test1() throws Exception {
        RandomAccessFile aFile = new RandomAccessFile("D:/bookmarks_15-5-5.html", "rw");
        FileChannel inChannel = aFile.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {

            System.out.println("Read " + bytesRead);
            buf.flip();

            while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }

            buf.clear();
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }

    
    public void test2() throws Exception {
        RandomAccessFile aFile = new RandomAccessFile("D:/bookmarks_15-5-5.html", "rw");
        FileChannel inChannel = aFile.getChannel();

        // create buffer with capacity of 48 bytes
        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = inChannel.read(buf); // read into buffer.
        while (bytesRead != -1) {

            buf.flip(); // make buffer ready for read

            while (buf.hasRemaining()) {
                System.out.print((char) buf.get()); // read 1 byte at a time
            }

            buf.clear(); // make buffer ready for writing
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }
    
    public void test3() throws Exception {
        
    }

}
