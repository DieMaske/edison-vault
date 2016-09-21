package de.otto.edison.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class VaultReader {

    private final ConfigProperties configProperties;
    private final VaultClient vaultClient;

    private Logger LOG = LoggerFactory.getLogger(VaultReader.class);

    public VaultReader(final ConfigProperties configProperties, VaultClient vaultClient) {
        this.configProperties = configProperties;
        this.vaultClient = vaultClient;
    }

    public boolean vaultEnabled() {
        return configProperties.isEnabled();
    }

    public Properties fetchPropertiesFromVault() {
        final Properties vaultProperties = new Properties();
        configProperties.getProperties().forEach(key -> {
            configProperties.getPropertyFieldnames(key).forEach(field -> {
                final String secret = vaultClient.readField(key, field).orElse(null);
                if(field.equals("value")) {
                    vaultProperties.setProperty(key, secret);
                }
                vaultProperties.setProperty(key + "@" + field, secret);
            });
        });
        return vaultProperties;
    }

}
