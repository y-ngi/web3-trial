package com.example.web3server;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Web3ServerController {

    private static final Logger log = LoggerFactory.getLogger( Web3ServerController.class );

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    @RequestMapping(value="/check", method = RequestMethod.POST, consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
     @CrossOrigin(origins = {"http://localhost:3000"})
    public String chechSign(@RequestBody SignatureCheck sign ) {
        log.info( sign.toString() );
        String recoverAddress = sign.recoverAddress();

        /* (1) 電子署名からEOAアドレスを復元できなければNG */
        if (recoverAddress == null) {
            return "NG";
        }
        /* (2) 復元したEOAアドレスが署名者と異なるならNG */
        if (recoverAddress == sign.getAddress()) {
            return "NG";
        }
        /* (3) 復元したEOAアドレスがログイン中ユーザと異なるならNG */
        //if (recoverAddress == memmber.EOA()) {
        //    return "NG";
        //}
        /* (4) 電子署名の有効期限チェック(10分) */
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sign.getTimestamp());
        calendar.add(Calendar.MINUTE, 10);
        Date signDate = calendar.getTime();
        if (signDate.before(now)) {
            return "NG";
        }
        return "OK";
    }
}

