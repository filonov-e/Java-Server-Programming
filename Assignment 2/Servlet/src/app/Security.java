package app;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.Entry;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class Security {
    public static char[] password = "changeit".toCharArray();
    // Creating the KeyStore.ProtectionParameter object
    public static KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);

    public static String secure(String login, String password) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, KeyStoreException, CertificateException,
            UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException, IOException {

        KeyStore keyStore = getKeyStore(
                "C:/Users/cliff/Documents/Work/Studies/Java Server Programming/Assignment 2/webapp/keys/Keys.txt");

        if (isDuplicateKeyEntry(login, keyStore, protectionParam)) {
            return ""; // todo
        }

        Map<byte[], SecretKey> pair = encrypt(password);

        SecretKey key = pair.values().iterator().next();

        byte[] encryptedPassword = new byte[0];

        for (byte[] pwdItem : pair.keySet()) {
            encryptedPassword = pwdItem;
        }

        storeKey(key, login,
                "C:/Users/cliff/Documents/Work/Studies/Java Server Programming/Assignment 2/webapp/keys/Keys.txt");

        return encryptedPassword.toString();
    }

    public static Map<byte[], SecretKey> encrypt(String text) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, KeyStoreException, CertificateException, IOException,
            UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException {
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        KeySpec spec = new PBEKeySpec(text.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);

        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secret);

        byte[] cipherText = cipher.doFinal(text.getBytes());

        Map<byte[], SecretKey> pair = new HashMap<byte[], SecretKey>();

        pair.put(cipherText, secret);

        return pair;
    }

    public static String decrypt(String encryptedText, String alias) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException, CertificateException,
            UnrecoverableEntryException, IOException, IllegalBlockSizeException, BadPaddingException {
        SecretKey key = findKey(alias);
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedText.getBytes()).toString();
    }

    public static boolean isDuplicateKeyEntry(String alias, KeyStore keyStore,
            KeyStore.ProtectionParameter protectionParam)
            throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        return keyStore.getEntry(alias, protectionParam) != null;
    }

    public static KeyStore getKeyStore(String path)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");

        // Loading the KeyStore object
        java.io.FileInputStream fis = new FileInputStream(path);

        if (fis.available() != 0) {
            keyStore.load(fis, password);
        } else {
            keyStore.load(null, password);
        }

        fis.close();

        return keyStore;
    }

    public static SecretKey findKey(String alias) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = getKeyStore(
                "C:/Users/cliff/Documents/Work/Studies/Java Server Programming/Assignment 2/webapp/keys/Keys.txt");
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protectionParam);
        return entry.getSecretKey();
    }

    // public static void storePassword(String password, String alias, String path) throws IOException {
    //     KeyStore keyStore = getKeyStore(path);
        
    //     // Creating SecretKeyEntry object
    //     KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(password.toCharArray());

    //     // Set the entry to the keystore
    //     keyStore.setEntry("db-encryption-secret", , passwordProtection);

    //     // Storing the KeyStore object
    //     java.io.FileOutputStream fos = null;
    //     fos = new java.io.FileOutputStream(path);
    //     keyStore.store(fos, password);
    //     System.out.println("data stored");

    //     fos.close();
    // }

    public static void storeKey(SecretKey key, String alias, String path) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = getKeyStore(path);

        // Creating SecretKeyEntry object
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);

        // Set the entry to the keystore
        keyStore.setEntry(alias, secretKeyEntry, protectionParam);

        // Storing the KeyStore object
        java.io.FileOutputStream fos = null;
        fos = new java.io.FileOutputStream(path);
        keyStore.store(fos, password);
        System.out.println("data stored");

        fos.close();
    }
}