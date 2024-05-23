package com.example.healthcareapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class paymentGateway extends AppCompatActivity {

    Button btnPay;
    String SECRET_KEY = "sk_test_51FrObCGhLCFS7Di8NEpHq1ojb8z3qjlJriKl3OzwwyteyWnHhgBp4VKLfLeMBMojjJqfjkiwhPIcgNyQZHeODfkP00VkCD2iWx";
    String PUBLISH_KEY = "pk_test_BF46po9sgJREvSmwrqXgFbDm00LvdBac2Y";
    String customerID, EphericalKey, ClientSecret;
    PaymentSheet paymentSheet;
    final LoadingDialog loading = new LoadingDialog(paymentGateway.this);
    String ringgit = "";
    String sen = "";
    TextView txtPayAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        ringgit = getIntent().getStringExtra("ringgit");
        sen = getIntent().getStringExtra("sen");
        btnPay = findViewById(R.id.btnPay);
        txtPayAmt = findViewById(R.id.txtPayAmt);
        btnPay.setEnabled(false);//btnCall is only available when "customerID", "EphericalKey", "ClientSecret" is extracted
        txtPayAmt.setText("Pay RM" + ringgit + "."+sen + " to HealthCare Malaysia Sdn Bhd");
        init();
    }

    public void init(){
        PaymentConfiguration.init(paymentGateway.this, PUBLISH_KEY);
        paymentSheet=new PaymentSheet(paymentGateway.this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(ringgit+sen) >= 200)
                    paymentFlow();
                else
                    ToastUtils.showToast(paymentGateway.this, "Error: minimum payment amount is RM 2", Toast.LENGTH_SHORT);
            }
        });

        loading.startLoadingDialog();//loading until "customerID", "EphericalKey", "ClientSecret" is extracted.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            getEphericalKey(customerID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(paymentGateway.this);
        requestQueue.add(stringRequest);
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            //payment success
            // Set the result as OK and finish the activity
            setResult(RESULT_OK);
            finish();
        }else {
            //payment cancelled by user / failed
            // Set the result as CANCELED and finish the activity
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void getEphericalKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            getClientSecret(customerID, EphericalKey);
                            loading.dismissDialog();
                            btnPay.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                header.put("Stripe-Version","2019-12-03");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(paymentGateway.this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret  = object.getString("client_secret");
                            loading.dismissDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", ringgit+sen);
                params.put("currency", "myr");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(paymentGateway.this);
        requestQueue.add(stringRequest);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration("HealthCare Malaysia SDN BHD",
                        new PaymentSheet.CustomerConfiguration(
                                customerID,
                                EphericalKey
                        ))
        );
    }
}