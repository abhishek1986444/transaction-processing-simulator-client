package com.example.version1.client;

import com.example.version1.client.helpermodule.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

import java.sql.*;




public class SimpleClient {

    private static final String BASE_URL =
            "https://localhost:9090";

    private static final HttpClient client;

    private static final ObjectMapper mapper =
            new ObjectMapper();

    private static String token;
    private static String username;


    // =====================================================
    // HTTPS CLIENT
    // =====================================================

    static {

        try {

            TrustManager[] trustAll =
                    new TrustManager[] {

                        new X509TrustManager() {

                            @Override
                            public X509Certificate[]
                            getAcceptedIssuers() {

                                return new X509Certificate[0];
                            }


                            @Override
                            public void checkClientTrusted(
                                    X509Certificate[] certs,
                                    String authType) {
                            }


                            @Override
                            public void checkServerTrusted(
                                    X509Certificate[] certs,
                                    String authType) {
                            }
                        }
                    };


            SSLContext sslContext =
                    SSLContext.getInstance("TLS");


            sslContext.init(
                    null,
                    trustAll,
                    new java.security.SecureRandom()
            );


            client =
                    HttpClient.newBuilder()
                            .sslContext(sslContext)
                            .build();

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create HTTPS client",
                    e
            );
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        Scanner scanner =
                new Scanner(System.in);


        System.out.println("================================");
        System.out.println("       BANKING CLIENT");
        System.out.println("================================");


        // =================================================
        // LOGIN DETAILS
        // =================================================

        System.out.print("Username: ");

        username =
                scanner.nextLine();


        System.out.print("Password: ");

        String password =
                scanner.nextLine();


        try {

            // =================================================
            // CREATE SQLITE TABLE
            // =================================================

            DatabaseManager.createTables();


            // =================================================
            // LOGIN
            // =================================================

            ObjectNode loginJson =
                    mapper.createObjectNode();


            loginJson.put(
                    "username",
                    username
            );


            loginJson.put(
                    "password",
                    password
            );


            String loginResponse =
                    post(
                            "/login",
                            loginJson.toString(),
                            null,
                            null,
                            null
                    );


            System.out.println();
            System.out.println("Login response:");
            System.out.println(loginResponse);


            JsonNode loginNode =
                    mapper.readTree(
                            loginResponse
                    );


            if (!"accepted".equals(
                    loginNode
                            .path("response")
                            .asText())) {

                System.out.println(
                        "Login failed."
                );

                return;
            }


            // =================================================
            // OTP
            // =================================================

            System.out.println();

            System.out.println(
                    "OTP has been sent to your email."
            );


            System.out.print(
                    "Enter OTP: "
            );


            String otp =
                    scanner.nextLine();


            ObjectNode otpJson =
                    mapper.createObjectNode();


            otpJson.put(
                    "username",
                    username
            );


            otpJson.put(
                    "otp",
                    otp
            );


            String otpResponse =
                    post(
                            "/login/verify-otp",
                            otpJson.toString(),
                            null,
                            null,
                            null
                    );


            System.out.println();

            System.out.println(
                    "OTP response:"
            );

            System.out.println(
                    otpResponse
            );


            JsonNode otpNode =
                    mapper.readTree(
                            otpResponse
                    );


            if (!"success".equals(
                    otpNode
                            .path("response")
                            .asText())) {

                System.out.println(
                        "OTP verification failed."
                );

                return;
            }


            // =================================================
            // GET JWT
            // =================================================

            token =
                    otpNode
                            .path("message")
                            .asText();


            if (token == null ||
                    token.isEmpty()) {

                System.out.println(
                        "JWT was not received."
                );

                return;
            }


            System.out.println();

            System.out.println(
                    "Login successful."
            );

            System.out.println(
                    "JWT received."
            );


            // =================================================
            // LOGIN STATUS
            // =================================================

            checkLoginStatus();


            // =================================================
            // USER MENU
            // =================================================

            userMenu(scanner);


        }
        catch (Exception e) {

            e.printStackTrace();

        }
        finally {

            scanner.close();
        }
    }


    // =====================================================
    // GENERIC POST
    // =====================================================

    private static String post(
            String path,
            String json,
            String bearerToken,
            String usernameHeader,
            String requestId)
            throws Exception {


        HttpRequest.Builder builder =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL + path
                                )
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest
                                        .BodyPublishers
                                        .ofString(json)
                        );


        // =================================================
        // JWT
        // =================================================

        if (bearerToken != null) {

            builder.header(
                    "Authorization",
                    "Bearer " + bearerToken
            );
        }


        // =================================================
        // USERNAME
        // =================================================

        if (usernameHeader != null) {

            builder.header(
                    "username",
                    usernameHeader
            );
        }


        // =================================================
        // REQUEST ID
        // =================================================

        if (requestId != null) {

            builder.header(
                    "request_id",
                    requestId
            );
        }


        HttpRequest request =
                builder.build();


        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse
                                .BodyHandlers
                                .ofString()
                );


        System.out.println(
                "HTTP Status: "
                        + response.statusCode()
        );


        return response.body();
    }


    // =====================================================
    // LOGIN STATUS
    // =====================================================

    private static void checkLoginStatus()
            throws Exception {


        String response =
                post(
                        "/login/status",
                        "",
                        token,
                        username,
                        null
                );


        System.out.println();

        System.out.println(
                "Login status:"
        );

        System.out.println(
                response
        );
    }


    // =====================================================
    // USER MENU
    // =====================================================

    private static void userMenu(
            Scanner scanner)
            throws Exception {


        while (true) {

            System.out.println();

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "          USER MENU"
            );

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "1. Make Payment"
            );

            System.out.println(
                    "2. Transaction History"
            );

            System.out.println(
                    "3. Check Transaction Record"
            );

            System.out.println(
                    "4. Logout"
            );


            System.out.print(
                    "Enter choice: "
            );


            String choice =
                    scanner.nextLine();


            switch (choice) {


                // =================================================
                // MAKE PAYMENT
                // =================================================

                case "1":

                    String requestId =
                            makePayment(scanner);


                    if (requestId != null) {

                        System.out.println();

                        System.out.println(
                                "Waiting for payment processing..."
                        );


                        Thread.sleep(3000);


                        System.out.println();

                        System.out.println(
                                "Checking payment status..."
                        );


                        paymentStatus(
                                requestId
                        );
                    }

                    break;


                // =================================================
                // TRANSACTION HISTORY
                // =================================================

                case "2":

                    DatabaseManager
                            .showTransactionHistory();

                    break;


                // =================================================
                // CHECK TRANSACTION RECORD
                // =================================================

                case "3":

                    System.out.print(
                            "Enter Request ID: "
                    );


                    String requestIdToCheck =
                            scanner.nextLine();


                    if (DatabaseManager
                            .transactionExists(
                                    requestIdToCheck
                            )) {

                        System.out.println();

                        System.out.println(
                                "Record EXISTS in local SQLite database."
                        );


                        DatabaseManager
                                .showTransaction(
                                        requestIdToCheck
                                );

                    }
                    else {

                        System.out.println();

                        System.out.println(
                                "Record DOES NOT EXIST in local SQLite database."
                        );
                    }

                    break;


                // =================================================
                // LOGOUT
                // =================================================

                case "4":

                    logout();

                    return;


                default:

                    System.out.println();

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }


    // =====================================================
    // MAKE PAYMENT
    // =====================================================

    private static String makePayment(
            Scanner scanner)
            throws Exception {


        System.out.println();

        System.out.println(
                "========== MAKE PAYMENT =========="
        );


        System.out.print(
                "Enter receiver username: "
        );


        String toUsername =
                scanner.nextLine();


        System.out.print(
                "Enter amount: "
        );


        double amount =
                Double.parseDouble(
                        scanner.nextLine()
                );


        System.out.print(
                "Enter payment password: "
        );


        String paymentPassword =
                scanner.nextLine();


        System.out.print(
                "Enter your account ID: "
        );


        String fromAccount =
                scanner.nextLine();


        System.out.print(
                "Enter receiver account ID: "
        );


        String toAccount =
                scanner.nextLine();


        // =================================================
        // PAYMENT JSON
        // =================================================

        ObjectNode payment =
                mapper.createObjectNode();


        payment.put(
                "fromUsername",
                username
        );


        payment.put(
                "paymentPassword",
                paymentPassword
        );


        payment.put(
                "toUsername",
                toUsername
        );


        payment.put(
                "fromAccount",
                fromAccount
        );


        payment.put(
                "toAccount",
                toAccount
        );


        payment.put(
                "amount",
                amount
        );


        // =================================================
        // SEND PAYMENT
        // =================================================

        String response =
                post(
                        "/payment/pay",
                        payment.toString(),
                        token,
                        null,
                        null
                );


        System.out.println();

        System.out.println(
                "Payment response:"
        );

        System.out.println(
                response
        );


        JsonNode node =
                mapper.readTree(
                        response
                );


        // =================================================
        // CHECK SERVER RESPONSE
        // =================================================

        if (!"success".equals(
                node
                        .path("response")
                        .asText())) {

            System.out.println();

            System.out.println(
                    "Payment request failed."
            );

            return null;
        }


        // =================================================
        // GET REQUEST ID
        // =================================================

        String requestId =
                node
                        .path("requestId")
                        .asText();


        if (requestId == null ||
                requestId.isEmpty()) {

            System.out.println();

            System.out.println(
                    "Request ID was not received."
            );

            return null;
        }


        String initialStatus =
                node
                        .path("status")
                        .asText();


        System.out.println();

        System.out.println(
                "Request ID: "
                        + requestId
        );


        System.out.println(
                "Initial status: "
                        + initialStatus
        );


        // =================================================
        // SAVE TRANSACTION TO SQLITE
        // =================================================

        DatabaseManager.saveTransaction(
                requestId,
                username,
                toUsername,
                fromAccount,
                toAccount,
                amount,
                initialStatus
        );


        System.out.println();

        System.out.println(
                "Transaction saved locally."
        );


        return requestId;
    }


    // =====================================================
    // PAYMENT STATUS
    // =====================================================

    private static void paymentStatus(
            String requestId)
            throws Exception {


        String response =
                post(
                        "/payment/status",
                        "",
                        token,
                        username,
                        requestId
                );


        System.out.println();

        System.out.println(
                "Payment status response:"
        );

        System.out.println(
                response
        );


        // =================================================
        // PARSE RESPONSE
        // =================================================

        JsonNode root =
                mapper.readTree(
                        response
                );


        // =================================================
        // CHECK SERVER RESPONSE
        // =================================================

        if (!"success".equals(
                root
                        .path("response")
                        .asText())) {

            System.out.println();

            System.out.println(
                    "Could not retrieve payment status."
            );

            return;
        }


        // =================================================
        // GET TRANSACTION DATA
        // =================================================

        JsonNode data =
                root.path("data");


        String status =
                data
                        .path("status")
                        .asText();


        String transactionId =
                data
                        .path("transactionId")
                        .asText(null);


        String reason =
                data
                        .path("reason")
                        .asText(null);


        // =================================================
        // UPDATE SQLITE
        // =================================================

        DatabaseManager.updateTransactionStatus(
                requestId,
                transactionId,
                status,
                reason
        );


        System.out.println();

        System.out.println(
                "Transaction status updated locally."
        );
    }


    // =====================================================
    // LOGOUT
    // =====================================================

    private static void logout()
            throws Exception {


        String response =
                post(
                        "/logout",
                        "",
                        token,
                        username,
                        null
                );


        System.out.println();

        System.out.println(
                "Logout response:"
        );

        System.out.println(
                response
        );


        JsonNode node =
                mapper.readTree(
                        response
                );


        if ("success".equals(
                node
                        .path("response")
                        .asText())) {

            token = null;


            System.out.println();

            System.out.println(
                    "Logout successful."
            );
        }
        else {

            System.out.println();

            System.out.println(
                    "Logout failed."
            );
        }
    }
}