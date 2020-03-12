package src.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Main {

    private static HashMap destMap, jumpMap, compMap;
    private static final String[][] jumpMapValues = {{null, "000"}, {"JGT", "001"}, {"JEQ", "010"}, {"JGE", "011"}, {"JLT", "100"}, {"JNE", "101"}, {"JLE", "110"}, {"JMP", "111"}};
    private static final String[][] destMapValues = {{null, "000"},{"M", "001"}, {"D", "010"}, {"MD", "011"}, {"A", "100"}, {"AM", "101"}, {"AD", "110"}, {"AMD", "111"}};
    private static final String[][] compMapValues = {{"0", "0101010"},{"1", "0111111"}, {"-1", "0111010"}, {"D", "0001100"}, {"A", "0110000"}, {"!D", "0001101"}, {"!A", "0110001"}, {"-D", "0001111"}, {"-A", "0110011"},
            {"D+1", "0011111"}, {"A+1", "0110111"}, {"D-1", "0001110"}, {"A-1", "0110010"}, {"D+A", "0000010"}, {"D-A", "0010011"}, {"A-D", "0000111"}, {"D&A", "0000000"}, {"D|A", "0010101"}, {"M", "1110000"},
            {"!M", "1110001"}, {"-M", "1110011"}, {"M+1", "1110111"}, {"M-1", "1110010"}, {"M-1", "1110010"}, {"D+M", "1000010"}, {"D-M", "1010011"}, {"M-D", "1000111"}, {"D&M", "1000000"}, {"D|M", "1010101"}};

    public static void main(String[] args) {
        buildHashMaps();
        try {
            readFile("program.txt");
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    private static void readFile(String fileName) throws IOException {
        try (PrintWriter wr = new PrintWriter("Binary.txt", "UTF-8"); BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            System.out.println("i got here!");
            String line;
            while((line = br.readLine()) != null) {
                writeInstruction(parseInstruction(line), wr);
            }
        }
    }

    private static void writeInstruction(String binaryInstruction, PrintWriter wr) throws IOException {
        wr.println(binaryInstruction);
    }

    private static String parseInstruction(String assemblyInstruction) {
        //remove whitespace
        System.out.println(assemblyInstruction);
        assemblyInstruction = assemblyInstruction.replaceAll("\\s+", "");
        //Standardize Charachters
        assemblyInstruction = assemblyInstruction.toUpperCase();
        //Separate A type from C type
        if(assemblyInstruction.charAt(0) == '@') {
            return parseAInstruction(assemblyInstruction);
        }
        else {
            return parseCInstruction(assemblyInstruction);
        }
    }

    private static String parseAInstruction(String assemblyInstruction){
        int addressNumber;
        try {
            addressNumber = Integer.parseInt(assemblyInstruction.replaceAll("[^0-9]", ""));
        }
        catch(NumberFormatException e) {
            addressNumber = 0;
        }
        String output = "0000000000000000";
        String bit = Integer.toBinaryString(addressNumber);
        output = (output.substring(0, ((output.length()) - bit.length())) + bit);
        return output;
    }

    private static String parseCInstruction(String assemblyInstruction){
        //String has 3 values, 0 is destination, 1 is computation, 2 is jump.
        String[] instructions = splitStrings(assemblyInstruction);
        String destination = destMap.get(instructions[0]).toString();
        String computation = compMap.get(instructions[1]).toString();
        String jump = jumpMap.get(instructions[2]).toString();

        return("111" + computation + destination + jump);
    }

    private static String[] splitStrings(String instruction){
        String[] output = new String[]{null, null, null};
        String split1[] = instruction.split("=");
        String split2[] = split1[1].split(";");
        try {
            output[0] = split1[0];
            output[1] = split2[0];
            output[2] = split2[1];
        }
        catch(ArrayIndexOutOfBoundsException e){

        }
        return output;
    }

    private static void buildHashMaps(){
        jumpMap = new HashMap<String, String>();
        destMap = new HashMap<String, String>();
        compMap = new HashMap<String, String>();
        for(String[] hash : jumpMapValues) {
            jumpMap.put(hash[0], hash[1]);
        }
        for(String[] hash : destMapValues){
            destMap.put(hash[0], hash[1]);
        }
        for(String[] hash : compMapValues) {
            compMap.put(hash[0], hash[1]);
        }
    }
}

