package com.ssi.cred.fabricssi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssi.cred.fabricssi.Models.SSIResponse;
import com.ssi.cred.fabricssi.Models.UserModel;
import org.hyperledger.fabric.gateway.*;

import javax.json.Json;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class ChaincodeSSIFunctions {
    Path walletPath;
    Wallet wallet;
    Path networkConfigFile;

    public ChaincodeSSIFunctions() throws IOException {
        this.walletPath = Paths.get("Wallets/wallet");
        this.wallet = Wallets.newFileSystemWallet(walletPath);

        // Path to a common connection profile describing the network.
        this.networkConfigFile = Paths.get("/Users/grootan/go/src/github.com/arulanandhaguru/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.json");

    }

    public SSIResponse addNewCred(UserModel user, String fabricUserId, String channel, String chaincode) {
        try {
            // Configure the gateway connection used to access the network.
            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, fabricUserId)
                    .networkConfig(networkConfigFile);
            // Create a gateway connection
            try (Gateway gateway = builder.connect()) {

                // Obtain a smart contract deployed on the network.
                Network network = gateway.getNetwork(channel);
                Contract contract = network.getContract(chaincode);

                // Submit transactions that store state to the ledger.
                byte[] createCredResult = contract.submitTransaction("CreateCred",user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), String.valueOf(user.getPhno()));
                System.out.println(new String(createCredResult, StandardCharsets.UTF_8));

            } catch (ContractException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
                return new SSIResponse(400,null,e.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new SSIResponse(400,null,ex.getMessage());

        }
        return new SSIResponse(200,null,"New Cred Added successfully");
    }

    public SSIResponse getCred(String CredId, String fabricUserId, String channel, String chaincode) {
        Json userJson = null;
        String Result = "";
        try {
            // Configure the gateway connection used to access the network.
            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, fabricUserId)
                    .networkConfig(networkConfigFile);
            // Create a gateway connection
            try (Gateway gateway = builder.connect()) {

                // Obtain a smart contract deployed on the network.
                Network network = gateway.getNetwork(channel);
                Contract contract = network.getContract(chaincode);

                // Submit transactions that store state to the ledger.
                byte[] getCredResult = contract.evaluateTransaction("ReadCred",CredId);
                 Result = new String(getCredResult, StandardCharsets.UTF_8);

            } catch (ContractException e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return new SSIResponse<>(200, Result, "Successfully fetched ssi creds");

    }
}
