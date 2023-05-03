package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Project name required!");
            System.exit(1);
        }
        String projectName = args[0];
        URL payLoadURL = Resources.getResource("payload.xml");
        Set<User> users = parseByJAXB(projectName, payLoadURL);
        users.forEach(u -> System.out.println(u.getValue()));

        List<String> namesAndEmails = parseByStAX(projectName, payLoadURL);
        namesAndEmails.stream().sorted().forEach(System.out::println);
    }

    private static List<String> parseByStAX(String projectName, URL payloadURL) throws Exception {
        List<String> namesAndEmails = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(payloadURL.openStream())) {
            XMLStreamReader reader = processor.getReader();

            List<String> groupsOfProject = new ArrayList<>();
            //цикл по всей payload
            int event;
            while (reader.hasNext()) {
                event = reader.next();
                if (event == XMLEvent.END_ELEMENT
                && "Projects".equals(reader.getLocalName())) {
                    break;
                }
                if (event == XMLEvent.START_ELEMENT
                        && "Project".equals(reader.getLocalName())) {
                    if ("projectName".equals(reader.getAttributeLocalName(0))
                            && projectName.equals(reader.getAttributeValue(0))) {
                        while (reader.hasNext()) {
                            event = reader.next();
                            if (event == XMLEvent.END_ELEMENT && "Project".equals(reader.getLocalName())) {
                                break;
                            }
                            if (event == XMLEvent.START_ELEMENT
                                    && "Group".equals(reader.getLocalName())) {
                                groupsOfProject.add(reader.getAttributeValue(0));
                            }
                        }
                    }
                }
            }
            //цикл по Users
            while (reader.hasNext()) {
                event = reader.next();
                if (event == XMLEvent.END_ELEMENT
                        && "Users".equals(reader.getLocalName())) {
                    break;
                }
                if (event == XMLEvent.START_ELEMENT
                        && "User".equals(reader.getLocalName())) {
                    String email = reader.getAttributeValue(0);
                    String groupRefs = reader.getAttributeValue(3);
                    if (groupRefs == null || groupRefs.isEmpty()) {
                        continue;
                    }
                    List<String> groupsOfUser = new ArrayList<>();
                    Collections.addAll(groupsOfUser, groupRefs.split(" "));
                    if (!Collections.disjoint(groupsOfProject, groupsOfUser)) {
                        String fullName = reader.getElementText();
                        namesAndEmails.add(fullName + " " + email);
                    }
                }
            }


        }
        return namesAndEmails;
    }

    private static Set<User> parseByJAXB(String projectName, URL payloadURL) throws Exception {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload;
        try (InputStream is = payloadURL.openStream()) {
            payload = parser.unmarshal(is);
        }
        List<Project> allProjects = payload.getProjects().getProject();
        Project project = allProjects.stream()
                .filter(x -> projectName.equals(x.getProjectName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Неверное имя проекта'" + projectName));
        Set<Group> groupsOfProject = new HashSet<>(project.getGroup());
        List<User> sllUsers = payload.getUsers().getUser();
        return sllUsers.stream()
                .filter(u -> !Collections.disjoint(groupsOfProject, u.getGroupRefs()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
    }
}
