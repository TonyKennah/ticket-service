package uk.org.kennah.dto;
/**
 * Immutable ticket request.
 */
public record TicketTypeRequest(Type ticketType, int noOfTickets) {
    public enum Type {
        ADULT, CHILD, INFANT
    }

    public Type getTicketType() {
        return ticketType;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }
}
