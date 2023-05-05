package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Project name required!");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadURL = Resources.getResource("payload.xml");
        Set<User> users = parseByJAXB(projectName, payloadURL);
        System.out.println("Parsing by JAXB:");
        users.forEach(u -> System.out.println(u.getValue()));

        System.out.println();
        System.out.println("Processing by StAX:");
        List<String> namesAndEmails = processByStAX(projectName, payloadURL);
        namesAndEmails.stream().sorted().forEach(System.out::println);

        System.out.println();
        String html = toHtml(users, projectName);
        System.out.println(html);
        try(Writer writer = Files.newBufferedWriter(Paths.get("out/users.html"))) {
            writer.write(html);
        }

        System.out.println("transformByXslt:");
        html = transformByXslt(projectName, payloadURL);
        System.out.println(html);
        try(Writer writer = Files.newBufferedWriter(Paths.get("out/groups.html"))) {
            writer.write(html);
        }
    }

    private static List<String> processByStAX(String projectName, URL payloadURL) throws Exception {
        List<String> namesAndEmails = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(payloadURL.openStream())) {
            XMLStreamReader reader = processor.getReader();
            List<String> groupsOfProject = new ArrayList<>();
            while (processor.startElement("Project","Projects")) {
                if (projectName.equals(processor.getAttribute("projectName"))) {
                    while (processor.startElement("Group","Project")) {
                        groupsOfProject.add(reader.getAttributeValue(null,"groupName"));
                    }
                    break;
                }
            }
            if (groupsOfProject.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
            }
            while (processor.startElement("User","Users")) {
                String email = processor.getAttribute("email");
                String groupRefs = processor.getAttribute("groupRefs");
                String fullName = processor.getText();
                if (groupRefs == null || groupRefs.isEmpty()) {
                    continue;
                }
                List<String> groupsOfUser = new ArrayList<>();
                Collections.addAll(groupsOfUser, groupRefs.split(" "));
                if (!Collections.disjoint(groupsOfProject, groupsOfUser)) {
                    namesAndEmails.add(fullName + " " + email);
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

    private static String toHtml(Set<User> users, String projectName) {
        final ContainerTag table = table().with(
                tr().with(th("FullName"), th("Email")))
                .attr("border","1")
                .attr("cellpadding","8")
                .attr("cellspacing","0");

        users.forEach(u -> table.with(
                tr().with(td(u.getValue()), td(u.getEmail()))));
        return html().with(
                head().with(title(projectName + " users")),
                body().with(h1(projectName + " users"), table)
        ).render();
    }

    private static String transformByXslt(String projectName, URL payloadURL) throws Exception {
        try (InputStream xsltStream = Resources
                .getResource("groups.xsl").openStream();
        InputStream xmlStream = payloadURL.openStream()) {
            XsltProcessor processor = new XsltProcessor(xsltStream);
            processor.setParameter("projectName", projectName);
            return processor.transform(xmlStream);
        }
    }
}
