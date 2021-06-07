package poc.eventchain.accountservice.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;

@Configuration
@Slf4j
public class HyperledgerConfig {

    @Value("${event-resolver.blockchain.pemFile}")
    private String pemFile;

    @Value("classpath:cryptomaterials/wallet")
    private Resource walletFolder;

    @Value("${event-resolver.blockchain.configFile}")
    private String networkConfigFile;

    @Value("appUser")
    private String walletId;

    @Autowired
    private Wallet wallet;

    @Bean
    public Wallet wallet() throws Exception {
        enrollAdmin();
        enrollUser();
        // Load a file system based wallet for managing identities.
        return Wallets.newFileSystemWallet(Paths.get("wallet"));
    }

    @Bean
    public Gateway gateway() throws IOException {
        // helper function for getting connected to the gateway
        return Gateway.createBuilder()
                .identity(wallet, walletId) // load a CCP
                .networkConfig(Paths.get(networkConfigFile)) // connection profile
                .discovery(true)
                .connect();
    }

    public void enrollAdmin() throws Exception {

        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Check to see if we've already enrolled the admin user.
        if (wallet.get("admin") != null) {
            log.warn("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }

        try {
            // Enroll the admin user, and import the new identity into the wallet.
            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");
            Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put("admin", user);
            log.info("Successfully enrolled user \"admin\" and imported it into the wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void enrollUser() throws Exception {
        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));


        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Check to see if we've already enrolled the user.
        if (wallet.get("appUser") != null) {
            log.warn("An identity for the user \"appUser\" already exists in the wallet");
            return;
        }

        X509Identity adminIdentity = (X509Identity) wallet.get("admin");
        if (adminIdentity == null) {
            log.warn("\"admin\" needs to be enrolled and added to the wallet first");
            return;
        }
        User admin = new User() {

            @Override
            public String getName() {
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return "org1.department1";
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return "Org1MSP";
            }

        };


        try {
            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest("appUser");
            registrationRequest.setAffiliation("org1.department1");
            registrationRequest.setEnrollmentID("appUser");
            String enrollmentSecret = caClient.register(registrationRequest, admin);
            Enrollment enrollment = caClient.enroll("appUser", enrollmentSecret);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put("appUser", user);
            log.info("Successfully enrolled user \"appUser\" and imported it into the wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
