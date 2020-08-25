package com.kris.docker_spring_boot.j2ee.json;

import java.io.*;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonTest {

    /**
     * 读取json文件，返回json串
     * 
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String path = Objects.requireNonNull(JsonTest.class.getClassLoader().getResource("qishi.txt")).getPath();
        String s = readJsonFile(path);
        System.out.println(s);
        JSONObject jobj = JSON.parseObject(s);
        System.out.println(jobj);
        // System.out.println("name" + jobj.get("name"));
        // JSONObject address1 = jobj.getJSONObject("address");
        // String street = (String) address1.get("street");
        // String city = (String) address1.get("city");
        // String country = (String) address1.get("country");

        // System.out.println("street :" + street);
        // System.out.println("city :" + city);
        // System.out.println("country :" + country);

        // JSONArray links = jobj.getJSONArray("links");
        //
        // for (Object link : links) {
        // JSONObject key1 = (JSONObject) link;
        // String name = (String) key1.get("name");
        // String url = (String) key1.get("url");
        // System.out.println(name);
        // System.out.println(url);
        // }
    }
}
