import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static double getMeanFromSample(List<List<String>> data) {
        int size = data.size();

        double mean =0;
        double sum=0;
       
        for(int i=0;i<size;i++) {
            String nr =data.get(i).get(2);
            double value=Double.parseDouble(nr);
            sum=sum+value;

            System.out.println(value);
        }

        mean=sum/30;
        System.out.println("media este :"+mean);
        return mean;

    }
    public static double getStandardDeviationfromSample(List<List<String>> data) {
        double mean= getMeanFromSample(data);
        int sum=0;
        int size=data.size();
        double deviation=0;

        for(int i=0;i<size;i++) {
            String nr =data.get(i).get(2);
            double value=Double.parseDouble(nr);
            //sum=sum+value;

            sum += Math.pow(value-mean,2);
        }
        deviation=Math.sqrt(sum/30);
        System.out.println("deviatia este:"+deviation);
        return deviation;


    }

    public static List<List<String>>processData(String file)
    {

        try {

         
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            System.out.println(csvReader);
            List<List<String>> records = new ArrayList<>();

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    System.out.print(cell + "\t");
                }
                List<String> row = new ArrayList<>();
                for (int i = 0; i < nextRecord.length; i++)
                {
                    row.add(nextRecord[i]);
                    if ((i + 1) % 3 == 0) {
                        records.add(new ArrayList<>(row));
                        row.clear();
                    }
                }
            }
            Random random = new Random();
            int startIndex = random.nextInt(records.size() - 30);

            List<List<String>> newRecords = new ArrayList<>();
            for (int i = startIndex; i < startIndex + 30; i++) {
                newRecords.add(records.get(i));
            }

            return newRecords;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static boolean isOutlier(double number,double mean,double deviation) {
        if((number>mean+2*deviation)||(number<mean-2*deviation)){

            return true;
        }
        return false;

    }
    public static List<List<String>> getOutliers(List<List<String>> data) {
        int size = data.size();
        double mean = getMeanFromSample(data);
        double deviation = getStandardDeviationfromSample(data);
        List<List<String>> newOutliers = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String nr = data.get(i).get(2);
            double value = Double.parseDouble(nr);
            if (isOutlier(value, mean, deviation)) {
                List<String> outlierRow = new ArrayList<>(data.get(i));
                double percentageDeviation = (deviation / mean) * 100;
                outlierRow.add(String.valueOf(mean));
                outlierRow.add(String.valueOf(deviation));
                outlierRow.add(String.valueOf(percentageDeviation));
                newOutliers.add(outlierRow);
            }
        }

        return newOutliers;
    }
    public static void writeOutliersToCSV(List<List<String>> outliers, String outputFileName) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName))) {


            for (List<String> outlier : outliers) {
                csvWriter.writeNext(outlier.toArray(new String[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void processOutliersForSubfolders(int nr_of_files) {
        String[] subfolders = {"LSE", "NASDAQ", "NYSE"};
        String currentDirectory = System.getProperty("user.dir");
        for (String subfolder : subfolders) {
            File folder = new File(currentDirectory + "\\"  + subfolder);

            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    int filesToProcess = Math.min(nr_of_files, files.length);

                    for (int i = 0; i < filesToProcess; i++) {
                        File file = files[i];

                        // Process data from current file
                        List<List<String>> data = processData(file.getAbsolutePath());

                        // Get outliers
                        List<List<String>> outliers = getOutliers(data);

                        // Generate output file name
                        String outputFileName = "output" + subfolder + "_" + file.getName();

                        // Write outliers to CSV
                        writeOutliersToCSV(outliers, outputFileName);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {



        processOutliersForSubfolders(2);


    }

}