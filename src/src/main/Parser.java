package src.main;

import java.io.*;
import java.util.HashMap;

public class Parser {
    private File file;
    private HashMap destMap, jumpMap, compMap, labelMap;
    private final String[][] jumpMapValues = {{null, "000"}, {"JGT", "001"}, {"JEQ", "010"}, {"JGE", "011"}, {"JLT", "100"}, {"JNE", "101"}, {"JLE", "110"}, {"JMP", "111"}};
    private final String[][] destMapValues = {{null, "000"}, {"M", "001"}, {"D", "010"}, {"MD", "011"}, {"A", "100"}, {"AM", "101"}, {"AD", "110"}, {"AMD", "111"}};
    private final String[][] compMapValues = {{"0", "0101010"}, {"1", "0111111"}, {"-1", "0111010"}, {"D", "0001100"}, {"A", "0110000"}, {"!D", "0001101"}, {"!A", "0110001"}, {"-D", "0001111"}, {"-A", "0110011"},
            {"D+1", "0011111"}, {"A+1", "0110111"}, {"D-1", "0001110"}, {"A-1", "0110010"}, {"D+A", "0000010"}, {"D-A", "0010011"}, {"A-D", "0000111"}, {"D&A", "0000000"}, {"D|A", "0010101"}, {"M", "1110000"},
            {"!M", "1110001"}, {"-M", "1110011"}, {"M+1", "1110111"}, {"M-1", "1110010"}, {"M-1", "1110010"}, {"D+M", "1000010"}, {"D-M", "1010011"}, {"M-D", "1000111"}, {"D&M", "1000000"}, {"D|M", "1010101"}};


    public Parser(File file){
        this.file = file;
    }

    public void convertToBinary(){
        buildHashMaps();
        String hackName = file.getName().substring(0,file.getName().length()-4) + ".hack";
        try (PrintWriter wr = new PrintWriter(hackName, "UTF-8"); BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            //PASS 1: Gather Labels
            int i = 0;
            br.mark(1000000);
            while((line = br.readLine()) != null){
                line = cleanString(line);
                //skip whitespace
                if((line.length() == 0)){
                    continue;
                }
                if(line.charAt(0) == '('){
                    String label = line.replaceAll("[()]", "");
                    labelMap.put(label,i);
                }
                i++;
          }
            br.reset();
            while((line = br.readLine()) != null) {
                line = cleanString(line);
                if((line.length() == 0)|| (line.charAt(0) == '(')){
                    continue;
                }
                wr.println(parseInstruction(line));
            }
        }
        catch(IOException e){
            System.out.println(e + "Error: Could not find file");
        }
    }


    private String parseInstruction(String assemblyInstruction) {
        //remove whitespace
        //System.out.println(assemblyInstruction);
        assemblyInstruction = assemblyInstruction.trim();
        assemblyInstruction = assemblyInstruction.replaceAll("\"(?m)^[ \\t]*\\r?\\n\", \"\"\n", "");
        //Standardize Charachters
        assemblyInstruction = assemblyInstruction.toUpperCase();
        //If comment, return null
        if(assemblyInstruction.charAt(0) == '/'){
            return "";
        }
        //Separate A type from C type
        else if(assemblyInstruction.charAt(0) == '@') {
            return parseAInstruction(assemblyInstruction);
        }
        else {
            return parseCInstruction(assemblyInstruction);
        }
    }

    private String parseAInstruction(String assemblyInstruction){
        int addressNumber;
        String addressNumberString;
        if(assemblyInstruction.matches("[a-zA-Z]")){
            addressNumberString = labelMap.get(assemblyInstruction.substring(1)).toString();
        }
        else{
            addressNumberString = assemblyInstruction.replaceAll("[^0-9]", "");
        }
        try {
            addressNumber = Integer.parseInt(addressNumberString);
        }
        catch(NumberFormatException e) {
            addressNumber = 0;
        }
        String output = "0000000000000000";
        String bit = Integer.toBinaryString(addressNumber);
        output = (output.substring(0, ((output.length()) - bit.length())) + bit);
        return output;
    }

    private String parseCInstruction(String assemblyInstruction){
        //String has 3 values, 0 is destination, 1 is computation, 2 is jump.
        String[] instructions = splitStrings(assemblyInstruction);
        String destination = destMap.get(instructions[0]).toString();
        String computation = compMap.get(instructions[1]).toString();
        String jump = jumpMap.get(instructions[2]).toString();

        return("111" + computation + destination + jump);
    }
    private static String[] splitStrings(String instruction){
        String[] output = new String[]{null, null, null};
        String split[] = instruction.split("=|;");
        if(instruction.contains("=")){
            output[0] = split[0];
            output[1] = split[1];
            output[2] = null;
        }
        if(instruction.contains(";")){
            output[0] = null;
            output[1] = split[0];
            output[2] = split[1];
        }
        return output;
    }

    private void buildHashMaps(){
        jumpMap = new HashMap<String, String>();
        destMap = new HashMap<String, String>();
        compMap = new HashMap<String, String>();
        labelMap = new HashMap<String, String>();
        for(String[] hash : jumpMapValues) {
            jumpMap.put(hash[0], hash[1]);
        }
        for(String[] hash : destMapValues){
            destMap.put(hash[0], hash[1]);
        }
        for(String[] hash : compMapValues) {
            compMap.put(hash[0], hash[1]);
        }
        labelMap.put("SCREEN", "16384");
        labelMap.put("KBD", "24567");
        for(int i = 0; i < 16; i++){
            labelMap.put("R"+i,i);
        }
    }
    private String cleanString(String input){
        //go to uppercase
        input = input.toUpperCase();
        //remove whitespace
        input.replaceAll("\\s+]", "");
        //trunacate comments
        char[] charArray = input.toCharArray();
        StringBuilder output = new StringBuilder();
        for(char c : charArray){
            if(c == '/'){
                return output.toString();
            }
            output.append(c);
        }
        return output.toString();
    }

}
