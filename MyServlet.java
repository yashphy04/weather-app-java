package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String city = request.getParameter("userInput");

        if (city == null || city.trim().isEmpty()) {
            response.sendRedirect("index.html");
            return;
        }

        city = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String apiKey = "b19620b9e7094af7e4b7f36e194bd6d3";

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + apiKey + "&units=metric";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream is = (conn.getResponseCode() == 200)
                ? conn.getInputStream()
                : conn.getErrorStream();

        Scanner scanner = new Scanner(is);
        StringBuilder json = new StringBuilder();
        while (scanner.hasNext()) {
            json.append(scanner.nextLine());
        }
        scanner.close();

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(json.toString(), JsonObject.class);

        JsonObject main = obj.getAsJsonObject("main");

        String cityName = obj.get("name").getAsString();
        double temp = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        // 🔹 Send data to JSP
        request.setAttribute("city", cityName);
        request.setAttribute("temp", temp);
        request.setAttribute("humidity", humidity);

        request.getRequestDispatcher("result.jsp").forward(request, response);
    }
}
