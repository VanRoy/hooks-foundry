package com.github.vanroy.hooksfoundry.endpoints.apis;

import com.github.vanroy.hooksfoundry.domain.usecases.CreateSubscriptionUseCase;
import com.github.vanroy.hooksfoundry.endpoints.apis.dtos.CreateSubscriptionRequestBody;
import com.github.vanroy.hooksfoundry.endpoints.apis.dtos.CreateSubscriptionResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/tenants/{tenantId}/subscriptions")
public class SubscriptionController {

    private final CreateSubscriptionUseCase createUseCase;
/*
    @GetMapping
    public List<WebhookDetailResponse> findAll(@PathVariable String tenantId) {
        return webhookService.findByAppId(tenantId);
    }

    @GetMapping("/{id}")
    public WebhookDetailResponse findById(@PathVariable String tenantId, @PathVariable UUID id) {
        return webhookService.findByIdAndAppId(id, tenantId);
    }
*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSubscriptionResponseBody create(@PathVariable String tenantId, @Valid @RequestBody CreateSubscriptionRequestBody creationRequest) {
        createUseCase.execute(creationRequest.toRequest(tenantId));
        return new CreateSubscriptionResponseBody();
    }
/*
    @PutMapping("/{id}")
    public WebhookDetailResponse update(@PathVariable String tenantId, @PathVariable UUID id, @RequestBody WebhookEditionRequest updateRequest) {
        updateRequest.setAppId(tenantId);
        return webhookService.update(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String tenantId, @PathVariable UUID id) {
        webhookService.delete(id, tenantId);
    }

    @PostMapping("/{id}/signature/roll")
    public Map<String, UUID> regenerateSecret(@PathVariable String tenantId, @PathVariable UUID id) {
        return Map.of("secret", webhookService.generateNewSecret(id, tenantId));
    }

    @PostMapping("/{id}/test")
    public void sendTest(@PathVariable String tenantId, @PathVariable UUID id) {
        webhookService.sendTestEvent(id, tenantId);
    }

    @GetMapping("/{id}/logs")
    public List<EventLog> getLastEventLogs(@PathVariable String tenantId, @PathVariable UUID id, @RequestParam("type") LogKeys logType) {
        return webhookService.findLastEventLogs(id, logType, tenantId);
    }
    */
}
