package com.cserver.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Certificate;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptLib {
	private static final String TAG = "CryptLib";
	private Provider provider = null;
	private static volatile CryptLib instance = null;
	
	public static CryptLib getInstance()
	{
		if (instance != null)
			return instance;
		synchronized(CryptLib.class) {
			if (instance == null) {
				instance = new CryptLib();
				instance.setup();
			}
		}
		return instance;
	}
	
	private void setup() {
		this.provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
		Security.addProvider(this.provider);
	}
	
	public byte[] rsaEncrypt(Key key, byte [] data) {
		byte[] encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", this.provider);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypted = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (NoSuchPaddingException e) {
			SLog.exception(TAG, e);
		} catch (InvalidKeyException e) {
			SLog.exception(TAG, e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (Exception e) {
			SLog.exception(TAG, e);
		}
		
		return encrypted;
	}
	
	public byte[] rsaDecrypt(Key key, byte [] data) {
		byte[] plain = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", this.provider);
			cipher.init(Cipher.DECRYPT_MODE, key);
			plain = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (NoSuchPaddingException e) {
			SLog.exception(TAG, e);
		} catch (InvalidKeyException e) {
			SLog.exception(TAG, e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (Exception e) {
			SLog.exception(TAG, e);
		}
		
		
		return plain;
	}
	
	public byte[] sha256(byte[] data) {
		byte []hash = null;
		try {
			MessageDigest md = null;
			md = MessageDigest.getInstance("SHA-256", this.provider);
	        md.update(data);
	        hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (Exception e) {
			SLog.exception(TAG, e);
		} finally {
			
		}
		
		return hash;
	}
	
	public byte[] aesEncrypt(byte[] key, byte[] data, byte[] iv) {
		byte []encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", this.provider);
			cipher.init( Cipher.ENCRYPT_MODE,  new SecretKeySpec(key, "AES"),  new IvParameterSpec(iv));
			encrypted = cipher.doFinal(data);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		}  catch (Exception e) {
			SLog.exception(TAG, e);
		} finally {
			
		}
		return encrypted;
	}
	
	public byte[] aesDecrypt(byte[] key, byte[] data, byte[] iv) {
		byte []plain = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", this.provider);
			cipher.init( Cipher.DECRYPT_MODE,  new SecretKeySpec(key, "AES"),  new IvParameterSpec(iv));
			plain = cipher.doFinal(data);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		}  catch (Exception e) {
			SLog.exception(TAG, e);
		} finally {
			
		}
		return plain;
	}
	
	public byte[] getSign(byte[] data, PrivateKey privKey) {
		
		byte dataHash[] = sha256(data);
		byte dataSign[] = rsaEncrypt(privKey, dataHash);
		
		return dataSign;
	}
	
	public boolean checkSign(byte[] data, byte[] dataSign, PublicKey pubKey)
	{
		
		byte[] dataHash = sha256(data);
		byte[] decryptedDataHash = rsaDecrypt(pubKey, dataSign);
		
		return Arrays.equals(dataHash, decryptedDataHash);
	}
	
	public KeyPair genKeys(int keysize) {
	    KeyPairGenerator keyGen = null;
	    KeyPair kp = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA", this.provider);
		    keyGen.initialize(keysize);
		    kp = keyGen.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} catch (Exception e) {
			SLog.exception(TAG, e);
		}
		
		return kp;
	}

	public KeyPair getKeysByKs(File ksFile, String ksPass, String keyAlias, String keyPass) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableEntryException, NoSuchProviderException {
		FileInputStream is = new FileInputStream(ksFile);
	    KeyStore ks = KeyStore.getInstance("BKS");
	    ks.load(is, ksPass.toCharArray());
	    KeyStore.ProtectionParameter protParam =
	            new KeyStore.PasswordProtection(keyPass.toCharArray());

	    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
	            ks.getEntry(keyAlias, protParam);
	    PrivateKey privKey = pkEntry.getPrivateKey();
	    PublicKey pubKey = pkEntry.getCertificate().getPublicKey();
	    
	    return new KeyPair(pubKey, privKey);
	  }
}
