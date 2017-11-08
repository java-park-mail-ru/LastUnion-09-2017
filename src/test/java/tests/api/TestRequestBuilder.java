package tests.api;


import net.minidev.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;

class TestRequestBuilder {
    private final ArrayList<String> jsonKeys;

    TestRequestBuilder() {
        jsonKeys = new ArrayList<>();
    }

    void init(String... keys) {
        Collections.addAll(jsonKeys, keys);
    }

    static String getJsonRequestForSignUp(@NotNull String uName, @NotNull String uPassword, @NotNull String uEmail) {
        final JSONObject jso = new JSONObject();
        jso.put("userName", uName);
        jso.put("userPassword", uPassword);
        jso.put("userEmail", uEmail);
        return jso.toString();
    }

    String getJsonRequest(String... values) {
        final JSONObject jso = new JSONObject();

        if (values == null) {
            jso.put(jsonKeys.get(0), values);
        } else {
            for (int i = 0; i < jsonKeys.size(); i++) {
                jso.put(jsonKeys.get(i), values[i]);
            }
        }
        return jso.toString();
    }
}