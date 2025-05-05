package dev.park.dailynews.common.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.park.dailynews.common.security.CryptoUtil;
import dev.park.dailynews.model.SessionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCrypto {

    private final ObjectMapper objectMapper;

    public String encode(SessionContext sessionContext) throws Exception {
        String json = objectMapper.writeValueAsString(sessionContext);
        String encryptedSession = CryptoUtil.encrypt(json);

        return encryptedSession;
    }
}
