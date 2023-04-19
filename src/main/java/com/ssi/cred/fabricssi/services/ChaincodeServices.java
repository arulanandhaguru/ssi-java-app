package com.ssi.cred.fabricssi.services;

import com.ssi.cred.fabricssi.Models.SSIResponse;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

@Component
public class ChaincodeServices {

    ChaincodeServices() {

    }

    private static final String caDirectory = "/Users/grootan/go/src/github.com/arulanandhaguru/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";

    public SSIResponse enrollAdmin(String AdminUser) throws Exception{
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", caDirectory);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Delete wallet if it exists from prior runs
        FileUtils.deleteDirectory(new File("Wallets/wallet"));

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("Wallets/wallet"));

        // Check to see if we've already enrolled the admin user.
        if (wallet.get(AdminUser) != null) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return new SSIResponse(400, null, "Failed to enroll admin");
        }

        // Enroll the admin user, and import the new identity into the wallet.
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = caClient.enroll(AdminUser, "adminpw", enrollmentRequestTLS);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put(AdminUser, user);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
        return new SSIResponse(200, null, "Successfully Enrolled Admin");
    }

    public SSIResponse registerUser(String userId) throws Exception {
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", caDirectory);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("Wallets/wallet"));

        // Check to see if we've already enrolled the user.
        if (wallet.get(userId) != null) {
            System.out.println("An identity for the user \"" + userId + "\" already exists in the wallet");
            return null;
        }

        X509Identity adminIdentity = (X509Identity)wallet.get("admin");
        if (adminIdentity == null) {
            System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
            return null;
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

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(userId);
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(userId);
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll(userId, enrollmentSecret);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put(userId, user);
        System.out.println("Successfully enrolled user \"" + userId + "\" and imported it into the wallet");
        return new SSIResponse(200, null, "Successfully Registered user");
    }

}
