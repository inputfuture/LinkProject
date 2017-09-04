package com.letv.leauto.ecolink.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.letv.leauto.ecolink.net.GetCallBack;
import com.letv.leauto.ecolink.net.GsonUtils;
import com.letv.leauto.ecolink.net.OkHttpRequest;
import com.letv.leauto.ecolink.net.PostCallBack;
import com.letv.leauto.ecolink.userinfo.LoginManager;
import com.letv.leauto.favorcar.contract.LoginContract;
import com.letv.loginsdk.activity.PersonalInfoActivity;
import com.letv.loginsdk.bean.DataHull;
import com.letv.loginsdk.bean.PersonalInfoBean;
import com.letv.loginsdk.bean.UserBean;
import com.letv.loginsdk.constant.LoginConstant;
import com.letv.loginsdk.network.task.GetResponseTask;
import com.letv.loginsdk.network.volley.VolleyRequest;
import com.letv.loginsdk.network.volley.VolleyResponse;
import com.letv.loginsdk.network.volley.toolbox.SimpleResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/16.
 */
public class LoginUtil {

    public final static String ACCOUNT_TYPE = "com.letv";
    public static final String AUTH_TOKEN_TYPE_LETV = "tokenTypeLetv";

    public static boolean hasLetvAuthenticator(Context context) {
        //判断是否为乐视手机
        AuthenticatorDescription[] allTypes = AccountManager.get(context).getAuthenticatorTypes();
        for (AuthenticatorDescription authenticatorType : allTypes) {
            if (ACCOUNT_TYPE.equals(authenticatorType.type)) {
                return true;

            }
        }
        return false;
    }

    public static void checkUpdate(final Context context) {
        GetResponseTask.getGetResponseTaskInstance().getUserInfoByUid(LoginManager.getUid(context), new SimpleResponse<PersonalInfoBean>() {
            @Override
            public void onCacheResponse(VolleyRequest<PersonalInfoBean> request, PersonalInfoBean result, DataHull hull, VolleyResponse.CacheResponseState state) {
                if (state == VolleyResponse.CacheResponseState.SUCCESS) {
                    String nickName = result.getNickname();
                    String headPicUrl = result.getPicture200x200();

                    if (!nickName.equals(LoginContract.getNickname())) {
                        LoginContract.setNickName(nickName);
                    }

                    if (!headPicUrl.equals(LoginContract.getHeadPicUrl())) {
                        LoginContract.setHeadPicUrl(headPicUrl);
                    }
                }
            }
        });
    }

    public static boolean isLogin(Context context) {
        //判断乐视手机账号是否登录
        AccountManager am = AccountManager.get(context);
        boolean isLogin = false;
        final Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            return true;
        }
        return isLogin;
    }

    public static void getUserToken(final Activity activity,final Context context, final Handler handler){
        //乐视手机单点登录成功获取token，用此token登录易车卡
        final AccountManager am = AccountManager.get(context);
        final Account account=am.getAccountsByType(ACCOUNT_TYPE)[0];
        final AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    if (future.getResult() != null) {
                        SpUtils.putString(context, "ssoTk",future.getResult().getString(AccountManager.KEY_AUTHTOKEN));
                        getToken(future.getResult().getString(AccountManager.KEY_AUTHTOKEN),handler,context);
                    }
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                am.getAuthToken(account, AUTH_TOKEN_TYPE_LETV, null, activity, callback, new Handler());
            }
        });

    }
    public static String getLoginName(Context context) {
        //乐视手机单点登录获取用户名以及头像等
        AccountManager am = AccountManager.get(context);
        final Account[] accountList = am.getAccountsByType(ACCOUNT_TYPE);
        String loginName = "";
        if (accountList != null && accountList.length > 0) {
            loginName = accountList[0].name;
        }
        SpUtils.putString(context,"userName",loginName);
        return loginName;
    }

    private static void getToken(String ssToken, final Handler handler,final Context context){
        //乐视集团SDK登录成功，用ssotoken登录易车卡，获取易车卡的token
        HashMap<String, String> bodys = new HashMap<>();
        bodys.put("sso_tk",ssToken);
        String params = GsonUtils.toJson(bodys);
        OkHttpRequest.postJson("login", VehicleConst.qauthUrl+ "v1/leSso", null, params, new PostCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("post请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("login请求成功");
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    JSONObject jsonObject1= (JSONObject) jsonObject.get("credential");
                    VehicleConst.testToken="Bearer "+jsonObject1.getString("access_token");
                    SpUtils.putString(context,"token","Bearer "+jsonObject1.getString("access_token"));
                    SpUtils.putString(context,"refresh_token",jsonObject1.getString("refresh_token"));
                    SpUtils.putString(context,"mobile",jsonObject.getString("mobile"));
                    SpUtils.putString(context,"id",jsonObject.getString("id"));
                    getOwner("Bearer "+jsonObject1.getString("access_token"),context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
                System.out.println("post请求出错" + errorCode);
            }
        });
    }

    private static void getOwner(String token,final Context context){
        //登录成功，获取用户唯一ID
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization",token);
        OkHttpRequest.get("owner", VehicleConst.qauthUrl+ "v1/id",headers, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("get请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("get请求成功");
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    SpUtils.putString(context,"owner",jsonObject.getString("id"));
                    System.out.println(jsonObject.getString("id"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response) {

            }
        });
    }
}
