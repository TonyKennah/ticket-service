package uk.org.kennah.services;

import uk.org.kennah.dto.TicketTypeRequest;
import uk.org.kennah.exceptions.InvalidPurchaseException;

public interface TicketService {
    void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException;
}

