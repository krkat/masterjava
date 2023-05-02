package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainXmlJAXB {
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
