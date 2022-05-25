import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class Server {
    public static final int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;
    private final HttpHandler handler;


    Server(HttpHandler handler) {
        this.handler = handler;
    }

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 80));


            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }

        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        System.out.println("new client connection ");

        AsynchronousSocketChannel clientChannel = future.get();

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;
            while (keepReading) {
                int readResult = clientChannel.read(buffer).get();

                keepReading = readResult == BUFFER_SIZE;
                buffer.flip();

                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                builder.append(charBuffer);

                buffer.clear();
            }

            HttpRequest httpRequest = new HttpRequest(builder.toString());
            HttpResponse httpResponse = new HttpResponse();

            if (handler != null) {
                try {
                    String body = this.handler.handle(httpRequest, httpResponse);
                    if (body != null && !body.isEmpty()) {
                        if (httpResponse.getHeaders().get("Content-Type") == null) {
                            httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
                        }

                        httpResponse.setBody(body);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    httpResponse.setStatusCode(500);
                    httpResponse.setStatus("Internal server error");
                    httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
                    httpResponse.setBody("<html><body><h1>Internal server error</h1></body></html>");
                }
            } else {
                httpResponse.setStatusCode(404);
                httpResponse.setStatus("Not found");
                httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
                httpResponse.setBody("<html><body><h1>Resource not found</h1></body></html>");
            }

            ByteBuffer response = ByteBuffer.wrap(httpResponse.getBytes());
            clientChannel.write(response);
            clientChannel.close();
        }
    }

}
