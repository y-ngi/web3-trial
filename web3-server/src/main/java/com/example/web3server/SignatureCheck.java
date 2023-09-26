package com.example.web3server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;
import java.math.BigInteger;

public class SignatureCheck implements Serializable{

    private static final Logger log = LoggerFactory.getLogger( Web3ServerController.class );

    private static final long serialVersionUID = 1L;
    private String message;
    private String address;
    private String signature;

    public String getMessage() { return message; }
    public void setMessage(String s) { this.message = s; }
    public String getAddress() { return address; }
    public void setAddress(String s) { this.address = s; }
    public String getSignature() { return signature; }
    public void setSignature(String s) { this.signature = s; }
    public Date getTimestamp() {
        Date date = null;
        try {
            String dataStr = message.substring((message.length() - 19));
            log.info( "dataStr:" + dataStr );
            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String toString(){
        return "\nmessage:\n" + this.message + "\neeoaAddress: " + this.address + "\nsignature:" + this.signature;
    }

    public String recoverAddress() {
        /* (1) 署名データを v,r,s に分割 */
        String signatureInHex = signature;
        if (signatureInHex.startsWith("0x")) {
            signatureInHex = signatureInHex.substring(2);
        }
        String r = signatureInHex.substring(0, 64);
        String s = signatureInHex.substring(64, 128);
        String v = signatureInHex.substring(128, 130);
        SignatureData signData = new SignatureData(Numeric.hexStringToByteArray(v)[0] , Numeric.hexStringToByteArray(r), Numeric.hexStringToByteArray(s));
    
        /* (2) v,r,s よりEOAアドレスを復元 */
        try {
            BigInteger recoveredKey = Sign.signedPrefixedMessageToKey(message.getBytes(), signData);
            String recoverAddress = "0x" + Keys.getAddress(recoveredKey);
            log.info( "recoverAddress:" + recoverAddress );
            return recoverAddress;
        } catch (Exception e) {
            /* 署名検証失敗 */
            log.error("SignatureException: ", e.getMessage());
            return null;
        }
    }
}