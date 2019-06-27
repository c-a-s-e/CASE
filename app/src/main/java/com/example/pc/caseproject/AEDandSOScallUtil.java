package com.example.pc.caseproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AEDandSOScallUtil {
    private static RequestQueue queue;

    public static void getAEDdataFromAPI(final Context context, final Location myLocation, final AED_FIND_REQUEST aed_find_request,
                                                final boolean isSendPush, final boolean isNewActivity) {
        //AED API로 콜해서 넣어서 보내기...
        String url = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?"
                + "ServiceKey=h81QdjEyCaCY33uMnxkCku8XkhtY%2FZcgPxudUDzFlE7YCC%2BcUTm%2F1gBnVx9oz44IPUyteI8akUb8gQIuEwhbqg%3D%3D"
                + "&WGS84_LON=" + myLocation.getLongitude() + "&WGS84_LAT=" + myLocation.getLatitude() + "&pageNum=1&numOfRows=1";

        queue= Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = new String(response.getBytes("ISO-8859-1"),"utf-8");
                            //((TextView)findViewById(R.id.textView)).setText(response);

                            InputSource is = new InputSource(new StringReader(response));
                            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = myFactory.newDocumentBuilder();
                            Document document = documentBuilder.parse(is);

                            Element root = document.getDocumentElement();
                            NodeList myNL = root.getChildNodes(); //헤더랑 바디
                            Element body = (Element)myNL.item(1);
                            myNL = (body).getChildNodes();
                            Element items = (Element)myNL.item(0);
                            myNL = items.getChildNodes();
                            Element item = (Element)myNL.item(0);
                            myNL = item.getChildNodes();

                            aed_find_request.setAedAddress(myNL.item(12).getTextContent()+" "+
                                    myNL.item(5).getTextContent() + " " + myNL.item(0).getTextContent()
                                    + " " + myNL.item(10).getTextContent() + " " + myNL.item(1).getTextContent());
                            aed_find_request.setAedLatitude(Double.parseDouble(myNL.item(13).getTextContent()));
                            aed_find_request.setAedLongtitude(Double.parseDouble(myNL.item(14).getTextContent()));

                            if(isNewActivity) {
                                Toast.makeText(context, "이제 새 액티비티로 넘어갑니다", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, NearAEDActivity.class);
                                intent.putExtra("AED_find_request", aed_find_request);
                                requestPush(context,aed_find_request);
                                ((Activity)context).startActivity(intent);
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Log.d("parsing", "파싱 중 예외 발생"+e1.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("aed 정보","aed 정보 받아오는 중 오류 발생"+error.getMessage());
            }
        });
        stringRequest.setRetryPolicy
                (new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public static void requestPush(Context context, AED_FIND_REQUEST myAEdRequest){
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority","high");
            requestData.put("to","/topics/all");

            JSONObject dataObj = new JSONObject();
            dataObj.put("sender-token", FirebaseInstanceId.getInstance().getToken());
            dataObj.put("sender_address","내 주소");
            dataObj.put("sender_latitude",myAEdRequest.getMyLatitude());
            dataObj.put("sender_longitude",myAEdRequest.getMyLongtitiude());
            dataObj.put("date","일단 날짜");
            dataObj.put("aed_address",myAEdRequest.getAedAddress());
            dataObj.put("aed_latitude",myAEdRequest.getAedLatitude());
            dataObj.put("aed_longitude",myAEdRequest.getAedLongtitude());
            requestData.put("data",dataObj);
        }
        catch(Exception e){
            Toast.makeText(context,e.getMessage()+"send 실패", Toast.LENGTH_LONG).show();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                Log.d("sendData","onRequestCompleted() 호출됨.");
                ////Toast.makeText(context,"요청이 완료되었습니다",Toast.LENGTH_LONG);
                //addToDB(); 요청 완료되면 DB에 추가
            }

            @Override
            public void onRequestStarted() {
                //Log.d("sendData","onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Log.d("sendData","onRequestWithError() 호출됨.");
               // Toast.makeText(context,"요청 실패되었습니다",Toast.LENGTH_LONG);
            }
        });
    }

    public interface SendResponseListener {
        void onRequestStarted();
        void onRequestCompleted();
        void onRequestWithError(VolleyError error);
    }

    public static void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",
                        "key=AAAAJpBDuCc:APA91bGkulRX5N7mahz_o6E6PGKXPrSAsfpr6QmnMEmX8SbiVS8XX7ChCT1737SvKzuShTvirLtDoGAMlt3bNAKWJ8xW4m0ntdaCYzxJ7ohxl3AavuIP2t2HsmTaQRCCIVHJaouASrPB");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }
}
