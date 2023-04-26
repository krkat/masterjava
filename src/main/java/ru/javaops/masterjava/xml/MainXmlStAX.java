package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainXmlStAX {

    public static void main(String[] args) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
             StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources
                             .getResource("payload.xml")
                             .openStream())) {
            String projectName = bufferedReader.readLine();
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT
                        && "Project".equals(reader.getLocalName())) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLEvent.END_ELEMENT && "Project".equals(reader.getLocalName())) {
                            break;
                        }
                        if (event == XMLEvent.START_ELEMENT
                                && "projectName".equals(reader.getLocalName())
                                && projectName.equals(reader.getElementText())) {
                            List<String> fullNames = new ArrayList<>();
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLEvent.END_ELEMENT
                                        && "Groups".equals(reader.getLocalName())) {
                                    break;
                                }
                                String fullName = "";
                                String email = "";
                                if (event == XMLEvent.START_ELEMENT
                                && "User".equals(reader.getLocalName())) {
                                    email = reader.getAttributeValue(0);
                                    while (reader.hasNext()) {
                                        event = reader.next();
                                        if (event == XMLEvent.START_ELEMENT
                                                && ("fullName").equals(reader.getLocalName())) {
                                            fullName = reader.getElementText();
                                            fullNames.add(fullName+" "+email);
                                            break;
                                        }
                                    }
                                }
                            }
                            fullNames.stream().sorted().forEach(System.out::println);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
