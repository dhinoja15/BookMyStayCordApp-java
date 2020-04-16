package com.Booking.states;
import com.Booking.contracts.BookingContract;
import io.netty.util.concurrent.AbstractFuture;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
// *********
// * State *
// *********
@BelongsToContract(BookingContract.class)
public class BookingState implements ContractState {
    private final String custName;
    private final int custAge;
    private final Instant checkInDate;
    private final Instant checkOutDate;
    private final String roomType;
    private final int roomRate;
    private final String creditCardNumber;
    private final Instant creditCardExpDate;
    private final double creditCardAmount;
    private final Party BookYourStay;
    private final Party HotelHeaven;
    public BookingState(String custName, int custAge, Instant checkInDate, Instant checkOutDate, String roomType, int roomRate, String creditCardNumber, Instant creditCardExpDate, double creditCardAmount, Party bookYourStay, Party hotelHeaven) {
        this.custName = custName;
        this.custAge = custAge;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.roomRate = roomRate;
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpDate = creditCardExpDate;
        this.creditCardAmount = creditCardAmount;
        this.BookYourStay = bookYourStay;
        this. HotelHeaven = hotelHeaven;
    }
    public String getCustName() {
        return custName;
    }
    public int getCustAge() {
        return custAge;
    }
    public Instant getCheckInDate() {
        return checkInDate;
    }
    public Instant getCheckOutDate() {
        return checkOutDate;
    }
    public String getRoomType() {
        return roomType;
    }
    public int getRoomRate() {
        return roomRate;
    }
    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    public Instant getCreditCardExpDate() {
        return creditCardExpDate;
    }
    public double getCreditCardAmount() {
        return creditCardAmount;
    }
    public Party getBookYourStay() {
        return BookYourStay;
    }
    public Party getHotelHeaven() {
        return HotelHeaven;
    }
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(BookYourStay,HotelHeaven);
    }
}
