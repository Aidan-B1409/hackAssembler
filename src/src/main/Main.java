package src.main;

import java.io.*;
import java.util.HashMap;

public class Main {
    private static File asmFile;
    private static Parser parser;
    private static float startTime, elapsedTime;

    public static void main(String[] args) {
        startTime = System.nanoTime();
        if(args.length > 0){
            String arg = args[0].trim();
            asmFile =  new FileChecker(arg).getFile();
            if(asmFile != null) {
                parser = new Parser(asmFile);
                parser.convertToBinary();
            }
        }
        else{
            System.out.println("No file was inputted");
            return;
        }
        elapsedTime = System.nanoTime() - startTime;
        System.out.println(elapsedTime/1000000000);
    }
}

