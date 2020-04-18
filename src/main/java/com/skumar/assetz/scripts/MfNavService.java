package com.skumar.assetz.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class MfNavService {

    final static String url = "https://www.amfiindia.com/spages/NAVOpen.txt";
    final static String mountPoint = "/Users/skuma596/Desktop/mnt/";
    final static String datePattern = "d-MMM-uuuu";
    static int count=0;

public static void main(String[] args) throws IOException {
    System.out.println("Starting..");
    String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(datePattern));
    System.out.println(date);
    final Pattern regexPattern = Pattern.compile(date+"$");
    
    String fileName = mountPoint + "abc.txt";
    //InputStream in = new URL(url).openStream();
    //Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
    try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))){
        reader.lines().filter(line -> regexPattern.matcher(line).find()).forEach(c -> doX(c));
    }
    catch(Exception e){
        e.printStackTrace();
    }
    
  

    
}

private static void doX(String c) {

    String[] arr = c.split(";");
    if(arr.length==6) {
        System.out.println(arr[1]);
        System.out.println(arr[3]);
        System.out.println(arr[4]);
        System.out.println(arr[5]);
        count++;
    }
    System.out.println("total no. of records:" +count);
    
}

}
