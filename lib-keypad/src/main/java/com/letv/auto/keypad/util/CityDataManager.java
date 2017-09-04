package com.letv.auto.keypad.util;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CityDataManager {
    private static final String TAG = "CityDataManager";

    private ArrayList<String> mProvinceList = new ArrayList<String>(); //province list
    private HashMap<String, ArrayList<String>> mCitiesMap = new HashMap<String, ArrayList<String>>();      //province : city list
    private HashMap<String, ArrayList<String>> mCountriesMap = new HashMap<String, ArrayList<String>>();   //province_city : country list


    private static final String SEP_PROVINCE_CITY = "_";
    //json keys
    private static final String KEY_COUNT = "cnt";
    private static final String KEY_PROVINCES = "provinces";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_CITIES = "cities";
    private static final String KEY_CITY = "city";
    private static final String KEY_COUNTIES = "counties";

    private static final String DATA_FILE_NAME = "allcitydata.txt";

    private static CityDataManager mInstance = null;

    public static CityDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new CityDataManager();
        }
        return mInstance;
    }

    private CityDataManager() {
    }

    public String buildCountryKey(String province, String city) {
        return province + SEP_PROVINCE_CITY + city;
    }

    private String sepCountryKey(String key) {
        return key.replace(SEP_PROVINCE_CITY, "");
    }

    private void parseCityData(JSONObject obj) throws JSONException {
        JSONArray provinces = obj.optJSONArray(KEY_PROVINCES);
        for (int index = 0; index < provinces.length(); index++) {
            JSONObject item = provinces.optJSONObject(index);
            String province = item.getString(KEY_PROVINCE);
            mProvinceList.add(province);
            JSONArray cities = item.optJSONArray(KEY_CITIES);
            ArrayList<String> cityList = new ArrayList<String>();
            for (int cindex = 0; cindex < cities.length(); cindex++) {
                JSONObject cItem = cities.optJSONObject(cindex);
                String city = cItem.getString(KEY_CITY);
                JSONArray countries = cItem.optJSONArray(KEY_COUNTIES);
                String key = buildCountryKey(province, city);
                if (countries.length() > 0) {
                    ArrayList<String> countryList = new ArrayList<String>();
                    for (int j = 0; j < countries.length(); j++) {
                        countryList.add(countries.getString(j));
                    }
                    mCountriesMap.put(key, countryList);
                }
                cityList.add(city);
            }
            mCitiesMap.put(province, cityList);
        }
    }

    public void reload(Context context) {
        if (mProvinceList.isEmpty() || mCitiesMap.isEmpty() || mCountriesMap.isEmpty()) {
            reload(context, true);
        }
    }

    private boolean reload(Context context, boolean isReload) {
        clearCache();

        InputStream is = null;
        try {
            is = context.getAssets().open(DATA_FILE_NAME);
            if (is != null) {
                final int BUFFER_SIZE = 64 * 1024;
                byte[] bs = new byte[BUFFER_SIZE];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = is.read(bs)) != -1) {
                    String buffer = new String(bs, 0, len, Charset.forName("UTF-8"));
                    sb.append(buffer);
                }
                JSONObject obj = new JSONObject(sb.toString());

                parseCityData(obj);
                return true;
            }
        } catch (Exception e) {
            LetvLog.e(TAG, e.toString());
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public void clearCache() {
        mProvinceList.clear();
        mCitiesMap.clear();
        mCountriesMap.clear();
    }

    public int getProvinceCount() {
        return mProvinceList.size();
    }

    public List<String> getProvinceList() {
        return mProvinceList;
    }

    public HashMap<String, ArrayList<String>> getCityMap() {
        return mCitiesMap;
    }

    public HashMap<String, ArrayList<String>> getCountryMap() {
        return mCountriesMap;
    }

    public String getProvince(int index) {
        if (index < 0 || index >= mProvinceList.size())
            return null;

        return mProvinceList.get(index);
    }

    public int getProvinceIndex(String province) {
        if (!TextUtils.isEmpty(province)) {
            for (int pIndex = 0; pIndex < mProvinceList.size(); pIndex++) {
                if (province.equals(mProvinceList.get(pIndex))) {
                    return pIndex;
                }
            }
        }
        return -1;
    }

    public int getCityMapCount() {
        return mCitiesMap.size();
    }

    public int getCityCountByProvince(String province) {
        List<String> cities = mCitiesMap.get(province);
        return cities != null ? cities.size() : 0;
    }

    public String getCityByProvince(String province, int index) {
        List<String> cities = mCitiesMap.get(province);
        if (cities == null)
            return null;

        if (index < 0 || index >= cities.size())
            return null;

        return cities.get(index);
    }

    public int getCityIndex(String province, String city) {
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
            List<String> cities = mCitiesMap.get(province);
            if (cities != null && cities.size() > 0) {
                for (int cIndex = 0; cIndex < cities.size(); cIndex++) {
                    if (city.equals(cities.get(cIndex))) {
                        return cIndex;
                    }
                }
            }
        }
        return -1;
    }

    public int getCountryMapCount() {
        return mCountriesMap.size();
    }

    public int getCountryCountByProvinceAndCity(String province, String city) {
        List<String> countries = mCountriesMap.get(buildCountryKey(province, city));
        return countries != null ? countries.size() : 0;
    }

    public String getCountryByProvinceAndCity(String province, String city, int index) {
        String key = buildCountryKey(province, city);
        List<String> countries = mCountriesMap.get(key);
        if (countries == null)
            return null;

        if (index < 0 || index >= countries.size())
            return null;

        return countries.get(index);
    }

    public int getCountryIndex(String province, String city, String country) {
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(country)) {
            String key = buildCountryKey(province, city);
            List<String> countries = mCountriesMap.get(key);
            if (countries != null && countries.size() > 0) {
                for (int cIndex = 0; cIndex < countries.size(); cIndex++) {
                    if (country.equals(countries.get(cIndex))) {
                        return cIndex;
                    }
                }
            }
        }
        return -1;
    }

    private String getProvinceAndCityFromContry(String country) {
        if (!TextUtils.isEmpty(country)) {
            Set<String> keySet = mCountriesMap.keySet();
            for (String key : keySet) {
                ArrayList<String> values = mCountriesMap.get(key);
                if (values.contains(country)) {
                    return key;
                }
            }

        }
        return null;
    }


    private String getProvinceAndCityFromCity(String city) {
        if (!TextUtils.isEmpty(city)) {
            Set<String> keySet = mCitiesMap.keySet();
            for (String key : keySet) {
                ArrayList<String> values = mCitiesMap.get(key);
                if (values.contains(city)) {
                    return key;
                }
            }

        }
        return null;
    }

    private static String formatProvinceCityDistrict(String data) {
        return TextUtils.isEmpty(data) ? "" : data.replace("省", "").replace("市", "").replace("区", "");
    }


    public static String getWeatherIdString(Context context, String city) {
        city = formatProvinceCityDistrict(city);
        String weatherIdString = null;
        if (!TextUtils.isEmpty(city)) {
            CityDataManager manager = CityDataManager.getInstance();
            manager.reload(context);

            //check city
            weatherIdString = manager.getProvinceAndCityFromCity(city);
            if (!TextUtils.isEmpty(weatherIdString) && !weatherIdString.equals(city)) {
                weatherIdString += city;
            }
            //check district
            if (TextUtils.isEmpty(weatherIdString)) {
                weatherIdString = manager.getProvinceAndCityFromContry(city);
                if (!TextUtils.isEmpty(weatherIdString)) {
                    weatherIdString = manager.sepCountryKey(weatherIdString);
                }
            }
        }
        return weatherIdString;

    }
}
