import java.io.*;
import java.util.*;
import java.lang.*;

public class Main
{
    static HashMap<String, List<String>> cfgTable = new HashMap<>();
    static HashMap<String, List<String>> variableTable = new HashMap<>();
    static ArrayList<String> sVar = new ArrayList<>();
    static ArrayList<String> value = new ArrayList<>();
    static ArrayList<Character> terminals = new ArrayList<>();


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
            inputFileName = inputFileName.toUpperCase();
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

        FileScanning(inputReader, outputWriter);
    }

    public static void FileScanning(Scanner inputReader, PrintWriter outputWriter) throws FileNotFoundException
    {

        while(inputReader.hasNext())
        {
            String line = inputReader.nextLine();

            String[] parts = line.split("-");

            String leftHand = parts[0].trim();

            String[] rightHand = parts[1].split("(\\|)");

            sVar.add(leftHand);

            List<String> rightList = new ArrayList<>();

            for(String temp : rightHand)
            {
                rightList.add(temp.trim());
            }

            cfgTable.put(leftHand, rightList);
        }

        EpsilonRuleStepOne();
        VarTablePopulate();
        EpsRuleStepThree();

        for(String key : sVar)
        {
            System.out.print(key + ": " + cfgTable.get(key) + "\n");
        }

        for(String key : sVar)
        {
            System.out.print(key + ": " + variableTable.get(key) + "\n");
        }

        System.out.println("\nStep 4 results\n");

        EpsRuleStepFour();

        inputReader.close();
        outputWriter.close();
    }

    public static void EpsilonRuleStepOne()
    {
        ArrayList<String> emptySVar = new ArrayList<>();

        for(String key : sVar)
        {
            ArrayList<String> temp = new ArrayList<>();

            for(String checking : cfgTable.get(key))
            {
                if(!checking.equals("0"))
                {
                    temp.add(checking);
                }

                else
                {
                    value.add(key);
                }
            }

            if(temp.isEmpty())
            {
                emptySVar.add(key);
            }

            else
            {
                cfgTable.put(key, temp);
            }

        }

        for(String key : emptySVar)
        {
            cfgTable.remove(key);
        }

        sVar.removeAll(emptySVar);
        value.removeAll(emptySVar);

    }

    public static void VarTablePopulate()
    {
        for(String key : sVar)
        {
            List<String> var = new ArrayList<>();

            for(String checking : cfgTable.get(key))
            {
                for(char c : checking.toCharArray())
                {
                    String s = String.valueOf(c);

                    if(s.equals(s.toUpperCase()))
                    {
                        var.add(s.trim());
                    }
                }
            }

            variableTable.put(key, var);
        }
    }

    public static void EpsRuleStepThree()
    {
        ArrayList<String> targetValues = new ArrayList<>(value);

        boolean anyChangesMade = false;

        while(!anyChangesMade)
        {
            ArrayList<String> copyVar = new ArrayList<>(targetValues);

            for (String state : targetValues)
            {
                List<String> result = calE(state);

                for (String s : result)
                {
                    if (!copyVar.contains(s))
                    {
                        copyVar.add(s);
                        anyChangesMade = true;
                    }
                }
            }

            targetValues = copyVar;

            for(int i = 0; i < copyVar.size(); i++)
            {
                for (String s : value)
                {
                    if (!copyVar.get(i).equals(s))
                    {
                        targetValues.remove(i);
                    }
                }
            }
        }

        value.addAll(targetValues);

        System.out.println();

    }

    public static List<String> calE(String q)
    {
        List<String> result = new ArrayList<>();
        List<String> transition = variableTable.get(q);

        for(String s : transition)
        {
            for (String string : value)
            {
                if(!string.equals(s) && !result.contains(s))
                {
                    result.add(s);
                }
            }
        }

        return result;
    }

   /* public static void EpsRuleStepFour()
    {
        ArrayList<String> upper = new ArrayList<>();
        ArrayList<String> lower = new ArrayList<>();

        ArrayList<String> newCNFCom = new ArrayList<>();

        for(String key : value)
        {
            for(String s : cfgTable.get(key))
            {
                newCNFCom.add(s);
                char[] temp = s.toCharArray();

                for(char c: temp)
                {
                    String sample = String.valueOf(c);

                    if(sample.equals(sample.toUpperCase()) && value.contains(sample))
                    {
                        upper.add(sample);
                    }

                    else
                    {
                        lower.add(sample);
                    }
                }

                //List<String> combo = comboniations(upper, lower);

                List<String> combo = generateCombinations(upper, lower);

                for(String checking : combo)
                {
                    if(!newCNFCom.contains(checking))
                    {
                        newCNFCom.add(checking);
                    }
                }

                newCNFCom.addAll(combo);
            }
        }

        for(String key : value)
        {
            System.out.println("The new " + key + ": ");

            for (String s : newCNFCom)
            {
                System.out.print(s + " ");
            }
        }

    }*/

    public static void EpsRuleStepFour()
    {
        for (String key : new ArrayList<>(cfgTable.keySet())) {
            List<String> productions = cfgTable.get(key);
            List<String> newProductions = new ArrayList<>(productions);

            for (String production : productions)
            {
                newProductions.addAll(generateCombinations(production));
            }

            cfgTable.put(key, newProductions);
        }

        for(String key : sVar)
        {
            System.out.print("The new " + key + ": ");

            for (String s :cfgTable.get(key))
            {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    public static List<String> generateCombinations(String production)
    {
        List<String> results = new ArrayList<>();
        results.add(production); // Add the original production

        for (int i = 0; i < production.length(); i++)
        {
            char c = production.charAt(i);
            if (Character.isUpperCase(c) && value.contains(String.valueOf(c)))
            {
                String newProduction = production.substring(0, i) + production.substring(i + 1);
                if (!results.contains(newProduction))
                {
                    results.add(newProduction);
                }
            }
        }

        return results;
    }


    /*public static List<String> comboniations(ArrayList<String> upper, ArrayList<String> lower)
    {
        List<String> results = new ArrayList<>();
        int upperLen = upper.size();
        int n = 1 << upperLen;

        for (int i = 1; i < n; i++)
        {
            StringBuilder sb = new StringBuilder();
            int upIndex = 0;
            int lowIndex = 0;

            for (int j = 0; j < n; j++)
            {
                if ((i & (1 << j)) != 0)
                {
                    sb.append(upper.get(upIndex));
                }

                else if (lowIndex < lower.size())
                {
                    sb.append(lower.get(lowIndex++));
                }

                upIndex++;
            }
            results.add(sb.toString());
        }

        return results;
    }*/

    public static void UselessRule()
    {

    }
}