package org.webapp.dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CSVFileContext {
    List<Map<String, String>> csvInputList = new CopyOnWriteArrayList<>();
    List<Map<String, Integer>> headerList = new CopyOnWriteArrayList<>();
    String fileName = "src\\station.csv";
    CSVFormat format = CSVFormat.newFormat(',').withHeader();

    public List<Map<String, String>> readFile() {
        try {
            BufferedReader inputReader = new BufferedReader(new FileReader(new File(fileName)));
            CSVParser dataCSVParser = new CSVParser(inputReader, format);
            List<CSVRecord> csvRecords = dataCSVParser.getRecords();
            Map<String, Integer> headerMap = dataCSVParser.getHeaderMap();
            headerList.add(headerMap);

            for(CSVRecord record : csvRecords) {
                Map<String, String> inputMap = new LinkedHashMap<>();
                for(Map.Entry<String, Integer> header : headerMap.entrySet()){
                    inputMap.put(header.getKey(), record.get(header.getValue()));
                }
                if (!inputMap.isEmpty()) {
                    csvInputList.add(inputMap);
                }
            }
        } catch(Exception e) {
            System.out.println(e);
        } finally {
            return csvInputList;
        }
    }
}
