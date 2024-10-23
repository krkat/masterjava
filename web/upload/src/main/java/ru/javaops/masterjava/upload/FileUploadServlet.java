package ru.javaops.masterjava.upload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet("/upload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/WEB-INF/jsp/FileUpload.jsp")
                .forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            System.out.println("Name: ");
            System.out.println(part.getName());
            System.out.println("Header: ");
            for (String headerName : part.getHeaderNames()) {
                System.out.println(headerName);
                System.out.println(part.getHeader(headerName));
            }
            System.out.println("Size: ");
            System.out.println(part.getSize());
            part.write(part.getName() + ".down");
        }
        getServletContext().getRequestDispatcher("/WEB-INF/jsp/Users.jsp")
                .forward(req, resp);
    }
}
