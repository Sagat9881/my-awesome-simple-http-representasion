import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final static String NEW_LINE = "\r\n";
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";
    private int statusCode = 200;
    private String status = "OK";

    public HttpResponse() {
        this.headers.put("Server", "my-awesome-simple-http-server");
        this.headers.put("Connection", "close");
    }

    public String message() {
        StringBuilder builder = new StringBuilder();

        builder.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(status)
                .append(NEW_LINE);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NEW_LINE);
        }

        return builder
                .append(NEW_LINE)
                .append(body)
                .toString();
    }

    public byte[] getBytes() {
        return message().getBytes();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.headers.put("Content-Length", String.valueOf(body.length()));
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }
}
