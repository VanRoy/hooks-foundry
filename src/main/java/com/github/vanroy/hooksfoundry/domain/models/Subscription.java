package com.github.vanroy.hooksfoundry.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Subscription {

    private static final int MAXIMUM_SIGNATURES = 2;
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final String SIGNATURE_PLACEHOLDER = "v1=";
    private static final String SIGNATURE_DELIMITER = ",";

    UUID id;
    String tenantId;
    String url;
    Deque<Signature> signatures;
    String name;
    SubscriptionStatus status;
    Set<String> eventTypes;
    Instant createdAt;
    Instant updatedAt;

    public Subscription(String tenantId, String name, String url, Set<String> eventTypes) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.name = name;
        this.url = url;
        this.signatures = new ArrayDeque<>(MAXIMUM_SIGNATURES);
        this.signatures.add(new Signature());
        this.status = SubscriptionStatus.ENABLED;
        this.eventTypes = eventTypes;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Add a new signature and remove the last one if webhook already has the maximum number of signatures or more.
     * With this method, we always  keep the latest signature on top of the Deque.
     *
     * @return The secret of the newly generated signature
     */
    public UUID addSignatureAndReturnSecret() {
        while (signatures.size() >= MAXIMUM_SIGNATURES) {
            signatures.pollLast();
        }
        Signature newSignature = new Signature();
        this.signatures.addFirst(newSignature);
        Signature oldSignature = this.signatures.peekLast();
        oldSignature.expire();
        return newSignature.getSecret();
    }

    /**
     * Generate signature for payload
     */
    public String sign(String payload) {
        StringBuilder sb = new StringBuilder();
        for (Signature signature : this.signatures) {
            if (!sb.isEmpty()) {
                sb.append(SIGNATURE_DELIMITER);
            }
            sb.append(SIGNATURE_PLACEHOLDER);
            sb.append(signPayload(payload, signature));
        }
        return sb.toString();
    }

    private String signPayload(String payload, Signature signature) {
        try {
            Mac hashBuilder = getHashBuilder();
            SecretKeySpec secretKey = new SecretKeySpec(signature.getSecret().toString().getBytes(), SIGNATURE_ALGORITHM);
            hashBuilder.init(secretKey);
            byte[] signedContent = hashBuilder.doFinal(payload.getBytes());
            return HexFormat.of().formatHex(signedContent);
        } catch (InvalidKeyException e) {
            log.error("Unable to sign event", e);
        }

        return "";
    }

    private Mac getHashBuilder() {
        try {
            return Mac.getInstance(SIGNATURE_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to setup event signer", e);
            throw new UnsupportedOperationException("Unable to setup event signer");
        }
    }
}
