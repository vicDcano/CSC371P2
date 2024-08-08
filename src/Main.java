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

    public static void FileScanning(Scanner inputReader, PrintWriter outputWriter)
    {

        // Taking the text file and populate it into the cfgtable hashmap and start value to the arraylist
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
        EpsRuleStepFour();
        UselessRuleStepOne();
        uselessRule();
        printresults(inputReader, outputWriter); // Results to be printed here
    }

    /*
        Goes into the cfg table and identify the epsilon empty on the right hand side
        takes note where they were at that and saved the ones who had it to only modify them in the next function
     */
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

    /*
        Gets every variable on each starting variable to place them in a variable table
        to be later used for CalE function
     */
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

    /*
    This function deals with deleting starting value when needed to for having null value or anything
    then it goes threw the entire the starting value content and deletes the string that has that starting value
    we need to delete the starting value and anything associated with all the starting value
     */

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

    /*
        populate the value and use CalE to do step three of Epsilon rule
        get the value of who recursively together and connected
        then add the S as it is the value that is need to be used to due to epsilon rule
        and adds them to the terminating value function to delete the unused variables
     */
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

    /*
        CalE function to recursively on each starting value
        and it sees if another variable is there and not a terminal value
        then goes to that next starting value to see if another variable is there and continues the cycle
        till it finds all the variables needed
     */
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

    /*
        deletes the variables that are in the list
     */
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

    /*
        The final step of the epsilon rule of making new combinations for each starting values
        it calls the function to do more work of making the combinations
        making a variety of combinations with the Variables and terminals
        making the variables into 1 or 0 and ignores the terminals
     */
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

    /*
        this is the function that will turn the variables into a 0 or 1
        making different combinations fo the starting values that need to make changes
        to fulfill the epsilon rule
     */
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

    /*
        Follows the rule of Useless rule of step one of adding the epsilon and the terminal values
        putting them into an Arraylist and save it for later
     */
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

    // removes the terminal duplicates that appears in the arraylist
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

    /*
        Does the rest of the Useless rule of going threw deleting values that are not reached,
        that are infinite looping because of useless rule
     */
    public static void uselessRule()
    {
        boolean changesMade;

        do {
            changesMade = false;

            List<String> usingVar = new ArrayList<>();

            for (String key : sVar)
            {
                for (String s : cfgTable.get(key))
                {
                    boolean checking = true;

                    for (char c : s.toCharArray())
                    {
                        if (Character.isUpperCase(c) && !usingVar.contains(String.valueOf(c)))
                        {
                            checking = false;
                            break;
                        }
                    }

                    if (checking)
                    {
                        usingVar.add(key);
                        break;
                    }
                }
            }

            List<String> removingKey = new ArrayList<>();

            for (String key : sVar)
            {
                if (!usingVar.contains(key))
                {
                    removingKey.add(key);
                }

                else
                {
                    List<String> newSetTerms = new ArrayList<>();

                    for (String production : cfgTable.get(key))
                    {
                        boolean beingUsed = true;

                        for (char c : production.toCharArray())
                        {
                            if (Character.isUpperCase(c) && !usingVar.contains(String.valueOf(c)))
                            {
                                beingUsed = false;
                                break;
                            }
                        }

                        if (beingUsed)
                        {
                            newSetTerms.add(production);
                        }
                    }

                    if (newSetTerms.isEmpty())
                    {
                        removingKey.add(key);
                    }

                    else
                    {
                        cfgTable.put(key, newSetTerms);
                    }
                }
            }

            for (String key : removingKey)
            {
                cfgTable.remove(key);
                sVar.remove(key);
                changesMade = true;
            }

        } while (changesMade);
    }

    // Prints the results into the text file
    public static void printresults(Scanner inputReader, PrintWriter outputWriter)
    {

        for(String key : sVar)
        {
            List<String> cnfValue = new ArrayList<>(cfgTable.get(key));

            outputWriter.println(key + "-" + String.join("|", cnfValue));
        }

        inputReader.close();
        outputWriter.close();
    }
}