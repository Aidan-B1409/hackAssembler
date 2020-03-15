package src.main;

import java.io.File;

public class FileChecker {
    String fileName;
    public FileChecker(String fileName){
        this.fileName = fileName;
    }

    public File getFile(){
        if(isASM(fileName)){
            File output = new File(fileName);
            if(output.isFile()){
                return output;
            }
        }
        else {
            System.out.println("The specified file path was not a valid Hack ASM file. (Tip: Make sure the file path ends in .asm");
        }
        return null;
    }

    private boolean isASM(String fileName){
        String extension = fileName.substring((fileName.length()-4));
        return extension.equals(".asm");
    }
}
