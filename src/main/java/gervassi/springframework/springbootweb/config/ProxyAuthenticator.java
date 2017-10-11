package gervassi.springframework.springbootweb.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator {
    public ProxyAuthenticator() {
        initializeProxyAuthenticator();
    }

    private void initializeProxyAuthenticator() {
        final String proxyUser = System.getProperty("http.proxyUser");
        final String proxyPassword = System.getProperty("http.proxyPassword");
        System.out.println(proxyUser);
        System.out.println(proxyPassword);
        if (proxyUser != null && proxyPassword != null) {
            Authenticator.setDefault(
                    new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    proxyUser, proxyPassword.toCharArray()
                            );
                        }
                    }
            );
        }
    }
}
