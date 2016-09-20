package com.peotic.ssl.demo1;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

/**
 * 本类只作为一个包装类：主要是根据指定的别名(alias)获取证书和私钥等信息
 * 
 */
public final class JSSEKeyManager implements X509KeyManager {

    private X509KeyManager delegate;
    private String serverKeyAlias;

    public JSSEKeyManager(X509KeyManager mgr, String serverKeyAlias) {
        this.delegate = mgr;
        this.serverKeyAlias = serverKeyAlias;
    }

    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return delegate.chooseClientAlias(keyType, issuers, socket);
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return serverKeyAlias;
    }

    public X509Certificate[] getCertificateChain(String alias) {
        return delegate.getCertificateChain(alias);
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return delegate.getClientAliases(keyType, issuers);
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegate.getServerAliases(keyType, issuers);
    }

    public PrivateKey getPrivateKey(String alias) {
        return delegate.getPrivateKey(alias);
    }
}