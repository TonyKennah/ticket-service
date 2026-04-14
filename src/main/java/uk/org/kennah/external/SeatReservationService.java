package uk.org.kennah.external;

public interface SeatReservationService {
    void reserveSeats(long accountId, int totalSeatsToAllocate);
}

