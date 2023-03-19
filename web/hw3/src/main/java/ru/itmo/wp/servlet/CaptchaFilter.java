package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {

    private final Random random = new Random();

    private int generateNumber() {
        return 100 + random.nextInt(900);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ("/captcha/pic".equals(request.getRequestURI())) {
            response.setContentType("image/png");
            String number = String.valueOf(request.getSession().getAttribute("CaptchaValue"));
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(ImageUtils.toPng(number)));
            try (OutputStream outputStream = response.getOutputStream()) {
                ImageIO.write(image, "png", outputStream);
            }
        } else if ("GET".equals(request.getMethod())) {
            request.getSession().setAttribute("CaptchaValue", String.valueOf(generateNumber()));
            if (request.getSession().getAttribute("CaptchaStatus") == null) {
                request.getSession().setAttribute("CaptchaStatus", "0");
                request.getSession().setAttribute("uri", request.getRequestURI());
                response.sendRedirect("/captcha.html");
            } else if ("0".equals(request.getSession().getAttribute("CaptchaStatus")) && !"/captcha.html".equals(request.getRequestURI())) {
                request.getSession().setAttribute("uri", request.getRequestURI());
                response.sendRedirect("/captcha.html");
            }
            chain.doFilter(request, response);
        } else if ("POST".equals(request.getMethod())) {
            if (request.getParameter("captcha").equals(request.getSession().getAttribute("CaptchaValue"))) {
                request.getSession().setAttribute("CaptchaStatus", "1");
                response.sendRedirect(request.getSession().getAttribute("uri").toString());
            } else {
                request.getSession().setAttribute("CaptchaValue", String.valueOf(generateNumber()));
                response.sendRedirect("/captcha.html");
            }
        }
    }
}