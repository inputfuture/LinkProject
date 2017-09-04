package com.letv.leauto.ecolink.json;

import android.util.Log;

import com.letv.leauto.ecolink.cfg.VoiceCfg;
import com.letv.leauto.ecolink.database.model.VoiceResult;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liweiwei on 16/3/21.
 */
public class VoiceRecgParse {
    public static VoiceResult parseVoiceRecg(String origin_result) {
       Trace.Debug("#####BDVoice="+origin_result);
        VoiceResult voiceResult = new VoiceResult();
        try {
            JSONObject originObject = new JSONObject(origin_result);
            JSONObject resultObject = originObject.optJSONObject("result");
            int errCode = resultObject.optInt("err_no");
            if (errCode == 0) {
                String json_res = resultObject.optString("json_res");
                JSONObject jsonObject = new JSONObject(json_res);
                JSONObject mergedObject = jsonObject.optJSONObject("merged_res");
                JSONObject semanticObject = mergedObject.optJSONObject("semantic_form");

                voiceResult.raw_text = semanticObject.getString("raw_text");
                //拿到最终的result
                JSONArray jsonArray = semanticObject.optJSONArray("results");
                JSONObject jsonResults;
                if (jsonArray.length() > 0) {
                    jsonResults = jsonArray.optJSONObject(0);
                    voiceResult.domain = jsonResults.getString("domain");
                    voiceResult.intention = jsonResults.getString("intent");
                    JSONObject keyObject = jsonResults.optJSONObject("object");

                    if (voiceResult.domain.equals("music")) {
                        if (keyObject.optString("song") != null && !keyObject.optString("song").equals("")) {
                            voiceResult.key_word = keyObject.optString("song");
                        } else if (keyObject.optString("singer") != null && !keyObject.optString("singer").equals("")) {
                            voiceResult.key_word = keyObject.optString("singer");
                        }
                    }

                    if (voiceResult.domain.equals("map")) {
                        if (keyObject.optString("arrival") != null && !keyObject.optString("arrival").equals("")) {
                            voiceResult.key_word = keyObject.optString("arrival");
                        } else if (keyObject.optString("location") != null && !keyObject.optString("location").equals("")) {
                            voiceResult.key_word = keyObject.optString("location");
                        } else if (keyObject.optString("poi_type") != null && !keyObject.optString("poi_type").equals("")) {
                            voiceResult.key_word = keyObject.optString("poi_type");
                        }
                    }

                    if (voiceResult.domain.equals("contact")) {
                        if (keyObject.optString("name") != null && !keyObject.optString("name").equals("")) {
                            voiceResult.key_word = keyObject.optString("name");
                            voiceResult.intention = "name";
                        } else if (keyObject.optString("phone_number") != null && !keyObject.optString("phone_number").equals("")) {
                            voiceResult.key_word = keyObject.optString("phone_number");
                            voiceResult.intention = "number";
                        }
                    }

                    if (voiceResult.domain.equals("other")) {
                        voiceResult.key_word=voiceResult.raw_text;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voiceResult;
    }

    public static VoiceResult ParseBsn(String result) {
        VoiceResult voiceResult = new VoiceResult();
        voiceResult.raw_text = result;
        Trace.Debug("bsn", result);
        try {
            //[{"entity": [[0, 1, "person_name"]], "tag": ["nr"], "word": ["郭德纲"]}]
            //{"entity":[[0,1,"time"]],"tag":["t","n"],"word":["今日","天气"]}
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                JSONObject resultJson = jsonArray.getJSONObject(0);
                if (!resultJson.isNull("entity")) {
                    JSONArray entityArray = resultJson.optJSONArray("entity");
                    Trace.Debug("bsn", entityArray.toString());
                    if (entityArray.length() > 0) {
                        JSONArray entityDetail = entityArray.getJSONArray(0);
                        Trace.Debug("bsn", entityDetail.toString());
                        String intent = entityDetail.getString(2);
                        Trace.Debug("bsn", intent);

                        if (intent.equals("person_name")) {
                            voiceResult.domain = VoiceCfg.DOMAIN_MUSIC;
                            voiceResult.intention = "play";
                        } else if (intent.equals("time")) {
                            voiceResult.domain = VoiceCfg.DOMAIN_OTHER;
                            voiceResult.intention = "weather";
                        }
                        if (voiceResult.domain != null && !resultJson.isNull("word")) {
                            JSONArray wordArray = resultJson.optJSONArray("word");
                            if (wordArray.length() > 0) {
                                voiceResult.key_word = wordArray.getString(wordArray.length() - 1);
                                Trace.Debug("bsn", voiceResult.key_word);
                            }
                        }
                    } /*else {
                        if (!resultJson.isNull("word")) {
                            JSONArray wordArray = resultJson.optJSONArray("word");
                            if (wordArray.length() > 0) {
                                voiceResult.domain = VoiceCfg.DOMAIN_OTHER;
                                voiceResult.intention = "other";
                                voiceResult.key_word = wordArray.getString(wordArray.length() - 1);
                                Trace.Debug("bsn", voiceResult.key_word);
                            }
                        }
                    }*/
                }
            }
        } catch (JSONException e) {
            Trace.Debug("bsn", "error");
            e.printStackTrace();
        }
        return voiceResult;
    }

}
