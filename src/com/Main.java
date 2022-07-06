package com;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        Locale locale  = new Locale("en", "UK");
        String numPat = "###.##";

        DecimalFormat df = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        df.applyPattern(numPat);


        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        /* Build Document */
        Document document = builder.parse(new File("src/example.xml"));
        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();


        //Here comes the root node
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());

        //Get all employees
        NodeList nList = document.getElementsByTagName("city");
        String[] cityName = new String[nList.getLength()];
        int[] cityPopulations = new int[nList.getLength()];
        Double[] cityArea = new Double[nList.getLength()];

        HashMap<String, Integer> cityPopHash = new HashMap<>();
        HashMap<String, Double> cityAreaHash = new HashMap<>();
        int popVar=0;
        Double areaVar=0.00;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            Element eElement = (Element) node;
            //System.out.println("Name : " + eElement.getAttribute("name"));
            //System.out.println("Area : " + eElement.getElementsByTagName("area").item(0).getTextContent());
            cityName[temp] = eElement.getAttribute("name");
            popVar = Integer.parseInt(eElement.getAttribute("population"));
            areaVar = Double.valueOf(eElement.getElementsByTagName("area").item(0).getTextContent());
            cityPopHash.put(eElement.getAttribute("name"),popVar);
            cityAreaHash.put(eElement.getAttribute("name"),areaVar);
        }
        /*
        System.out.println("name of the cities : " + Arrays.toString(cityName));
        System.out.println("populations of the cities : " + Arrays.toString(cityPopulations));
        System.out.println("areas of the cities : " + Arrays.toString(cityArea));
        */
        // convert Array to Stream
        Pattern pattern = Pattern.compile(".*n.+");
        Stream<String> cityNameStream = Arrays.stream(cityName);
        Stream<String> matching = cityNameStream.filter(pattern.asPredicate());
        String[] filteredName = matching.toArray(String[]::new);
        System.out.println("filtered cities : " + Arrays.toString(filteredName));

        // perform Operation on filtered cities

        // average operation
        Double sum=0.0;
        Double avg;
        for (int temp = 0; temp < filteredName.length; temp++) {

            sum += cityPopHash.get(filteredName[temp]);
        }
        avg = sum/filteredName.length;
        System.out.println("Average population (filtered cities) : " + df.format(avg));


        // convert to HasMap for results
        HashMap<String, String> Results = new HashMap<String, String>();
        Results.put("information", df.format(avg));
        Results.put("inf", df.format(sum));

        
        // writing to an XML file
        FileWriter fw = new FileWriter("src/results.xml",false);
        BufferedWriter buffWriter = new BufferedWriter(fw);
        buffWriter.write("<" + "results" + ">");
        for (Map.Entry<String, String> entry : Results.entrySet()) {
            buffWriter.write("<result name=" + "\""+entry.getKey()+"\"" + ">");
            buffWriter.write(String.valueOf(entry.getValue()));
            buffWriter.write("</" + "result" + ">");
            buffWriter.newLine();
        }
        buffWriter.write("</" + "results" + ">");
        buffWriter.flush();
        buffWriter.close();

        }

}
