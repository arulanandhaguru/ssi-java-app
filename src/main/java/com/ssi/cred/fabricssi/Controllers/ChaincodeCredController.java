package com.ssi.cred.fabricssi.Controllers;

import com.ssi.cred.fabricssi.Models.SSIResponse;
import com.ssi.cred.fabricssi.Models.UserModel;
import com.ssi.cred.fabricssi.services.ChaincodeSSIFunctions;
import com.ssi.cred.fabricssi.services.ChaincodeServices;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("ssi")
class ChaincodeCredController {
    @Autowired
    ChaincodeServices chaincodeServices;
    private static final String AdminUser = "admin";
    private static final Logger logger = Logger.getLogger(ChaincodeCredController.class.getName());

    ChaincodeSSIFunctions ssiFunctions = new ChaincodeSSIFunctions();

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    ChaincodeCredController() throws IOException {
    }


    @PostMapping("/enrollAdmin")
    SSIResponse enrollAdmin() throws Exception {
        logger.info("Enroll Admin");
        return chaincodeServices.enrollAdmin(AdminUser);
    }

    @PostMapping("/registerUser/{userId}")
    SSIResponse registerUser(@PathVariable String userId) throws Exception {
        logger.info("Registering new user");
        return chaincodeServices.registerUser(userId);
    }

    @PostMapping("/postCreds/{userId}")
    SSIResponse postCred(@RequestBody UserModel user, @PathVariable String userId, @RequestParam String ch, @RequestParam String contract) {
        logger.info("Creating new credentials for user " + user.getId());
        return ssiFunctions.addNewCred(user, userId, ch, contract);
    }

    @GetMapping("getCreds/{userId}/{credId}")
    SSIResponse<UserModel> getUserCreds(@PathVariable String userId, @PathVariable String credId, @RequestParam String ch, @RequestParam String contract) throws Exception {
        logger.info("Fetching Creds for Id" + credId);
        return ssiFunctions.getCred(credId, userId, ch, contract );
    }

}