package com.customerapp.server;

import com.customerapp.dao.CustomerDAO;
import com.customerapp.model.Customer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Handles all /api/customers requests: list, search, add, update, delete,
 * and a summary report endpoint.
 */
public class ApiHandler implements HttpHandler {

    private final CustomerDAO dao = new CustomerDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        try {
            if (path.equals("/api/customers") && method.equals("GET")) {
                handleListOrSearch(exchange, query);
            } else if (path.equals("/api/customers") && method.equals("POST")) {
                handleAdd(exchange);
            } else if (path.matches("/api/customers/\\d+") && method.equals("PUT")) {
                int id = extractId(path);
                handleUpdate(exchange, id);
            } else if (path.matches("/api/customers/\\d+") && method.equals("DELETE")) {
                int id = extractId(path);
                handleDelete(exchange, id);
            } else if (path.equals("/api/customers/report") && method.equals("GET")) {
                handleReport(exchange);
            } else {
                sendJson(exchange, 404, "{\"error\":\"Not found\"}");
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"error\":\"Database error: " + escape(e.getMessage()) + "\"}");
        } catch (Exception e) {
            sendJson(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private void handleListOrSearch(HttpExchange exchange, String query) throws SQLException, IOException {
        List<Customer> customers;
        String keyword = extractQueryParam(query, "q");
        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = dao.searchCustomers(keyword);
        } else {
            customers = dao.getAllCustomers();
        }
        sendJson(exchange, 200, toJsonArray(customers));
    }

    private void handleAdd(HttpExchange exchange) throws IOException, SQLException {
        String body = readBody(exchange);
        Map<String, String> fields = JsonUtil.parseFlatObject(body);
        Customer c = customerFromFields(fields);
        int newId = dao.addCustomer(c);
        c.setId(newId);
        sendJson(exchange, 201, c.toJson());
    }

    private void handleUpdate(HttpExchange exchange, int id) throws IOException, SQLException {
        String body = readBody(exchange);
        Map<String, String> fields = JsonUtil.parseFlatObject(body);
        Customer c = customerFromFields(fields);
        c.setId(id);
        boolean updated = dao.updateCustomer(c);
        if (updated) {
            sendJson(exchange, 200, c.toJson());
        } else {
            sendJson(exchange, 404, "{\"error\":\"Customer not found\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException, SQLException {
        boolean deleted = dao.deleteCustomer(id);
        if (deleted) {
            sendJson(exchange, 200, "{\"deleted\":true}");
        } else {
            sendJson(exchange, 404, "{\"error\":\"Customer not found\"}");
        }
    }

    private void handleReport(HttpExchange exchange) throws SQLException, IOException {
        sendJson(exchange, 200, dao.getSummaryReportJson());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Customer customerFromFields(Map<String, String> f) {
        Customer c = new Customer();
        c.setFirstName(f.get("firstName"));
        c.setLastName(f.get("lastName"));
        c.setEmail(f.get("email"));
        c.setPhone(f.get("phone"));
        c.setAddress(f.get("address"));
        c.setCity(f.get("city"));
        c.setStatus(f.getOrDefault("status", "Active"));
        return c;
    }

    private String toJsonArray(List<Customer> customers) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < customers.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(customers.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    private String extractQueryParam(String query, String key) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "'");
    }
}
