import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        URL url = Resources.getResource("orders.json");
        String rawData = Resources.toString(url, StandardCharsets.UTF_8);
        System.out.println("Snippet of input data:\n" + rawData.substring(0, 200));

        final ObjectMapper objMapper = new ObjectMapper();
        Order[] orders = objMapper.readValue(rawData, Order[].class);

        System.out.println("First order: " + orders[0]);
    }
}
