import java.io.*;
import java.util.*;
import java.lang.*;

public class Main
{
    static HashMap<String, List<String>> cfgTable = new HashMap<>();
    static HashMap<String, List<String>> variableTable = new HashMap<>();
    static ArrayList<String> sVar = new ArrayList<>();
    static ArrayList<String> value = new ArrayList<>();
    static ArrayList<String> terminateVariable = new ArrayList<>();
    static ArrayList<String> uselessValue = new ArrayList<>();


    public static void main(String[] args) throws FileNotFoundException
    {
        FileReading();
    }

    public static void FileReading() throws FileNotFoundException
    {

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
        dealingDeletedSVar();
        EpsRuleStepThree();
        removeValue();


        System.out.println("Step 4 results: ");

        EpsRuleStepFour();

        for(String key : sVar)
        {
            System.out.print(key + ": " + cfgTable.get(key) + "\n");
        }

        UselessRuleStepOne();

        System.out.print("This is the variables we got ");

        for(String s : uselessValue)
        {
            System.out.print(s + " ");
        }

        System.out.print("\n");

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

                else if(!value.contains(key))
                {
                    value.add(key);
                }
            }

            if(temp.isEmpty())
            {
                terminateVariable.add(key);
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
            List<String> temp = new ArrayList<>(cfgTable.get(key));

            for(String checking : temp)
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

    public static void dealingDeletedSVar()
    {
        for(String key : cfgTable.keySet())
        {
            List<String> newVarSets = new ArrayList<>();

            for(String temp : cfgTable.get(key))
            {
                StringBuilder sb = new StringBuilder();

                for(char c : temp.toCharArray())
                {
                    if(!terminateVariable.contains(String.valueOf(c)))
                    {
                        sb.append(c);
                    }
                }

                if(!sb.isEmpty())
                {
                    newVarSets.add(sb.toString());
                }
            }

            cfgTable.put(key, newVarSets);
        }
    }


    public static void EpsRuleStepThree()
    {
        ArrayList<String> targetValues = new ArrayList<>(value);

        boolean anyChangesMade = true;

        while(anyChangesMade)
        {
            anyChangesMade = false;

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
        }


        for(String val : targetValues)
        {
            if(!value.contains(val))
            {
                value.add(val);
            }

            else if(!value.contains("S"))
            {
                value.add(sVar.get(0));
            }
        }
    }

    public static List<String> calE(String q)
    {
        List<String> result = new ArrayList<>();

        if(variableTable.containsKey(q))
        {
            List<String> transition = variableTable.get(q);

            for(String s: transition)
            {
                if(!value.contains(s) && !result.contains(s))
                {
                    result.add(s);
                    result.addAll(calE(s));
                }
            }
        }

        return result;
    }

    public static void removeValue()
    {
        for(String v : terminateVariable)
        {
            if(value.contains(v))
            {
                value.remove(v);
            }
        }
    }

    public static void EpsRuleStepFour()
    {
        for (String key : value)
        {
            List<String> copyString = cfgTable.get(key);
            List<String> temp = new ArrayList<>();

            for (String s : copyString)
            {
                List<String> combo = makingCombo(s);

                for(String t : combo)
                {
                    if(!temp.contains(t))
                    {
                        temp.add(t);
                    }
                }
            }

            cfgTable.put(key, temp);
        }
    }

    public static List<String> makingCombo(String copyString)
    {
        List<String> results = new ArrayList<>();
        results.add(copyString);

        for (int i = 0; i < copyString.length(); i++)
        {
            char c = copyString.charAt(i);

            if (Character.isUpperCase(c))
            {
                String newValue = copyString.substring(0, i) + copyString.substring(i + 1);

                if (!results.contains(newValue))
                {
                    results.addAll(makingCombo(newValue));
                }
            }
        }

        results.removeIf(String::isEmpty);

        return results;
    }

    public static void UselessRuleStepOne()
    {
        uselessValue.add("0");

        List<String> gathering = new ArrayList<>();

        for(String key : sVar)
        {
            List<String> temp = new ArrayList<>(cfgTable.get(key));

            for(String checking : temp)
            {
                for(char c : checking.toCharArray())
                {
                    String s = String.valueOf(c);

                    if(s.equals(s.toLowerCase()))
                    {
                        gathering.add(s.trim());
                    }
                }
            }
        }

        removeDups(gathering);
    }

    public static void removeDups(List<String> gathering)
    {
        List<String> temp = new ArrayList<>(gathering);

        for(String s : temp)
        {
            if(!uselessValue.contains(s))
            {
                uselessValue.add(s);
            }
        }
    }

    public static void uselessRuleStepThree()
    {
        
    }
}