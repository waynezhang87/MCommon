package com.waynezhang.mcommon.util;

import android.content.Context;

import com.google.gson.Gson;
import com.waynezhang.mcommon.network.HttpPolicies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by waynezhang on 4/30/15.
 */
public class ResourceUtil {
    public static HashMap<String, String> getExpirePoliciesFromRaw(Context context) {
        HashMap<String, String> expireMap = new HashMap();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getResources().openRawResource(ResourceHelper.getId(context, "R.raw.expireMap")));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] tmp = line.split(",");
                expireMap.put(tmp[0], tmp[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expireMap;
    }

    public static HttpPolicies getPoliciesFromRaw(Context context) throws IOException {
        StringBuffer strBuff = new StringBuffer();
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStreamReader = new InputStreamReader(context.getResources().openRawResource(ResourceHelper.getId(context, "R.raw.expire_map")));
            reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                strBuff.append(line);
            }
        } finally {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        Gson gson = new Gson();
        HttpPolicies httpPolicies = gson.fromJson(strBuff.toString(), HttpPolicies.class);
        return httpPolicies;
    }

}
