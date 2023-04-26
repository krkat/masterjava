package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainXmlJAXB {

    public static void main(String[] args) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
             InputStream is = Resources.getResource("payload.xml").openStream()) {

            String projectName = bufferedReader.readLine();

            JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);
            JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
            Payload payload = JAXB_PARSER.unmarshal(is);
            List<Project> listOfProjects = payload.getProjects().getProject();
            Project project = listOfProjects.stream()
                    .filter(x -> projectName.equals(x.getProjectName()))
                    .findFirst().orElse(null);
            if (project == null) {
                System.exit(1);
            }
            List<Group> groups = project.getGroups().getGroup();
            List<User> users = new ArrayList<>();
            groups.forEach(x -> users.addAll(x.getUsers().getUser()));
            users.stream().sorted(Comparator.comparing(User::getFullName)).forEach(x -> System.out.println(x.getFullName() + " " + x.getEmail()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
