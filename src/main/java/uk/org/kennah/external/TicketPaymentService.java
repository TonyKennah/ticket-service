package uk.org.kennah.external;

public interface TicketPaymentService {
    void makePayment(long accountId, int totalAmountToPay);
}

