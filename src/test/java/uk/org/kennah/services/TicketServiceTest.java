package uk.org.kennah.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.org.kennah.exceptions.InvalidPurchaseException;
import uk.org.kennah.dto.TicketTypeRequest;
import uk.org.kennah.external.SeatReservationService;
import uk.org.kennah.external.TicketPaymentService;
import uk.org.kennah.services.TicketServiceImpl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void testPurchaseTicketsCallsExternalServices() {
        Long accountId = 1L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        ticketService.purchaseTickets(accountId, adultRequest);

        // Verify that the payment service was called with the correct amount (1 Adult = £25)
        verify(paymentService).makePayment(accountId, 25);

        // Verify that the reservation service was called with the correct seat count (1 Adult = 1 seat)
        verify(reservationService).reserveSeats(accountId, 1);
    }

    @Test
    void testPurchaseMixedTicketsSuccess() {
        Long accountId = 1L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(accountId, adultRequest, childRequest, infantRequest);

        // Verify total cost: 1 Adult (£25) + 1 Child (£15) + 1 Infant (£0) = £40
        verify(paymentService).makePayment(accountId, 40);

        // Verify total seats: 1 Adult + 1 Child = 2 seats (Infants do not require a seat)
        verify(reservationService).reserveSeats(accountId, 2);
    }

    @Test
    void testPurchaseMoreThanMaxTicketsThrowsException() {
        Long accountId = 1L;
        // MAX_TICKETS is 25, so requesting 26 should fail
        TicketTypeRequest tooManyTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(accountId, tooManyTickets)
        );

        // Verify no calls were made to external services
        verifyNoInteractions(paymentService, reservationService);
    }

    @Test
    void testPurchaseChildTicketWithoutAdultThrowsException() {
        Long accountId = 1L;
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(accountId, childRequest)
        );

        verifyNoInteractions(paymentService, reservationService);
    }

    @Test
    void testPurchaseInfantsExceedAdultsThrowsException() {
        Long accountId = 1L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(accountId, adultRequest, infantRequest)
        );

        verifyNoInteractions(paymentService, reservationService);
    }

    @Test
    void testPurchaseWithZeroTicketsInRequestThrowsException() {
        Long accountId = 1L;
        // Requesting 0 tickets for a type should now throw an exception
        TicketTypeRequest zeroAdults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(accountId, zeroAdults)
        );

        verifyNoInteractions(paymentService, reservationService);
    }

    @Test
    void testInvalidAccountIdThrowsException() {
        Long invalidAccountId = 0L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(invalidAccountId, adultRequest)
        );

        verifyNoInteractions(paymentService, reservationService);
    }
}