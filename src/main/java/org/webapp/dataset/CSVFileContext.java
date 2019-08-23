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
//csv파일 원본 그대로 가져와서 열 골라쓸 수 있게 구현
//readFile(string fileName){} 으로 구현
@Component
public class CSVFileContext {
    List<Map<String, String>> csvInputList = new CopyOnWriteArrayList<>();
    List<Map<String, Integer>> headerList = new CopyOnWriteArrayList<>();
    CSVFormat format = CSVFormat.newFormat(',').withHeader();

    public List<Map<String, String>> readFile(String url) {
        try {
            BufferedReader inputReader = new BufferedReader(new FileReader(new File(url)));
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
