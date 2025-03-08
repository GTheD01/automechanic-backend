package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;

/**
 * Interface for publishing email requests to a messaging system.
 * <p>
 * This interface defines the contract for publishing email requests to different
 * messaging systems (e.g., RabbitMQ, Kafka). Implementations of this interface
 * will handle the actual communication with the chosen messaging platform.
 * <p>
 * Use this interface to decouple the email sending logic from the underlying
 * messaging infrastructure, enabling easier testing, extension, and modification
 * of the messaging system without affecting business logic.
 *
 * Implementing classes should define the logic for serializing the {@link EmailRequest}
 * object and sending it to the appropriate destination using the corresponding
 * messaging platform (e.g., RabbitMQ, Kafka).
 *
 * Example use case:
 *
 * <pre>
 *     emailPublisher.publishEmailRequest(new EmailRequest(...));
 * </pre>
 *
 * @see RabbitMqEmailPublisher
 */
public interface EmailPublisher {
    void publishEmailRequest(EmailRequest emailRequest);
}
