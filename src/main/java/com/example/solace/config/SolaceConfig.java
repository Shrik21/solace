package com.example.solace.config;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceConfig {

    @Value("${solace.host}")
    private String host;

    @Value("${solace.vpn}")
    private String vpn;

    @Value("${solace.username}")
    private String username;

    @Value("${solace.password}")
    private String password;

    @Bean
    public JCSMPSession jcsmpSession() throws JCSMPException {
        JCSMPProperties props = new JCSMPProperties();
        props.setProperty(JCSMPProperties.HOST, host);
        props.setProperty(JCSMPProperties.VPN_NAME, vpn);
        props.setProperty(JCSMPProperties.USERNAME, username);
        props.setProperty(JCSMPProperties.PASSWORD, password);
        return JCSMPFactory.onlyInstance().createSession(props);
    }
}