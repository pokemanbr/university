package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {

    private File getFile(String uri) {
        File file = new File("/home/pokemanbr/Desktop/M3239/Web/hw3/src/main/webapp/static" + uri);
        if (!file.isFile()) {
            file = new File(getServletContext().getRealPath("/static" + uri));
        }
        return file;
    }

    private String makeURI(String uri) {
        return uri.startsWith("/") ? uri : '/' + uri;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String[] uris = uri.split("[+]");
        for (String s : uris) {
            File file = getFile(makeURI(s));
            if (!file.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        File firstFile = getFile(makeURI(uris[0]));
        response.setContentType(getServletContext().getMimeType(firstFile.getName()));
        for (String s : uris) {
            File file = getFile(makeURI(s));
            try (OutputStream outputStream = response.getOutputStream()) {
                Files.copy(file.toPath(), outputStream);
            }
        }
    }
}
