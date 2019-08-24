package com.example.pc.caseproject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AEDUtil {
    private static RequestQueue queue;
    public static APIListener myListener;
    private final static String []aed_info_name =
            {"sido", "gugun", "buildAddress", "org", "buildPlace", "wgs84Lon", "wgs84Lat", "distance"};

    public interface APIListener {
        void update();
    }

    public static void getAEDData(final Context context, final Location myLocation, final AED_FIND_REQUEST aed_find_request, final APIListener myListener, final boolean sos) {
//        AEDUtil.myListener = myListener;
//        String url = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?"
//                + "ServiceKey=h81QdjEyCaCY33uMnxkCku8XkhtY%2FZcgPxudUDzFlE7YCC%2BcUTm%2F1gBnVx9oz44IPUyteI8akUb8gQIuEwhbqg%3D%3D"
//                + "&WGS84_LON=" + myLocation.getLongitude() + "&WGS84_LAT=" + myLocation.getLatitude() + "&pageNum=1&numOfRows=1";
//
//        queue = Volley.newRequestQueue(context);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            response = new String(response.getBytes("ISO-8859-1"), "utf-8");
//                            //((TextView)findViewById(R.id.textView)).setText(response);
//
//                            InputSource is = new InputSource(new StringReader(response));
//                            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
//                            DocumentBuilder documentBuilder = myFactory.newDocumentBuilder();
//                            Document document = documentBuilder.parse(is);
//
//                            Element root = document.getDocumentElement();
//                            NodeList nl = root.getChildNodes();
//                            Element body = (Element) nl.item(1);
//                            nl = body.getChildNodes();
//                            Element items = (Element)nl.item(0);
//                            nl = items.getChildNodes();
//                            Element item = (Element)nl.item(0);
//                            nl =item.getChildNodes();
//
//                            String []AED_data = new String[8];
//                            for (int i=0; i<nl.getLength(); i++){
//                                Element data = (Element)nl.item(i);
//                                String dataName = data.getNodeName();
//                                for(int j=0; j<aed_info_name.length; j++){
//                                    if (dataName.equals(aed_info_name[j])){
//                                        AED_data[j]=data.getTextContent();
//                                        break;
//                                    }
//                                }
//                            }
//
//                            //주소는 sido + gugun + buildAddress + org + buildPlace
//                            String aed_address = "";
//                            for(int i=0;i<5;i++) {
//                                if (AED_data[i]==null) continue;
//                                aed_address+=(AED_data[i]+" ");
//                            }
//                            aed_find_request.setAedAddress(aed_address);
//
//                            aed_find_request.setAEDLongitude(Double.parseDouble(AED_data[5]));
//                            aed_find_request.setAedLatitude(Double.parseDouble(AED_data[6]));
//
//                            myListener.update();
//                            if (sos==true) requestPush(context, aed_find_request);
//
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                            Log.d("aed_api", "data parsing 실패" + e1.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("aed_api", "aed 정보 받아오기 실패" + error.getMessage());
//            }
//        });
//        stringRequest.setRetryPolicy
//                (new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(stringRequest);
        aed_find_request.setAedAddress("Fuming Residential District Fuming Homeland Community Service Station");
        aed_find_request.setAEDLongitude(121.5987163);
        aed_find_request.setAedLatitude(29.87487);

        myListener.update();
        if (sos==true) requestPush(context, aed_find_request);
    }

    public static void sendAccept(Context context, String user_token) {
        JSONObject acceptData = new JSONObject();
        try {
            acceptData.put("priority", "high");
            acceptData.put("to", user_token);

            JSONObject dataObj = new JSONObject();
            dataObj.put("type", "accept");
            dataObj.put("sender-token", FirebaseInstanceId.getInstance().getToken());
            acceptData.put("data", dataObj);

            sendSOS(acceptData, new SendResponseListener() {
                @Override
                public void onRequestCompleted() {
                    Log.d("sendAccept", "onRequestCompleted() 호출됨.");
                    //addToDB(); 요청 완료되면 DB에 추가
                }

                @Override
                public void onRequestStarted() {
                    //Log.d("sendSOS","onRequestStarted() 호출됨.");
                }

                @Override
                public void onRequestWithError(VolleyError error) {
                    Log.d("sendAccept", "onRequestWithError() 호출됨.");
                    // Toast.makeText(context,"요청 실패되었습니다",Toast.LENGTH_LONG);
                }
            }, context);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("accept", "accept 메시지 보내는 과정에서 문제 발생");
        }
    }

    public static void requestPush(Context context, AED_FIND_REQUEST myAEdRequest) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority", "high");
            requestData.put("to", "/topics/all");

            JSONObject dataObj = new JSONObject();
            dataObj.put("sender-token", FirebaseInstanceId.getInstance().getToken());

            //위,경도 주소로 변환
            Geocoder mGeoCoder = new Geocoder(context, Locale.ENGLISH);
            List<Address> address;
            String nowAddress = "";
            try {
                if (mGeoCoder != null) {
                    address = mGeoCoder.getFromLocation(myAEdRequest.getMyLatitude(), myAEdRequest.getMyLongitude(), 1);
                    if (address != null && address.size() > 0) {
                        String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                        nowAddress = currentLocationAddress;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            dataObj.put("type", "sos");
            dataObj.put("sender_address", nowAddress);
            dataObj.put("sender_latitude", myAEdRequest.getMyLatitude());
            dataObj.put("sender_longitude", myAEdRequest.getMyLongitude());
            long now = System.currentTimeMillis();
            dataObj.put("date", (new Date(now)).toString());
            dataObj.put("aed_address", myAEdRequest.getAedAddress());
            dataObj.put("aed_latitude", myAEdRequest.getAedLatitude());
            dataObj.put("aed_longitude", myAEdRequest.getAEDLongitude());
            requestData.put("data", dataObj);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage() + "send 실패", Toast.LENGTH_LONG).show();
        }

        sendSOS(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                Log.d("sendSOS", "onRequestCompleted() 호출됨.");
                ////Toast.makeText(context,"요청이 완료되었습니다",Toast.LENGTH_LONG);
                //addToDB(); 요청 완료되면 DB에 추가
            }

            @Override
            public void onRequestStarted() {
                //Log.d("sendSOS","onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Log.d("sendSOS", "onRequestWithError() 호출됨.");
                // Toast.makeText(context,"요청 실패되었습니다",Toast.LENGTH_LONG);
            }
        }, context);
    }

    public interface SendResponseListener {
        void onRequestStarted();

        void onRequestCompleted();

        void onRequestWithError(VolleyError error);
    }

    public static void sendSOS(JSONObject requestData, final SendResponseListener listener, Context context) {
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
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
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
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
