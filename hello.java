import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class hello {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", hello::showPage);
        server.createContext("/analyze", hello::analyze);
        server.start();
        System.out.println("Log Monitor running at http://localhost:8080");
    }

    private static void showPage(HttpExchange exchange) throws IOException {
        String sample = "2026-09-17 09:00:00 INFO Application started\n"
                + "2026-09-17 09:01:12 WARN Connection timeout\n"
                + "2026-09-17 09:02:03 ERROR Login failed";
        send(exchange, page(sample, ""));
    }

    private static void analyze(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String log = body.startsWith("log=") ? body.substring(4) : body;
        log = URLDecoder.decode(log, StandardCharsets.UTF_8);
        send(exchange, page(log, report(log)));
    }

    private static String report(String log) {
        int info = 0;
        int warnings = 0;
        int errors = 0;
        int alerts = 0;

        for (String line : log.toUpperCase().split("\\R")) {
            if (line.contains("INFO")) info++;
            if (line.contains("WARN")) warnings++;
            if (line.contains("ERROR")) errors++;
            if (line.contains("FAILED") || line.contains("EXCEPTION")
                    || line.contains("TIMEOUT")) alerts++;
        }
        return "<h2>Log Monitor Report</h2>"
            + "<div class='stats'>"
            + card("INFO", info, "blue")
            + card("WARNINGS", warnings, "gold")
            + card("ERRORS", errors, "red")
            + card("ALERTS", alerts, "dark")
            + "</div>";
        }

        private static String card(String label, int value, String color) {
        return "<div class='card " + color + "'><strong>" + value
            + "</strong><span>" + label + "</span></div>";
    }

    private static String page(String log, String result) {
        return "<!DOCTYPE html><html><head><title>Java Log Monitor</title>"
                + "<style>body{font-family:Arial;max-width:760px;margin:0 auto;padding:32px 18px;"
                + "background:#eef3f8;color:#172033}.box{background:white;padding:28px;"
                + "border-radius:12px;box-shadow:0 4px 16px #cbd5e1}h1{margin-top:0;color:#1769aa}"
                + "textarea{box-sizing:border-box;width:100%;height:220px;padding:12px;border:1px solid #b8c4d1;"
                + "border-radius:6px;font:14px monospace}button{padding:11px 22px;background:#1769aa;"
                + "color:white;border:0;border-radius:6px;font-weight:bold;cursor:pointer}.stats{display:grid;"
                + "grid-template-columns:repeat(4,1fr);gap:10px}.card{padding:14px;border-radius:8px;"
                + "border-left:5px solid}.card strong,.card span{display:block}.card strong{font-size:24px}.card span{"
                + "font-size:11px;margin-top:5px;color:#526273}.blue{border-color:#1769aa;background:#e8f2fb}.gold{"
                + "border-color:#d49a00;background:#fff7db}.red{border-color:#c62828;background:#ffebeb}.dark{"
                + "border-color:#172033;background:#e9edf2}@media(max-width:600px){.stats{grid-template-columns:"
                + "repeat(2,1fr)}}h2{margin-bottom:12px}</style></head><body><div class='box'>"
                + "<h1>Java Log Monitor</h1><p>Choose a log file or paste log text below:</p>"
                + "<input type='file' id='file' accept='.log,.txt'><br><br>"
                + "<form method='post' action='/analyze'><textarea name='log'>"
                + escape(log) + "</textarea><br><br><button>Analyze Log</button></form>"
                + result + "</div><script>document.getElementById('file').onchange=function(){"
                + "var reader=new FileReader();reader.onload=function(){document.querySelector('textarea').value="
                + "reader.result};reader.readAsText(this.files[0])}</script></body></html>";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void send(HttpExchange exchange, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
