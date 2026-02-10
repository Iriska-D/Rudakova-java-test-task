import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static String wordCheck(String word){
        String filename = "integers.txt";
        String digits = "0123456789";
        boolean firstdot = true, firstE = true;
        char [] line = word.toCharArray();
        for(int i = 0; i < line.length; i++){
            if(line[i] == '-' && i == 0)
                continue;
            if(line[i] == '.' && firstdot){
                filename = "floats.txt";
                firstdot = false;
            }
            else if(line[i] == 'E' && firstE){
                firstE = false;
                if(line[i + 1] == '-')
                    i++;
            }
            else if(!digits.contains(String.valueOf(line[i])) && line[i] != ' '){
                filename = "strings.txt";
                break;
            }
        }
        return filename;
    }

    public static Map<String, Map<String, Double>> smallStat(Map<String, Map<String, Double>> stat, String filename){
        Map<String, Double> temp = stat.get(filename);
        temp.put("amo", temp.get("amo") + 1);
        stat.put(filename, temp);
        return stat;
    }

    public static Map<String, Map<String, Double>> bigStat(Map<String, Map<String, Double>> stat, String filename, String line){
        stat = smallStat(stat, filename);
        Map<String, Double> temp = stat.get(filename);
        if(!temp.containsKey("min")){
            if(filename.equals("strings.txt")){
                temp.put("min", (double)line.length());
                temp.put("max", (double)line.length());
            }
            else{
                temp.put("min", Double.valueOf(line));
                temp.put("max", Double.valueOf(line));
                temp.put("sum", Double.valueOf(line));
            }
        }
        else{
            if(filename.equals("strings.txt")){
                if(temp.get("min") > (double)line.length())
                    temp.put("min", (double)line.length());
                if(temp.get("max") < (double)line.length())
                    temp.put("max", (double)line.length());
            }
            else{
                if(temp.get("min") > Double.valueOf(line))
                    temp.put("min", Double.valueOf(line));
                if(temp.get("max") < Double.valueOf(line))
                    temp.put("max", Double.valueOf(line));
                temp.put("sum", Double.valueOf(line) + temp.get("sum"));
            }
        }
        return stat;
    }

    public static void output(Map<String, Map<String, Double>> stat, String [] names, String firstname){
        if(!stat.get("strings.txt").containsKey("min"))
            System.out.println("Краткая статистика:");
        else
            System.out.println("Полная статистика:");
        for(String name : names){
            System.out.println('\n' + firstname + name);
            System.out.println("Количество строчек: " + stat.get(name).get("amo"));
            if(stat.get("strings.txt").containsKey("min")){
                if(name.equals("strings.txt")){
                    System.out.println("Минимальная длина строки: " + stat.get(name).get("min"));
                    System.out.println("Максимальная длина строки: " + stat.get(name).get("max"));
                }
                else{
                    System.out.println("Минимальное число: " + stat.get(name).get("min"));
                    System.out.println("Максимальное число: " + String.format("%.4f",stat.get(name).get("max")));
                    System.out.println("Сумма чисел: " + String.format("%.4f",stat.get(name).get("sum")));
                    System.out.println("Среднее значение: " + String.format("%.4f",(stat.get(name).get("sum") / stat.get(name).get("amo"))));
                }
            }
        }
    }

    public static void main(String[] args){
        String filePath = "", firstName = "";
        char stat = ' ';
        boolean add = false;
        for(int i = 0; i < args.length; i++){
            switch(args[i]){
                case "-o":
                    if(i + 1 >= args.length)
                        throw new IllegalArgumentException("Отсутствует аргумент для пути для результатов после -o");
                    filePath = args[i + 1];
                    i++;
                    break;
                case "-p":
                    if(i + 1 >= args.length)
                        throw new IllegalArgumentException("Отсутствует аргумент для имени файла после -p");
                    firstName = args[i + 1];
                    i++;
                    break;
                case "-a":
                    add = true;
                    break;
                case "-s":
                    stat = 's';
                    break;
                case "-f":
                    stat = 'f';
                    break;
            }
        }
        try{
            Map<String, Map<String, Double>> statistic = new HashMap<>();
            String[] lastNames = {"integers.txt", "floats.txt", "strings.txt"};
            for(String txt : lastNames){
                Map<String, Double> temp = new HashMap<>();
                temp.put("amo", 0.0);
                statistic.put(txt, temp);
            }
            
            if(!filePath.equals("")){
                Path pathToDirrectory = Paths.get(filePath);
                filePath = filePath + '\\';
                if (!Files.exists(pathToDirrectory)) {
                    Files.createDirectories(pathToDirrectory);
                }
            }
            if(!add){
                for(String txt : lastNames){
                    if (Files.exists(Paths.get(filePath + firstName + txt))) 
                    Files.writeString(Paths.get(filePath + firstName + txt), "");
                }
            }
            Scanner sc = new Scanner(new File("example.txt"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(stat == 's'){
                    statistic = smallStat(statistic, wordCheck(line));
                }
                else if (stat == 'f'){
                    statistic = bigStat(statistic, wordCheck(line), line);
                }
                
                Path pathToFile = Paths.get(filePath + firstName + wordCheck(line)); 
                if (!Files.exists(pathToFile)) { 
                    Files.createFile(pathToFile);
                }
                Files.writeString(pathToFile, line + '\n', StandardOpenOption.APPEND);
            }
            sc.close();
            if(stat != ' ')
                output(statistic, lastNames, firstName);
        }
        catch(IOException e){
           System.out.println("Ошибка открывания файла");
        }
        
    }
}
