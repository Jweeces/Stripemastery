package com.stripe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public final class ExampleStripeJava8OAuth {
    public static final String AUTHORIZE_URI
        = "https://connect.stripe.com/oauth/authorize";
    public static final String TOKEN_URI
        = "https://connect.stripe.com/oauth/token";

    private ExampleStripeJava8OAuth() { }

    public static void main(final String[] args) throws IOException {
        Map<String, String> env = System.getenv();

        // Retrieve Stripe platform's client ID and secret API key
        final String clientId = env.get("ca_DPZjtortTrayxu2lalBUbvpk0VLDM18j");
        final String apiKey = env.get("sk_test_uFgCJ2UxnOlE03X6cU22LqCs");

        // Set the webserver's port, if necessary
        if (env.containsKey("PORT")) {
            port(Integer.parseInt(env.get("PORT")));
        }

        // Path to static files
        staticFiles.location("/public");

        get("/", (request, response) -> {
            // Simply display the index.ftl template
            Map<String, Object> viewObjects
                = new HashMap<String, Object>();
            return new ModelAndView(viewObjects, "index.ftl");
        }, new FreeMarkerEngine());

        get("/authorize", (request, response) -> {
            try {
                URI uri = new URIBuilder(AUTHORIZE_URI)
                        .setParameter("response_type", "code")
                        .setParameter("scope", "read_write")
                        .setParameter("client_id", clientId)
                        .build();

                // Redirect to Stripe /oauth/authorize endpoint
                response.status(HttpStatus.SC_CREATED);
                response.redirect(uri.toString());
            } catch (Exception e) {
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            return "";
        });

        get("/oauth/callback", (request, response) -> {
            Map<String, Object> viewObjects
                = new HashMap<String, Object>();

            try {
                CloseableHttpClient httpClient
                    = HttpClients.createDefault();
                String code = request.queryParams("code");
                URI uri = new URIBuilder(TOKEN_URI)
                        .setParameter("client_secret", apiKey)
                        .setParameter("grant_type", "authorization_code")
                        .setParameter("client_id", clientId)
                        .setParameter("code", code)
                        .build();

                // Make /oauth/token endpoint POST request
                HttpPost httpPost = new HttpPost(uri);
                CloseableHttpResponse resp = httpClient.execute(httpPost);

                // Grab stripe_user_id (use this to authenticate as the
                // connected account)
                String bodyAsString
                    = EntityUtils.toString(resp.getEntity());
                Type t = new TypeToken<Map<String, String>>() { }.getType();
                Map<String, String> map
                    = new GsonBuilder().create().fromJson(bodyAsString, t);
                String accountId = map.get("stripe_user_id");

                viewObjects.put("account_id", accountId);
                viewObjects.put("raw_body", bodyAsString);

                return new ModelAndView(viewObjects, "callback.ftl");
            } catch (Exception e) {
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                viewObjects.put("error", e.getMessage());
                return new ModelAndView(viewObjects, "error.ftl");
            }
        }, new FreeMarkerEngine());
    }
}
