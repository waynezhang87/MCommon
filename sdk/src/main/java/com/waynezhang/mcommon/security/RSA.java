package com.waynezhang.mcommon.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;

public class RSA {

	private static PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	private static PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	public static String encrypt(String publicKey, String data) throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// 加解密类
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		// Cipher cipher = Cipher.getInstance("RSA");
		// 明文
		byte[] plainText = data.getBytes("UTF-8");
		// 通过密钥字符串得到密钥
		PublicKey key = getPublicKey(publicKey);
		// 加密
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] enBytes = cipher.doFinal(plainText);

		return Base64.encodeToString(enBytes, Base64.DEFAULT);
	}

	public static String decrypt(String privateKey, String data) throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// 加解密类
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		// Cipher cipher = Cipher.getInstance("RSA");
		// 密文BASE64
		byte[] enBytes = Base64.decode(data, Base64.DEFAULT);
		// 通过密钥字符串得到密钥
		PrivateKey key = getPrivateKey(privateKey);
		// 解密
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] deBytes = cipher.doFinal(enBytes);

		return new String(deBytes, "utf-8");
	}
}
