package utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadCSV
{
    public List<String[]> csvData;
    public ReadCSV(String filePath){
        String line = "";
        csvData = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(filePath)))
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                String[] lineData = line.split(",");
                csvData.add(lineData);
            }
        }
        catch (IOException exception)
        {
            System.err.println(exception.getMessage());
        }
    }

    public String[] readData(int i)
    {
        return csvData.get(i);
    }
}