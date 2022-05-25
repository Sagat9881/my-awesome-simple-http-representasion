import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AwesomeSimpleHttpClient {
    public static void main(String[] args) throws IOException {
        int c = 0;
        try (Socket socket = new Socket("httpbin.org", 80);
             OutputStream request = socket.getOutputStream();
             InputStream response = socket.getInputStream()) {

            byte[] data = ("GET / HTTP/1.1\n" +
                    "Host: httpbin.org\n\n").getBytes();

            request.write(data);

            while ((c = response.read()) != -1) {
                System.out.print((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(c);
    }
}
