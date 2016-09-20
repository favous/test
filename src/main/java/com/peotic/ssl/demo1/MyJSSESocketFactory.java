package com.peotic.ssl.demo1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

public class MyJSSESocketFactory {

    /**
     * 用于Server端的ServerSocketFactory代理
     */
    protected SSLServerSocketFactory serverSslProxy = null;
    /**
     * 用于Client端的SocketFactory代理
     */
    protected SSLSocketFactory clientSslProxy = null;
    protected boolean initialized;

    private String configFile = "ssl.properties";

    static String defaultProtocol = "TLS";
    static String defaultKeystoreType = "JKS";
    private static final String defaultKeystoreFile = ".keystore";
    private static final String defaultTruststoreFile = ".truststore";
    private static final String defaultKeyPass = "changeit";

    protected static Properties properties = new Properties();
    private static final String KEY_PREFIX = "ssl.";

    private static final String KEY_PROTOCOL = KEY_PREFIX + "protocol";
    private static final String KEY_ALGORITHM = KEY_PREFIX + "algorithm";

    private static final String KEY_KEYSTORE = KEY_PREFIX + "keystore";
    private static final String KEY_KEY_ALIAS = KEY_PREFIX + "keystoreAlias";
    private static final String KEY_KEYSTORE_TYPE = KEY_PREFIX + "keystoreType";
    private static final String KEY_KEYSTORE_PROVIDER = KEY_PREFIX + "keystoreProvider";
    private static final String KEY_KEYPASS = KEY_PREFIX + "keypass";
    private static final String KEY_KEYSTORE_PASS = KEY_PREFIX + "keystorePass";

    private static final String KEY_TRUSTSTORE = KEY_PREFIX + "trustStore";
    private static final String KEY_TRUSTSTORE_TYPE = KEY_PREFIX + "truststoreType";
    private static final String KEY_TRUSTSTORE_PASS = KEY_PREFIX + "truststorePass";
    private static final String KEY_TRUSTSTORE_PROVIDER = KEY_PREFIX + "truststoreProvider";
    private static final String KEY_TRUSTSTORE_ALGORITHM = KEY_PREFIX + "truststoreAlgorithm";

    public MyJSSESocketFactory() {
    }

    public MyJSSESocketFactory(String configFile_) {
        this.configFile = configFile_;
    }

    /**
     * 使用代理Factory创建ServerSocket
     */
    public ServerSocket createSocket(int port) throws Exception {
        if (!initialized)
            init();
        ServerSocket socket = serverSslProxy.createServerSocket(port);
        return socket;
    }

    /**
     * 使用代理Factory创建ServerSocket
     */
    public ServerSocket createSocket(int port, int backlog) throws Exception {
        if (!initialized)
            init();
        ServerSocket socket = serverSslProxy.createServerSocket(port, backlog);
        return socket;
    }

    /**
     * 使用代理Factory创建ServerSocket
     */
    public ServerSocket createSocket(int port, int backlog, InetAddress ifAddress) throws Exception {
        if (!initialized)
            init();
        ServerSocket socket = serverSslProxy.createServerSocket(port, backlog, ifAddress);
        return socket;
    }

    public Socket acceptSocket(ServerSocket socket) throws IOException {
        SSLSocket asock = null;
        try {
            asock = (SSLSocket) socket.accept();
        } catch (SSLException e) {
            throw new SocketException("SSL handshake error" + e.toString());
        }
        return asock;
    }

    /**
     * 客户端Socket建立
     */
    public Socket createSocket(String host, int port) throws Exception {
        if (!initialized)
            init();
        Socket socket = clientSslProxy.createSocket(host, port);
        return socket;
    }

    /**
     * 初始化：
     * 1，从configFile文件里边获取keystore相关配置(比如keystore和truststore路径、密码等信息)
     * 2，调用SSLContext.getInstance，使用指定的protocol(默认为TLS)获取SSLContext 
     * 3, 构造KeyManager和TrustManager，并使用构造出来的Manager初始化第二步获取到的SSLContext 
     * 4，从SSLContext获取基于SSL的SocketFactory
     */
    private void init() throws Exception {
        FileInputStream fileInputStream = null;
        try {
            File sslPropertyFile = new File(configFile);
            fileInputStream = new FileInputStream(sslPropertyFile);
            properties.load(fileInputStream);
        } catch (Exception e) {
            System.out.println("Because no " + configFile + " config file, server will use default value.");
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e2) {
            }
        }

        String protocol = properties.getProperty(KEY_PROTOCOL);
        if (protocol == null || "".equals(protocol)) {
            protocol = defaultProtocol;
        }

        // Certificate encoding algorithm (e.g., SunX509)
        String algorithm = (String) properties.getProperty(KEY_ALGORITHM);
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }

        String keystoreType = (String) properties.getProperty(KEY_KEYSTORE_TYPE);
        if (keystoreType == null) {
            keystoreType = defaultKeystoreType;
        }

        String keystoreProvider = (String) properties.getProperty(KEY_KEYSTORE_PROVIDER);

        String trustAlgorithm = (String) properties.getProperty(KEY_TRUSTSTORE_ALGORITHM);
        if (trustAlgorithm == null) {
            trustAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        }

        // Create and init SSLContext
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(
                getKeyManagers(keystoreType, keystoreProvider, algorithm,
                        (String) properties.getProperty(KEY_KEY_ALIAS)), null, new SecureRandom());

        // 用于Server端的ServerSocketFactory获取
        serverSslProxy = context.getServerSocketFactory();
        // 用于Client端的SocketFactory获取
        clientSslProxy = context.getSocketFactory();
    }

    /**
     * 获取KeyManagers，KeyManagers根据keystore文件进行初始化，以便Socket能够获取到相应的证书
     */
    protected KeyManager[] getKeyManagers(String keystoreType, String keystoreProvider, String algorithm,
            String keyAlias) throws Exception {

        KeyManager[] kms = null;
        String keystorePass = getKeystorePassword();

        /**
         * 先获取到Keystore对象之后，使用KeyStore对KeyManagerFactory进行初始化， 然后从KeyManagerFactory获取KeyManagers
         */
        KeyStore ks = getKeystore(keystoreType, keystoreProvider, keystorePass);
        if (keyAlias != null && !ks.isKeyEntry(keyAlias)) {
            throw new IOException("No specified keyAlias[" + keyAlias + "]");
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(ks, keystorePass.toCharArray());

        kms = kmf.getKeyManagers();
        if (keyAlias != null) {
            if (defaultKeystoreType.equals(keystoreType)) {
                keyAlias = keyAlias.toLowerCase();
            }
            for (int i = 0; i < kms.length; i++) {
                kms[i] = new JSSEKeyManager((X509KeyManager) kms[i], keyAlias);
            }
        }

        return kms;

    }

    /**
     * 获取TrustManagers，TrustManagers根据truststore文件进行初始化， 以便Socket能够获取到相应的信任证书
     */
    protected TrustManager[] getTrustManagers(String keystoreType, String keystoreProvider, String algorithm)
            throws Exception {

        TrustManager[] tms = null;

        /**
         * 先获取到Keystore对象之后，使用KeyStore对TrustManagerFactory进行初始化， 然后从TrustManagerFactory获取TrustManagers
         */
        KeyStore trustStore = getTrustStore(keystoreType, keystoreProvider);
        if (trustStore != null) {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(trustStore);
            tms = tmf.getTrustManagers();
        }

        return tms;
    }

    /**
     * 主要是调用getStore方法，传入keystore文件以供getStore方法解析.
     */
    protected KeyStore getKeystore(String type, String provider, String pass) throws IOException {

        String keystoreFile = (String) properties.getProperty(KEY_KEYSTORE);
        if (keystoreFile == null)
            keystoreFile = defaultKeystoreFile;

        try {
            return getStore(type, provider, keystoreFile, pass);
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    /**
     * keystore相关的keyPass和storePass密码.
     */
    protected String getKeystorePassword() {
        String keyPass = (String) properties.get(KEY_KEYPASS);
        if (keyPass == null) {
            keyPass = defaultKeyPass;
        }
        String keystorePass = (String) properties.get(KEY_KEYSTORE_PASS);
        if (keystorePass == null) {
            keystorePass = keyPass;
        }
        return keystorePass;
    }

    /**
     * 主要是调用getStore方法，传入truststore文件以供getStore方法解析.
     */
    protected KeyStore getTrustStore(String keystoreType, String keystoreProvider) throws IOException {
        KeyStore trustStore = null;

        /**
         * truststore文件优先级:指定的KEY_TRUSTSTORE属性->系统属性->当前路径<.truststore>文件
         */
        String truststoreFile = (String) properties.getProperty(KEY_TRUSTSTORE);
        if (truststoreFile == null) {
            truststoreFile = System.getProperty("javax.net.ssl.trustStore");
        }
        if (truststoreFile == null) {
            truststoreFile = defaultTruststoreFile;
        }

        /**
         * truststorePassword设置
         */
        String truststorePassword = (String) properties.getProperty(KEY_TRUSTSTORE_PASS);
        if (truststorePassword == null) {
            truststorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        }
        if (truststorePassword == null) {
            truststorePassword = getKeystorePassword();
        }

        /**
         * trustStoreType设置优先级：指定的KEY_TRUSTSTORE_TYPE属性 ->系统属性javax.net.ssl.trustStoreType -><keystoreType>
         */
        String truststoreType = (String) properties.getProperty(KEY_TRUSTSTORE_TYPE);
        if (truststoreType == null) {
            truststoreType = System.getProperty("javax.net.ssl.trustStoreType");
        }
        if (truststoreType == null) {
            truststoreType = keystoreType;
        }

        /**
         * truststoreProvider设置优先级：指定的KEY_TRUSTSTORE_PROVIDER属性 ->系统属性javax.net.ssl.trustStoreProvider -><keystoreProvider>
         */
        String truststoreProvider = (String) properties.getProperty(KEY_TRUSTSTORE_PROVIDER);
        if (truststoreProvider == null) {
            truststoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider");
        }
        if (truststoreProvider == null) {
            truststoreProvider = keystoreProvider;
        }

        /**
         * 通过调用getStore方法获取到keystore对象(也就是truststore对象)
         */
        if (truststoreFile != null) {
            try {
                trustStore = getStore(truststoreType, truststoreProvider, truststoreFile, truststorePassword);
            } catch (FileNotFoundException fnfe) {
                throw fnfe;
            } catch (IOException ioe) {
                if (truststorePassword != null) {
                    try {
                        trustStore = getStore(truststoreType, truststoreProvider, truststoreFile, null);
                        ioe = null;
                    } catch (IOException ioe2) {
                        ioe = ioe2;
                    }
                }
                if (ioe != null) {
                    throw ioe;
                }
            }
        }

        return trustStore;
    }

    /**
     * 使用KeyStore的API从指定的keystore文件中构造出KeyStore对象，KeyStore对象用于初始化KeystoreManager和TrustManager.
     */
    private KeyStore getStore(String type, String provider, String path, String pass) throws IOException {

        KeyStore ks = null;
        InputStream istream = null;
        try {
            if (provider == null) {
                ks = KeyStore.getInstance(type);
            } else {
                ks = KeyStore.getInstance(type, provider);
            }
            if (!("PKCS11".equalsIgnoreCase(type) || "".equalsIgnoreCase(path))) {
                File keyStoreFile = new File(path);
                istream = new FileInputStream(keyStoreFile);
            }

            char[] storePass = null;
            if (pass != null && !"".equals(pass)) {
                storePass = pass.toCharArray();
            }
            ks.load(istream, storePass);
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException ioe) {
                    // Do nothing
                }
            }
        }

        return ks;
    }
}