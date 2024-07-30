import java.io.*;
import java.util.*;
import java.lang.*;

public class Main
{
    static HashMap<String, List<String>> cfgTable = new HashMap<>();
    static ArrayList<String> sVar = new ArrayList<>();
    static ArrayList<String> variables = new ArrayList<>();
    static String line;
    static String[] temp = new String[0];

    public static void main(String[] args) throws FileNotFoundException
    {
        FileReading();
    }

    public static void FileReading() throws FileNotFoundException
    {
        /*ArrayList<String> sVar = new ArrayList<>();
        ArrayList<String> variables = new ArrayList<>();
        String line;
        String[] temp = new String[0];*/

        Scanner input = new Scanner(System.in);

        System.out.print("What is the name of the CFG file to read from: ");

        String inputFileName = input.nextLine();
        // if the user inputs the right name and does not add .txt then this adds it to be able to find the file
        if(!inputFileName.contains(".txt"))
        {
            inputFileName = inputFileName + ".txt";
        }

        File fileName = new File(inputFileName);

        // if the file does not exist then this if statement is activated
        if(!fileName.exists())
        {
            System.out.printf("Input File %s was not found.\n", fileName);
            System.exit(0); // terminates program
        }

        Scanner inputReader = new Scanner(fileName);

        // prompt and ask user input to name the output file
        System.out.print("Please enter the name of file to write to: ");
        String outputFileName = input.next();

        if(!outputFileName.contains(".txt"))
        {
            outputFileName = outputFileName + ".txt";
        }

        // creates the output file to write on with the PrintWriter
        File outputFile = new File (outputFileName);
        PrintWriter outputWriter = new PrintWriter(outputFile);

        while(inputReader.hasNext())
        {
            line = inputReader.nextLine();

            temp = line.split("-", 2);

            sVar.add(temp[0].trim());
            variables.add(temp[1].trim());
        }

        for(int i = 0; i < sVar.size(); i++)
        {
            String startVar = sVar.get(i);
            String var = variables.get(i);

            outputWriter.println(startVar + "- " + variables);

        }

        inputReader.close();
        outputWriter.close();
    }

    public static void FileScanning(Scanner inputReader, String outputFileName) throws FileNotFoundException
    {



    }
}