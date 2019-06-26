package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private final static int LOCATION_REQ_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
    }

    public void AEDFindButtonClicked(View v){
        AEDRequest myAedRequest = new AEDRequest();
        getAEDdataFromAPIandSet(findMyLocation(),myAedRequest);
        //Intent intent = new Intent(this, 어딘가로);
        //intent.putExtra("AEDRequest",myAedRequest); //Parceble로 바꾸자
        //startActivity(intent);
    }


    public void getLocationPermission(){
        //위치권한 수용 요구하기
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(check == PackageManager.PERMISSION_GRANTED) return; //수신 권한 이미 존재
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(getApplicationContext(),"위치 권한을 허락해야 서비스 이용이 가능합니다.", Toast.LENGTH_LONG);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==LOCATION_REQ_CODE){
            if(grantResults!=null && grantResults.length!=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Log.d("Permission","사용자가 권한 승인");
            else Log.d("permission","사용자가 권한 거부");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //현재 위치와 AEDRequest객체를 생성해서 넣어주면, 제일 가까운 AED의 정보를 AEDRequest에 넣어주는 메서드
    public void getAEDdataFromAPIandSet (final Location myLocation, final AEDRequest myAedRequest) {
        //AED API로 콜해서 넣어서 보내기...
        String url = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?"
                + "ServiceKey=h81QdjEyCaCY33uMnxkCku8XkhtY%2FZcgPxudUDzFlE7YCC%2BcUTm%2F1gBnVx9oz44IPUyteI8akUb8gQIuEwhbqg%3D%3D"
                + "&WGS84_LON=" + myLocation.getLongitude() + "&WGS84_LAT=" + myLocation.getLatitude() + "&pageNum=1&numOfRows=1";

        queue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = new String(response.getBytes("ISO-8859-1"),"utf-8");

                            InputSource is = new InputSource(new StringReader(response));
                            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = myFactory.newDocumentBuilder();
                            Document document = documentBuilder.parse(is);

                            Element root = document.getDocumentElement();
                            NodeList myNL = root.getChildNodes(); //헤더랑 바디
                            Element body = (Element)myNL.item(1);
                            myNL = ((Element)body).getChildNodes();
                            Element items = (Element)myNL.item(0);
                            myNL = items.getChildNodes();
                            Element item = (Element)myNL.item(0);
                            myNL = item.getChildNodes();

                            myAedRequest.setAed_address(myNL.item(12).getTextContent()+" "+
                                    myNL.item(5).getTextContent() + " " + myNL.item(0).getTextContent()
                                    + " " + myNL.item(10).getTextContent() + " " + myNL.item(1).getTextContent());

                            Location aed_location = new Location(myLocation);
                            aed_location.setLatitude(Double.parseDouble(myNL.item(13).getTextContent()));
                            aed_location.setLongitude(Double.parseDouble(myNL.item(14).getTextContent()));
                            myAedRequest.setAed_location(aed_location);

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
                (new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    //자신의 현재 위치를 파악하는 메서드 입니다.
    public Location findMyLocation(){
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            getLocationPermission();
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return myLocation;
        }
        else{
            Toast.makeText(getApplicationContext(),"먼저 위치 권한을 확인해주세요",Toast.LENGTH_LONG).show();
            return null;
        }
    }



}
