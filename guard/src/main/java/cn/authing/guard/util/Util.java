package cn.authing.guard.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.ErrorTextView;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.data.Safe;
import cn.authing.guard.flow.AuthFlow;

public class Util {

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uMGKl64GDcIq3au+aqJQIDAQAB";

    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float px2dp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static String encryptPassword(String password) {
        try {
            byte[] keyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherMsg = cipher.doFinal(password.getBytes());
            return new String(Base64.encode(cipherMsg, Base64.NO_WRAP));
        } catch (Exception e) {
            return "{\"2020\":\"" + e + "\"}";
        }
    }

    public static List<Integer> intDigits(int i) {
        int temp = i;
        ArrayList<Integer> array = new ArrayList<Integer>();
        do{
            array.add(0, temp % 10);
            temp /= 10;
        } while  (temp > 0);
        return array;
    }

    public static List<View> findAllViewByClass(View current, Class<?> T) {
        View view = current.getRootView();
        List<View> result = new ArrayList<>();
        _findAllViewByClass((ViewGroup)view, T, result);
        return result;
    }

    private static void _findAllViewByClass(ViewGroup parent, Class<?> T, List<View> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                _findAllViewByClass((ViewGroup)child, T, result);
            }

            if (child.getClass().equals(T)) {
                result.add(child);
            }
        }
    }

    public static View findViewByClass(View current, Class<?> T) {
        return findViewByClass(current, T, true);
    }

    public static View findViewByClass(View current, Class<?> T, boolean onlyVisible) {
        View view = current.getRootView();
        return findChildViewByClass((ViewGroup)view, T, onlyVisible);
    }

    public static View findChildViewByClass(ViewGroup parent, Class<?> T, boolean onlyVisible) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup && (!onlyVisible || child.isShown())) {
                View result = findChildViewByClass((ViewGroup)child, T, onlyVisible);
                if (result != null) {
                    return result;
                }
            }

            if (T.isInstance(child)) {
                return child;
            }
        }
        return null;
    }

    public static String getAccount(View current) {
        String account = null;
        View v = findViewByClass(current, AccountEditText.class);
        if (v != null) {
            AccountEditText editText = (AccountEditText)v;
            account = editText.getText().toString();
        }
        if (TextUtils.isEmpty(account)) {
            account = AuthFlow.getAccount(current.getContext());
        }
        if (TextUtils.isEmpty(account)) {
            account = Safe.loadAccount();
        }
        return account;
    }

    public static String getPhoneNumber(View current) {
        String phone = null;
        View v = findViewByClass(current, PhoneNumberEditText.class);
        if (v != null) {
            PhoneNumberEditText editText = (PhoneNumberEditText)v;
            phone = editText.getText().toString();
        }
        if (TextUtils.isEmpty(phone)) {
            phone = (String) AuthFlow.get(current.getContext(), AuthFlow.KEY_MFA_PHONE);
        }
        if (TextUtils.isEmpty(phone)) {
            String account = AuthFlow.getAccount(current.getContext());
            if (Validator.isValidPhoneNumber(account)) {
                phone = account;
            }
        }
        if (TextUtils.isEmpty(phone)) {
            phone = Safe.loadAccount();
        }
        return phone;
    }

    public static String getPassword(View current) {
        String password = null;
        View v = findViewByClass(current, PasswordEditText.class);
        if (v != null) {
            PasswordEditText editText = (PasswordEditText)v;
            password = editText.getText().toString();
        }
        if (TextUtils.isEmpty(password)) {
            password = Safe.loadPassword();
        }
        return password;
    }

    public static String getVerifyCode(View current) {
        View v = findViewByClass(current, VerifyCodeEditText.class);
        if (v != null) {
            VerifyCodeEditText editText = (VerifyCodeEditText)v;
            return editText.getText().toString();
        }
        return null;
    }

    public static void setErrorText(View view, String text) {
        view.post(()->{
            View v = Util.findViewByClass(view, ErrorTextView.class);
            if (v == null) {
                return;
            }
            ErrorTextView errorView = (ErrorTextView)v;
            errorView.setText(text);
            if (TextUtils.isEmpty(text)) {
                v.setVisibility(View.INVISIBLE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        });
    }

    public static Map<String, List<String>> splitQuery(URL url, String charset) throws UnsupportedEncodingException {
        final Map<String, List<String>> queryPairs = new LinkedHashMap<>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), charset) : pair;
            if (!queryPairs.containsKey(key)) {
                queryPairs.put(key, new LinkedList<>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            Objects.requireNonNull(queryPairs.get(key)).add(value);
        }
        return queryPairs;
    }

    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }

    public static String randomString(int length) {
        String seed;
        Random rand = new Random();
        int seedLength;
        String asciiUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String asciiLowerCase = asciiUpperCase.toLowerCase();
        String digits = "1234567890";
        seed = asciiUpperCase + asciiLowerCase + digits;
        seedLength = seed.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < length;++i) {
            sb.append(seed.charAt(rand.nextInt(seedLength)));
        }
        return sb.toString();
    }

    public static boolean isNull(String s) {
        return TextUtils.isEmpty(s) || "null".equals(s);
    }
}
