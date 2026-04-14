package uk.org.kennah.services;

import uk.org.kennah.dto.TicketTypeRequest;
import uk.org.kennah.external.SeatReservationService;
import uk.org.kennah.external.TicketPaymentService;
import uk.org.kennah.exceptions.InvalidPurchaseException;


public class TicketServiceImpl implements TicketService {

    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    private static final int MAX_TICKETS = 25;
    private static final int ADULT_TICKET_PRICE = 25;
    private static final int CHILD_TICKET_PRICE = 15;
    private static final int INFANT_TICKET_PRICE = 0;

    /**
     * Constructs a new TicketServiceImpl with required external services.
     *
     * @param paymentService     the service used to process payments
     * @param reservationService the service used to reserve seats
     */
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    /**
     * Processes a ticket purchase request.
     * <p>
     * Validates the request against business rules, calculates total cost and seat requirements,
     * and interacts with external payment and reservation services.
     *
     * @param accountId          the ID of the account making the purchase; must be greater than 0
     * @param ticketTypeRequests one or more requests specifying the type and quantity of tickets
     * @throws InvalidPurchaseException if the account ID is invalid, no tickets are requested,
     *                                  the total exceeds the maximum limit, or other business rules are violated
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // 1. Validate Basic Inputs
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid Account ID");
        }

        if (ticketTypeRequests == null || ticketTypeRequests.length == 0 ) {
            throw new InvalidPurchaseException("No tickets requested");
        }

        int totalTickets = 0;
        int adultCount = 0;
        int childCount = 0;
        int infantCount = 0;
        int totalCost = 0;

        // 2. Aggregate counts and calculate costs
        for (TicketTypeRequest request : ticketTypeRequests) {
            int count = request.getNoOfTickets();
            if (count <= 0) throw new InvalidPurchaseException("Each ticket request must be for at least 1 ticket");
            
            totalTickets += count;
            
            switch (request.getTicketType()) {
                case ADULT -> {
                    adultCount += count;
                    totalCost += (count * ADULT_TICKET_PRICE);
                }
                case CHILD -> {
                    childCount += count;
                    totalCost += (count * CHILD_TICKET_PRICE);
                }
                case INFANT -> {
                    infantCount += count;
                    totalCost += (count * INFANT_TICKET_PRICE);
                }
            }
        }

        // 3. Business Rule Validations
        validateBusinessRules(totalTickets, adultCount, childCount, infantCount);

        // 4. Execute external service calls
        // Seats: Adults + Children (Infants sit on laps)
        int seatsToReserve = adultCount + childCount;
        
        paymentService.makePayment(accountId, totalCost);
        reservationService.reserveSeats(accountId, seatsToReserve);
    }

    private void validateBusinessRules(int total, int adults, int children, int infants) {
        if (total > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets");
        }

        if ((children > 0 || infants > 0) && adults == 0) {
            throw new InvalidPurchaseException("Child/Infant tickets require at least one Adult ticket");
        }
        
        if (infants > adults) {
            throw new InvalidPurchaseException("Each infant must have an adult lap; infants cannot exceed adults");
        }
    }
}