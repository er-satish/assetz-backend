//package com.skumar.assetz.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//
//public class TestClass {
//    public static void main(String[] args) throws FileNotFoundException {
//              
//        int x = 0;
//        
////        for(int i=0;i<1000000;i++) {
////            try {
////                File initialFile = new File("./20200413.pdf");
////                InputStream targetStream = new FileInputStream(initialFile);
////                System.out.println("running time:"+i);
////                String pwd="123" + (x++);
////                System.out.println("pwd for try:"+pwd);
////                final PDDocument documentToBeParsed = PDDocument.load(targetStream,pwd);
////                final PDFTextStripper stripper = new PDFTextStripper();
////                final String pdfText = stripper.getText(documentToBeParsed);
////                
////                System.out.println("Parsed text size is " + pdfText.length() + " characters:");
////                System.out.println(pdfText);
////                System.out.print("password is:" + pwd);
////                break;
////            } catch (IOException ioEx) {
////                System.out.println("Got error:"+ioEx.getMessage());
////                //ioEx.printStackTrace();
////            }
////        }
//        
//    }
//}
